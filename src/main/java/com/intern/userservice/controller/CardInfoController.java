package com.intern.userservice.controller;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @Autowired
    public CardInfoController(CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @PostMapping
    public ResponseEntity<CardInfoResponse> createCard(@Validated @RequestBody CardInfoCreateDto dto) {
        CardInfoResponse created = cardInfoService.createCard(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponse> getCardById(@PathVariable Long id) {
        Optional<CardInfoResponse> card = cardInfoService.getCardById(id);
        return card.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CardInfoResponse>> getCardsByUserId(@RequestParam Long userId) {
        List<CardInfoResponse> userCards = cardInfoService.getCardsByUserId(userId);
        return ResponseEntity.ok(userCards);
    }

    @GetMapping
    public ResponseEntity<Page<CardInfoResponse>> getAllCards(Pageable pageable) {
        Page<CardInfoResponse> cards = cardInfoService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardById(@PathVariable Long id) {
        cardInfoService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}
