package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthRoleRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthRole;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthRolePersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthRoleRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthRolePersistenceAdapter implements AuthRoleRepositoryPort {

    private static final Integer ACTIVE_STATUS = 1;

    private final ReactiveAuthRoleRepository reactiveAuthRoleRepository;
    private final AuthRolePersistenceMapper authRolePersistenceMapper;

    public AuthRolePersistenceAdapter(
            ReactiveAuthRoleRepository reactiveAuthRoleRepository,
            AuthRolePersistenceMapper authRolePersistenceMapper
    ) {
        this.reactiveAuthRoleRepository = reactiveAuthRoleRepository;
        this.authRolePersistenceMapper = authRolePersistenceMapper;
    }

    @Override
    public Mono<AuthRole> findById(Integer roleId) {
        return reactiveAuthRoleRepository.findById(roleId)
                .map(authRolePersistenceMapper::toDomain);
    }

    @Override
    public Mono<AuthRole> findByRoleCode(String roleCode) {
        return reactiveAuthRoleRepository.findByRoleCode(roleCode)
                .map(authRolePersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthRole> findAll() {
        return reactiveAuthRoleRepository.findAll()
                .map(authRolePersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthRole> findAllActive() {
        return reactiveAuthRoleRepository.findByRoleStatus(ACTIVE_STATUS)
                .map(authRolePersistenceMapper::toDomain);
    }
}