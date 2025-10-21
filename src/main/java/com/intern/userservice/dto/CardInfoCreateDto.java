package com.intern.userservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CardInfoCreateDto(
        @NotBlank(message = "Card number is required")
        @Size(min = 8, max = 19, message = "Card number length must be in [8, 19]")
        String number,

        @NotBlank(message = "Card holder is required")
        @Size(min = 5, max = 150, message = "Card holder name length must be in [5, 150]")
        String holder,

        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate,

        @NotNull(message = "User ID is required")
        Long userId
) {}