package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper;

import com.vendingcom.auth_service.domain.model.AuthRole;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthRoleEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthRolePersistenceMapper {

    public AuthRole toDomain(AuthRoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuthRole(
                entity.getRoleId(),
                entity.getRoleCode(),
                entity.getRoleName(),
                entity.getRoleDescription(),
                entity.getRoleStatus(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AuthRoleEntity toEntity(AuthRole domain) {
        if (domain == null) {
            return null;
        }

        return AuthRoleEntity.builder()
                .roleId(domain.roleId())
                .roleCode(domain.roleCode())
                .roleName(domain.roleName())
                .roleDescription(domain.roleDescription())
                .roleStatus(domain.roleStatus())
                .createdByUserId(domain.createdByUserId())
                .updatedByUserId(domain.updatedByUserId())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}