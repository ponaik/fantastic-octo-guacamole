package com.intern.userservice.dto;

import java.time.LocalDate;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        List<CardInfoResponse> cards
) {
}
