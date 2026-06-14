package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthUserRoleRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthUserRole;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthUserRolePersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthUserRoleRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthUserRolePersistenceAdapter implements AuthUserRoleRepositoryPort {

    private final ReactiveAuthUserRoleRepository reactiveAuthUserRoleRepository;
    private final AuthUserRolePersistenceMapper authUserRolePersistenceMapper;

    public AuthUserRolePersistenceAdapter(
            ReactiveAuthUserRoleRepository reactiveAuthUserRoleRepository,
            AuthUserRolePersistenceMapper authUserRolePersistenceMapper
    ) {
        this.reactiveAuthUserRoleRepository = reactiveAuthUserRoleRepository;
        this.authUserRolePersistenceMapper = authUserRolePersistenceMapper;
    }

    @Override
    public Mono<AuthUserRole> save(AuthUserRole authUserRole) {
        return reactiveAuthUserRoleRepository.save(authUserRolePersistenceMapper.toEntity(authUserRole))
                .map(authUserRolePersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthUserRole> findByUserId(Integer userId) {
        return reactiveAuthUserRoleRepository.findByUserId(userId)
                .map(authUserRolePersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUserIdAndRoleId(Integer userId, Integer roleId) {
        return reactiveAuthUserRoleRepository.existsByUserIdAndRoleId(userId, roleId);
    }
}