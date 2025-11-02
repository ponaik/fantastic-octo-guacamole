package com.intern.userservice.service.impl;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.exception.UserCardPairAlreadyExistsException;
import com.intern.userservice.mapper.CardInfoMapper;
import com.intern.userservice.model.CardInfo;
import com.intern.userservice.repository.CardInfoRepository;
import com.intern.userservice.repository.UserRepository;
import com.intern.userservice.service.CardInfoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Override
    @Cacheable(value = "card", key = "#id")
    public Optional<CardInfoResponse> getCardById(Long id) {
        return cardInfoRepository.findByIdNative(id)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    @Override
    @Cacheable(value = "userCards", key = "#userId")
    public List<CardInfoResponse> getCardsByUserId(Long userId) {
        return cardInfoRepository.getCardInfosByUserId(userId).stream()
                .map(cardInfoMapper::toCardInfoResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "cards", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CardInfoResponse> getAllCards(Pageable pageable) {
        return cardInfoRepository.findAll(pageable)
                .map(cardInfoMapper::toCardInfoResponse);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "card", key = "#id"),
            @CacheEvict(value = "cards", allEntries = true),
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public void deleteCardById(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("Card not found with id " + id);
        }
        cardInfoRepository.deleteByIdNative(id);
    }

    @Transactional
    @Override
    @Caching(
            put = {
                    @CachePut(value = "card", key = "#result.id")
            },
            evict = {
                    @CacheEvict(value = "cards", allEntries = true),
                    @CacheEvict(value = "userCards", key = "#dto.userId()")
            }
    )
    public CardInfoResponse createCard(CardInfoCreateDto dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new EntityNotFoundException("User not found with id " + dto.userId());
        }

        if (cardInfoRepository.existsCardInfoByUserIdAndNumber(dto.userId(), dto.number())) {
            throw new UserCardPairAlreadyExistsException(dto.userId(), dto.number());
        }

        CardInfo saved = cardInfoRepository.createCardNative(
                dto.number(),
                dto.holder(),
                dto.expirationDate(),
                dto.userId()
        );
        return cardInfoMapper.toCardInfoResponse(saved);
    }
}
