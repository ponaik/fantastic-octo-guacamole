package com.intern.userservice.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CardInfoService {
    Optional<CardInfoResponse> getCardById(Long id);

    Page<CardInfoResponse> getAllCards(Pageable pageable);

    void deleteCardById(Long id);

    CardInfoResponse createCard(CardInfoCreateDto dto);
}
