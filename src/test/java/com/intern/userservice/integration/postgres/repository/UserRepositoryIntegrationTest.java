package com.intern.userservice.integration.postgres.repository;

import com.intern.userservice.integration.AbstractPostgresIntegrationTest;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testCreateUserNative() {
        User created = userRepository.createUserNative(
                "George",
                "Washington",
                LocalDate.of(1980, 2, 22),
                "george.washington@example.com"
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("George");
        assertThat(created.getSurname()).isEqualTo("Washington");
        assertThat(created.getEmail()).isEqualTo("george.washington@example.com");
    }

    @Test
    @Transactional
    void testFindByIdJPQL() {
        Optional<User> found = userRepository.findByIdJPQL(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
        assertThat(found.get().getSurname()).isEqualTo("Johnson");
    }

    @Test
    @Transactional
    void testFindByEmail_seededAlice() {
        Optional<User> alice = userRepository.findByEmail("alice.johnson@example.com");

        assertThat(alice).isPresent();
        assertThat(alice.get().getName()).isEqualTo("Alice");
        assertThat(alice.get().getCards()).hasSize(2); // seeded with 2 cards
    }

    @Test
    @Transactional
    void testFindByEmail_seededDavid() {
        Optional<User> david = userRepository.findByEmail("david.brown@example.com");

        assertThat(david).isPresent();
        assertThat(david.get().getCards()).isEmpty(); // seeded with 0 cards
    }


    @Test
    @Transactional
    void testExistsByEmail() {
        // Create a user
        userRepository.createUserNative(
                "Isaac",
                "Newton",
                LocalDate.of(1643, 1, 4),
                "isaac.newton@example.com"
        );

        boolean exists = userRepository.existsByEmail("isaac.newton@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Transactional
    void testUpdateSeededUser() {
        User updated = userRepository.updateByIdNative(
                2L,
                "Robert",
                "Smith",
                java.time.LocalDate.of(1985, 9, 23),
                "robert.smith@example.com"
        );

        assertThat(updated.getName()).isEqualTo("Robert");
        assertThat(updated.getEmail()).isEqualTo("robert.smith@example.com");
    }

    @Test
    @Transactional
    void testDeleteSeededUser() {
        int deleted = userRepository.deleteByIdJPQL(5L); // Eva
        assertThat(deleted).isEqualTo(1);

        Optional<User> eva = userRepository.findByIdJPQL(5L);
        assertThat(eva).isEmpty();
    }


}
