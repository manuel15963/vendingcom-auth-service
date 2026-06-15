package com.vendingcom.auth_service.application.dto.response;

import com.vendingcom.auth_service.domain.model.AuthRole;

public record AuthRoleResponse(
        Integer roleId,
        String roleCode,
        String roleName,
        String roleDescription,
        Integer roleStatus
) {

    public static AuthRoleResponse fromDomain(AuthRole authRole) {
        return new AuthRoleResponse(
                authRole.roleId(),
                authRole.roleCode(),
                authRole.roleName(),
                authRole.roleDescription(),
                authRole.roleStatus()
        );
    }
}