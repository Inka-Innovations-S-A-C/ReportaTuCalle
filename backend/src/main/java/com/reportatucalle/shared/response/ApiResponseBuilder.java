package com.reportatucalle.shared.response;

import java.time.LocalDateTime;

/**
 * Patrón Builder puro para la construcción de ApiResponse.
 * Demostración de Builder manual.
 */
public class ApiResponseBuilder<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponseBuilder<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiResponse<T> build() {
        if (success) {
            return ApiResponse.success(data, message);
        } else {
            return ApiResponse.error(message);
        }
    }
    
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }
}
