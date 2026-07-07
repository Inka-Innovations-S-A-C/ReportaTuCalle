package com.reportatucalle.shared.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * Representación inmutable de un error en el sistema.
 * Ideal para ser capturado por sistemas de monitoreo y logs.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final List<String> details;
    private final LocalDateTime timestamp;

    public ErrorResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}