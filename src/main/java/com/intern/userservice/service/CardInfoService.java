package com.intern.userservice.service;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface CardInfoService {
    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByUserId(authentication, #dto.userId())")
    CardInfoResponse createCard(CardInfoCreateDto dto);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByCardId(authentication, #id)")
    Optional<CardInfoResponse> getCardById(Long id);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByUserId(authentication, #userId)")
    List<CardInfoResponse> getCardsByUserId(Long userId);

    @PreAuthorize("hasRole('admin')")
    Page<CardInfoResponse> getAllCards(Pageable pageable);

    @PreAuthorize("hasRole('admin') or @authorizationService.isOwnerByCardId(authentication, #id)")
    void deleteCardById(Long id);
}
