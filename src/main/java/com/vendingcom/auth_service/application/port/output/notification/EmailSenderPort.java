package com.vendingcom.auth_service.application.port.output.notification;

import reactor.core.publisher.Mono;

public interface EmailSenderPort {

    Mono<Void> sendPasswordRecoveryCode(String to, String code, Integer expirationMinutes);
}