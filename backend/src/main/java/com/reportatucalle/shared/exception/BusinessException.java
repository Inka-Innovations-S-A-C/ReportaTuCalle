package com.reportatucalle.shared.exception;

import lombok.Getter;

/**
 * Excepción base para reglas de negocio violadas.
 * Al heredar de RuntimeException, no obligamos a llenar el código de bloques try-catch (Fail-fast).
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructor estricto: Permite enviar un mensaje y un código de error específico 
     * (Ej: "USER_NOT_FOUND", "AUTH_001") muy útil para que el Frontend traduzca errores.
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor flexible: Permite lanzar la excepción solo con un mensaje.
     * Asigna el código genérico BUSINESS_ERROR por defecto.
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
}