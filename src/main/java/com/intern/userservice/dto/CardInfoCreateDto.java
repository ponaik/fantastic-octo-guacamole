package com.intern.userservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CardInfoCreateDto(
        @NotBlank(message = "Card number is required")
        @Size(min = 20, max = 20, message = "Invalid card number size")
        String number,

        @NotBlank(message = "Card holder is required")
        @Size(min = 5, max = 150, message = "Card holder name size exceeded")
        String holder,

        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate,

        @NotNull(message = "User ID is required")
        Long userId
) {}