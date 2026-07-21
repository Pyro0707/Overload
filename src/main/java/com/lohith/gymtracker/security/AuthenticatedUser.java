package com.lohith.gymtracker.security;

import org.springframework.security.core.Authentication;

/**
 * Lightweight principal stored in SecurityContext after JWT validation.
 * Avoids a DB lookup on every authenticated request.
 */
public record AuthenticatedUser(Long id, String username) {
    public static Long id(Authentication auth) {
        return ((AuthenticatedUser) auth.getPrincipal()).id();
    }
}
