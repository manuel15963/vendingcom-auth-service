package com.vendingcom.auth_service.application.port.input;

import com.vendingcom.auth_service.application.dto.request.ChangePasswordRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryConfirmRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryRequest;
import reactor.core.publisher.Mono;

public interface PasswordUseCase {

    Mono<Void> changeMyPassword(String username, ChangePasswordRequest request);

    Mono<Void> changePasswordByAdmin(Integer userId, ChangePasswordRequest request);

    Mono<Void> requestPasswordRecovery(PasswordRecoveryRequest request);

    Mono<Void> confirmPasswordRecovery(PasswordRecoveryConfirmRequest request);
}