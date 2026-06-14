package com.vendingcom.auth_service.domain.model;

import java.time.LocalDateTime;

public record AuthAuditLog(
        Long auditLogId,
        Integer affectedUserId,
        String actionType,
        String affectedTableName,
        Integer affectedRecordId,
        String actionDescription,
        String oldData,
        String newData,
        String ipAddress,
        String userAgent,
        Integer executedByUserId,
        LocalDateTime executedAt
) {
}