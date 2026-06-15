package com.vendingcom.auth_service.application.port.input;

import com.vendingcom.auth_service.domain.model.AuthRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleUseCase {

    Mono<AuthRole> findById(Integer roleId);

    Mono<AuthRole> findByRoleCode(String roleCode);

    Flux<AuthRole> findAll();

    Flux<AuthRole> findAllActive();
}