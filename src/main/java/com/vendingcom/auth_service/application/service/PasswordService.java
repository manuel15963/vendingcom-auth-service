package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.dto.request.ChangePasswordRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryConfirmRequest;
import com.vendingcom.auth_service.application.dto.request.PasswordRecoveryRequest;
import com.vendingcom.auth_service.application.port.input.PasswordUseCase;
import com.vendingcom.auth_service.application.port.output.notification.EmailSenderPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthPasswordRecoveryCodeRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthUserRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.PasswordEncoderPort;
import com.vendingcom.auth_service.domain.exception.BusinessRuleException;
import com.vendingcom.auth_service.domain.exception.InvalidCredentialsException;
import com.vendingcom.auth_service.domain.exception.ResourceNotFoundException;
import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import com.vendingcom.auth_service.domain.model.AuthPasswordRecoveryCode;
import com.vendingcom.auth_service.domain.model.AuthUser;
import com.vendingcom.auth_service.util.audit.AuditDataSerializer;
import com.vendingcom.auth_service.util.request.RequestContext;
import com.vendingcom.auth_service.util.request.RequestContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordService implements PasswordUseCase {

    private static final Integer ACTIVE_STATUS = 1;
    private static final Integer INITIAL_ATTEMPTS = 0;

    private final SecureRandom secureRandom = new SecureRandom();

    private final AuthUserRepositoryPort authUserRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthAuditLogRepositoryPort authAuditLogRepositoryPort;
    private final AuthPasswordRecoveryCodeRepositoryPort recoveryCodeRepositoryPort;
    private final EmailSenderPort emailSenderPort;

    private final Integer codeExpirationMinutes;
    private final Integer maxAttempts;
    private final Integer requestCooldownMinutes;

    public PasswordService(
            AuthUserRepositoryPort authUserRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            AuthAuditLogRepositoryPort authAuditLogRepositoryPort,
            AuthPasswordRecoveryCodeRepositoryPort recoveryCodeRepositoryPort,
            EmailSenderPort emailSenderPort,
            @Value("${auth.password-recovery.code-expiration-minutes}") Integer codeExpirationMinutes,
            @Value("${auth.password-recovery.max-attempts}") Integer maxAttempts,
            @Value("${auth.password-recovery.request-cooldown-minutes}") Integer requestCooldownMinutes
    ) {
        this.authUserRepositoryPort = authUserRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.authAuditLogRepositoryPort = authAuditLogRepositoryPort;
        this.recoveryCodeRepositoryPort = recoveryCodeRepositoryPort;
        this.emailSenderPort = emailSenderPort;
        this.codeExpirationMinutes = codeExpirationMinutes;
        this.maxAttempts = maxAttempts;
        this.requestCooldownMinutes = requestCooldownMinutes;
    }

    @Override
    public Mono<Void> changeMyPassword(String username, ChangePasswordRequest request) {
        return authUserRepositoryPort.findByUsername(normalizeUsername(username))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario: " + username)))
                .flatMap(user -> validateChangePassword(user, request)
                        .then(updatePassword(user, request.newPassword(), user.userId(), "PASSWORD_CHANGED"))
                )
                .then();
    }

    @Override
    public Mono<Void> changePasswordByAdmin(Integer userId, ChangePasswordRequest request) {
        return authUserRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario con id: " + userId)))
                .flatMap(user -> validateChangePassword(user, request)
                        .then(updatePassword(user, request.newPassword(), user.userId(), "PASSWORD_CHANGED"))
                )
                .then();
    }

    @Override
    public Mono<Void> requestPasswordRecovery(PasswordRecoveryRequest request) {
        String email = normalizeEmail(request.email());

        return authUserRepositoryPort.findByEmail(email)
                .flatMap(user -> {
                    if (!ACTIVE_STATUS.equals(user.userStatus())) {
                        return Mono.empty();
                    }

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime cooldownLimit = now.minusMinutes(requestCooldownMinutes);

                    return recoveryCodeRepositoryPort.existsRecentRequestByEmail(email, cooldownLimit)
                            .flatMap(existsRecentRequest -> {
                                if (Boolean.TRUE.equals(existsRecentRequest)) {
                                    return Mono.error(new BusinessRuleException(
                                            "PASSWORD_RECOVERY_RATE_LIMIT",
                                            "Debe esperar unos minutos antes de solicitar un nuevo código."
                                    ));
                                }

                                String code = generateRecoveryCode();
                                String codeHash = passwordEncoderPort.encode(code);

                                AuthPasswordRecoveryCode recoveryCode = new AuthPasswordRecoveryCode(
                                        null,
                                        user.userId(),
                                        email,
                                        codeHash,
                                        false,
                                        now.plusMinutes(codeExpirationMinutes),
                                        now,
                                        null,
                                        INITIAL_ATTEMPTS,
                                        maxAttempts
                                );

                                return recoveryCodeRepositoryPort.markActiveCodesAsUsedByEmail(email)
                                        .then(recoveryCodeRepositoryPort.save(recoveryCode))
                                        .flatMap(savedCode -> emailSenderPort.sendPasswordRecoveryCode(
                                                email,
                                                code,
                                                codeExpirationMinutes
                                        ))
                                        .then(saveAuditLogSimple(
                                                "PASSWORD_RECOVERY_REQUESTED",
                                                user.userId(),
                                                user.userId(),
                                                "Código de recuperación enviado al correo para usuario: " + user.username()
                                        ))
                                        .then();
                            });
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> confirmPasswordRecovery(PasswordRecoveryConfirmRequest request) {
        String email = normalizeEmail(request.email());

        return authUserRepositoryPort.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró un usuario con el correo indicado.")))
                .flatMap(user -> recoveryCodeRepositoryPort.findLastActiveCodeByEmail(email)
                        .switchIfEmpty(Mono.error(new BusinessRuleException(
                                "RECOVERY_CODE_NOT_FOUND",
                                "No existe un código activo para este correo."
                        )))
                        .flatMap(recoveryCode -> validateRecoveryCode(request, recoveryCode)
                                .then(validateNewPasswordIsDifferent(request.newPassword(), user))
                                .then(updatePassword(user, request.newPassword(), user.userId(), "PASSWORD_RESET"))
                                .then(recoveryCodeRepositoryPort.markAsUsedById(recoveryCode.recoveryCodeId()))
                        )
                );
    }

    private Mono<Void> validateRecoveryCode(
            PasswordRecoveryConfirmRequest request,
            AuthPasswordRecoveryCode recoveryCode
    ) {
        if (!passwordEncoderPort.matches(request.code(), recoveryCode.codeHash())) {
            return recoveryCodeRepositoryPort.incrementAttempts(recoveryCode.recoveryCodeId())
                    .then(Mono.error(new BusinessRuleException(
                            "INVALID_RECOVERY_CODE",
                            "El código de recuperación es incorrecto."
                    )));
        }

        return Mono.empty();
    }

    private Mono<Void> validateNewPasswordIsDifferent(String newPassword, AuthUser user) {
        if (passwordEncoderPort.matches(newPassword, user.passwordHash())) {
            return Mono.error(new BusinessRuleException(
                    "PASSWORD_SAME_AS_CURRENT",
                    "La nueva contraseña no puede ser igual a la contraseña actual."
            ));
        }

        return Mono.empty();
    }

    private Mono<Void> validateChangePassword(AuthUser user, ChangePasswordRequest request) {
        if (!ACTIVE_STATUS.equals(user.userStatus())) {
            return Mono.error(new BusinessRuleException(
                    "USER_NOT_ACTIVE",
                    "Solo un usuario activo puede cambiar su contraseña."
            ));
        }

        if (!passwordEncoderPort.matches(request.currentPassword(), user.passwordHash())) {
            return Mono.error(new InvalidCredentialsException("La contraseña actual es incorrecta."));
        }

        if (passwordEncoderPort.matches(request.newPassword(), user.passwordHash())) {
            return Mono.error(new BusinessRuleException(
                    "PASSWORD_SAME_AS_CURRENT",
                    "La nueva contraseña no puede ser igual a la contraseña actual."
            ));
        }

        return Mono.empty();
    }

    private Mono<AuthUser> updatePassword(
            AuthUser user,
            String newPassword,
            Integer executedByUserId,
            String auditAction
    ) {
        AuthUser userToUpdate = new AuthUser(
                user.userId(),
                user.username(),
                user.email(),
                passwordEncoderPort.encode(newPassword),
                user.fullName(),
                user.phoneNumber(),
                user.documentType(),
                user.documentNumber(),
                user.userStatus(),
                user.lastLoginAt(),
                user.createdByUserId(),
                executedByUserId,
                user.createdAt(),
                LocalDateTime.now()
        );

        return authUserRepositoryPort.save(userToUpdate)
                .flatMap(updatedUser -> saveAuditLogWithData(
                        auditAction,
                        updatedUser.userId(),
                        executedByUserId,
                        "Contraseña actualizada correctamente para usuario: " + updatedUser.username(),
                        AuditDataSerializer.serializeUser(user),
                        AuditDataSerializer.serializeUser(updatedUser)
                ).thenReturn(updatedUser));
    }

    private Mono<AuthAuditLog> saveAuditLogSimple(
            String actionType,
            Integer affectedUserId,
            Integer executedByUserId,
            String actionDescription
    ) {
        // Para acciones sin cambio de datos (PASSWORD_RECOVERY_REQUESTED, etc.)
        return saveAuditLogWithData(actionType, affectedUserId, executedByUserId, actionDescription, null, null);
    }

    private Mono<AuthAuditLog> saveAuditLog(
            String actionType,
            Integer affectedUserId,
            Integer executedByUserId,
            String actionDescription
    ) {
        return saveAuditLogSimple(actionType, affectedUserId, executedByUserId, actionDescription);
    }

    private Mono<AuthAuditLog> saveAuditLogWithData(
            String actionType,
            Integer affectedUserId,
            Integer executedByUserId,
            String actionDescription,
            String oldData,
            String newData
    ) {
        return Mono.deferContextual(ctx -> {
            String clientIp = "UNKNOWN";
            String userAgent = "UNKNOWN";

            try {
                RequestContext requestContext = (RequestContext) ctx.get(RequestContextFilter.REQUEST_CONTEXT_KEY);
                clientIp = requestContext.clientIp();
                userAgent = requestContext.userAgent();
            } catch (Exception e) {
                // Si no existe contexto reactivo, se usa UNKNOWN
            }

            AuthAuditLog auditLog = new AuthAuditLog(
                    null,
                    affectedUserId,
                    actionType,
                    "auth_users",
                    affectedUserId,
                    actionDescription,
                    oldData,
                    newData,
                    clientIp,
                    userAgent,
                    executedByUserId,
                    LocalDateTime.now()
            );

            return authAuditLogRepositoryPort.save(auditLog);
        });
    }

    private String generateRecoveryCode() {
        int number = secureRandom.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    private String normalizeUsername(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}