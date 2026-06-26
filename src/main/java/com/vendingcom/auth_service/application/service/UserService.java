package com.vendingcom.auth_service.application.service;

import com.vendingcom.auth_service.application.dto.request.CreateUserRequest;
import com.vendingcom.auth_service.application.dto.request.LoginRequest;
import com.vendingcom.auth_service.application.dto.request.UpdateUserRequest;
import com.vendingcom.auth_service.application.dto.response.AuthRoleResponse;
import com.vendingcom.auth_service.application.dto.response.LoginResponse;
import com.vendingcom.auth_service.application.port.input.UserUseCase;
import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthRoleRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthUserRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.AuthUserRoleRepositoryPort;
import com.vendingcom.auth_service.application.port.output.persistence.PasswordEncoderPort;
import com.vendingcom.auth_service.domain.exception.BusinessRuleException;
import com.vendingcom.auth_service.domain.exception.InvalidCredentialsException;
import com.vendingcom.auth_service.domain.exception.ResourceNotFoundException;
import com.vendingcom.auth_service.domain.exception.UserAlreadyExistsException;
import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import com.vendingcom.auth_service.domain.model.AuthRole;
import com.vendingcom.auth_service.domain.model.AuthUser;
import com.vendingcom.auth_service.domain.model.AuthUserRole;
import com.vendingcom.auth_service.util.audit.AuditDataSerializer;
import com.vendingcom.auth_service.util.request.RequestContext;
import com.vendingcom.auth_service.util.request.RequestContextFilter;
import com.vendingcom.auth_service.util.security.JwtService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements UserUseCase {

    private static final Integer INACTIVE_STATUS = 0;
    private static final Integer ACTIVE_STATUS = 1;
    private static final Integer LOCKED_STATUS = 2;

    private final AuthUserRepositoryPort authUserRepositoryPort;
    private final AuthRoleRepositoryPort authRoleRepositoryPort;
    private final AuthUserRoleRepositoryPort authUserRoleRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AuthAuditLogRepositoryPort authAuditLogRepositoryPort;
    private final JwtService jwtService;

    public UserService(
            AuthUserRepositoryPort authUserRepositoryPort,
            AuthRoleRepositoryPort authRoleRepositoryPort,
            AuthUserRoleRepositoryPort authUserRoleRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            AuthAuditLogRepositoryPort authAuditLogRepositoryPort,
            JwtService jwtService
    ) {
        this.authUserRepositoryPort = authUserRepositoryPort;
        this.authRoleRepositoryPort = authRoleRepositoryPort;
        this.authUserRoleRepositoryPort = authUserRoleRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.authAuditLogRepositoryPort = authAuditLogRepositoryPort;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<AuthUser> create(CreateUserRequest request) {
        String username = normalizeUsername(request.username());
        String email = normalizeEmail(request.email());
        String roleCode = normalizeRoleCode(request.roleCode());

        return validateUsernameDoesNotExist(username)
                .then(validateEmailDoesNotExist(email))
                .then(findActiveRole(roleCode))
                .flatMap(role -> createUserWithRole(request, username, email, role));
    }

    @Override
    public Mono<AuthUser> update(Integer userId, UpdateUserRequest request) {
        String username = normalizeUsername(request.username());
        String email = normalizeEmail(request.email());

        return authUserRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario con id: " + userId)))
                .flatMap(existingUser ->
                        validateUsernameForUpdate(userId, username)
                                .then(validateEmailForUpdate(userId, email))
                                .then(updateExistingUser(existingUser, request, username, email))
                );
    }

    @Override
    public Mono<AuthUser> findById(Integer userId) {
        return authUserRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario con id: " + userId)));
    }

    @Override
    public Mono<AuthUser> findByUsername(String username) {
        return authUserRepositoryPort.findByUsername(normalizeUsername(username))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario con username: " + username)));
    }

    @Override
    public Flux<AuthUser> findAll(Integer status) {
        if (status == null) {
            return authUserRepositoryPort.findAll();
        }

        if (!isValidUserStatus(status)) {
            return Flux.error(new IllegalArgumentException("El estado del usuario debe ser 0, 1 o 2"));
        }

        return authUserRepositoryPort.findByUserStatus(status);
    }

    @Override
    public Mono<Void> deactivate(Integer userId) {
        return changeUserStatus(userId, INACTIVE_STATUS).then();
    }

    @Override
    public Mono<AuthUser> activate(Integer userId) {
        return changeUserStatus(userId, ACTIVE_STATUS);
    }

    @Override
    public Mono<AuthUser> lock(Integer userId) {
        return changeUserStatus(userId, LOCKED_STATUS);
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        String username = normalizeUsername(request.username());

        return authUserRepositoryPort.findByUsername(username)
                .switchIfEmpty(Mono.defer(() ->
                        saveAuditLogSimple(
                                "LOGIN_FAILED",
                                null,
                                null,
                                "Intento de inicio de sesión fallido. Usuario no encontrado: " + username
                        ).then(Mono.error(new InvalidCredentialsException("Usuario o contraseña incorrectos.")))
                ))
                .flatMap(user -> validateLoginUser(user, request.password())
                        .onErrorResume(exception ->
                                saveAuditLogSimple(
                                        "LOGIN_FAILED",
                                        user.userId(),
                                        user.userId(),
                                        "Intento de inicio de sesión fallido para usuario: "
                                                + user.username()
                                                + ". Motivo: "
                                                + exception.getMessage()
                                ).then(Mono.error(exception))
                        )
                        .then(updateLastLogin(user))
                        .flatMap(updatedUser -> getActiveRolesByUser(updatedUser.userId())
                                .flatMap(roles -> {
                                    String token = jwtService.generateToken(updatedUser, roles);

                                    return saveAuditLogSimple(
                                            "LOGIN_SUCCESS",
                                            updatedUser.userId(),
                                            updatedUser.userId(),
                                            "Inicio de sesión correcto: " + updatedUser.username()
                                    ).thenReturn(new LoginResponse(
                                            token,
                                            "Bearer",
                                            jwtService.getExpirationSeconds(),
                                            updatedUser.userId(),
                                            updatedUser.username(),
                                            updatedUser.email(),
                                            updatedUser.fullName(),
                                            updatedUser.userStatus(),
                                            updatedUser.lastLoginAt(),
                                            roles,
                                            "Inicio de sesión correcto."
                                    ));
                                })
                        )
                );
    }

    private Mono<AuthUser> createUserWithRole(
            CreateUserRequest request,
            String username,
            String email,
            AuthRole role
    ) {
        AuthUser userToSave = new AuthUser(
                null,
                username,
                email,
                passwordEncoderPort.encode(request.password()),
                request.fullName(),
                normalizeNullable(request.phoneNumber()),
                normalizeNullable(request.documentType()),
                normalizeNullable(request.documentNumber()),
                ACTIVE_STATUS,
                null,
                null,
                null,
                null,
                null
        );

        return authUserRepositoryPort.save(userToSave)
                .flatMap(savedUser -> assignRole(savedUser, role)
                        .then(saveAuditLogWithData(
                                "USER_CREATED",
                                savedUser.userId(),
                                null,
                                "Usuario creado correctamente: " + savedUser.username(),
                                null,  // oldData = null (nuevo registro)
                                AuditDataSerializer.serializeUser(savedUser)  // newData = datos creados
                        ))
                        .thenReturn(savedUser));
    }

    private Mono<AuthUser> updateExistingUser(
            AuthUser existingUser,
            UpdateUserRequest request,
            String username,
            String email
    ) {
        AuthUser userToUpdate = new AuthUser(
                existingUser.userId(),
                username,
                email,
                existingUser.passwordHash(),
                request.fullName(),
                normalizeNullable(request.phoneNumber()),
                normalizeNullable(request.documentType()),
                normalizeNullable(request.documentNumber()),
                existingUser.userStatus(),
                existingUser.lastLoginAt(),
                existingUser.createdByUserId(),
                request.updatedByUserId(),
                existingUser.createdAt(),
                LocalDateTime.now()
        );

        return authUserRepositoryPort.save(userToUpdate)
                .flatMap(updatedUser -> saveAuditLogWithData(
                        "USER_UPDATED",
                        updatedUser.userId(),
                        request.updatedByUserId(),
                        "Usuario actualizado correctamente: " + updatedUser.username(),
                        AuditDataSerializer.serializeUser(existingUser),
                        AuditDataSerializer.serializeUser(updatedUser)
                ).thenReturn(updatedUser));
    }

    private Mono<AuthUser> changeUserStatus(Integer userId, Integer newStatus) {
        return authUserRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el usuario con id: " + userId)))
                .flatMap(existingUser -> {
                    validateStatusTransition(existingUser, newStatus);

                    AuthUser userToUpdate = new AuthUser(
                            existingUser.userId(),
                            existingUser.username(),
                            existingUser.email(),
                            existingUser.passwordHash(),
                            existingUser.fullName(),
                            existingUser.phoneNumber(),
                            existingUser.documentType(),
                            existingUser.documentNumber(),
                            newStatus,
                            existingUser.lastLoginAt(),
                            existingUser.createdByUserId(),
                            existingUser.updatedByUserId(),
                            existingUser.createdAt(),
                            LocalDateTime.now()
                    );

                    return authUserRepositoryPort.save(userToUpdate)
                            .flatMap(updatedUser -> saveAuditLogWithData(
                                    resolveAuditActionByStatus(newStatus),
                                    updatedUser.userId(),
                                    null,
                                    resolveAuditDescriptionByStatus(newStatus, updatedUser.username()),
                                    AuditDataSerializer.serializeUser(existingUser),
                                    AuditDataSerializer.serializeUser(updatedUser)
                            ).thenReturn(updatedUser));
                });
    }

    private Mono<AuthUserRole> assignRole(AuthUser savedUser, AuthRole role) {
        AuthUserRole userRole = new AuthUserRole(
                null,
                savedUser.userId(),
                role.roleId(),
                null,
                ACTIVE_STATUS,
                null
        );

        return authUserRoleRepositoryPort.save(userRole);
    }

    private Mono<AuthUser> updateLastLogin(AuthUser user) {
        AuthUser userToUpdate = new AuthUser(
                user.userId(),
                user.username(),
                user.email(),
                user.passwordHash(),
                user.fullName(),
                user.phoneNumber(),
                user.documentType(),
                user.documentNumber(),
                user.userStatus(),
                LocalDateTime.now(),
                user.createdByUserId(),
                user.updatedByUserId(),
                user.createdAt(),
                LocalDateTime.now()
        );

        return authUserRepositoryPort.save(userToUpdate);
    }

    private Mono<Void> validateUsernameForUpdate(Integer currentUserId, String username) {
        return authUserRepositoryPort.findByUsername(username)
                .flatMap(user -> {
                    if (!user.userId().equals(currentUserId)) {
                        return Mono.error(new UserAlreadyExistsException("Ya existe otro usuario con username: " + username));
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> validateEmailForUpdate(Integer currentUserId, String email) {
        return authUserRepositoryPort.findByEmail(email)
                .flatMap(user -> {
                    if (!user.userId().equals(currentUserId)) {
                        return Mono.error(new UserAlreadyExistsException("Ya existe otro usuario con email: " + email));
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> validateUsernameDoesNotExist(String username) {
        return authUserRepositoryPort.existsByUsername(username)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new UserAlreadyExistsException("Ya existe un usuario con username: " + username));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateEmailDoesNotExist(String email) {
        return authUserRepositoryPort.existsByEmail(email)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new UserAlreadyExistsException("Ya existe un usuario con email: " + email));
                    }
                    return Mono.empty();
                });
    }

    private Mono<AuthRole> findActiveRole(String roleCode) {
        return authRoleRepositoryPort.findByRoleCode(roleCode)
                .filter(AuthRole::isActive)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró un rol activo con código: " + roleCode)));
    }

    private Mono<Void> validateLoginUser(AuthUser user, String rawPassword) {
        if (INACTIVE_STATUS.equals(user.userStatus())) {
            return Mono.error(new BusinessRuleException(
                    "USER_INACTIVE",
                    "El usuario se encuentra inactivo."
            ));
        }

        if (LOCKED_STATUS.equals(user.userStatus())) {
            return Mono.error(new BusinessRuleException(
                    "USER_LOCKED",
                    "El usuario se encuentra bloqueado."
            ));
        }

        if (!ACTIVE_STATUS.equals(user.userStatus())) {
            return Mono.error(new BusinessRuleException(
                    "USER_INVALID_STATUS",
                    "El usuario no tiene un estado válido para iniciar sesión."
            ));
        }

        if (!passwordEncoderPort.matches(rawPassword, user.passwordHash())) {
            return Mono.error(new InvalidCredentialsException("Usuario o contraseña incorrectos."));
        }

        return Mono.empty();
    }

    private Mono<List<AuthRoleResponse>> getActiveRolesByUser(Integer userId) {
        return authUserRoleRepositoryPort.findByUserId(userId)
                .filter(AuthUserRole::isActive)
                .flatMap(userRole -> authRoleRepositoryPort.findById(userRole.roleId()))
                .filter(AuthRole::isActive)
                .map(AuthRoleResponse::fromDomain)
                .collectList()
                .flatMap(roles -> {
                    if (roles.isEmpty()) {
                        return Mono.error(new BusinessRuleException(
                                "USER_WITHOUT_ACTIVE_ROLE",
                                "El usuario no tiene roles activos asignados."
                        ));
                    }
                    return Mono.just(roles);
                });
    }

    private void validateStatusTransition(AuthUser existingUser, Integer newStatus) {
        if (INACTIVE_STATUS.equals(existingUser.userStatus()) && INACTIVE_STATUS.equals(newStatus)) {
            throw new BusinessRuleException(
                    "USER_ALREADY_INACTIVE",
                    "El usuario ya se encuentra inactivo."
            );
        }

        if (ACTIVE_STATUS.equals(existingUser.userStatus()) && ACTIVE_STATUS.equals(newStatus)) {
            throw new BusinessRuleException(
                    "USER_ALREADY_ACTIVE",
                    "El usuario ya se encuentra activo."
            );
        }

        if (LOCKED_STATUS.equals(existingUser.userStatus()) && LOCKED_STATUS.equals(newStatus)) {
            throw new BusinessRuleException(
                    "USER_ALREADY_LOCKED",
                    "El usuario ya se encuentra bloqueado."
            );
        }
    }

    private boolean isValidUserStatus(Integer status) {
        return INACTIVE_STATUS.equals(status)
                || ACTIVE_STATUS.equals(status)
                || LOCKED_STATUS.equals(status);
    }

    private Mono<AuthAuditLog> saveAuditLogSimple(
            String actionType,
            Integer affectedUserId,
            Integer executedByUserId,
            String actionDescription
    ) {
        // Para acciones sin cambio de datos (login, etc.)
        return saveAuditLogWithData(actionType, affectedUserId, executedByUserId, actionDescription, null, null);
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

    private String resolveAuditActionByStatus(Integer status) {
        if (INACTIVE_STATUS.equals(status)) {
            return "USER_DEACTIVATED";
        }

        if (ACTIVE_STATUS.equals(status)) {
            return "USER_ACTIVATED";
        }

        if (LOCKED_STATUS.equals(status)) {
            return "USER_LOCKED";
        }

        return "USER_STATUS_CHANGED";
    }

    private String resolveAuditDescriptionByStatus(Integer status, String username) {
        if (INACTIVE_STATUS.equals(status)) {
            return "Usuario inactivado correctamente: " + username;
        }

        if (ACTIVE_STATUS.equals(status)) {
            return "Usuario activado correctamente: " + username;
        }

        if (LOCKED_STATUS.equals(status)) {
            return "Usuario bloqueado correctamente: " + username;
        }

        return "Estado de usuario actualizado correctamente: " + username;
    }

    private String normalizeUsername(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private String normalizeRoleCode(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeNullable(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim().toUpperCase();
    }
}