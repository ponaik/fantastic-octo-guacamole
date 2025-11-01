package com.intern.userservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record UserCreateDto(
        @NotNull(message = "sub is required")
        UUID sub,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Invalid name length")
        String name,

        @NotBlank(message = "Surname is required")
        @Size(min = 2, max = 100, message = "Invalid surname length")
        String surname,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email length exceeded")
        @NotBlank(message = "Email is required")
        String email
) {}


