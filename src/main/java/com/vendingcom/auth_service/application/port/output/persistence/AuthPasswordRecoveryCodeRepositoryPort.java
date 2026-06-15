package com.vendingcom.auth_service.application.port.output.persistence;

import com.vendingcom.auth_service.domain.model.AuthPasswordRecoveryCode;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AuthPasswordRecoveryCodeRepositoryPort {

    Mono<AuthPasswordRecoveryCode> save(AuthPasswordRecoveryCode recoveryCode);

    Mono<AuthPasswordRecoveryCode> findLastActiveCodeByEmail(String email);

    Mono<Void> markActiveCodesAsUsedByEmail(String email);

    Mono<Void> markAsUsedById(Long recoveryCodeId);

    Mono<Void> incrementAttempts(Long recoveryCodeId);

    Mono<Void> deleteExpiredOrUsed(LocalDateTime now);

    Mono<Boolean> existsRecentRequestByEmail(String email, LocalDateTime since);
}