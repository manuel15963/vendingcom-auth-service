package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.controller;

import com.vendingcom.auth_service.application.dto.response.AuthAuditLogResponse;
import com.vendingcom.auth_service.application.port.input.AuditLogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/auth/audit-logs")
@Tag(name = "Auditoría", description = "Endpoints para consulta de auditoría del sistema")
@SecurityRequirement(name = "bearerAuth")
public class AuthAuditLogController {

    private final AuditLogUseCase auditLogUseCase;

    public AuthAuditLogController(AuditLogUseCase auditLogUseCase) {
        this.auditLogUseCase = auditLogUseCase;
    }

    @Operation(
            summary = "Listar auditoría",
            description = """
                    Lista los eventos auditados del sistema.
                    Incluye eventos como login exitoso, login fallido, creación de usuarios,
                    cambio de contraseña, recuperación de contraseña y cambios de estado.
                    """
    )
    @GetMapping
    public Flux<AuthAuditLogResponse> findAll() {
        return auditLogUseCase.findAll()
                .map(AuthAuditLogResponse::fromDomain);
    }

    @Operation(
            summary = "Buscar auditoría por usuario afectado",
            description = "Lista los eventos de auditoría relacionados a un usuario específico."
    )
    @GetMapping("/user/{userId}")
    public Flux<AuthAuditLogResponse> findByAffectedUserId(@PathVariable Integer userId) {
        return auditLogUseCase.findByAffectedUserId(userId)
                .map(AuthAuditLogResponse::fromDomain);
    }

    @Operation(
            summary = "Buscar auditoría por tipo de acción",
            description = """
                    Lista los eventos de auditoría filtrados por tipo de acción.
                    Ejemplos: LOGIN_SUCCESS, LOGIN_FAILED, USER_CREATED, PASSWORD_CHANGED,
                    PASSWORD_RECOVERY_REQUESTED, PASSWORD_RESET.
                    """
    )
    @GetMapping("/action/{actionType}")
    public Flux<AuthAuditLogResponse> findByActionType(@PathVariable String actionType) {
        return auditLogUseCase.findByActionType(actionType)
                .map(AuthAuditLogResponse::fromDomain);
    }
}