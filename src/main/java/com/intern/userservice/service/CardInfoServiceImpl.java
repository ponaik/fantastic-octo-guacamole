package com.intern.userservice.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.mapper.CardInfoMapper;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;


    @Override
    public Optional<CardInfoResponse> getCardById(Long id) {
        return cardInfoRepository.findByIdNative(id)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    @Override
    public Page<CardInfoResponse> getAllCards(Pageable pageable) {
        return cardInfoRepository.findAll(pageable)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    @Transactional
    @Override
    public void deleteCardById(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("Card not found with id " + id);
        }
        cardInfoRepository.deleteByIdNative(id);
    }

    @Transactional
    @Override
    public CardInfoResponse createCard(CardInfoCreateDto dto) {
        User user = userRepository.findByIdJPQL(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.userId()));

        CardInfo saved = cardInfoRepository.createCardNative(
                dto.number(),
                dto.holder(),
                dto.expirationDate(),
                dto.userId()
        );
        return cardInfoMapper.toCardInfoResponse(saved);
    }
}

