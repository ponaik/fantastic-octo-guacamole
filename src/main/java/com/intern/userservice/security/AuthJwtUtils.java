package com.intern.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthJwtUtils {

    public Jwt getJwt(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) return (Jwt) principal;
        return null;
    }

    public UUID getSubject(Authentication authentication) {
        Jwt jwt = getJwt(authentication);
        if (jwt == null) return null;

        String sub = jwt.getSubject();
        UUID tokenUuid;
        try {
            tokenUuid = UUID.fromString(sub);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return tokenUuid;
    }
}