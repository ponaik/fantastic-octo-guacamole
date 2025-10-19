package com.intern.userservice.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserResponse createUser(UserCreateDto request);

    UserResponse getUserById(Long id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UserUpdateDto request);

    void deleteUser(Long id);
}
