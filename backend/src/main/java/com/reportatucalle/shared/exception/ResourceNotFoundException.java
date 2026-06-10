package com.reportatucalle.shared.exception;

/**
 * Excepción específica cuando un recurso (Reporte, Usuario, Categoría) no existe.
 */
public class ResourceNotFoundException extends BusinessException {

    private static final String DEFAULT_CODE = "ERR_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with identifier '%s' was not found.", resourceName, identifier), DEFAULT_CODE);
    }
}