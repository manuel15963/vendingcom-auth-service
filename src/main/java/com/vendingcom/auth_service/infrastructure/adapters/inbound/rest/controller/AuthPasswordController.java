package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.controller;

import com.vendingcom.auth_service.application.dto.request.ChangePasswordRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryConfirmRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryRequest;
import com.vendingcom.auth_service.application.dto.response.MessageResponse;
import com.vendingcom.auth_service.application.port.input.PasswordUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth/password")
@Tag(name = "Contraseñas", description = "Endpoints para cambio y recuperación de contraseña")
public class AuthPasswordController {

    private final PasswordUseCase passwordUseCase;

    public AuthPasswordController(PasswordUseCase passwordUseCase) {
        this.passwordUseCase = passwordUseCase;
    }

    @Operation(
            summary = "Cambiar mi contraseña",
            description = """
                    Permite que un usuario autenticado cambie su contraseña.
                    Requiere enviar la contraseña actual y la nueva contraseña.
                    El usuario se obtiene desde el token JWT.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/me")
    public Mono<MessageResponse> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(Authentication::getName)
                .flatMap(username -> passwordUseCase.changeMyPassword(username, request))
                .thenReturn(MessageResponse.of(
                        "PASSWORD_CHANGED",
                        "Contraseña actualizada correctamente."
                ));
    }

    @Operation(
            summary = "Solicitar código de recuperación",
            description = """
                    Genera un código temporal de recuperación de contraseña y lo envía al correo registrado del usuario.
                    El código se guarda hasheado en base de datos.
                    Aplica rate limit para evitar múltiples solicitudes seguidas.
                    """
    )
    @PostMapping("/recovery/request")
    public Mono<MessageResponse> requestPasswordRecovery(
            @Valid @RequestBody PasswordRecoveryRequest request
    ) {
        return passwordUseCase.requestPasswordRecovery(request)
                .thenReturn(MessageResponse.of(
                        "RECOVERY_CODE_SENT",
                        "Si el correo existe, se enviará un código de recuperación."
                ));
    }

    @Operation(
            summary = "Confirmar recuperación de contraseña",
            description = """
                    Valida el código enviado al correo del usuario y restablece la contraseña.
                    El código solo puede usarse una vez, tiene expiración y máximo de intentos.
                    """
    )
    @PostMapping("/recovery/confirm")
    public Mono<MessageResponse> confirmPasswordRecovery(
            @Valid @RequestBody PasswordRecoveryConfirmRequest request
    ) {
        return passwordUseCase.confirmPasswordRecovery(request)
                .thenReturn(MessageResponse.of(
                        "PASSWORD_RESET",
                        "Contraseña restablecida correctamente."
                ));
    }
}