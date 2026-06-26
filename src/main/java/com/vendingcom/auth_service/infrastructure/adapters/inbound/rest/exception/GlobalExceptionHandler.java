package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.exception;

import com.vendingcom.auth_service.domain.exception.BusinessRuleException;
import com.vendingcom.auth_service.domain.exception.InvalidCredentialsException;
import com.vendingcom.auth_service.domain.exception.ResourceNotFoundException;
import com.vendingcom.auth_service.domain.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Reglas de negocio que NO son un conflicto (409): se mapean a su código HTTP semántico.
    private static final Set<String> FORBIDDEN_CODES = Set.of(
            "USER_INACTIVE", "USER_LOCKED", "USER_INVALID_STATUS", "USER_NOT_ACTIVE", "USER_TEMPORARILY_LOCKED"
    );
    private static final String RATE_LIMIT_CODE = "PASSWORD_RECOVERY_RATE_LIMIT";

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyExists(UserAlreadyExistsException exception) {
        return build(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessRule(BusinessRuleException exception) {
        return build(resolveBusinessStatus(exception.getCode()), exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidCredentials(InvalidCredentialsException exception) {
        return build(HttpStatus.UNAUTHORIZED, exception.getCode(), exception.getMessage());
    }

    // Errores propios de WebFlux (cuerpo JSON malformado, tipo no soportado, etc.):
    // conservamos su código HTTP original en vez de degradarlos a 500.
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = exception.getReason() != null ? exception.getReason() : status.getReasonPhrase();
        return build(status, "REQUEST_ERROR", message);
    }

    // Red de seguridad: cualquier error no contemplado se registra y se devuelve un 500
    // genérico, sin filtrar stack traces ni detalles internos al cliente.
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpected(Exception exception) {
        log.error("Error inesperado no controlado", exception);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Ocurrió un error inesperado. Inténtelo nuevamente más tarde."
        );
    }

    private HttpStatus resolveBusinessStatus(String code) {
        if (RATE_LIMIT_CODE.equals(code)) {
            return HttpStatus.TOO_MANY_REQUESTS;
        }
        if (FORBIDDEN_CODES.contains(code)) {
            return HttpStatus.FORBIDDEN;
        }
        return HttpStatus.CONFLICT;
    }

    private Mono<ResponseEntity<ErrorResponse>> build(HttpStatus status, String code, String message) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), code, message);
        return Mono.just(ResponseEntity.status(status).body(body));
    }

}