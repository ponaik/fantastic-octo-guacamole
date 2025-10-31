package com.intern.userservice.integration.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.integration.extension.PostgresTestContainerExtension;
import com.intern.userservice.integration.extension.RedisTestContainerExtension;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.UserRepository;
import com.intern.userservice.service.CardInfoService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@ExtendWith({RedisTestContainerExtension.class, PostgresTestContainerExtension.class})
class CardInfoServiceRedisPostgresIntegrationTest {

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private Cache cardCache;
    private Cache userCardsCache;
    private Cache cardsCache;

    @BeforeEach
    void setup() {
        cardCache = cacheManager.getCache("card");
        userCardsCache = cacheManager.getCache("userCards");
        cardsCache = cacheManager.getCache("cards");

        cardCache.clear();
        userCardsCache.clear();
        cardsCache.clear();
    }

    @Test
    @Transactional
    void createCard_shouldPersistAndCache() {
        // given
        User user = userRepository.createUserNative(UUID.fromString("00000000-0000-0000-0000-000000000000"),"John", "Doe", LocalDate.of(1990,1,1), "john@example.com");
        CardInfoCreateDto dto = new CardInfoCreateDto("1234-5678", "John Doe", LocalDate.of(2030,1,1), user.getId());

        // when
        CardInfoResponse response = cardInfoService.createCard(dto);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(cardCache.get(response.id(), CardInfoResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void getCardById_shouldReturnAndCache() {
        User user = userRepository.createUserNative(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Jane", "Smith", LocalDate.of(1995,5,5), "jane@example.com");
        CardInfoResponse created = cardInfoService.createCard(
                new CardInfoCreateDto("1111-2222", "Jane Smith", LocalDate.of(2031,1,1), user.getId()));

        Optional<CardInfoResponse> response = cardInfoService.getCardById(created.id());

        assertThat(response).isPresent();
        assertThat(cardCache.get(created.id(), CardInfoResponse.class)).isNotNull();
    }

    @Test
    @Transactional
    void getCardsByUserId_shouldReturnAndCache() {
        User user = userRepository.createUserNative(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Tom", "Jerry", LocalDate.of(1988,8,8), "tom@example.com");
        cardInfoService.createCard(new CardInfoCreateDto("2222-3333", "Tom Jerry", LocalDate.of(2032,1,1), user.getId()));

        List<CardInfoResponse> cards = cardInfoService.getCardsByUserId(user.getId());

        assertThat(cards).isNotEmpty();
        assertThat(userCardsCache.get(user.getId(), List.class)).isNotNull();
    }

    @Test
    @Transactional
    void getAllCards_shouldReturnAndCache() {
        User user = userRepository.createUserNative(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Alice", "Wonder", LocalDate.of(1992,2,2), "alice@example.com");
        cardInfoService.createCard(new CardInfoCreateDto("3333-4444", "Alice Wonder", LocalDate.of(2033,1,1), user.getId()));

        Page<CardInfoResponse> page = cardInfoService.getAllCards(PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(cardsCache.get("0-10")).isNotNull();
    }

    @Test
    @Transactional
    void deleteCardById_shouldRemoveAndEvictCache() {
        User user = userRepository.createUserNative(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Del", "User", LocalDate.of(1970,1,1), "del@example.com");
        CardInfoResponse created = cardInfoService.createCard(
                new CardInfoCreateDto("4444-5555", "Del User", LocalDate.of(2034,1,1), user.getId()));

        cardInfoService.getCardById(created.id());
        cardInfoService.deleteCardById(created.id());

        assertThat(cardCache.get(created.id())).isNull();
        assertThat(cardsCache.get("0-10")).isNull();


        Optional<CardInfoResponse> card = cardInfoService.getCardById(created.id());
        assertThat(card).isEmpty();
    }
}