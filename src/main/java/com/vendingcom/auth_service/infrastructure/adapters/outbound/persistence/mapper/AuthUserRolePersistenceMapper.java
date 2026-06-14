package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper;

import com.vendingcom.auth_service.domain.model.AuthUserRole;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthUserRoleEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthUserRolePersistenceMapper {

    public AuthUserRole toDomain(AuthUserRoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AuthUserRole(
                entity.getUserRoleId(),
                entity.getUserId(),
                entity.getRoleId(),
                entity.getAssignedByUserId(),
                entity.getAssignmentStatus(),
                entity.getAssignedAt()
        );
    }

    public AuthUserRoleEntity toEntity(AuthUserRole domain) {
        if (domain == null) {
            return null;
        }

        return AuthUserRoleEntity.builder()
                .userRoleId(domain.userRoleId())
                .userId(domain.userId())
                .roleId(domain.roleId())
                .assignedByUserId(domain.assignedByUserId())
                .assignmentStatus(domain.assignmentStatus())
                .assignedAt(domain.assignedAt())
                .build();
    }
}