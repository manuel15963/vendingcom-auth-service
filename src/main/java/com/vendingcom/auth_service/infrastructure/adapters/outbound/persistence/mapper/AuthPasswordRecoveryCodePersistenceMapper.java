package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper;

import com.vendingcom.auth_service.domain.model.AuthPasswordRecoveryCode;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthPasswordRecoveryCodeEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthPasswordRecoveryCodePersistenceMapper {

    public AuthPasswordRecoveryCode toDomain(AuthPasswordRecoveryCodeEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuthPasswordRecoveryCode(
                entity.getRecoveryCodeId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getCodeHash(),
                entity.getUsed(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUsedAt(),
                entity.getAttempts(),
                entity.getMaxAttempts()
        );
    }

    public AuthPasswordRecoveryCodeEntity toEntity(AuthPasswordRecoveryCode domain) {
        if (domain == null) {
            return null;
        }

        return AuthPasswordRecoveryCodeEntity.builder()
                .recoveryCodeId(domain.recoveryCodeId())
                .userId(domain.userId())
                .email(domain.email())
                .codeHash(domain.codeHash())
                .used(domain.used())
                .expiresAt(domain.expiresAt())
                .createdAt(domain.createdAt())
                .usedAt(domain.usedAt())
                .attempts(domain.attempts())
                .maxAttempts(domain.maxAttempts())
                .build();
    }
}