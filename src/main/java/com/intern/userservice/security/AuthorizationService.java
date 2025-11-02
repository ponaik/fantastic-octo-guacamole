package com.intern.userservice.security;

import com.intern.userservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final AuthJwtUtils authJwtUtils;

    public AuthorizationService(UserRepository userRepository, AuthJwtUtils authJwtUtils) {
        this.userRepository = userRepository;
        this.authJwtUtils = authJwtUtils;
    }

    public boolean isOwnerByCardId(Authentication authentication, Long cardId) {
        UUID tokenUuid = authJwtUtils.getSubject(authentication);
        if (tokenUuid == null) return false;

        return userRepository.findSubByCardId(cardId)
                .map(tokenUuid::equals)
                .orElse(false);
    }

    public boolean isOwnerByUserId(Authentication authentication, Long resourceUserId) {
        UUID tokenUuid = authJwtUtils.getSubject(authentication);
        if (tokenUuid == null) return false;

        return userRepository.findSubById(resourceUserId)
                .map(tokenUuid::equals)
                .orElse(false);
    }

    public boolean isOwnerByEmail(Authentication authentication, String email) {
        UUID tokenUuid = authJwtUtils.getSubject(authentication);
        if (tokenUuid == null) return false;

        return userRepository.findSubByEmail(email)
                .map(tokenUuid::equals)
                .orElse(false);
    }

}
