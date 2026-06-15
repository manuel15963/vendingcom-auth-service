package com.vendingcom.auth_service.application.port.input;

import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import reactor.core.publisher.Flux;

public interface AuditLogUseCase {

    Flux<AuthAuditLog> findAll();

    Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId);

    Flux<AuthAuditLog> findByActionType(String actionType);
}