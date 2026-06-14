package com.vendingcom.auth_service.application.dto.response;

import com.vendingcom.auth_service.domain.model.AuthUser;

import java.time.LocalDateTime;

public record AuthUserResponse(
        Integer userId,
        String username,
        String email,
        String fullName,
        String phoneNumber,
        String documentType,
        String documentNumber,
        Integer userStatus,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AuthUserResponse fromDomain(AuthUser authUser) {
        return new AuthUserResponse(
                authUser.userId(),
                authUser.username(),
                authUser.email(),
                authUser.fullName(),
                authUser.phoneNumber(),
                authUser.documentType(),
                authUser.documentNumber(),
                authUser.userStatus(),
                authUser.lastLoginAt(),
                authUser.createdAt(),
                authUser.updatedAt()
        );
    }
}