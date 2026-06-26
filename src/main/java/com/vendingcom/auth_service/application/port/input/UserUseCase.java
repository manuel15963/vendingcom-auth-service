package com.vendingcom.auth_service.application.port.input;

import com.vendingcom.auth_service.application.dto.request.ChangePasswordRequest;
import com.vendingcom.auth_service.application.dto.request.CreateUserRequest;
import com.vendingcom.auth_service.application.dto.request.LoginRequest;
import com.vendingcom.auth_service.application.dto.request.UpdateUserRequest;
import com.vendingcom.auth_service.application.dto.response.AuthenticatedUserResponse;
import com.vendingcom.auth_service.application.dto.response.LoginResponse;
import com.vendingcom.auth_service.domain.model.AuthUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserUseCase {

    Mono<AuthUser> create(CreateUserRequest request);

    /** Datos frescos del usuario autenticado (leídos de BD, no del token). */
    Mono<AuthenticatedUserResponse> getAuthenticatedUser(String username);

    Mono<AuthUser> update(Integer userId, UpdateUserRequest request);

    Mono<AuthUser> findById(Integer userId);

    Mono<AuthUser> findByUsername(String username);

    Flux<AuthUser> findAll(Integer status);

    Mono<Void> deactivate(Integer userId);

    Mono<AuthUser> activate(Integer userId);

    Mono<AuthUser> lock(Integer userId);

    Mono<LoginResponse> login(LoginRequest request);

}