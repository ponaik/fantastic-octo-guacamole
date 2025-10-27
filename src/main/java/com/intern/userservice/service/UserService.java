package com.intern.userservice.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserResponse createUser(UserCreateDto request);

    Optional<UserResponse> getUserById(Long id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    Optional<UserResponse> getUserByEmail(String email);

    UserResponse updateUser(Long id, UserUpdateDto request);

    void deleteUser(Long id);
}
