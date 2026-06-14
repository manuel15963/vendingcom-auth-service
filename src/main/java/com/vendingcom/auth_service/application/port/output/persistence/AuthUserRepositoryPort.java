package com.vendingcom.auth_service.application.port.output.persistence;

import com.vendingcom.auth_service.domain.model.AuthUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthUserRepositoryPort {

    Mono<AuthUser> save(AuthUser authUser);

    Mono<AuthUser> findById(Integer userId);

    Mono<AuthUser> findByUsername(String username);

    Mono<AuthUser> findByEmail(String email);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Flux<AuthUser> findAll();

    Flux<AuthUser> findByUserStatus(Integer userStatus);
}