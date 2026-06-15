package com.vendingcom.auth_service.application.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {

    public static MessageResponse of(String code, String message) {
        return new MessageResponse(
                code,
                message,
                LocalDateTime.now()
        );
    }
}