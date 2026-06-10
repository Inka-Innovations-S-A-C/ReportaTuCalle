package com.reportatucalle.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Envoltorio estandarizado para todas las respuestas de la API.
 * Utilizamos el patrón Factory Method para su instanciación.
 * * @param <T> El tipo de dato del payload o cuerpo de la respuesta.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // Constructor privado para forzar el uso de los Factory Methods
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Factory Method para respuestas exitosas con datos.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Factory Method para respuestas exitosas sin datos (ej. DELETE o POST simples).
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Factory Method para respuestas de error manejadas.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}