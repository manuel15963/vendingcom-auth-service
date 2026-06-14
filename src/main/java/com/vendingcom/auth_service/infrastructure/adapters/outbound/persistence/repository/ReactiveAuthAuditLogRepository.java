package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthAuditLogEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReactiveAuthAuditLogRepository extends ReactiveCrudRepository<AuthAuditLogEntity, Long> {

    Flux<AuthAuditLogEntity> findByAffectedUserId(Integer affectedUserId);

    Flux<AuthAuditLogEntity> findByActionType(String actionType);
}