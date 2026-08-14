package com.hoagiayphudong.helper.security;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<String> getCurrentUsernameLogin() {
        return getCurrentAuthentication().map(SecurityUtil::extractUsername);
    }

    public static Optional<Long> getCurrentUserIdLogin() {
        return getCurrentJwt().flatMap(SecurityUtil::extractUserId);
    }

    public static List<String> getCurrentRoles() {
        return getCurrentAuthentication()
                .map(authentication -> authentication.getAuthorities()
                        .stream()
                        .map(authority -> authority.getAuthority())
                        .toList())
                .orElseGet(List::of);
    }

    public static Optional<Jwt> getCurrentJwt() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast);
    }

    public static boolean isLoggedIn() {
        return getCurrentAuthentication().isPresent();
    }

    private static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of(authentication);
    }

    private static String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        if (principal instanceof String username) {
            return username;
        }

        return authentication.getName();
    }

    private static Optional<Long> extractUserId(Jwt jwt) {
        Object idClaim = jwt.getClaim("id");
        if (idClaim == null) {
            return Optional.empty();
        }

        if (idClaim instanceof Number number) {
            return Optional.of(number.longValue());
        }

        try {
            return Optional.of(Long.parseLong(idClaim.toString()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
