package com.gestoteam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAuditException.class)
    public ResponseEntity<String> handleInvalidAuditException(InvalidAuditException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audit inválido, usuario no autorizado");
    }
    
    // Puedes agregar otros manejadores para diferentes excepciones
}
