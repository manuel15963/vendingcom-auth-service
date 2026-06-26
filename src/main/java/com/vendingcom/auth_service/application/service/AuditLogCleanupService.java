package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Limpieza periódica de la auditoría (retención).
 *
 * No borra todo: elimina únicamente los registros más antiguos que la ventana
 * de retención configurada, conservando el historial reciente. Se ejecuta
 * semanalmente.
 */
@Service
public class AuditLogCleanupService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogCleanupService.class);
    private static final long ONE_WEEK_MILLIS = 7L * 24 * 60 * 60 * 1000;

    private final AuthAuditLogRepositoryPort authAuditLogRepositoryPort;
    private final long retentionDays;

    public AuditLogCleanupService(
            AuthAuditLogRepositoryPort authAuditLogRepositoryPort,
            @Value("${auth.audit-log.retention-days:7}") long retentionDays
    ) {
        this.authAuditLogRepositoryPort = authAuditLogRepositoryPort;
        this.retentionDays = retentionDays;
    }

    @Scheduled(fixedRate = ONE_WEEK_MILLIS)
    public void cleanOldAuditLogs() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);

        authAuditLogRepositoryPort.deleteOlderThan(threshold)
                .doOnSuccess(unused -> log.info(
                        "Limpieza de auditoría: eliminados registros anteriores a {}", threshold))
                .onErrorResume(error -> {
                    log.error("Error al limpiar la auditoría", error);
                    return Mono.empty();
                })
                .subscribe();
    }
}
