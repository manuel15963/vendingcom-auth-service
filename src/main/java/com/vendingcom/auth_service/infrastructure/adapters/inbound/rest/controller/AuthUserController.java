package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.controller;

import com.vendingcom.auth_service.application.dto.request.CreateUserRequest;
import com.vendingcom.auth_service.application.dto.request.UpdateUserRequest;
import com.vendingcom.auth_service.application.dto.response.AuthUserResponse;
import com.vendingcom.auth_service.application.dto.response.MessageResponse;
import com.vendingcom.auth_service.application.port.input.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class AuthUserController {

    private final UserUseCase userUseCase;

    public AuthUserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @Operation(
            summary = "Listar usuarios",
            description = """
                    Lista usuarios registrados en el sistema.
                    Puede filtrar por estado:
                    0 = Inactivo, 1 = Activo, 2 = Bloqueado.
                    """
    )
    @GetMapping
    public Flux<AuthUserResponse> findAll(
            @RequestParam(name = "status", required = false) Integer status
    ) {
        return userUseCase.findAll(status)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Retorna la información de un usuario específico usando su identificador."
    )
    @GetMapping("/{userId}")
    public Mono<AuthUserResponse> findById(@PathVariable Integer userId) {
        return userUseCase.findById(userId)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Buscar usuario por username",
            description = "Retorna la información de un usuario usando su nombre de usuario."
    )
    @GetMapping("/search")
    public Mono<AuthUserResponse> findByUsername(@RequestParam String username) {
        return userUseCase.findByUsername(username)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Crear usuario",
            description = """
                    Registra un nuevo usuario en el sistema.
                    Valida que el username y email no existan.
                    Asigna un rol activo al usuario.
                    """
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return userUseCase.create(request)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = """
                    Actualiza la información básica de un usuario.
                    No modifica la contraseña ni el rol.
                    """
    )
    @PutMapping("/{userId}")
    public Mono<AuthUserResponse> update(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userUseCase.update(userId, request)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Eliminar usuario lógicamente",
            description = """
                    Inactiva un usuario cambiando su estado a 0.
                    No elimina físicamente el registro de la base de datos.
                    """
    )
    @DeleteMapping("/{userId}")
    public Mono<MessageResponse> deactivate(@PathVariable Integer userId) {
        return userUseCase.deactivate(userId)
                .thenReturn(MessageResponse.of(
                        "USER_DEACTIVATED",
                        "Usuario eliminado correctamente."
                ));
    }

    @Operation(
            summary = "Activar usuario",
            description = "Activa un usuario cambiando su estado a 1."
    )
    @PatchMapping("/{userId}/activate")
    public Mono<AuthUserResponse> activate(@PathVariable Integer userId) {
        return userUseCase.activate(userId)
                .map(AuthUserResponse::fromDomain);
    }

    @Operation(
            summary = "Bloquear usuario",
            description = "Bloquea un usuario cambiando su estado a 2."
    )
    @PatchMapping("/{userId}/lock")
    public Mono<AuthUserResponse> lock(@PathVariable Integer userId) {
        return userUseCase.lock(userId)
                .map(AuthUserResponse::fromDomain);
    }
}