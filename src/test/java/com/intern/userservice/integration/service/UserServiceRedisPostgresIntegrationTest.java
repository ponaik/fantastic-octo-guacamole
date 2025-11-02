package com.intern.userservice.integration.service;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.integration.extension.RedisTestContainerExtension;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("test")
@ExtendWith({RedisTestContainerExtension.class, PostgresTestContainerExtension.class})
class UserServiceRedisPostgresIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private CacheManager cacheManager;

    private Cache userCache;
    private Cache userByEmailCache;
    private Cache usersCache;
    private Cache userCardsCache;

    @BeforeEach
    void setup() {
        userCache = cacheManager.getCache("user");
        userByEmailCache = cacheManager.getCache("userByEmail");
        usersCache = cacheManager.getCache("users");
        userCardsCache = cacheManager.getCache("userCards");

        userCache.clear();
        userByEmailCache.clear();
        usersCache.clear();
        userCardsCache.clear();
    }

    @Test
    @Transactional
    void createUser_shouldPersistAndCache() {
        UserCreateDto dto = new UserCreateDto(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990,1,1), "john@example.com");

        UserResponse response = userService.createUser(dto);

        assertThat(response.id()).isNotNull();
        assertThat(userCache.get(response.id(), UserResponse.class)).isNotNull();
        assertThat(userByEmailCache.get("john@example.com", UserResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void getUserById_shouldReturnAndCache() {
        UserCreateDto dto = new UserCreateDto(UUID.randomUUID(),"Jane", "Smith", LocalDate.of(1995,5,5), "jane@example.com");
        UserResponse created = userService.createUser(dto);

        Optional<UserResponse> response = userService.getUserById(created.id());

        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().email()).isEqualTo("jane@example.com");
        assertThat(userCache.get(created.id(), UserResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void getAllUsers_shouldCachePage() {
        userService.createUser(new UserCreateDto(UUID.randomUUID(),"A", "B", LocalDate.of(2000,1,1), "a@example.com"));

        Page<UserResponse> page = userService.getAllUsers(PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(usersCache.get("0-10")).isNotNull();
    }

    @Test
    @Transactional
    void getUserByEmail_shouldReturnAndCache() {
        userService.createUser(new UserCreateDto(UUID.randomUUID(),"Tom", "Jerry", LocalDate.of(1988,8,8), "tom@example.com"));

        Optional<UserResponse> response = userService.getUserByEmail("tom@example.com");

        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().name()).isEqualTo("Tom");
        assertThat(userByEmailCache.get("tom@example.com", UserResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void updateUser_shouldUpdateAndRefreshCache() {
        UserResponse created = userService.createUser(
                new UserCreateDto(UUID.randomUUID(),"Old", "Name", LocalDate.of(1980,1,1), "old@example.com"));

        UserUpdateDto dto = new UserUpdateDto("New", "Name", LocalDate.of(1980,1,1), "new@example.com");
        UserResponse updated = userService.updateUser(created.id(), dto);

        assertThat(updated.email()).isEqualTo("new@example.com");
        assertThat(userCache.get(created.id(), UserResponse.class).email()).isEqualTo("new@example.com");
        assertThat(userByEmailCache.get("new@example.com", UserResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void deleteUser_shouldRemoveFromDbAndEvictCache() {
        UserResponse created = userService.createUser(
                new UserCreateDto(UUID.randomUUID(),"Del", "User", LocalDate.of(1970,1,1), "del@example.com"));
        cardInfoRepository.createCardNative("1234-5678", "John Doe", LocalDate.of(2030, 1, 1), created.id());

        userService.getUserById(created.id()); // populate cache
        userService.deleteUser(created.id());

        assertThat(userCache.get(created.id())).isNull();
        assertThat(usersCache.get("0-10")).isNull();
        assertThat(userCardsCache.get(created.id())).isNull();

        Optional<UserResponse> user = userService.getUserById(created.id());
        assertThat(user).isEmpty();
    }
}
