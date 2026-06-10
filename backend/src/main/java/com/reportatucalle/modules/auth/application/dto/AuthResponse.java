package com.reportatucalle.modules.auth.application.dto;

/**
 * Record inmutable que se devuelve al cliente tras un registro o login exitoso.
 * Solo contiene los datos puros (Token), delegando los mensajes de éxito al Controlador.
 */
public record AuthResponse(
        String token
) {}