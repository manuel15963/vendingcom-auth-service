package com.vendingcom.auth_service.application.dto.response;

import com.vendingcom.auth_service.domain.model.AuthAuditLog;

import java.time.LocalDateTime;

public record AuthAuditLogResponse(
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

    public static AuthAuditLogResponse fromDomain(AuthAuditLog authAuditLog) {
        return new AuthAuditLogResponse(
                authAuditLog.auditLogId(),
                authAuditLog.affectedUserId(),
                authAuditLog.actionType(),
                authAuditLog.affectedTableName(),
                authAuditLog.affectedRecordId(),
                authAuditLog.actionDescription(),
                authAuditLog.oldData(),
                authAuditLog.newData(),
                authAuditLog.ipAddress(),
                authAuditLog.userAgent(),
                authAuditLog.executedByUserId(),
                authAuditLog.executedAt()
        );
    }
}