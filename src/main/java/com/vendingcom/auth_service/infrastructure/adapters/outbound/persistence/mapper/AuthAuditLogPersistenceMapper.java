package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper;

import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthAuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthAuditLogPersistenceMapper {

    public AuthAuditLog toDomain(AuthAuditLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuthAuditLog(
                entity.getAuditLogId(),
                entity.getAffectedUserId(),
                entity.getActionType(),
                entity.getAffectedTableName(),
                entity.getAffectedRecordId(),
                entity.getActionDescription(),
                entity.getOldData(),
                entity.getNewData(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getExecutedByUserId(),
                entity.getExecutedAt()
        );
    }

    public AuthAuditLogEntity toEntity(AuthAuditLog domain) {
        if (domain == null) {
            return null;
        }

        return AuthAuditLogEntity.builder()
                .auditLogId(domain.auditLogId())
                .affectedUserId(domain.affectedUserId())
                .actionType(domain.actionType())
                .affectedTableName(domain.affectedTableName())
                .affectedRecordId(domain.affectedRecordId())
                .actionDescription(domain.actionDescription())
                .oldData(domain.oldData())
                .newData(domain.newData())
                .ipAddress(domain.ipAddress())
                .userAgent(domain.userAgent())
                .executedByUserId(domain.executedByUserId())
                .executedAt(domain.executedAt())
                .build();
    }
}