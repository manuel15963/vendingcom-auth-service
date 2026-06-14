package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthPasswordRecoveryCodeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReactiveAuthPasswordRecoveryCodeRepository
        extends ReactiveCrudRepository<AuthPasswordRecoveryCodeEntity, Long> {

    Flux<AuthPasswordRecoveryCodeEntity> findByEmail(String email);

    @Query("""
            SELECT *
            FROM auth_password_recovery_codes
            WHERE email = :email
              AND used = FALSE
              AND expires_at > CURRENT_TIMESTAMP
              AND attempts < max_attempts
            ORDER BY recovery_code_id DESC
            LIMIT 1
            """)
    Mono<AuthPasswordRecoveryCodeEntity> findLastActiveCodeByEmail(String email);

    @Query("""
            UPDATE auth_password_recovery_codes
            SET used = TRUE,
                used_at = CURRENT_TIMESTAMP
            WHERE email = :email
              AND used = FALSE
            """)
    Mono<Void> markActiveCodesAsUsedByEmail(String email);

    @Query("""
            UPDATE auth_password_recovery_codes
            SET used = TRUE,
                used_at = CURRENT_TIMESTAMP
            WHERE recovery_code_id = :recoveryCodeId
            """)
    Mono<Void> markAsUsedById(Long recoveryCodeId);

    @Query("""
            UPDATE auth_password_recovery_codes
            SET attempts = attempts + 1
            WHERE recovery_code_id = :recoveryCodeId
            """)
    Mono<Void> incrementAttempts(Long recoveryCodeId);

    @Query("""
            DELETE FROM auth_password_recovery_codes
            WHERE expires_at < :now
               OR used = TRUE
            """)
    Mono<Void> deleteExpiredOrUsed(LocalDateTime now);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM auth_password_recovery_codes
                WHERE email = :email
                  AND created_at >= :since
            )
            """)
    Mono<Boolean> existsRecentRequestByEmail(String email, LocalDateTime since);
}