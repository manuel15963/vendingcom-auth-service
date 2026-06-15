package com.vendingcom.auth_service.application.port.output.persistence;

import com.vendingcom.auth_service.domain.model.AuthUserRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthUserRoleRepositoryPort {

    Mono<AuthUserRole> save(AuthUserRole authUserRole);

    Flux<AuthUserRole> findByUserId(Integer userId);

    Mono<Boolean> existsByUserIdAndRoleId(Integer userId, Integer roleId);
}