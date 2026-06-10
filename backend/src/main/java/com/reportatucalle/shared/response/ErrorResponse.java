package com.reportatucalle.shared.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representación inmutable de un error en el sistema.
 * Ideal para ser capturado por sistemas de monitoreo y logs.
 *
 * @param code        Código de error interno del negocio (ej. "ERR_VALIDATION").
 * @param message     Mensaje legible para el usuario.
 * @param details     Lista de detalles (útil para errores de validación de formularios).
 * @param timestamp   Momento exacto del fallo.
 */
public record ErrorResponse(
        String code,
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
    // Constructor compacto (Java feature) para asignar el timestamp automáticamente
    public ErrorResponse(String code, String message, List<String> details) {
        this(code, message, details, LocalDateTime.now());
    }
}