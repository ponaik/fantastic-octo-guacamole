package com.intern.userservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UserCreateDto(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name size exceeded")
        String name,

        @NotBlank(message = "Surname is required")
        @Size(min = 2, max = 100, message = "Surname size exceeded")
        String surname,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email size exceeded")
        @NotBlank(message = "Email is required")
        String email
) {}


