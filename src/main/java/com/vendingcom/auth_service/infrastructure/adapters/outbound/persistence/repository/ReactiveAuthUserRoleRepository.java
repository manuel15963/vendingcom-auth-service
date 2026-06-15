package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthUserRoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveAuthUserRoleRepository extends ReactiveCrudRepository<AuthUserRoleEntity, Integer> {

    Flux<AuthUserRoleEntity> findByUserId(Integer userId);

    Mono<Boolean> existsByUserIdAndRoleId(Integer userId, Integer roleId);
}