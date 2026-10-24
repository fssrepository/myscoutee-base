package com.raxim.myscoutee.domain.accounts.api;

import jakarta.servlet.http.HttpServletRequest;

/** Transport contract shared by every authenticated application account. */
public final class SessionRequestSupport {
    public static final String LOGIN_PATH = "/auth/session/login";
    public static final String API_LOGIN_PATH = "/api/auth/session/login";
    public static final String AUTHORIZATION_SCHEME = "Bearer";

    private SessionRequestSupport() {}

    public static boolean isLoginRequest(HttpServletRequest request) {
        return request != null && (LOGIN_PATH.equals(request.getRequestURI())
                || API_LOGIN_PATH.equals(request.getRequestURI()));
    }

    public static String bearerToken(HttpServletRequest request) {
        if (request == null) return null;
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authorization.substring(7).trim();
        }
        String path = request.getRequestURI();
        if (path != null && (path.endsWith("/activities/chats/ws")
                || path.endsWith("/admin/notifications/ws"))) {
            return request.getParameter("token");
        }
        return null;
    }
}
