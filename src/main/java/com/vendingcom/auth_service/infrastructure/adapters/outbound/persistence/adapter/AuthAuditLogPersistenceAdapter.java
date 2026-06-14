package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthAuditLogPersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthAuditLogRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthAuditLogPersistenceAdapter implements AuthAuditLogRepositoryPort {

    private final ReactiveAuthAuditLogRepository reactiveAuthAuditLogRepository;
    private final AuthAuditLogPersistenceMapper authAuditLogPersistenceMapper;

    public AuthAuditLogPersistenceAdapter(
            ReactiveAuthAuditLogRepository reactiveAuthAuditLogRepository,
            AuthAuditLogPersistenceMapper authAuditLogPersistenceMapper
    ) {
        this.reactiveAuthAuditLogRepository = reactiveAuthAuditLogRepository;
        this.authAuditLogPersistenceMapper = authAuditLogPersistenceMapper;
    }

    @Override
    public Mono<AuthAuditLog> save(AuthAuditLog authAuditLog) {
        return reactiveAuthAuditLogRepository.save(authAuditLogPersistenceMapper.toEntity(authAuditLog))
                .map(authAuditLogPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId) {
        return reactiveAuthAuditLogRepository.findByAffectedUserId(affectedUserId)
                .map(authAuditLogPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthAuditLog> findByActionType(String actionType) {
        return reactiveAuthAuditLogRepository.findByActionType(actionType)
                .map(authAuditLogPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthAuditLog> findAll() {
        return reactiveAuthAuditLogRepository.findAll()
                .map(authAuditLogPersistenceMapper::toDomain);
    }
}