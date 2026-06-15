package com.vendingcom.auth_service.domain.model;

import java.time.LocalDateTime;

public record AuthPasswordRecoveryCode(
        Long recoveryCodeId,
        Integer userId,
        String email,
        String codeHash,
        Boolean used,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime usedAt,
        Integer attempts,
        Integer maxAttempts
) {

    public boolean isUsed() {
        return Boolean.TRUE.equals(used);
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean hasExceededAttempts() {
        return attempts != null
                && maxAttempts != null
                && attempts >= maxAttempts;
    }

    public boolean isActive() {
        return !isUsed() && !isExpired() && !hasExceededAttempts();
    }
}