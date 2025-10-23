package com.intern.userservice.UserService;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.exception.EmailAlreadyExistsException;
import com.intern.userservice.mapper.UserMapper;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import com.intern.userservice.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @Captor
    private ArgumentCaptor<User> userCaptor;

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
                "Alicia",
                "Wonders",
                LocalDate.of(1990, 1, 1),
                "alicia@example.com"
        );
    }


    @Test
    void createUser_whenEmailDoesNotExist_createsAndReturnsResponse() {
        // given
        given(userRepository.existsByEmail("alice@example.com")).willReturn(false);
        given(userRepository.createUserNative(
                "Alice",
                "Wonder",
                LocalDate.of(1990, 1, 1),
                "alice@example.com"
        )).willReturn(sampleUser);
        given(userMapper.toUserResponse(sampleUser)).willReturn(sampleResponse);

        // when
        UserResponse result = userService.createUser(createDto);

        // then
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
        // given
        given(userRepository.existsByEmail("alice@example.com")).willReturn(true);

        // when + then
        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("alice@example.com");

        verify(userRepository).existsByEmail("alice@example.com");
        verify(userRepository, never()).createUserNative(any(), any(), any(), any());
    }


//    @Test
//    void getUserById_whenFound_returnsResponse() {
//        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
//        given(userMapper.toResponse(sampleUser)).willReturn(sampleResponse);
//
//        UserResponse result = userService.getUserById(1L);
//
//        assertThat(result).isEqualTo(sampleResponse);
//        verify(userRepository).findById(1L);
//    }
//
//    @Test
//    void getUserById_whenNotFound_throwsNotFoundException() {
//        given(userRepository.findById(2L)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.getUserById(2L))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("User not found");
//
//        verify(userRepository).findById(2L);
//    }
//
//    @Test
//    void getUserByEmail_whenFound_returnsResponse() {
//        given(userRepository.findByEmail("alice@example.com")).willReturn(Optional.of(sampleUser));
//        given(userMapper.toResponse(sampleUser)).willReturn(sampleResponse);
//
//        UserResponse result = userService.getUserByEmail("alice@example.com");
//
//        assertThat(result.getEmail()).isEqualTo("alice@example.com");
//        verify(userRepository).findByEmail("alice@example.com");
//    }
//
//    @Test
//    void getUserByEmail_whenNotFound_throwsNotFoundException() {
//        given(userRepository.findByEmail("missing@example.com")).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.getUserByEmail("missing@example.com"))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("User not found");
//
//        verify(userRepository).findByEmail("missing@example.com");
//    }
//
//    @Test
//    void getAllUsers_returnsPagedResponses() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<User> page = new PageImpl<>(Collections.singletonList(sampleUser), pageable, 1);
//        given(userRepository.findAll(pageable)).willReturn(page);
//        given(userMapper.toResponse(sampleUser)).willReturn(sampleResponse);
//
//        Page<UserResponse> result = userService.getAllUsers(pageable);
//
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getEmail()).isEqualTo("alice@example.com");
//        verify(userRepository).findAll(pageable);
//    }
//
//    @Test
//    void updateUser_whenFound_updatesAndReturnsResponse() {
//        given(userRepository.findById(1L)).willReturn(Optional.of(sampleUser));
//        // mapper updates entity in-place and returns void in this example
//        // If your mapper returns entity, adjust accordingly
//        given(userRepository.save(any(User.class))).willReturn(sampleUser);
//        given(userMapper.toResponse(sampleUser)).willReturn(sampleResponse);
//
//        UserResponse result = userService.updateUser(1L, updateDto);
//
//        assertThat(result).isEqualTo(sampleResponse);
//        verify(userRepository).findById(1L);
//        verify(userRepository).save(any(User.class));
//        verify(userMapper).updateEntityFromDto(eq(updateDto), eq(sampleUser));
//    }
//
//    @Test
//    void updateUser_whenNotFound_throwsNotFoundException() {
//        given(userRepository.findById(99L)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.updateUser(99L, updateDto))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("User not found");
//
//        verify(userRepository).findById(99L);
//    }
//
//    @Test
//    void deleteUser_whenExists_deletes() {
//        given(userRepository.existsById(1L)).willReturn(true);
//
//        userService.deleteUser(1L);
//
//        verify(userRepository).existsById(1L);
//        verify(userRepository).deleteById(1L);
//    }
//
//    @Test
//    void deleteUser_whenNotExists_throwsNotFoundException() {
//        given(userRepository.existsById(5L)).willReturn(false);
//
//        assertThatThrownBy(() -> userService.deleteUser(5L))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("User not found");
//
//        verify(userRepository).existsById(5L);
//    }
}
