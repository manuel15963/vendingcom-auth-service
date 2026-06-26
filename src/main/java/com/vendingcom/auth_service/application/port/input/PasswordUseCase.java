package com.vendingcom.auth_service.application.port.input;

import com.vendingcom.auth_service.application.dto.request.AdminResetPasswordRequest;
import com.vendingcom.auth_service.application.dto.request.ChangePasswordRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryConfirmRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryRequest;
import reactor.core.publisher.Mono;

public interface PasswordUseCase {

    Mono<Void> changeMyPassword(String username, ChangePasswordRequest request);

    /**
     * Restablece la contraseña de un usuario por parte de un administrador.
     * No requiere la contraseña actual; queda auditado bajo el username del admin.
     */
    Mono<Void> resetPasswordByAdmin(Integer targetUserId, String adminUsername, AdminResetPasswordRequest request);

    Mono<Void> requestPasswordRecovery(PasswordRecoveryRequest request);

    Mono<Void> confirmPasswordRecovery(PasswordRecoveryConfirmRequest request);
}