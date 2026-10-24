package com.raxim.myscoutee.domain.platform.security.bootstrap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.raxim.myscoutee.domain.accounts.api.BootstrapAccounts;
import com.raxim.myscoutee.domain.platform.api.RuntimeConfiguration;

/** Verifies the provisioned account's password; application sessions are issued by accounts. */
@Service
public class BootstrapAccountAuthenticationService {
    static final Duration FAILURE_WINDOW = Duration.ofMinutes(1);
    static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Pattern DIGEST = Pattern.compile("[0-9a-f]{64}");
    private static final Pattern PROVISIONED_EMAIL = Pattern.compile("operator-[0-9a-f]{24}@deployment\\.invalid");
    private final RuntimeConfiguration config;
    private final BootstrapAccounts accounts;
    private final Clock clock;
    private final Object failureLock = new Object();
    private String failureSubject = "";
    private Instant failureWindowStarted = Instant.EPOCH;
    private int failedAttempts;

    @Autowired
    public BootstrapAccountAuthenticationService(RuntimeConfiguration config, BootstrapAccounts accounts) {
        this(config, accounts, Clock.systemUTC());
    }

    BootstrapAccountAuthenticationService(RuntimeConfiguration config, BootstrapAccounts accounts, Clock clock) {
        this.config = config;
        this.accounts = accounts;
        this.clock = clock;
    }

    public UserDetails authenticate(String email, String password) {
        String subject = normalize(config.getOperatorBootstrapEmail());
        String digest = config.getOperatorBootstrapPasswordSha256();
        if (!subject.equals(normalize(config.getOperatorUser()))
                || !PROVISIONED_EMAIL.matcher(subject).matches()
                || digest == null || !DIGEST.matcher(digest).matches()
                || !subject.equals(normalize(email))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Local password authentication is not applicable.");
        }
        if (password == null || password.isBlank() || password.length() > 512) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }
        synchronized (failureLock) {
            Instant now = clock.instant();
            String identity = subject + ':' + digest;
            if (!identity.equals(failureSubject) || now.isBefore(failureWindowStarted)
                    || !now.isBefore(failureWindowStarted.plus(FAILURE_WINDOW))) {
                failureSubject = identity;
                failureWindowStarted = now;
                failedAttempts = 0;
            }
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Password login is temporarily throttled.");
            }
            byte[] candidate;
            try {
                candidate = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            } catch (java.security.NoSuchAlgorithmException impossible) {
                throw new IllegalStateException(impossible);
            }
            if (!MessageDigest.isEqual(HexFormat.of().parseHex(digest), candidate)) {
                failedAttempts++;
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
            }
            failedAttempts = 0;
            failureWindowStarted = now;
        }
        UserDetails account = accounts.loadOrCreateBootstrapOperator(subject);
        if (account == null || !account.isEnabled() || !account.isAccountNonLocked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is unavailable.");
        }
        return account;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
