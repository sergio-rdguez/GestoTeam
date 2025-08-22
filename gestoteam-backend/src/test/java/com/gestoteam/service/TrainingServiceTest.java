package com.gestoteam.service;

import com.gestoteam.dto.request.TrainingRequest;
import com.gestoteam.dto.response.TrainingResponse;
import com.gestoteam.exception.ResourceNotFoundException;
import com.gestoteam.model.Exercise;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.model.Training;
import com.gestoteam.model.User;
import com.gestoteam.model.Team;
import com.gestoteam.repository.ExerciseRepository;
import com.gestoteam.repository.TrainingRepository;
import com.gestoteam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TrainingService trainingService;

    private static final String USERNAME = "testuser";
    private static final Long USER_ID = 1L;
    private static final Long TEAM_ID = 1L;
    
    private User testUser;
    private Team testTeam;
    private Training testTraining;
    private Exercise testExercise;
    private TrainingRequest testTrainingRequest;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);
        SecurityContextHolder.setContext(securityContext);

        // Mock del usuario
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        // Mock del equipo
        testTeam = new Team();
        testTeam.setId(TEAM_ID);
        testTeam.setName("Test Team");
        testTeam.setOwnerId(USER_ID);

        testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setTitle("Test Exercise");
        testExercise.setUser(testUser);

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setDate(LocalDateTime.now());
        testTraining.setLocation("Test Field");
        testTraining.setTrainingType("Technical");
        testTraining.setUser(testUser);
        testTraining.setTeam(testTeam);
        testTraining.setObservations("Test observations");
        testTraining.setDeleted(false);
        testTraining.setExercises(new ArrayList<>());

        testTrainingRequest = new TrainingRequest();
        testTrainingRequest.setDate(LocalDateTime.now());
        testTrainingRequest.setLocation("Test Field");
        testTrainingRequest.setTrainingType("Technical");
        testTrainingRequest.setTeamId(TEAM_ID);
        testTrainingRequest.setObservations("Test observations");
        testTrainingRequest.setExerciseIds(Arrays.asList(1L));
    }

    @Test
    void createTraining_WithValidData_ShouldCreateTraining() {
        // Arrange
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);
        when(exerciseRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testExercise));

        // Act
        TrainingResponse result = trainingService.createTraining(testTrainingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining.getId(), result.getId());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void createTraining_WithInvalidUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            trainingService.createTraining(testTrainingRequest);
        });
    }

    @Test
    void getUserTrainings_ShouldReturnTrainings() {
        // Arrange
        when(trainingRepository.findByUserIdAndDeletedFalse(USER_ID)).thenReturn(Arrays.asList(testTraining));

        // Act
        List<TrainingResponse> result = trainingService.getUserTrainings();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTraining.getId(), result.get(0).getId());
    }

    @Test
    void getTrainingById_ShouldReturnTraining() {
        // Arrange
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));

        // Act
        TrainingResponse result = trainingService.getTrainingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTraining.getId(), result.getId());
    }

    @Test
    void getTrainingById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            trainingService.getTrainingById(1L);
        });
    }

    @Test
    void updateTraining_ShouldUpdateTraining() {
        // Arrange
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        // Act
        TrainingResponse result = trainingService.updateTraining(1L, testTrainingRequest);

        // Assert
        assertNotNull(result);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void deleteTraining_ShouldDeleteTraining() {
        // Arrange
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        // Act
        trainingService.deleteTraining(1L);

        // Assert
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void addExercisesToTraining_ShouldAddExercises() {
        // Arrange
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));
        when(exerciseRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(testExercise));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        // Act
        TrainingResponse result = trainingService.addExercisesToTraining(1L, Arrays.asList(1L));

        // Assert
        assertNotNull(result);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void removeExercisesFromTraining_ShouldRemoveExercises() {
        // Arrange
        testTraining.setExercises(new ArrayList<>(Arrays.asList(testExercise)));
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        // Act
        TrainingResponse result = trainingService.removeExercisesFromTraining(1L, Arrays.asList(1L));

        // Assert
        assertNotNull(result);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void getTrainingExercises_ShouldReturnExercises() {
        // Arrange
        testTraining.setExercises(Arrays.asList(testExercise));
        when(trainingRepository.findByIdAndUserIdAndDeletedFalse(1L, USER_ID)).thenReturn(Optional.of(testTraining));

        // Act
        List<ExerciseResponse> result = trainingService.getTrainingExercises(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExercise.getId(), result.get(0).getId());
    }
}
