package com.gestoteam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar el estado de salud del sistema
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * Endpoint básico de salud
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "GestoTeam Backend");
        response.put("version", "0.0.1");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint detallado de salud
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> response = new HashMap<>();
        
        // Estado general
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "GestoTeam Backend");
        response.put("version", "0.0.1");
        
        // Información del sistema
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("javaVendor", System.getProperty("java.vendor"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("userHome", System.getProperty("user.home"));
        system.put("workingDirectory", System.getProperty("user.dir"));
        
        response.put("system", system);
        
        // Estado de la base de datos (se puede expandir)
        Map<String, Object> database = new HashMap<>();
        database.put("status", "UP");
        database.put("type", "H2");
        database.put("profile", "desktop");
        
        response.put("database", database);
        
        return ResponseEntity.ok(response);
    }
}
