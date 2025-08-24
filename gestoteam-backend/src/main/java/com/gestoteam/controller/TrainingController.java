package com.gestoteam.controller;

import com.gestoteam.dto.request.TrainingRequest;
import com.gestoteam.dto.request.TrainingAttendanceRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.dto.response.TrainingResponse;
import com.gestoteam.dto.response.TrainingAttendanceResponse;
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
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Entrenamientos", description = "API para las operaciones CRUD de entrenamientos y su relación con ejercicios")
public class TrainingController {

    private final TrainingService trainingService;

    /**
     * Obtiene todos los entrenamientos del usuario autenticado
     */
    @GetMapping
    @Operation(summary = "Obtener todos los entrenamientos", description = "Devuelve todos los entrenamientos no eliminados que pertenecen al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de entrenamientos obtenida con éxito")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<TrainingResponse>> getUserTrainings() {
        List<TrainingResponse> trainings = trainingService.getUserTrainings();
        return ResponseEntity.ok(trainings);
    }

    /**
     * Obtiene todos los entrenamientos de un equipo específico
     */
    @GetMapping("/team/{teamId}")
    @Operation(summary = "Obtener entrenamientos por equipo", description = "Devuelve todos los entrenamientos no eliminados de un equipo específico que pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de entrenamientos del equipo obtenida con éxito")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    @ApiResponse(responseCode = "404", description = "Equipo no encontrado o no pertenece al usuario")
    public ResponseEntity<List<TrainingResponse>> getTeamTrainings(@PathVariable Long teamId) {
        List<TrainingResponse> trainings = trainingService.getTeamTrainings(teamId);
        return ResponseEntity.ok(trainings);
    }

    /**
     * Obtiene un entrenamiento específico del usuario autenticado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrenamiento por ID", description = "Devuelve los detalles de un entrenamiento específico si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Entrenamiento encontrado")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<TrainingResponse> getTrainingById(@PathVariable Long id) {
        TrainingResponse training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(training);
    }

    /**
     * Crea un nuevo entrenamiento para el usuario autenticado
     */
    @PostMapping
    @Operation(summary = "Crear nuevo entrenamiento", description = "Crea un nuevo entrenamiento y lo asocia al usuario autenticado. Opcionalmente puede incluir ejercicios existentes.")
    @ApiResponse(responseCode = "201", description = "Entrenamiento creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    @ApiResponse(responseCode = "404", description = "Ejercicios no encontrados (si se especifican)")
    public ResponseEntity<TrainingResponse> createTraining(@Valid @RequestBody TrainingRequest trainingRequest) {
        TrainingResponse createdTraining = trainingService.createTraining(trainingRequest);
        log.info("Entrenamiento creado con ID: {}", createdTraining.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTraining);
    }

    /**
     * Actualiza un entrenamiento existente del usuario autenticado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar entrenamiento existente", description = "Actualiza los datos de un entrenamiento existente si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Entrenamiento actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<TrainingResponse> updateTraining(
            @PathVariable Long id, 
            @Valid @RequestBody TrainingRequest trainingRequest) {
        
        TrainingResponse updatedTraining = trainingService.updateTraining(id, trainingRequest);
        log.info("Entrenamiento actualizado con ID: {}", updatedTraining.getId());
        return ResponseEntity.ok(updatedTraining);
    }

    /**
     * Elimina lógicamente un entrenamiento del usuario autenticado
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrenamiento (borrado lógico)", description = "Marca un entrenamiento como eliminado si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "204", description = "Entrenamiento eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        log.info("Entrenamiento eliminado con ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Añade ejercicios a un entrenamiento existente
     */
    @PostMapping("/{id}/exercises")
    @Operation(summary = "Añadir ejercicios al entrenamiento", description = "Añade ejercicios existentes a un entrenamiento específico.")
    @ApiResponse(responseCode = "200", description = "Ejercicios añadidos con éxito")
    @ApiResponse(responseCode = "400", description = "Lista de ejercicios inválida")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o ejercicios no pertenecen al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<TrainingResponse> addExercisesToTraining(
            @PathVariable Long id, 
            @RequestBody List<Long> exerciseIds) {
        
        TrainingResponse updatedTraining = trainingService.addExercisesToTraining(id, exerciseIds);
        log.info("Ejercicios añadidos al entrenamiento: {}", id);
        return ResponseEntity.ok(updatedTraining);
    }

    /**
     * Elimina ejercicios de un entrenamiento existente
     */
    @DeleteMapping("/{id}/exercises")
    @Operation(summary = "Eliminar ejercicios del entrenamiento", description = "Elimina ejercicios específicos de un entrenamiento existente.")
    @ApiResponse(responseCode = "200", description = "Ejercicios eliminados con éxito")
    @ApiResponse(responseCode = "400", description = "Lista de ejercicios inválida")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<TrainingResponse> removeExercisesFromTraining(
            @PathVariable Long id, 
            @RequestBody List<Long> exerciseIds) {
        
        TrainingResponse updatedTraining = trainingService.removeExercisesFromTraining(id, exerciseIds);
        log.info("Ejercicios eliminados del entrenamiento: {}", id);
        return ResponseEntity.ok(updatedTraining);
    }

    /**
     * Obtiene todos los ejercicios de un entrenamiento
     */
    @GetMapping("/{id}/exercises")
    @Operation(summary = "Obtener ejercicios del entrenamiento", description = "Devuelve todos los ejercicios asociados a un entrenamiento específico.")
    @ApiResponse(responseCode = "200", description = "Lista de ejercicios obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<ExerciseResponse>> getTrainingExercises(@PathVariable Long id) {
        List<ExerciseResponse> exercises = trainingService.getTrainingExercises(id);
        return ResponseEntity.ok(exercises);
    }

    /**
     * Obtiene la lista de asistencia de un entrenamiento
     */
    @GetMapping("/{id}/attendance")
    @Operation(summary = "Obtener lista de asistencia", description = "Devuelve la lista de asistencia de todos los jugadores del equipo para un entrenamiento específico.")
    @ApiResponse(responseCode = "200", description = "Lista de asistencia obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado o no pertenece al usuario")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<List<TrainingAttendanceResponse>> getTrainingAttendance(@PathVariable Long id) {
        List<TrainingAttendanceResponse> attendance = trainingService.getTrainingAttendance(id);
        return ResponseEntity.ok(attendance);
    }

    /**
     * Actualiza la asistencia de un jugador en un entrenamiento
     */
    @PutMapping("/{trainingId}/attendance/{playerId}")
    @Operation(summary = "Actualizar asistencia de jugador", description = "Actualiza el estado de asistencia de un jugador específico en un entrenamiento.")
    @ApiResponse(responseCode = "200", description = "Asistencia actualizada con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Entrenamiento o jugador no encontrado")
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    public ResponseEntity<TrainingAttendanceResponse> updatePlayerAttendance(
            @PathVariable Long trainingId,
            @PathVariable Long playerId,
            @Valid @RequestBody TrainingAttendanceRequest request) {
        
        TrainingAttendanceResponse attendance = trainingService.updatePlayerAttendance(trainingId, playerId, request);
        log.info("Asistencia actualizada para jugador: {} en entrenamiento: {}", playerId, trainingId);
        return ResponseEntity.ok(attendance);
    }
}
