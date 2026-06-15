package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository;

import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity.AuthRoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveAuthRoleRepository extends ReactiveCrudRepository<AuthRoleEntity, Integer> {

    Mono<AuthRoleEntity> findByRoleCode(String roleCode);

    Flux<AuthRoleEntity> findByRoleStatus(Integer roleStatus);
}