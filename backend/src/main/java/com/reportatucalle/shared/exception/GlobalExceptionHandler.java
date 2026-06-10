package com.reportatucalle.shared.exception;

import com.reportatucalle.shared.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Interceptor global de excepciones.
 * Garantiza que el frontend siempre reciba un formato JSON predecible,
 * incluso cuando ocurren errores inesperados (evitando fugas de stacktraces).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage(), List.of());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage(), List.of());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // HTTP 409 o 422 dependiendo de la convención
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Extraemos los errores de los DTOs que usan @Valid / @NotNull / @Size
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Validation error on request: {}", validationErrors);
        ErrorResponse response = new ErrorResponse("ERR_VALIDATION", "Invalid request parameters", validationErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception ex) {
        log.error("Unhandled exception occurred", ex); // Aquí sí logueamos el stacktrace internamente
        ErrorResponse response = new ErrorResponse("ERR_INTERNAL_SERVER", "An unexpected error occurred. Please contact support.", List.of());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}