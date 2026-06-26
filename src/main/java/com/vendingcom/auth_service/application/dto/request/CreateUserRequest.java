package com.vendingcom.auth_service.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "El username es obligatorio")
        @Size(max = 50, message = "El username no debe superar 50 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no debe superar 120 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,100}$",
                message = "La contraseña debe incluir al menos una letra y un número"
        )
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 150, message = "El nombre completo no debe superar 150 caracteres")
        String fullName,

        @Pattern(regexp = "^$|^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
        String phoneNumber,

        @Pattern(regexp = "^$|^DNI$", message = "El tipo de documento debe ser DNI")
        String documentType,

        @Pattern(regexp = "^$|^[0-9]{8}$", message = "El DNI debe tener 8 dígitos")
        String documentNumber,

        @NotBlank(message = "El rol es obligatorio")
        String roleCode
) {
}