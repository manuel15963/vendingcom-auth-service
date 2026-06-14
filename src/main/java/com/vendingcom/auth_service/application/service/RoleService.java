package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.port.input.RoleUseCase;
import com.vendingcom.auth_service.application.port.output.persistence.AuthRoleRepositoryPort;
import com.vendingcom.auth_service.domain.exception.ResourceNotFoundException;
import com.vendingcom.auth_service.domain.model.AuthRole;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoleService implements RoleUseCase {

    private final AuthRoleRepositoryPort authRoleRepositoryPort;

    public RoleService(AuthRoleRepositoryPort authRoleRepositoryPort) {
        this.authRoleRepositoryPort = authRoleRepositoryPort;
    }

    @Override
    public Mono<AuthRole> findById(Integer roleId) {
        return authRoleRepositoryPort.findById(roleId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el rol con id: " + roleId)));
    }

    @Override
    public Mono<AuthRole> findByRoleCode(String roleCode) {
        return authRoleRepositoryPort.findByRoleCode(normalizeRoleCode(roleCode))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el rol con código: " + roleCode)));
    }

    @Override
    public Flux<AuthRole> findAll() {
        return authRoleRepositoryPort.findAll();
    }

    @Override
    public Flux<AuthRole> findAllActive() {
        return authRoleRepositoryPort.findAllActive();
    }

    private String normalizeRoleCode(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}