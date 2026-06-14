package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper;

import com.vendingcom.auth_service.domain.model.AuthUser;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthUserPersistenceMapper {

    public AuthUser toDomain(AuthUserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuthUser(
                entity.getUserId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getUserStatus(),
                entity.getLastLoginAt(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AuthUserEntity toEntity(AuthUser domain) {
        if (domain == null) {
            return null;
        }

        return AuthUserEntity.builder()
                .userId(domain.userId())
                .username(domain.username())
                .email(domain.email())
                .passwordHash(domain.passwordHash())
                .fullName(domain.fullName())
                .phoneNumber(domain.phoneNumber())
                .documentType(domain.documentType())
                .documentNumber(domain.documentNumber())
                .userStatus(domain.userStatus())
                .lastLoginAt(domain.lastLoginAt())
                .createdByUserId(domain.createdByUserId())
                .updatedByUserId(domain.updatedByUserId())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}