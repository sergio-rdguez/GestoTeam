package com.gestoteam.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Manejo de excepciones de validación de Spring (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        String errorMessage = "Errores de validación: " + errors;
        log.warn(errorMessage);

        Map<String, Object> response = Map.of("message", errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones personalizadas de nuestra lógica de negocio
    @ExceptionHandler(GestoServiceException.class)
    public ResponseEntity<Map<String, Object>> handleGestoServiceException(GestoServiceException ex) {
        Map<String, Object> response = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejo de cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error inesperado: ", ex);
        Map<String, Object> response = Map.of("message", "Ocurrió un error inesperado en el servidor.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}