package com.intern.userservice.integration.repository;

import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private User sharedUser;

    @BeforeEach
    void setUpOnce() {
        sharedUser = userRepository.createUserNative(
                UUID.randomUUID(),
                "Shared",
                "User",
                LocalDate.of(1990, 1, 1),
                "shared.user@example.com"
        );
    }

    @Test
    void testCreateCardNative() {
        CardInfo created = cardInfoRepository.createCardNative(
                "1234567890123456",
                "Test Holder",
                LocalDate.of(2030, 12, 31),
                sharedUser.getId()
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getHolder()).isEqualTo("Test Holder");

        Optional<CardInfo> found = cardInfoRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void testFindByIdNative() {
        CardInfo card = cardInfoRepository.createCardNative(
                "4111111111111111",
                "Shared User",
                LocalDate.of(2028, 6, 30),
                sharedUser.getId()
        );

        Optional<CardInfo> fetched = cardInfoRepository.findByIdNative(card.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getHolder()).isEqualTo("Shared User");
        assertThat(fetched.get().getNumber()).isEqualTo("4111111111111111");
    }

    @Test
    void testDeleteByIdNative() {
        CardInfo cardToDelete = cardInfoRepository.createCardNative(
                "5555444433332222",
                "To Delete",
                LocalDate.of(2029, 7, 31),
                sharedUser.getId()
        );

        int deleted = cardInfoRepository.deleteByIdNative(cardToDelete.getId());
        assertThat(deleted).isEqualTo(1);

        Optional<CardInfo> shouldBeEmpty = cardInfoRepository.findByIdNative(cardToDelete.getId());
        assertThat(shouldBeEmpty).isEmpty();
    }

    @Test
    void testGetCardInfosByUserId() {
        // create two cards for shared user
        cardInfoRepository.createCardNative("4111111111111111", "Shared User", LocalDate.of(2028, 6, 30), sharedUser.getId());
        cardInfoRepository.createCardNative("4222222222222222", "Shared User", LocalDate.of(2029, 6, 30), sharedUser.getId());

        List<CardInfo> cards = cardInfoRepository.getCardInfosByUserId(sharedUser.getId());
        assertThat(cards).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testExistsCardInfoByUserIdAndNumber() {
        cardInfoRepository.createCardNative("4111111111111111", "Shared User", LocalDate.of(2028, 6, 30), sharedUser.getId());

        boolean exists = cardInfoRepository.existsCardInfoByUserIdAndNumber(sharedUser.getId(), "4111111111111111");
        boolean notExists = cardInfoRepository.existsCardInfoByUserIdAndNumber(sharedUser.getId(), "nonexistent");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByIdNamedMethod() {
        CardInfo card = cardInfoRepository.createCardNative(
                "4222222222222222",
                "Shared User",
                LocalDate.of(2029, 6, 30),
                sharedUser.getId()
        );

        Optional<CardInfo> fetched = cardInfoRepository.findById(card.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getHolder()).isEqualTo("Shared User");
    }

    @Test
    void testDeleteByIdNamedMethod() {
        CardInfo card = cardInfoRepository.createCardNative(
                "6666777788889999",
                "Shared User Delete",
                LocalDate.of(2031, 8, 31),
                sharedUser.getId()
        );

        cardInfoRepository.deleteById(card.getId());
        Optional<CardInfo> shouldBeEmpty = cardInfoRepository.findById(card.getId());
        assertThat(shouldBeEmpty).isEmpty();
    }

    @Test
    void testFindAllWithPagination() {
        // create 8 cards for predictable total (all assigned to the shared user)
        for (int i = 0; i < 8; i++) {
            String number = String.format("70000000000000%02d", i);
            cardInfoRepository.createCardNative(number, "Holder" + i, LocalDate.of(2030, 1, 1), sharedUser.getId());
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        Page<CardInfo> page = cardInfoRepository.findAll(pageable);

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(8);
        assertThat(page.getContent().size()).isLessThanOrEqualTo(3);
    }
}