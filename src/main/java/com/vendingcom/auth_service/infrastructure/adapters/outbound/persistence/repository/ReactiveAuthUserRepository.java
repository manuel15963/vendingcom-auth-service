package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthUserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveAuthUserRepository extends ReactiveCrudRepository<AuthUserEntity, Integer> {

    Mono<AuthUserEntity> findByUsername(String username);

    Mono<AuthUserEntity> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Flux<AuthUserEntity> findByUserStatus(Integer userStatus);
}