package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthUserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReactiveAuthUserRepository extends ReactiveCrudRepository<AuthUserEntity, Integer> {

    Mono<AuthUserEntity> findByUsername(String username);

    Mono<AuthUserEntity> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Flux<AuthUserEntity> findByUserStatus(Integer userStatus);

    @Query("""
            UPDATE auth_users
            SET failed_login_attempts = :attempts,
                locked_until = :lockedUntil
            WHERE user_id = :userId
            """)
    Mono<Void> updateFailedLoginState(Integer userId, Integer attempts, LocalDateTime lockedUntil);
}