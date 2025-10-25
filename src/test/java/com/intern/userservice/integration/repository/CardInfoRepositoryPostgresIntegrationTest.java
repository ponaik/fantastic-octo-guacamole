package com.intern.userservice.integration.repository;

import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.repository.CardInfoRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@ExtendWith(PostgresTestContainerExtension.class)
class CardInfoRepositoryPostgresIntegrationTest {

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Test
    @Transactional
    void testCreateCardNative() {
        CardInfo created = cardInfoRepository.createCardNative(
                "1234567890123456",
                "Test Holder",
                LocalDate.of(2030, 12, 31),
                4L // David has no cards in seeded data
        );

        assertThat(created.getId()).isNotNull();
        assertThat(created.getHolder()).isEqualTo("Test Holder");

        // Verify it was persisted
        Optional<CardInfo> found = cardInfoRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void testFindByIdNative() {
        Optional<CardInfo> card = cardInfoRepository.findByIdNative(1L); // Alice’s first card
        assertThat(card).isPresent();
        assertThat(card.get().getHolder()).isEqualTo("Alice Johnson");
    }

    @Test
    @Transactional
    void testDeleteByIdNative() {
        int deleted = cardInfoRepository.deleteByIdNative(3L); // Bob’s only card
        assertThat(deleted).isEqualTo(1);

        Optional<CardInfo> card = cardInfoRepository.findByIdNative(3L);
        assertThat(card).isEmpty();
    }

    @Test
    void testGetCardInfosByUserId() {
        List<CardInfo> aliceCards = cardInfoRepository.getCardInfosByUserId(1L);
        assertThat(aliceCards).hasSize(2);

        List<CardInfo> claraCards = cardInfoRepository.getCardInfosByUserId(3L);
        assertThat(claraCards).hasSize(3);

        List<CardInfo> davidCards = cardInfoRepository.getCardInfosByUserId(4L);
        assertThat(davidCards).isEmpty();
    }

    @Test
    void testExistsCardInfoByUserIdAndNumber() {
        boolean exists = cardInfoRepository.existsCardInfoByUserIdAndNumber(
                1L, "4111111111111111"); // Alice’s first card
        boolean notExists = cardInfoRepository.existsCardInfoByUserIdAndNumber(
                2L, "nonexistent");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByIdNamedMethod() {
        Optional<CardInfo> card = cardInfoRepository.findById(2L); // Alice’s second card
        assertThat(card).isPresent();
        assertThat(card.get().getHolder()).isEqualTo("Alice Johnson");
    }

    @Test
    @Transactional
    void testDeleteByIdNamedMethod() {
        cardInfoRepository.deleteById(8L); // Eva’s second card
        Optional<CardInfo> card = cardInfoRepository.findById(8L);
        assertThat(card).isEmpty();
    }

    @Test
    void testFindAllWithPagination() {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        Page<CardInfo> page = cardInfoRepository.findAll(pageable);

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(8); // seeded 8 cards
        assertThat(page.getContent().size()).isLessThanOrEqualTo(3);
    }
}
