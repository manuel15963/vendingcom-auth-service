package com.vendingcom.auth_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        // La contraseña actual se valida solo como obligatoria: puede ser una clave
        // antigua que no cumpla la política nueva, y aun así debe poder cambiarla.
        @NotBlank(message = "La contraseña actual es obligatoria")
        @Size(max = 100, message = "La contraseña actual no debe superar 100 caracteres")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,100}$",
                message = "La nueva contraseña debe incluir al menos una letra y un número"
        )
        String newPassword
) {
}