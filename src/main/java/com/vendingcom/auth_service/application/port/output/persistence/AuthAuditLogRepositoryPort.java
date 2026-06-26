package com.vendingcom.auth_service.application.port.output.persistence;

import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AuthAuditLogRepositoryPort {

    Mono<AuthAuditLog> save(AuthAuditLog authAuditLog);

    Flux<AuthAuditLog> findAll();

    Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId);

    Flux<AuthAuditLog> findByActionType(String actionType);

    /** Elimina los registros de auditoría anteriores a la fecha indicada (retención). */
    Mono<Void> deleteOlderThan(LocalDateTime threshold);
}