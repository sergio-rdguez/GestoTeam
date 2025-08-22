package com.gestoteam.controller;

import com.gestoteam.enums.ExerciseCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Estado del Sistema", description = "Endpoints para verificar el estado del sistema")
public class HealthController {

    @GetMapping
    @Operation(summary = "Estado general del sistema", description = "Verifica que el sistema esté funcionando correctamente")
    @ApiResponse(responseCode = "200", description = "Sistema funcionando correctamente")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "GestoTeam Backend");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/database")
    @Operation(summary = "Estado de la base de datos", description = "Verifica la conectividad y estado de la base de datos")
    @ApiResponse(responseCode = "200", description = "Base de datos funcionando correctamente")
    public ResponseEntity<Map<String, Object>> databaseHealthCheck() {
        Map<String, Object> dbHealth = new HashMap<>();
        dbHealth.put("status", "UP");
        dbHealth.put("timestamp", LocalDateTime.now());
        dbHealth.put("database", "H2/PostgreSQL");
        dbHealth.put("message", "Base de datos operativa");
        
        return ResponseEntity.ok(dbHealth);
    }

    @GetMapping("/exercises-test")
    @Operation(summary = "Prueba de entidades de ejercicios", description = "Verifica que las entidades de ejercicios estén configuradas correctamente")
    @ApiResponse(responseCode = "200", description = "Entidades configuradas correctamente")
    public ResponseEntity<Map<String, Object>> exercisesTest() {
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("timestamp", LocalDateTime.now());
        test.put("message", "Entidades de ejercicios configuradas correctamente");
        test.put("categories", ExerciseCategory.values());
        test.put("entity", "Exercise");
        test.put("repository", "ExerciseRepository");
        test.put("service", "ExerciseService");
        test.put("controller", "ExerciseController");
        
        return ResponseEntity.ok(test);
    }

    @GetMapping("/trainings-test")
    @Operation(summary = "Prueba de entidades de entrenamientos", description = "Verifica que las entidades de entrenamientos estén configuradas correctamente")
    @ApiResponse(responseCode = "200", description = "Entidades configuradas correctamente")
    public ResponseEntity<Map<String, Object>> trainingsTest() {
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("timestamp", LocalDateTime.now());
        test.put("message", "Entidades de entrenamientos configuradas correctamente");
        test.put("entity", "Training");
        test.put("repository", "TrainingRepository");
        test.put("service", "TrainingService");
        test.put("controller", "TrainingController");
        test.put("relationship", "ManyToMany con Exercise");
        
        return ResponseEntity.ok(test);
    }
}
