package com.gestoteam.controller;

import com.gestoteam.dto.request.ExerciseRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.service.ExerciseService;
import com.gestoteam.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Ejercicios", description = "API para las operaciones CRUD de ejercicios y pizarra táctica")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final TrainingService trainingService;

    /**
     * Obtiene todos los ejercicios del usuario autenticado
     */
    @GetMapping
    @Operation(summary = "Obtener todos los ejercicios", description = "Devuelve todos los ejercicios del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de ejercicios obtenida con éxito")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<ExerciseResponse>> getUserExercises() {
        List<ExerciseResponse> exercises = exerciseService.getAllUserExercises();
        return ResponseEntity.ok(exercises);
    }



    /**
     * Obtiene ejercicios del usuario autenticado filtrados por categoría
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar ejercicios por categoría", description = "Devuelve ejercicios del usuario autenticado filtrados por una categoría específica.")
    @ApiResponse(responseCode = "200", description = "Ejercicios filtrados obtenidos con éxito")
    @ApiResponse(responseCode = "400", description = "Categoría inválida")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<ExerciseResponse>> getUserExercisesByCategory(
            @PathVariable ExerciseCategory category) {
        
        List<ExerciseResponse> exercises = exerciseService.getUserExercisesByCategory(category);
        return ResponseEntity.ok(exercises);
    }

    /**
     * Busca ejercicios del usuario autenticado por término de búsqueda
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar ejercicios por título", description = "Busca ejercicios del usuario autenticado que contengan el término especificado en el título.")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito")
    @ApiResponse(responseCode = "400", description = "Término de búsqueda requerido")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<ExerciseResponse>> searchUserExercises(
            @RequestParam String q) {
        
        List<ExerciseResponse> exercises = exerciseService.searchUserExercises(q);
        return ResponseEntity.ok(exercises);
    }

    /**
     * Obtiene un ejercicio específico del usuario autenticado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejercicio por ID", description = "Devuelve los detalles de un ejercicio específico si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Ejercicio encontrado")
    @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable Long id) {
        ExerciseResponse exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exercise);
    }

    /**
     * Crea un nuevo ejercicio para el usuario autenticado
     */
    @PostMapping
    @Operation(summary = "Crear nuevo ejercicio", description = "Crea un nuevo ejercicio y lo asocia al usuario autenticado.")
    @ApiResponse(responseCode = "201", description = "Ejercicio creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<ExerciseResponse> createExercise(@Valid @RequestBody ExerciseRequest exerciseRequest) {
        ExerciseResponse createdExercise = exerciseService.createExercise(exerciseRequest);
        log.info("Ejercicio creado con ID: {}", createdExercise.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
    }

    /**
     * Crea un nuevo ejercicio "al vuelo" y lo vincula automáticamente a un entrenamiento
     */
    @PostMapping("/create-and-link")
    @Operation(summary = "Crear ejercicio y vincular a entrenamiento", description = "Crea un nuevo ejercicio y lo vincula automáticamente a un entrenamiento existente.")
    @ApiResponse(responseCode = "201", description = "Ejercicio creado y vinculado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado")
    public ResponseEntity<ExerciseResponse> createExerciseAndLinkToTraining(
            @Valid @RequestBody ExerciseRequest exerciseRequest,
            @RequestParam Long trainingId) {
        
        // Crear el ejercicio
        ExerciseResponse createdExercise = exerciseService.createExercise(exerciseRequest);
        
        // Vincular automáticamente al entrenamiento
        trainingService.addExercisesToTraining(trainingId, List.of(createdExercise.getId()));
        
        log.info("Ejercicio creado y vinculado al entrenamiento: {} - Entrenamiento: {}", 
                createdExercise.getId(), trainingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
    }

    /**
     * Actualiza un ejercicio existente del usuario autenticado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ejercicio existente", description = "Actualiza los datos de un ejercicio existente si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Ejercicio actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Long id, 
            @Valid @RequestBody ExerciseRequest exerciseRequest) {
        
        ExerciseResponse updatedExercise = exerciseService.updateExercise(id, exerciseRequest);
        log.info("Ejercicio actualizado con ID: {}", updatedExercise.getId());
        return ResponseEntity.ok(updatedExercise);
    }

    /**
     * Elimina lógicamente un ejercicio del usuario autenticado
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ejercicio", description = "Elimina lógicamente un ejercicio del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Ejercicio eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<String> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok("Ejercicio eliminado con éxito");
    }



    /**
     * Obtiene todas las categorías de ejercicios disponibles
     */
    @GetMapping("/categories")
    @Operation(summary = "Obtener categorías de ejercicios", description = "Devuelve todas las categorías de ejercicios disponibles en el sistema.")
    @ApiResponse(responseCode = "200", description = "Categorías obtenidas con éxito")
    public ResponseEntity<ExerciseCategory[]> getExerciseCategories() {
        ExerciseCategory[] categories = ExerciseCategory.values();
        return ResponseEntity.ok(categories);
    }
}
