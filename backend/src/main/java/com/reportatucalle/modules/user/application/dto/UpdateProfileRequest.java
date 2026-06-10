package com.reportatucalle.modules.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para actualizar el perfil del usuario.
 * Solo los campos editables — email y rol no se pueden cambiar aquí.
 */
public record UpdateProfileRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        String lastName,

        @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Formato de teléfono inválido")
        String phone
) {}