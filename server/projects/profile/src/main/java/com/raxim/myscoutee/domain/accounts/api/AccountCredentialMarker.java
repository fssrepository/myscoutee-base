package com.raxim.myscoutee.domain.accounts.api;

/** Proof of account authentication, independent of role and login provider. */
public interface AccountCredentialMarker {
    static boolean isPresent(org.springframework.security.core.Authentication authentication) {
        return authentication != null && authentication.getCredentials() instanceof AccountCredentialMarker;
    }
}
