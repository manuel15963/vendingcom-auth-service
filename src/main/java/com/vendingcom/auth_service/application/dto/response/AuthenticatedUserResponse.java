package com.vendingcom.auth_service.application.dto.response;

import java.util.List;

public record AuthenticatedUserResponse(
        Integer userId,
        String username,
        String email,
        String fullName,
        List<String> roles
) {
}