package com.vendingcom.auth_service.domain.model;

import java.time.LocalDateTime;

public record AuthRole(
        Integer roleId,
        String roleCode,
        String roleName,
        String roleDescription,
        Integer roleStatus,
        Integer createdByUserId,
        Integer updatedByUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public boolean isActive() {
        return Integer.valueOf(1).equals(roleStatus);
    }
}