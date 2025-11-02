package com.intern.userservice.integration.repository;

import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("test")
@ExtendWith(PostgresTestContainerExtension.class)
@Transactional
class CardInfoRepositoryPostgresIntegrationTest {

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser(String name, String surname, LocalDate dob) {
        String email = name.toLowerCase() + "." + surname.toLowerCase() + "+" + UUID.randomUUID() + "@example.com";
        return userRepository.createUserNative(UUID.randomUUID(), name, surname, dob, email);
    }

    private CardInfo createTestCard(String number, String holder, LocalDate expires, Long userId) {
        return cardInfoRepository.createCardNative(number, holder, expires, userId);
    }

    @Test
    void testCreateCardNative() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        CardInfo created = createTestCard("1234567890123456", "Test Holder", LocalDate.of(2030, 12, 31), user.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getHolder()).isEqualTo("Test Holder");

        Optional<CardInfo> found = cardInfoRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void testFindByIdNative() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        CardInfo card = createTestCard("4111111111111111", "Shared User", LocalDate.of(2028, 6, 30), user.getId());

        Optional<CardInfo> fetched = cardInfoRepository.findByIdNative(card.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getHolder()).isEqualTo("Shared User");
        assertThat(fetched.get().getNumber()).isEqualTo("4111111111111111");
    }

    @Test
    void testDeleteByIdNative() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        CardInfo cardToDelete = createTestCard("5555444433332222", "To Delete", LocalDate.of(2029, 7, 31), user.getId());

        int deleted = cardInfoRepository.deleteByIdNative(cardToDelete.getId());
        assertThat(deleted).isEqualTo(1);

        Optional<CardInfo> shouldBeEmpty = cardInfoRepository.findByIdNative(cardToDelete.getId());
        assertThat(shouldBeEmpty).isEmpty();
    }

    @Test
    void testGetCardInfosByUserId() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        createTestCard("4111111111111111", "Shared User", LocalDate.of(2028, 6, 30), user.getId());
        createTestCard("4222222222222222", "Shared User", LocalDate.of(2029, 6, 30), user.getId());

        List<CardInfo> cards = cardInfoRepository.getCardInfosByUserId(user.getId());
        assertThat(cards).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testExistsCardInfoByUserIdAndNumber() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        createTestCard("4111111111111111", "Shared User", LocalDate.of(2028, 6, 30), user.getId());

        boolean exists = cardInfoRepository.existsCardInfoByUserIdAndNumber(user.getId(), "4111111111111111");
        boolean notExists = cardInfoRepository.existsCardInfoByUserIdAndNumber(user.getId(), "nonexistent");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByIdNamedMethod() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        CardInfo card = createTestCard("4222222222222222", "Shared User", LocalDate.of(2029, 6, 30), user.getId());

        Optional<CardInfo> fetched = cardInfoRepository.findById(card.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getHolder()).isEqualTo("Shared User");
    }

    @Test
    void testDeleteByIdNamedMethod() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        CardInfo card = createTestCard("6666777788889999", "Shared User Delete", LocalDate.of(2031, 8, 31), user.getId());

        cardInfoRepository.deleteById(card.getId());
        Optional<CardInfo> shouldBeEmpty = cardInfoRepository.findById(card.getId());
        assertThat(shouldBeEmpty).isEmpty();
    }

    @Test
    void testFindAllWithPagination() {
        User user = createTestUser("Shared", "User", LocalDate.of(1990, 1, 1));

        for (int i = 0; i < 8; i++) {
            String number = String.format("70000000000000%02d", i);
            createTestCard(number, "Holder" + i, LocalDate.of(2030, 1, 1), user.getId());
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        Page<CardInfo> page = cardInfoRepository.findAll(pageable);

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(8);
        assertThat(page.getContent().size()).isLessThanOrEqualTo(3);
    }
}
