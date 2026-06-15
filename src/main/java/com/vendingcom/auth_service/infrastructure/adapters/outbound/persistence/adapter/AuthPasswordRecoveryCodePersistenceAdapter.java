package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthPasswordRecoveryCodeRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthPasswordRecoveryCode;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthPasswordRecoveryCodePersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthPasswordRecoveryCodeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class AuthPasswordRecoveryCodePersistenceAdapter implements AuthPasswordRecoveryCodeRepositoryPort {

    private final ReactiveAuthPasswordRecoveryCodeRepository repository;
    private final AuthPasswordRecoveryCodePersistenceMapper mapper;

    public AuthPasswordRecoveryCodePersistenceAdapter(
            ReactiveAuthPasswordRecoveryCodeRepository repository,
            AuthPasswordRecoveryCodePersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<AuthPasswordRecoveryCode> save(AuthPasswordRecoveryCode recoveryCode) {
        return repository.save(mapper.toEntity(recoveryCode))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<AuthPasswordRecoveryCode> findLastActiveCodeByEmail(String email) {
        return repository.findLastActiveCodeByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> markActiveCodesAsUsedByEmail(String email) {
        return repository.markActiveCodesAsUsedByEmail(email);
    }

    @Override
    public Mono<Void> markAsUsedById(Long recoveryCodeId) {
        return repository.markAsUsedById(recoveryCodeId);
    }

    @Override
    public Mono<Void> incrementAttempts(Long recoveryCodeId) {
        return repository.incrementAttempts(recoveryCodeId);
    }

    @Override
    public Mono<Void> deleteExpiredOrUsed(LocalDateTime now) {
        return repository.deleteExpiredOrUsed(now);
    }

    @Override
    public Mono<Boolean> existsRecentRequestByEmail(String email, LocalDateTime since) {
        return repository.existsRecentRequestByEmail(email, since);
    }
}