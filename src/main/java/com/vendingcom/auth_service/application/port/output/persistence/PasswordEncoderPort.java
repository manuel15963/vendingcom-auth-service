package com.vendingcom.auth_service.application.port.output.persistence;

public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}