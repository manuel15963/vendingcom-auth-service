package com.vendingcom.auth_service.domain.model;

import java.time.LocalDateTime;

public record AuthUser(
        Integer userId,
        String username,
        String email,
        String passwordHash,
        String fullName,
        String phoneNumber,
        String documentType,
        String documentNumber,
        Integer userStatus,
        LocalDateTime lastLoginAt,
        Integer createdByUserId,
        Integer updatedByUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public boolean isActive() {
        return Integer.valueOf(1).equals(userStatus);
    }

    public boolean isInactive() {
        return Integer.valueOf(0).equals(userStatus);
    }

    public boolean isLocked() {
        return Integer.valueOf(2).equals(userStatus);
    }
}