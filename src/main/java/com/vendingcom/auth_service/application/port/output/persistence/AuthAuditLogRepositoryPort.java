package com.vendingcom.auth_service.application.port.output.persistence;

import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthAuditLogRepositoryPort {

    Mono<AuthAuditLog> save(AuthAuditLog authAuditLog);

    Flux<AuthAuditLog> findAll();

    Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId);

    Flux<AuthAuditLog> findByActionType(String actionType);
}