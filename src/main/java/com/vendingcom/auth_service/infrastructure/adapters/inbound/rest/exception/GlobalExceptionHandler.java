package com.vendingcom.auth_service.infrastructure.adapters.inbound.rest.exception;

import com.vendingcom.auth_service.domain.exception.BusinessRuleException;
import com.vendingcom.auth_service.domain.exception.InvalidCredentialsException;
import com.vendingcom.auth_service.domain.exception.ResourceNotFoundException;
import com.vendingcom.auth_service.domain.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getCode(),
                exception.getMessage()
        ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException exception) {
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getCode(),
                exception.getMessage()
        ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorResponse> handleValidation(WebExchangeBindException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message
        ));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ErrorResponse> handleBusinessRule(BusinessRuleException exception) {
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getCode(),
                exception.getMessage()
        ));
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        return Mono.just(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getCode(),
                exception.getMessage()
        ));
    }

}