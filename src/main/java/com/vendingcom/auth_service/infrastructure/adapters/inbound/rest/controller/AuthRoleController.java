package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.controller;

import com.vendingcom.auth_service.application.dto.response.AuthRoleResponse;
import com.vendingcom.auth_service.application.port.input.RoleUseCase;
import com.vendingcom.auth_service.domain.model.AuthRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth/roles")
@Tag(name = "Roles", description = "Endpoints para consulta de roles del sistema")
@SecurityRequirement(name = "bearerAuth")
public class AuthRoleController {

    private final RoleUseCase roleUseCase;

    public AuthRoleController(RoleUseCase roleUseCase) {
        this.roleUseCase = roleUseCase;
    }

    @Operation(
            summary = "Listar roles",
            description = """
                    Lista los roles registrados en el sistema.
                    Si activeOnly es true, retorna solo roles activos.
                    """
    )
    @GetMapping
    public Flux<AuthRoleResponse> findAll(
            @RequestParam(name = "activeOnly", defaultValue = "false") boolean activeOnly
    ) {
        Flux<AuthRole> roles = activeOnly
                ? roleUseCase.findAllActive()
                : roleUseCase.findAll();

        return roles.map(AuthRoleResponse::fromDomain);
    }

    @Operation(
            summary = "Buscar rol por código",
            description = """
                    Busca un rol usando su código.
                    Ejemplos de códigos: ADMIN, SUPERVISOR, OPERATOR.
                    """
    )
    @GetMapping("/search")
    public Mono<AuthRoleResponse> findByRoleCode(@RequestParam String roleCode) {
        return roleUseCase.findByRoleCode(roleCode)
                .map(AuthRoleResponse::fromDomain);
    }
}