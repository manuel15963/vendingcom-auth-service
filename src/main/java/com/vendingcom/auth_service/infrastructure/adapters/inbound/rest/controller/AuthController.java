package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.controller;

import com.vendingcom.auth_service.application.dto.request.LoginRequest;
import com.vendingcom.auth_service.application.dto.response.AuthenticatedUserResponse;
import com.vendingcom.auth_service.application.dto.response.LoginResponse;
import com.vendingcom.auth_service.application.port.input.UserUseCase;
import com.vendingcom.auth_service.util.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y consulta del usuario autenticado")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserUseCase userUseCase;
    private final JwtService jwtService;

    public AuthController(
            UserUseCase userUseCase,
            JwtService jwtService
    ) {
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Valida las credenciales del usuario y genera un token JWT con la información básica y roles del usuario."
    )
    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userUseCase.login(request);
    }

    @Operation(
            summary = "Obtener usuario autenticado",
            description = "Retorna la información del usuario autenticado usando el token JWT enviado en el header Authorization.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    public Mono<AuthenticatedUserResponse> me(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        Integer userId = jwtService.extractClaims(token).get("userId", Integer.class);
        String username = jwtService.extractUsername(token);
        String email = jwtService.extractClaims(token).get("email", String.class);
        String fullName = jwtService.extractClaims(token).get("fullName", String.class);
        List<String> roles = jwtService.extractRoles(token);

        return Mono.just(new AuthenticatedUserResponse(
                userId,
                username,
                email,
                fullName,
                roles
        ));
    }
}