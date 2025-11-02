package com.intern.userservice.integration.repository;

import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
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

    private User createTestUser(String name, String surname, LocalDate dob) {
        return userRepository.createUserNative(
                UUID.randomUUID(),
                name,
                surname,
                dob,
                name.toLowerCase() + "." + surname.toLowerCase() + "+" + UUID.randomUUID() + "@example.com"
        );
    }

    @Test
    void testCreateUserNative() {
        User created = createTestUser("George", "Washington", LocalDate.of(1980, 2, 22));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("George");
        assertThat(created.getSurname()).isEqualTo("Washington");
        assertThat(created.getEmail()).contains("george.washington");
    }

    @Test
    void testFindByIdJPQL() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        Optional<User> found = userRepository.findByIdJPQL(user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Shared");
        assertThat(found.get().getSurname()).isEqualTo("User");
    }

    @Test
    void testFindByEmail_and_cards_relationship() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        Optional<User> fetched = userRepository.findByEmail(user.getEmail());

        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("Shared");
        assertThat(fetched.get().getCards()).isEmpty();
    }

    @Test
    void testExistsByEmail() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        boolean exists = userRepository.existsByEmail(user.getEmail());
        boolean notExists = userRepository.existsByEmail("nonexistent+" + UUID.randomUUID() + "@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testDeleteByIdJPQL() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        int deleted = userRepository.deleteByIdJPQL(user.getId());
        assertThat(deleted).isEqualTo(1);

        Optional<User> fetched = userRepository.findByIdJPQL(user.getId());
        assertThat(fetched).isEmpty();
    }
}
