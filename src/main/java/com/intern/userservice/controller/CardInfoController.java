package com.intern.userservice.controller;

import com.intern.userservice.dto.CardInfoCreateDto;
import com.intern.userservice.dto.CardInfoResponse;
import com.intern.userservice.service.CardInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cards", description = "CardInfo Management API")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @Autowired
    public CardInfoController(CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @PostMapping
    @Operation(summary = "Create card", description = "Links a new payment card to a user profile. Accessible by admins or the account owner.")
    public ResponseEntity<CardInfoResponse> createCard(@Validated @RequestBody CardInfoCreateDto dto) {
        CardInfoResponse created = cardInfoService.createCard(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID", description = "Retrieves card details for a specific payment card by its ID. Accessible by admins or the account owner.")
    public ResponseEntity<CardInfoResponse> getCardById(@PathVariable Long id) {
        Optional<CardInfoResponse> card = cardInfoService.getCardById(id);
        return card.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Get cards by User ID", description = "Lists all payment cards associated with a specific user. Accessible by admins or the account owner.")
    public ResponseEntity<List<CardInfoResponse>> getCardsByUserId(@RequestParam Long userId) {
        List<CardInfoResponse> userCards = cardInfoService.getCardsByUserId(userId);
        return ResponseEntity.ok(userCards);
    }

    @GetMapping
    @Operation(summary = "Get all cards", description = "Returns a paginated list of all payment cards in the system. Restricted to admins")
    public ResponseEntity<Page<CardInfoResponse>> getAllCards(Pageable pageable) {
        Page<CardInfoResponse> cards = cardInfoService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card", description = "Removes a payment card from a user's profile. Accessible by admins or the account owner.")
    public ResponseEntity<Void> deleteCardById(@PathVariable Long id) {
        cardInfoService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}