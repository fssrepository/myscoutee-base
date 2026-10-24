package com.raxim.myscoutee.microservices.transport.common;

import com.raxim.myscoutee.common.config.mongo.MongoSessionMode;
import com.raxim.myscoutee.common.config.mongo.MongoSessionModeContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class InternalReadCredentialFilterTest {
    private static final String TOKEN = "test-only-capability-token-0000000000000000000000000";
    private final InternalReadCredentialFilter filter = new InternalReadCredentialFilter(TOKEN, "test:read");

    @AfterEach void clear() {
        SecurityContextHolder.clearContext();
        MongoSessionModeContext.clear();
    }

    @Test void onlyVerifiedCapabilityHasAnExemptionAndItsContextIsRestored() throws Exception {
        var previous = UsernamePasswordAuthenticationToken.authenticated("user", null,
                List.of(new SimpleGrantedAuthority("U")));
        SecurityContextHolder.getContext().setAuthentication(previous);
        for (var mode : MongoSessionMode.values()) {
            var request = request(mode.name());
            assertFalse(InternalReadCredentialFilter.verifiedServiceRequest(request));
            var reached = new AtomicBoolean();
            filter.doFilter(request, new MockHttpServletResponse(), (incoming, response) -> {
                assertTrue(InternalReadCredentialFilter.verifiedServiceRequest(request));
                assertEquals(mode, MongoSessionModeContext.currentMode());
                reached.set(true);
            });
            assertTrue(reached.get());
            assertSame(previous, SecurityContextHolder.getContext().getAuthentication());
            assertFalse(InternalReadCredentialFilter.verifiedServiceRequest(request));
        }
    }

    @Test void cookieOrExistingUserAuthenticationCannotReplaceACapability() throws Exception {
        var request = request("REAL");
        request.removeHeader("Authorization");
        request.addHeader("Cookie", "JSESSIONID=forged; authorization=" + TOKEN);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("user", null, List.of()));
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (incoming, outgoing) -> fail("Unverified request reached the owner"));
        assertEquals(401, response.getStatus());
        assertFalse(InternalReadCredentialFilter.verifiedServiceRequest(request));
    }

    @Test void duplicateCredentialsAndInvalidModesCannotAcquireAnExemption() throws Exception {
        for (boolean duplicate : new boolean[]{false, true}) {
            var request = request(duplicate ? "REAL" : "unknown");
            if (duplicate) request.addHeader("Authorization", "Bearer " + TOKEN);
            var response = new MockHttpServletResponse();
            filter.doFilter(request, response, (incoming, outgoing) -> fail("Invalid request reached the owner"));
            assertEquals(duplicate ? 401 : 400, response.getStatus());
            assertFalse(InternalReadCredentialFilter.verifiedServiceRequest(request));
        }
    }

    @Test void ownerFailureDoesNotLeakTheVerifiedContext() {
        var request = request("DEMO");
        assertThrows(IllegalStateException.class, () -> filter.doFilter(request, new MockHttpServletResponse(),
                (incoming, outgoing) -> { throw new IllegalStateException("owner failed"); }));
        assertFalse(InternalReadCredentialFilter.verifiedServiceRequest(request));
    }

    private MockHttpServletRequest request(String mode) {
        var request = new MockHttpServletRequest("POST", "/_internal/test");
        request.addHeader("Authorization", "Bearer " + TOKEN);
        request.addHeader(InternalReadCredentialFilter.MODE_HEADER, mode);
        return request;
    }
}
