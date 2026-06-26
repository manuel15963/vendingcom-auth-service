package com.vendingcom.auth_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Petición usada por un ADMIN para restablecer la contraseña de otro usuario.
 * A diferencia de {@link ChangePasswordRequest}, NO requiere la contraseña actual,
 * porque el administrador no la conoce.
 */
public record AdminResetPasswordRequest(

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,100}$",
                message = "La nueva contraseña debe incluir al menos una letra y un número"
        )
        String newPassword
) {
}
