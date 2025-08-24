package com.gestoteam.service;

import com.gestoteam.dto.request.TrainingRequest;
import com.gestoteam.dto.request.TrainingAttendanceRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.dto.response.TrainingResponse;
import com.gestoteam.dto.response.TrainingAttendanceResponse;
import com.gestoteam.enums.AttendanceStatus;
import com.gestoteam.exception.ResourceNotFoundException;
import com.gestoteam.exception.AccessDeniedException;
import com.gestoteam.model.Exercise;
import com.gestoteam.model.Training;
import com.gestoteam.model.TrainingAttendance;
import com.gestoteam.model.User;
import com.gestoteam.model.Team;
import com.gestoteam.model.Player;
import com.gestoteam.repository.ExerciseRepository;
import com.gestoteam.repository.TrainingRepository;
import com.gestoteam.repository.TrainingAttendanceRepository;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.repository.PlayerRepository;
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
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TrainingAttendanceRepository attendanceRepository;

    /**
     * Obtiene todos los entrenamientos del usuario autenticado
     */
    public List<TrainingResponse> getUserTrainings() {
        Long userId = getCurrentUserId();
        List<Training> trainings = trainingRepository.findByUserIdAndDeletedFalse(userId);
        return trainings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Obtiene todos los entrenamientos de un equipo específico
     */
    public List<TrainingResponse> getTeamTrainings(Long teamId) {
        Long userId = getCurrentUserId();
        
        // Verificar que el equipo existe y pertenece al usuario
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        
        if (!team.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("No tienes acceso a este equipo");
        }
        
        List<Training> trainings = trainingRepository.findByTeamIdAndDeletedFalse(teamId);
        return trainings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Obtiene un entrenamiento específico del usuario autenticado
     */
    public TrainingResponse getTrainingById(Long id) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + id));
        return mapToResponse(training);
    }

    /**
     * Crea un nuevo entrenamiento para el usuario autenticado
     */
    public TrainingResponse createTraining(TrainingRequest trainingRequest) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar que el equipo existe y pertenece al usuario
        Team team = teamRepository.findById(trainingRequest.getTeamId())
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado"));
        
        if (!team.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("No tienes acceso a este equipo");
        }

        Training training = new Training();
        training.setTitle(trainingRequest.getTitle());
        training.setDate(trainingRequest.getDate());
        training.setLocation(trainingRequest.getLocation());
        training.setTrainingType(trainingRequest.getTrainingType());
        training.setObservations(trainingRequest.getObservations());
        training.setUser(user);
        training.setTeam(team);
        training.setDeleted(false);
        
        // Calcular número de sesión automáticamente
        Integer sessionNumber = calculateSessionNumber(team.getId(), trainingRequest.getDate());
        training.setSessionNumber(sessionNumber);
        
        training.setCreatedAt(LocalDateTime.now());
        training.setUpdatedAt(LocalDateTime.now());

        // Añadir ejercicios si se proporcionan
        if (trainingRequest.getExerciseIds() != null && !trainingRequest.getExerciseIds().isEmpty()) {
            List<Exercise> exercises = exerciseRepository.findAllById(trainingRequest.getExerciseIds());
            
            // Verificar que todos los ejercicios pertenecen al usuario
            exercises.forEach(exercise -> {
                if (!exercise.getUser().getId().equals(userId)) {
                    throw new ResourceNotFoundException("Ejercicio no encontrado o no pertenece al usuario");
                }
            });
            
            training.setExercises(exercises);
        }

        Training savedTraining = trainingRepository.save(training);
        
        // Crear lista de asistencia inicial con todos los jugadores del equipo
        createInitialAttendanceList(savedTraining, team);
        
        log.info("Entrenamiento creado: {} por usuario: {} para equipo: {}", savedTraining.getId(), userId, team.getId());
        return mapToResponse(savedTraining);
    }

    /**
     * Actualiza un entrenamiento existente del usuario autenticado
     */
    public TrainingResponse updateTraining(Long id, TrainingRequest trainingRequest) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + id));

        training.setTitle(trainingRequest.getTitle());
        training.setDate(trainingRequest.getDate());
        training.setLocation(trainingRequest.getLocation());
        training.setTrainingType(trainingRequest.getTrainingType());
        training.setObservations(trainingRequest.getObservations());
        training.setUpdatedAt(LocalDateTime.now());

        // Actualizar ejercicios si se proporcionan
        if (trainingRequest.getExerciseIds() != null) {
            List<Exercise> exercises = exerciseRepository.findAllById(trainingRequest.getExerciseIds());
            
            // Verificar que todos los ejercicios pertenecen al usuario
            exercises.forEach(exercise -> {
                if (!exercise.getUser().getId().equals(userId)) {
                    throw new ResourceNotFoundException("Ejercicio no encontrado o no pertenece al usuario");
                }
            });
            
            training.setExercises(exercises);
        }

        Training updatedTraining = trainingRepository.save(training);
        log.info("Entrenamiento actualizado: {} por usuario: {}", updatedTraining.getId(), userId);
        return mapToResponse(updatedTraining);
    }

    /**
     * Elimina lógicamente un entrenamiento del usuario autenticado
     */
    public void deleteTraining(Long id) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + id));

        training.setDeleted(true);
        trainingRepository.save(training);
        log.info("Entrenamiento eliminado: {} por usuario: {}", training.getId(), userId);
    }

    /**
     * Añade ejercicios a un entrenamiento existente
     */
    public TrainingResponse addExercisesToTraining(Long trainingId, List<Long> exerciseIds) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(trainingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + trainingId));

        List<Exercise> exercises = exerciseRepository.findAllById(exerciseIds);
        
        // Verificar que todos los ejercicios pertenecen al usuario
        exercises.forEach(exercise -> {
            if (!exercise.getUser().getId().equals(userId)) {
                throw new ResourceNotFoundException("Ejercicio no encontrado o no pertenece al usuario");
            }
        });

        training.getExercises().addAll(exercises);
        training.setUpdatedAt(LocalDateTime.now());

        Training updatedTraining = trainingRepository.save(training);
        log.info("Ejercicios añadidos al entrenamiento: {} por usuario: {}", trainingId, userId);
        return mapToResponse(updatedTraining);
    }

    /**
     * Elimina ejercicios de un entrenamiento existente
     */
    public TrainingResponse removeExercisesFromTraining(Long trainingId, List<Long> exerciseIds) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(trainingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + trainingId));

        training.getExercises().removeIf(exercise -> exerciseIds.contains(exercise.getId()));
        training.setUpdatedAt(LocalDateTime.now());

        Training updatedTraining = trainingRepository.save(training);
        log.info("Ejercicios eliminados del entrenamiento: {} por usuario: {}", trainingId, userId);
        return mapToResponse(updatedTraining);
    }

    /**
     * Obtiene todos los ejercicios de un entrenamiento
     */
    public List<ExerciseResponse> getTrainingExercises(Long trainingId) {
        Long userId = getCurrentUserId();
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(trainingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + trainingId));

        return training.getExercises().stream()
            .map(this::mapExerciseToResponse)
            .collect(Collectors.toList());
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
     * Mapea la entidad Training a TrainingResponse
     */
    private TrainingResponse mapToResponse(Training training) {
        TrainingResponse response = new TrainingResponse();
        response.setId(training.getId());
        response.setTitle(training.getTitle());
        response.setDate(training.getDate());
        response.setLocation(training.getLocation());
        response.setTrainingType(training.getTrainingType());
        response.setSessionNumber(training.getSessionNumber());
        response.setUserId(training.getUser().getId());
        response.setTeamId(training.getTeam().getId());
        response.setTeamName(training.getTeam().getName());
        response.setObservations(training.getObservations());
        response.setCreatedAt(training.getCreatedAt());
        response.setUpdatedAt(training.getUpdatedAt());
        
        // Mapear ejercicios si existen
        if (training.getExercises() != null) {
            response.setExercises(training.getExercises().stream()
                .map(this::mapExerciseToResponse)
                .collect(Collectors.toList()));
        }
        
        // Mapear asistencia si existe
        if (training.getAttendance() != null) {
            response.setAttendance(training.getAttendance().stream()
                .filter(att -> !att.getDeleted())
                .map(this::mapAttendanceToResponse)
                .collect(Collectors.toList()));
        }
        
        return response;
    }

    /**
     * Mapea la entidad Exercise a ExerciseResponse
     */
    private ExerciseResponse mapExerciseToResponse(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setTitle(exercise.getTitle());
        response.setDescription(exercise.getDescription());
        response.setTacticalObjectives(exercise.getTacticalObjectives());
        response.setTechnicalObjectives(exercise.getTechnicalObjectives());
        response.setPhysicalObjectives(exercise.getPhysicalObjectives());
        response.setMaterials(exercise.getMaterials());
        response.setCategory(exercise.getCategory());
        // Campo drawingData removido - ahora se usan diagramas tácticos separados
        response.setUserId(exercise.getUser().getId());
        response.setCreatedAt(exercise.getCreatedAt());
        response.setUpdatedAt(exercise.getUpdatedAt());
        return response;
    }

    /**
     * Crea la lista inicial de asistencia con todos los jugadores del equipo
     */
    private void createInitialAttendanceList(Training training, Team team) {
        List<Player> players = team.getPlayers().stream()
            .filter(player -> !player.getDeleted())
            .collect(Collectors.toList());
        
        List<TrainingAttendance> attendanceList = players.stream()
            .map(player -> {
                TrainingAttendance attendance = new TrainingAttendance();
                attendance.setTraining(training);
                attendance.setPlayer(player);
                attendance.setStatus(AttendanceStatus.ABSENT); // Por defecto ausente
                attendance.setDeleted(false);
                attendance.setCreatedAt(LocalDateTime.now());
                attendance.setUpdatedAt(LocalDateTime.now());
                return attendance;
            })
            .collect(Collectors.toList());
        
        attendanceRepository.saveAll(attendanceList);
        log.info("Lista de asistencia creada para entrenamiento: {} con {} jugadores", training.getId(), players.size());
    }

    /**
     * Actualiza la asistencia de un jugador en un entrenamiento
     */
    public TrainingAttendanceResponse updatePlayerAttendance(Long trainingId, Long playerId, TrainingAttendanceRequest request) {
        Long userId = getCurrentUserId();
        
        // Verificar que el entrenamiento existe y pertenece al usuario
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(trainingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + trainingId));
        
        // Verificar que el jugador pertenece al equipo del entrenamiento
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado"));
        
        if (!player.getTeam().getId().equals(training.getTeam().getId())) {
            throw new ResourceNotFoundException("El jugador no pertenece al equipo del entrenamiento");
        }
        
        // Buscar o crear registro de asistencia
        TrainingAttendance attendance = attendanceRepository
            .findByTrainingIdAndPlayerIdAndNotDeleted(trainingId, playerId)
            .orElseGet(() -> {
                TrainingAttendance newAttendance = new TrainingAttendance();
                newAttendance.setTraining(training);
                newAttendance.setPlayer(player);
                newAttendance.setDeleted(false);
                newAttendance.setCreatedAt(LocalDateTime.now());
                return newAttendance;
            });
        
        attendance.setStatus(request.getStatus());
        attendance.setNotes(request.getNotes());
        attendance.setUpdatedAt(LocalDateTime.now());
        
        TrainingAttendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Asistencia actualizada para jugador: {} en entrenamiento: {}", playerId, trainingId);
        
        return mapAttendanceToResponse(savedAttendance);
    }

    /**
     * Obtiene la lista de asistencia de un entrenamiento
     */
    public List<TrainingAttendanceResponse> getTrainingAttendance(Long trainingId) {
        Long userId = getCurrentUserId();
        
        Training training = trainingRepository.findByIdAndUserIdAndDeletedFalse(trainingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con id: " + trainingId));
        
        List<TrainingAttendance> attendanceList = attendanceRepository.findByTrainingIdAndNotDeleted(trainingId);
        
        return attendanceList.stream()
            .map(this::mapAttendanceToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Mapea la entidad TrainingAttendance a TrainingAttendanceResponse
     */
    private TrainingAttendanceResponse mapAttendanceToResponse(TrainingAttendance attendance) {
        TrainingAttendanceResponse response = new TrainingAttendanceResponse();
        response.setId(attendance.getId());
        response.setPlayerId(attendance.getPlayer().getId());
        response.setPlayerName(attendance.getPlayer().getName());
        response.setPlayerSurname(attendance.getPlayer().getSurnameFirst());
        response.setPlayerFullName(attendance.getPlayer().getFullName());
        response.setPosition(attendance.getPlayer().getPosition() != null ? attendance.getPlayer().getPosition().name() : null);
        response.setPositionOrder(attendance.getPlayer().getPosition() != null ? attendance.getPlayer().getPosition().getOrder() : 999);
        response.setStatus(attendance.getStatus());
        response.setNotes(attendance.getNotes());
        response.setCreatedAt(attendance.getCreatedAt());
        response.setUpdatedAt(attendance.getUpdatedAt());
        return response;
    }
    
    /**
     * Calcula el número de sesión automáticamente basado en la fecha
     * La sesión más antigua será la número 1
     */
    private Integer calculateSessionNumber(Long teamId, LocalDateTime trainingDate) {
        // Obtener todos los entrenamientos del equipo ordenados por fecha (más antiguos primero)
        List<Training> existingTrainings = trainingRepository.findByTeamIdAndDeletedFalse(teamId);
        
        // Si no hay entrenamientos previos, esta será la sesión 1
        if (existingTrainings.isEmpty()) {
            return 1;
        }
        
        // Contar cuántos entrenamientos tienen fecha anterior o igual a la fecha del nuevo entrenamiento
        long sessionsBefore = existingTrainings.stream()
            .filter(training -> !training.getDate().isAfter(trainingDate))
            .count();
        
        // El número de sesión será el siguiente al último existente
        return (int) (sessionsBefore + 1);
    }
}
