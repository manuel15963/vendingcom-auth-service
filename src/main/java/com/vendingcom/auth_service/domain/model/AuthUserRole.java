package com.vendingcom.auth_service.domain.model;

import java.time.LocalDateTime;

public record AuthUserRole(
        Integer userRoleId,
        Integer userId,
        Integer roleId,
        Integer assignedByUserId,
        Integer assignmentStatus,
        LocalDateTime assignedAt
) {

    public boolean isActive() {
        return Integer.valueOf(1).equals(assignmentStatus);
    }
}