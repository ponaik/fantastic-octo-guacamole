package com.intern.userservice.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface UserService {
    @PreAuthorize("hasRole('admin') or #request.sub() == @authJwtUtils.getSubject(authentication)")
    UserResponse createUser(UserCreateDto request);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByUserId(authentication, #id)")
    Optional<UserResponse> getUserById(Long id);

    @PreAuthorize("hasRole('admin')")
    Page<UserResponse> getAllUsers(Pageable pageable);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByEmail(authentication, #email)")
    Optional<UserResponse> getUserByEmail(String email);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByUserId(authentication, #id)")
    UserResponse updateUser(Long id, UserUpdateDto request);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByUserId(authentication, #id)")
    void deleteUser(Long id);
}
