package com.intern.userservice.unit.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.exception.UserCardPairAlreadyExistsException;
import com.intern.userservice.mapper.CardInfoMapper;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import com.intern.userservice.service.impl.CardInfoServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CardInfoServiceImplTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    private CardInfo cardInfo;
    private CardInfoResponse cardInfoResponse;

    @BeforeEach
    void setUp() {
        cardInfo = new CardInfo();
        cardInfo.setId(1L);
        cardInfo.setNumber("4111111111111111");
        cardInfo.setHolder("John Doe");
        cardInfo.setExpirationDate(LocalDate.of(2030, 12, 31));

        cardInfoResponse = new CardInfoResponse(1L, "4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L);
    }

    @Test
    void getCardById_whenExists_shouldReturnCard() {
        given(cardInfoRepository.findByIdNative(1L)).willReturn(Optional.of(cardInfo));
        given(cardInfoMapper.toCardInfoResponse(cardInfo)).willReturn(cardInfoResponse);

        Optional<CardInfoResponse> result = cardInfoService.getCardById(1L);

        assertThat(result).isPresent().contains(cardInfoResponse);

        verify(cardInfoRepository).findByIdNative(1L);
        verify(cardInfoMapper).toCardInfoResponse(cardInfo);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void getCardById_whenNotExists_shouldReturnEmpty() {
        given(cardInfoRepository.findByIdNative(1L)).willReturn(Optional.empty());

        Optional<CardInfoResponse> result = cardInfoService.getCardById(1L);

        assertThat(result).isEmpty();

        verify(cardInfoRepository).findByIdNative(1L);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void getCardsByUserId_whenExists_shouldReturnListOfCards() {
        given(cardInfoRepository.getCardInfosByUserId(10L)).willReturn(List.of(cardInfo));
        given(cardInfoMapper.toCardInfoResponse(cardInfo)).willReturn(cardInfoResponse);

        List<CardInfoResponse> result = cardInfoService.getCardsByUserId(10L);

        assertThat(result).hasSize(1).contains(cardInfoResponse);

        verify(cardInfoRepository).getCardInfosByUserId(10L);
        verify(cardInfoMapper).toCardInfoResponse(cardInfo);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void getAllCards_whenPaged_shouldReturnPagedCards() {
        PageRequest pageable = PageRequest.of(0, 10);
        given(cardInfoRepository.findAll(pageable)).willReturn(new PageImpl<>(List.of(cardInfo)));
        given(cardInfoMapper.toCardInfoResponse(cardInfo)).willReturn(cardInfoResponse);

        Page<CardInfoResponse> result = cardInfoService.getAllCards(pageable);

        assertThat(result.getContent()).containsExactly(cardInfoResponse);

        verify(cardInfoRepository).findAll(pageable);
        verify(cardInfoMapper).toCardInfoResponse(cardInfo);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void deleteCardById_whenExists_shouldDelete() {
        given(cardInfoRepository.existsById(1L)).willReturn(true);

        cardInfoService.deleteCardById(1L);

        verify(cardInfoRepository).existsById(1L);
        verify(cardInfoRepository).deleteByIdNative(1L);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void deleteCardById_whenNotExists_shouldThrow() {
        given(cardInfoRepository.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> cardInfoService.deleteCardById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Card not found with id 1");

        verify(cardInfoRepository).existsById(1L);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void createCard_whenValid_shouldCreateCard() {
        CardInfoCreateDto dto = new CardInfoCreateDto("4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L);

        given(userRepository.existsById(10L)).willReturn(true);
        given(cardInfoRepository.existsCardInfoByUserIdAndNumber(10L, "4111111111111111")).willReturn(false);
        given(cardInfoRepository.createCardNative("4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L)).willReturn(cardInfo);
        given(cardInfoMapper.toCardInfoResponse(cardInfo)).willReturn(cardInfoResponse);

        CardInfoResponse result = cardInfoService.createCard(dto);

        assertThat(result).isEqualTo(cardInfoResponse);

        verify(userRepository).existsById(10L);
        verify(cardInfoRepository).existsCardInfoByUserIdAndNumber(10L, "4111111111111111");
        verify(cardInfoRepository).createCardNative("4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L);
        verify(cardInfoMapper).toCardInfoResponse(cardInfo);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void createCard_whenUserNotFound_shouldThrow() {
        CardInfoCreateDto dto = new CardInfoCreateDto("4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L);

        given(userRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> cardInfoService.createCard(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id 10");

        verify(userRepository).existsById(10L);
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }

    @Test
    void createCard_whenCardAlreadyExists_shouldThrow() {
        CardInfoCreateDto dto = new CardInfoCreateDto("4111111111111111", "John Doe",
                LocalDate.of(2030, 12, 31), 10L);

        given(userRepository.existsById(10L)).willReturn(true);
        given(cardInfoRepository.existsCardInfoByUserIdAndNumber(10L, "4111111111111111")).willReturn(true);

        assertThatThrownBy(() -> cardInfoService.createCard(dto))
                .isInstanceOf(UserCardPairAlreadyExistsException.class);

        verify(userRepository).existsById(10L);
        verify(cardInfoRepository).existsCardInfoByUserIdAndNumber(10L, "4111111111111111");
        verifyNoMoreInteractions(cardInfoRepository, cardInfoMapper, userRepository);
    }
}
