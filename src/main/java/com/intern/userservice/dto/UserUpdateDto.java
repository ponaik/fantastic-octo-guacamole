package com.intern.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDto(
        @Size(min = 2, max = 100, message = "Inlavid name length")
        String name,

        @Size(min = 2, max = 100, message = "Invalid surname length")
        String surname,

        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email length exceeded")
        String email
) {}
