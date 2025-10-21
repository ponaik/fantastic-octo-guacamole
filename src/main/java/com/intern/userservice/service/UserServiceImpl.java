package com.intern.userservice.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.exception.EmailAlreadyExistsException;
import com.intern.userservice.mapper.UserMapper;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse createUser(UserCreateDto request) {
//        User user = userMapper.fromUserCreateDto(request);

        User created = userRepository.createUserNative(
                request.name(),
                request.surname(),
                request.birthDate(),
                request.email()
        );

        return userMapper.toUserResponse(created);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findByIdJPQL(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email " + email));
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long id, UserUpdateDto request) {
        User user = userRepository.findByIdJPQL(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        userMapper.updateEntityFromDto(request, user);

        User updated = userRepository.updateByIdNative(
                id,
                user.getName(),
                user.getSurname(),
                user.getBirthDate(),
                user.getEmail());
        return userMapper.toUserResponse(updated);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        userRepository.deleteByIdJPQL(id);
    }
}

