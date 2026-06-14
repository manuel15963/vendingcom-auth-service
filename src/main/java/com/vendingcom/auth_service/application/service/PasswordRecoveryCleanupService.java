package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.port.output.persistence.AuthPasswordRecoveryCodeRepositoryPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class PasswordRecoveryCleanupService {

    private final AuthPasswordRecoveryCodeRepositoryPort recoveryCodeRepositoryPort;

    public PasswordRecoveryCleanupService(
            AuthPasswordRecoveryCodeRepositoryPort recoveryCodeRepositoryPort
    ) {
        this.recoveryCodeRepositoryPort = recoveryCodeRepositoryPort;
    }

    @Scheduled(fixedRate = 300000)
    public void cleanExpiredOrUsedCodes() {
        recoveryCodeRepositoryPort.deleteExpiredOrUsed(LocalDateTime.now())
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }
}