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

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Manejo de InvalidAuditException
    @ExceptionHandler(InvalidAuditException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAuditException(InvalidAuditException ex) {
        Map<String, Object> response = Map.of(
                "message", "Audit inválido, usuario no autorizado"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Manejo de excepciones de validación utilizando GestoServiceException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        // Recolectar errores de validación
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        // Crear un mensaje consolidado para la excepción
        String consolidatedMessage = "Errores de validación: " + validationErrors;

        // Lanza GestoServiceException con el mensaje de error consolidado
        throw new GestoServiceException(consolidatedMessage);
    }

    // Manejo de excepciones personalizadas (GestoServiceException)
    @ExceptionHandler(GestoServiceException.class)
    public ResponseEntity<Map<String, Object>> handleGestoServiceException(GestoServiceException ex) {
        Map<String, Object> response = Map.of(
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error inesperado: ", ex);
        Map<String, Object> response = Map.of(
                "message", "Ocurrió un error inesperado"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
