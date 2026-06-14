package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.port.input.AuditLogUseCase;
import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AuditLogService implements AuditLogUseCase {

    private final AuthAuditLogRepositoryPort authAuditLogRepositoryPort;

    public AuditLogService(AuthAuditLogRepositoryPort authAuditLogRepositoryPort) {
        this.authAuditLogRepositoryPort = authAuditLogRepositoryPort;
    }

    @Override
    public Flux<AuthAuditLog> findAll() {
        return authAuditLogRepositoryPort.findAll();
    }

    @Override
    public Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId) {
        return authAuditLogRepositoryPort.findByAffectedUserId(affectedUserId);
    }

    @Override
    public Flux<AuthAuditLog> findByActionType(String actionType) {
        return authAuditLogRepositoryPort.findByActionType(normalizeActionType(actionType));
    }

    private String normalizeActionType(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}