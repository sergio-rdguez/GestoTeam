package com.gestoteam.service;

import com.gestoteam.dto.request.ExerciseRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.exception.ResourceNotFoundException;
import com.gestoteam.exception.AccessDeniedException;
import com.gestoteam.model.Exercise;
import com.gestoteam.model.User;
import com.gestoteam.repository.ExerciseRepository;
import com.gestoteam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;



    /**
     * Obtiene todos los ejercicios del usuario autenticado sin paginación
     */
    public List<ExerciseResponse> getAllUserExercises() {
        Long userId = getCurrentUserId();
        List<Exercise> exercises = exerciseRepository.findAllByUserIdAndNotDeleted(userId);
        return exercises.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Obtiene ejercicios del usuario autenticado filtrados por categoría
     */
    public List<ExerciseResponse> getUserExercisesByCategory(ExerciseCategory category) {
        Long userId = getCurrentUserId();
        List<Exercise> exercises = exerciseRepository.findByUserIdAndCategoryAndNotDeleted(userId, category);
        return exercises.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Busca ejercicios del usuario autenticado por término de búsqueda
     */
    public List<ExerciseResponse> searchUserExercises(String searchTerm) {
        Long userId = getCurrentUserId();
        List<Exercise> exercises = exerciseRepository.findByUserIdAndTitleContainingAndNotDeleted(userId, searchTerm);
        return exercises.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Obtiene un ejercicio específico del usuario autenticado
     */
    public ExerciseResponse getExerciseById(Long id) {
        Long userId = getCurrentUserId();
        Exercise exercise = exerciseRepository.findByIdAndUserIdAndNotDeleted(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con id: " + id));
        return mapToResponse(exercise);
    }

    /**
     * Crea un nuevo ejercicio para el usuario autenticado
     */
    public ExerciseResponse createExercise(ExerciseRequest exerciseRequest) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Exercise exercise = new Exercise();
        exercise.setTitle(exerciseRequest.getTitle());
        exercise.setDescription(exerciseRequest.getDescription());
        exercise.setTacticalObjectives(exerciseRequest.getTacticalObjectives());
        exercise.setTechnicalObjectives(exerciseRequest.getTechnicalObjectives());
        exercise.setPhysicalObjectives(exerciseRequest.getPhysicalObjectives());
        exercise.setMaterials(exerciseRequest.getMaterials());
        exercise.setCategory(exerciseRequest.getCategory());
        exercise.setUser(user);
        exercise.setDeleted(false);
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now());



        Exercise savedExercise = exerciseRepository.save(exercise);
        log.info("Ejercicio creado: {} por usuario: {}", savedExercise.getId(), userId);
        return mapToResponse(savedExercise);
    }

    /**
     * Actualiza un ejercicio existente del usuario autenticado
     */
    public ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest) {
        Long userId = getCurrentUserId();
        Exercise exercise = exerciseRepository.findByIdAndUserIdAndNotDeleted(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con id: " + id));

        exercise.setTitle(exerciseRequest.getTitle());
        exercise.setDescription(exerciseRequest.getDescription());
        exercise.setTacticalObjectives(exerciseRequest.getTacticalObjectives());
        exercise.setTechnicalObjectives(exerciseRequest.getTechnicalObjectives());
        exercise.setPhysicalObjectives(exerciseRequest.getPhysicalObjectives());
        exercise.setMaterials(exerciseRequest.getMaterials());
        exercise.setCategory(exerciseRequest.getCategory());
        

        
        exercise.setUpdatedAt(LocalDateTime.now());

        Exercise updatedExercise = exerciseRepository.save(exercise);
        log.info("Ejercicio actualizado: {} por usuario: {}", updatedExercise.getId(), userId);
        return mapToResponse(updatedExercise);
    }

    /**
     * Elimina lógicamente un ejercicio del usuario autenticado
     */
    public void deleteExercise(Long id) {
        Long userId = getCurrentUserId();
        Exercise exercise = exerciseRepository.findByIdAndUserIdAndNotDeleted(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con id: " + id));

        exercise.setDeleted(true);
        exercise.setUpdatedAt(LocalDateTime.now());
        exerciseRepository.save(exercise);
        log.info("Ejercicio eliminado: {} por usuario: {}", exercise.getId(), userId);
    }

    /**
     * Verifica si un ejercicio pertenece al usuario autenticado
     */
    public boolean exerciseBelongsToUser(Long exerciseId) {
        Long userId = getCurrentUserId();
        return exerciseRepository.findByIdAndUserIdAndNotDeleted(exerciseId, userId).isPresent();
    }



    /**
     * Obtiene el ID del usuario autenticado desde el contexto de seguridad
     */
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return user.getId();
    }

    /**
     * Mapea la entidad Exercise a ExerciseResponse
     */
    private ExerciseResponse mapToResponse(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setTitle(exercise.getTitle());
        response.setDescription(exercise.getDescription());
        response.setTacticalObjectives(exercise.getTacticalObjectives());
        response.setTechnicalObjectives(exercise.getTechnicalObjectives());
        response.setPhysicalObjectives(exercise.getPhysicalObjectives());
        response.setMaterials(exercise.getMaterials());
        response.setCategory(exercise.getCategory());
        response.setUserId(exercise.getUser().getId());
        response.setCreatedAt(exercise.getCreatedAt());
        response.setUpdatedAt(exercise.getUpdatedAt());
        
        // Incluir información de la imagen del ejercicio si existe
        if (exercise.getImagePath() != null && !exercise.getImagePath().isBlank()) {
            response.setImageUrl("/api/files/" + exercise.getImagePath());
        }
        
        return response;
    }
}
