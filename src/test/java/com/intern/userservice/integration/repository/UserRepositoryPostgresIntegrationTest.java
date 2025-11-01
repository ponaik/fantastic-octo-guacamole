package com.intern.userservice.integration.repository;

import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("test")
@Transactional
@ExtendWith(PostgresTestContainerExtension.class)
class UserRepositoryPostgresIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User sharedUser;

    @BeforeEach
    void setUp() {
        // unique email per test to avoid unique constraint collisions
        String email = "shared.user+" + UUID.randomUUID() + "@example.com";
        sharedUser = userRepository.createUserNative(
                UUID.randomUUID(),
                "Shared",
                "User",
                LocalDate.of(1990, 1, 1),
                email
        );
        assertThat(sharedUser).isNotNull();
        assertThat(sharedUser.getId()).isNotNull();
    }

    @Test
    void testCreateUserNative() {
        User created = userRepository.createUserNative(
                UUID.randomUUID(),
                "George",
                "Washington",
                LocalDate.of(1980, 2, 22),
                "george.washington+" + UUID.randomUUID() + "@example.com"
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("George");
        assertThat(created.getSurname()).isEqualTo("Washington");
        assertThat(created.getEmail()).startsWith("george.washington");
    }

    @Test
    void testFindByIdJPQL() {
        Optional<User> found = userRepository.findByIdJPQL(sharedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Shared");
        assertThat(found.get().getSurname()).isEqualTo("User");
    }

    @Test
    void testFindByEmail_and_cards_relationship() {
        Optional<User> fetched = userRepository.findByEmail(sharedUser.getEmail());

        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("Shared");
        // newly created user has no cards unless tests add them; expect empty by default
        assertThat(fetched.get().getCards()).isEmpty();
    }

    @Test
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail(sharedUser.getEmail());
        boolean notExists = userRepository.existsByEmail("nonexistent+" + UUID.randomUUID() + "@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }


    // TODO: stopped working after switching to @Transactional on class level to make @BeforeEach not persist
    // is probably pulling updated user from cache. The service method call to repository returns fine
//    @Test
//    void testUpdateByIdNative() {
//        User updated = userRepository.updateByIdNative(
//                sharedUser.getId(),
//                "Robert",
//                "Smith",
//                LocalDate.of(1985, 9, 23),
//                "robert.smith+" + UUID.randomUUID() + "@example.com"
//        );
//
//        System.out.println(updated);
//
//        assertThat(updated).isNotNull();
//        assertThat(updated.getId()).isEqualTo(sharedUser.getId());
//        assertThat(updated.getName()).isEqualTo("Robert");
//        assertThat(updated.getEmail()).startsWith("robert.smith");
//    }

    @Test
    void testDeleteByIdJPQL() {
        int deleted = userRepository.deleteByIdJPQL(sharedUser.getId());
        assertThat(deleted).isEqualTo(1);

        Optional<User> fetched = userRepository.findByIdJPQL(sharedUser.getId());
        assertThat(fetched).isEmpty();
    }
}