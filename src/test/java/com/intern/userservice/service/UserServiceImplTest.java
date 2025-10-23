package com.intern.userservice.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.exception.EmailAlreadyExistsException;
import com.intern.userservice.mapper.UserMapper;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;
    private UserResponse sampleResponse;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("Alice");
        sampleUser.setSurname("Wonder");
        sampleUser.setBirthDate(LocalDate.of(1990, 1, 1));
        sampleUser.setEmail("alice@example.com");

        sampleResponse = new UserResponse(
                1L,
                "Alice",
                "Wonder",
                LocalDate.of(1990, 1, 1),
                "alice@example.com"
        );

        createDto = new UserCreateDto(
                "Alice",
                "Wonder",
                LocalDate.of(1990, 1, 1),
                "alice@example.com"
        );

        updateDto = new UserUpdateDto(
                null,
                "Wonders",
                LocalDate.of(1990, 1, 1),
                "alicia@example.com"
        );
    }


    @Test
    void createUser_whenEmailDoesNotExist_createsAndReturnsResponse() {
        given(userRepository.existsByEmail("alice@example.com")).willReturn(false);
        given(userRepository.createUserNative(
                "Alice",
                "Wonder",
                LocalDate.of(1990, 1, 1),
                "alice@example.com"
        )).willReturn(sampleUser);
        given(userMapper.toUserResponse(sampleUser)).willReturn(sampleResponse);

        UserResponse result = userService.createUser(createDto);

        assertThat(result).isEqualTo(sampleResponse);

        verify(userRepository).existsByEmail("alice@example.com");
        verify(userRepository).createUserNative(
                "Alice",
                "Wonder",
                LocalDate.of(1990, 1, 1),
                "alice@example.com"
        );
        verify(userMapper).toUserResponse(sampleUser);
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsException() {
        given(userRepository.existsByEmail("alice@example.com")).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("alice@example.com");

        verify(userRepository).existsByEmail("alice@example.com");
        verify(userRepository, never()).createUserNative(any(), any(), any(), any());
    }


    @Test
    void getUserById_whenFound_returnsResponse() {
        given(userRepository.findByIdJPQL(1L)).willReturn(Optional.of(sampleUser));
        given(userMapper.toUserResponse(sampleUser)).willReturn(sampleResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isEqualTo(sampleResponse);
        verify(userRepository).findByIdJPQL(1L);
    }

    @Test
    void getUserById_whenNotFound_throwsNotFoundException() {
        given(userRepository.findByIdJPQL(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id 2");

        verify(userRepository).findByIdJPQL(2L);
    }

    @Test
    void getUserByEmail_whenFound_returnsResponse() {
        given(userRepository.findByEmail("alice@example.com")).willReturn(Optional.of(sampleUser));
        given(userMapper.toUserResponse(sampleUser)).willReturn(sampleResponse);

        UserResponse result = userService.getUserByEmail("alice@example.com");

        assertThat(result.email()).isEqualTo("alice@example.com");
        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void getUserByEmail_whenNotFound_throwsNotFoundException() {
        given(userRepository.findByEmail("missing@example.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("missing@example.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with email missing@example.com");

        verify(userRepository).findByEmail("missing@example.com");
    }

    @Test
    void getAllUsers_returnsPagedResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(sampleUser), pageable, 1);
        given(userRepository.findAll(pageable)).willReturn(page);
        given(userMapper.toUserResponse(sampleUser)).willReturn(sampleResponse);

        Page<UserResponse> result = userService.getAllUsers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).email()).isEqualTo("alice@example.com");
        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateUser_WhenValid_ShouldUpdateAndReturnResponse() {
        given(userRepository.findByIdJPQL(1L)).willReturn(Optional.of(sampleUser));
        given(userRepository.existsByEmail(updateDto.email())).willReturn(false);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Alice"); // unchanged
        updatedUser.setSurname("Wonders"); // updated
        updatedUser.setBirthDate(LocalDate.of(1990, 1, 1));
        updatedUser.setEmail("alicia@example.com"); // updated

        willAnswer(invocation -> {
            UserUpdateDto dto = invocation.getArgument(0);
            User u = invocation.getArgument(1);
            if (dto.name() != null) u.setName(dto.name());
            if (dto.surname() != null) u.setSurname(dto.surname());
            if (dto.birthDate() != null) u.setBirthDate(dto.birthDate());
            if (dto.email() != null) u.setEmail(dto.email());
            return null;
        }).given(userMapper).updateEntityFromDto(updateDto, sampleUser);

        UserResponse updatedUserResponse = new UserResponse(
                1L,
                "Alice",
                "Wonders",
                LocalDate.of(1990, 1, 1),
                "alicia@example.com");

        given(userRepository.updateByIdNative(
                eq(1L),
                eq("Alice"),
                eq("Wonders"),
                eq(LocalDate.of(1990, 1, 1)),
                eq("alicia@example.com")
        )).willReturn(updatedUser);

        given(userMapper.toUserResponse(updatedUser)).willReturn(updatedUserResponse);

        UserResponse result = userService.updateUser(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Alice");
        assertThat(result.surname()).isEqualTo("Wonders"); // sampleResponse stubbed
        verify(userRepository).findByIdJPQL(1L);
        verify(userRepository).existsByEmail("alicia@example.com");
        verify(userMapper).updateEntityFromDto(updateDto, sampleUser);
        verify(userRepository).updateByIdNative(1L, "Alice", "Wonders", LocalDate.of(1990, 1, 1), "alicia@example.com");
        verify(userMapper).toUserResponse(updatedUser);
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        given(userRepository.findByIdJPQL(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id 1");

        verify(userRepository, never()).updateByIdNative(anyLong(), any(), any(), any(), any());
    }

    @Test
    void updateUser_WhenEmailAlreadyExists_ShouldThrowException() {
        given(userRepository.findByIdJPQL(1L)).willReturn(Optional.of(sampleUser));
        given(userRepository.existsByEmail(updateDto.email())).willReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, updateDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email alicia@example.com is already in use");

        verify(userRepository, never()).updateByIdNative(anyLong(), any(), any(), any(), any());
    }

    @Test
    void deleteUser_whenExists_deletes() {
        given(userRepository.existsById(1L)).willReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteByIdJPQL(1L);
    }

    @Test
    void deleteUser_whenNotExists_throwsNotFoundException() {
        given(userRepository.existsById(5L)).willReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(5L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id 5");

        verify(userRepository).existsById(5L);
    }
}
