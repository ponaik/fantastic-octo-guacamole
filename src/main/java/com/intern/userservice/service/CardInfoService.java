package com.intern.userservice.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.mapper.CardInfoMapper;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.model.User;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;


    public Optional<CardInfoResponse> getCardById(Long id) {
        return cardInfoRepository.findCardByIdJPQL(id)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    public Page<CardInfoResponse> getAllCards(Pageable pageable) {
        return cardInfoRepository.findAll(pageable)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    @Transactional
    public void deleteCardById(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new IllegalArgumentException("Card with id " + id + " not found");
        }
        cardInfoRepository.deleteById(id);
    }

    public CardInfoResponse createCard(CardInfoCreateDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CardInfo card = cardInfoMapper.fromCardInfoCreateDto(dto);
        card.setUser(user);

        CardInfo saved = cardInfoRepository.save(card);
        return cardInfoMapper.toCardInfoResponse(saved);
    }
}

