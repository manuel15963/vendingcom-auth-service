package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthAuditLogEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReactiveAuthAuditLogRepository extends ReactiveCrudRepository<AuthAuditLogEntity, Long> {

    Flux<AuthAuditLogEntity> findByAffectedUserId(Integer affectedUserId);

    Flux<AuthAuditLogEntity> findByActionType(String actionType);

    @Query("DELETE FROM auth_audit_logs WHERE executed_at < :threshold")
    Mono<Void> deleteOlderThan(LocalDateTime threshold);
}