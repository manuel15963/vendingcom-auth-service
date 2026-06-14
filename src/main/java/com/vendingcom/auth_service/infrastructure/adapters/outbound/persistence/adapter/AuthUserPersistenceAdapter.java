package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthUserRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthUser;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthUserPersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthUserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthUserPersistenceAdapter implements AuthUserRepositoryPort {

    private final ReactiveAuthUserRepository reactiveAuthUserRepository;
    private final AuthUserPersistenceMapper authUserPersistenceMapper;

    public AuthUserPersistenceAdapter(
            ReactiveAuthUserRepository reactiveAuthUserRepository,
            AuthUserPersistenceMapper authUserPersistenceMapper
    ) {
        this.reactiveAuthUserRepository = reactiveAuthUserRepository;
        this.authUserPersistenceMapper = authUserPersistenceMapper;
    }

    @Override
    public Mono<AuthUser> save(AuthUser authUser) {
        return reactiveAuthUserRepository.save(authUserPersistenceMapper.toEntity(authUser))
                .map(authUserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<AuthUser> findById(Integer userId) {
        return reactiveAuthUserRepository.findById(userId)
                .map(authUserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<AuthUser> findByUsername(String username) {
        return reactiveAuthUserRepository.findByUsername(username)
                .map(authUserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<AuthUser> findByEmail(String email) {
        return reactiveAuthUserRepository.findByEmail(email)
                .map(authUserPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return reactiveAuthUserRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return reactiveAuthUserRepository.existsByEmail(email);
    }

    @Override
    public Flux<AuthUser> findAll() {
        return reactiveAuthUserRepository.findAll()
                .map(authUserPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthUser> findByUserStatus(Integer userStatus) {
        return reactiveAuthUserRepository.findByUserStatus(userStatus)
                .map(authUserPersistenceMapper::toDomain);
    }
}