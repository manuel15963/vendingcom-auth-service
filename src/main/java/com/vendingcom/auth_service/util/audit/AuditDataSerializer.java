package com.vendingcom.auth_service.util.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendingcom.auth_service.domain.model.AuthUser;

/**
 * Utility para serializar datos de auditoría en formato JSON.
 *
 * IMPORTANTE:
 * - Retorna String con JSON válido.
 * - En BD se guarda como jsonb usando CAST(:oldData AS jsonb).
 *
 * NUNCA incluir:
 * - passwordHash / password
 * - recovery_code_hash
 * - jwt / token
 * - api_keys
 * - secrets
 */
public class AuditDataSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeUser(AuthUser user) {
        if (user == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(
                    UserAuditData.fromDomain(user)
            );
        } catch (Exception e) {
            return null;
        }
    }

    public record UserAuditData(
            Integer userId,
            String username,
            String email,
            String fullName,
            String phoneNumber,
            String documentType,
            String documentNumber,
            Integer userStatus,
            String lastLoginAt,
            String createdAt,
            String updatedAt
    ) {
        public static UserAuditData fromDomain(AuthUser user) {
            return new UserAuditData(
                    user.userId(),
                    user.username(),
                    user.email(),
                    user.fullName(),
                    user.phoneNumber(),
                    user.documentType(),
                    user.documentNumber(),
                    user.userStatus(),
                    user.lastLoginAt() != null ? user.lastLoginAt().toString() : null,
                    user.createdAt() != null ? user.createdAt().toString() : null,
                    user.updatedAt() != null ? user.updatedAt().toString() : null
            );
        }
    }
}