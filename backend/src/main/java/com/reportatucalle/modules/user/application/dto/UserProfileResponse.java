package com.reportatucalle.modules.user.application.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida para el perfil del usuario.
 *
 * Combina datos de UserProfile (nombre, teléfono) con datos de AuthAccount (email, rol).
 * El frontend recibe todo lo necesario en una sola respuesta.
 */
public record UserProfileResponse(
        Long id,
        String fullName,
        String firstName,
        String lastName,
        String email,
        String role,
        String phone,
        LocalDateTime createdAt
) {}