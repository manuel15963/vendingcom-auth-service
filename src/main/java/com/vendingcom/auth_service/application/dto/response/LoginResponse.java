package com.vendingcom.auth_service.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn,
        Integer userId,
        String username,
        String email,
        String fullName,
        Integer userStatus,
        LocalDateTime lastLoginAt,
        List<AuthRoleResponse> roles,
        String message
) {
}