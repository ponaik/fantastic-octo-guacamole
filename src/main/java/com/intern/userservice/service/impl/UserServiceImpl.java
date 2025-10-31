package com.intern.userservice.service.impl;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.exception.EmailAlreadyExistsException;
import com.intern.userservice.mapper.UserMapper;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import com.intern.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    @Caching(
            put = {
                    @CachePut(value = "user", key = "#result.id"),
                    @CachePut(value = "userByEmail", key = "#result.email")
            },
            evict = {
                    @CacheEvict(value = "users", allEntries = true)
            }
    )
    public UserResponse createUser(UserCreateDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User created = userRepository.createUserNative(
                request.sub(),
                request.name(),
                request.surname(),
                request.birthDate(),
                request.email()
        );

        return userMapper.toUserResponse(created);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findByIdJPQL(id)
                .map(userMapper::toUserResponse);
    }

    @Override
    @Cacheable(value = "users", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Override
    @Cacheable(value = "userByEmail", key = "#email")
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserResponse);
    }

    @Transactional
    @Override
    @Caching(
            put = {
                    @CachePut(value = "user", key = "#id"),
                    @CachePut(value = "userByEmail", key = "#result.email")
            },
            evict = {
                    @CacheEvict(value = "users", allEntries = true)
            }
    )
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
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#id"),
                    @CacheEvict(value = "userByEmail", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "userCards", key = "#id")
            }
    )
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }
        userRepository.deleteByIdJPQL(id);
    }
}
