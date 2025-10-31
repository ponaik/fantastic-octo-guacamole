package com.intern.userservice.security;

import com.intern.userservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isOwnerByUserId(Long resourceUserId) {
        UUID tokenUuid = getUuid();
        if (tokenUuid == null) return false;

        return userRepository.findSubById(resourceUserId)
                .map(tokenUuid::equals)
                .orElse(false);
    }

    public boolean isOwnerByEmail(String email) {
        UUID tokenUuid = getUuid();
        if (tokenUuid == null) return false;

        return userRepository.findSubByEmail(email)
                .map(tokenUuid::equals)
                .orElse(false);
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt) return (Jwt) principal;
        return null;
    }

    public UUID getUuid() {
        Jwt jwt = getJwt();
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
