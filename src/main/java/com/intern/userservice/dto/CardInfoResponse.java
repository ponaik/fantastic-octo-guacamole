package com.intern.userservice.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record CardInfoResponse(
        Long id,
        String number,
        String holder,
        LocalDate expirationDate,
        Long userId
) implements Serializable {
}