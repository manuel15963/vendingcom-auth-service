package com.vendingcom.auth_service.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private final String code;

    public UserAlreadyExistsException(String message) {
        super(message);
        this.code = "USER_ALREADY_EXISTS";
    }

    public String getCode() {
        return code;
    }
}