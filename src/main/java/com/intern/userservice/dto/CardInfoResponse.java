package com.intern.userservice.dto;

import java.time.LocalDate;

public record CardInfoResponse(
        Long id,
        String cardNumber,
        String cardType,
        LocalDate expirationDate
) {
}