package com.vendingcom.auth_service.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    private final String code;

    public InvalidCredentialsException(String message) {
        super(message);
        this.code = "INVALID_CREDENTIALS";
    }

    public String getCode() {
        return code;
    }
}