package com.gestoteam.service;

import com.gestoteam.dto.request.ExerciseRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.exception.ResourceNotFoundException;
import com.gestoteam.model.Exercise;

import com.gestoteam.model.User;
import com.gestoteam.repository.ExerciseRepository;

import com.gestoteam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;



    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExerciseService exerciseService;

    private User testUser;
    private Exercise testExercise;
    private ExerciseRequest exerciseRequest;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setTitle("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setCategory(ExerciseCategory.TACTICO);
        testExercise.setUser(testUser);
        testExercise.setCreatedAt(LocalDateTime.now());
        testExercise.setUpdatedAt(LocalDateTime.now());



        exerciseRequest = new ExerciseRequest();
        exerciseRequest.setTitle("New Exercise");
        exerciseRequest.setDescription("New Description");
        exerciseRequest.setCategory(ExerciseCategory.TECNICO);
        exerciseRequest.setTacticalObjectives("Improve tactics");
        exerciseRequest.setTechnicalObjectives("Improve technique");
        exerciseRequest.setPhysicalObjectives("Improve fitness");
        exerciseRequest.setMaterials("Cones, balls");


        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        // Mock UserRepository - solo findByUsername que se usa en getCurrentUserId()
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getUserExercises_ShouldReturnAllExercises() {
        // Arrange
        List<Exercise> exercises = Arrays.asList(testExercise);
        when(exerciseRepository.findAllByUserIdAndNotDeleted(anyLong()))
                .thenReturn(exercises);

        // Act
        List<ExerciseResponse> result = exerciseService.getAllUserExercises();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Exercise", result.get(0).getTitle());
        verify(exerciseRepository).findAllByUserIdAndNotDeleted(anyLong());
    }

    @Test
    void getUserExercisesByCategory_ShouldReturnFilteredExercises() {
        // Arrange
        ExerciseCategory category = ExerciseCategory.TACTICO;
        List<Exercise> exercises = Arrays.asList(testExercise);
        when(exerciseRepository.findByUserIdAndCategoryAndNotDeleted(anyLong(), eq(category)))
                .thenReturn(exercises);

        // Act
        List<ExerciseResponse> result = exerciseService.getUserExercisesByCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ExerciseCategory.TACTICO, result.get(0).getCategory());
        verify(exerciseRepository).findByUserIdAndCategoryAndNotDeleted(anyLong(), eq(category));
    }

    @Test
    void searchUserExercises_ShouldReturnMatchingExercises() {
        // Arrange
        String searchTerm = "Test";
        List<Exercise> exercises = Arrays.asList(testExercise);
        when(exerciseRepository.findByUserIdAndTitleContainingAndNotDeleted(anyLong(), eq(searchTerm)))
                .thenReturn(exercises);

        // Act
        List<ExerciseResponse> result = exerciseService.searchUserExercises(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().contains(searchTerm));
        verify(exerciseRepository).findByUserIdAndTitleContainingAndNotDeleted(anyLong(), eq(searchTerm));
    }

    @Test
    void getExerciseById_ShouldReturnExercise_WhenExistsAndBelongsToUser() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.of(testExercise));

        // Act
        ExerciseResponse result = exerciseService.getExerciseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Exercise", result.getTitle());
        assertEquals(1L, result.getUserId());
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
    }

    @Test
    void getExerciseById_ShouldThrowException_WhenExerciseNotFound() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exerciseService.getExerciseById(1L));
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
    }

    @Test
    void createExercise_ShouldCreateAndReturnExercise() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(testExercise);

        // Act
        ExerciseResponse result = exerciseService.createExercise(exerciseRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Exercise", result.getTitle());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void updateExercise_ShouldUpdateAndReturnExercise_WhenExistsAndBelongsToUser() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.of(testExercise));
        
        // Crear un ejercicio actualizado para el mock del save
        Exercise updatedExercise = new Exercise();
        updatedExercise.setId(1L);
        updatedExercise.setTitle("New Exercise");
        updatedExercise.setDescription("New Description");
        updatedExercise.setCategory(ExerciseCategory.TECNICO);
        updatedExercise.setUser(testUser);
        updatedExercise.setCreatedAt(LocalDateTime.now());
        updatedExercise.setUpdatedAt(LocalDateTime.now());
        
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(updatedExercise);

        // Act
        ExerciseResponse result = exerciseService.updateExercise(1L, exerciseRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Exercise", result.getTitle());
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void updateExercise_ShouldThrowException_WhenExerciseNotFound() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exerciseService.updateExercise(1L, exerciseRequest));
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    void deleteExercise_ShouldMarkAsDeleted_WhenExistsAndBelongsToUser() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.of(testExercise));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(testExercise);

        // Act
        exerciseService.deleteExercise(1L);

        // Assert
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
        verify(exerciseRepository).save(any(Exercise.class));
        assertTrue(testExercise.getDeleted());
    }

    @Test
    void deleteExercise_ShouldThrowException_WhenExerciseNotFound() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exerciseService.deleteExercise(1L));
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    void exerciseBelongsToUser_ShouldReturnTrue_WhenExerciseBelongsToUser() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.of(testExercise));

        // Act
        boolean result = exerciseService.exerciseBelongsToUser(1L);

        // Assert
        assertTrue(result);
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
    }

    @Test
    void exerciseBelongsToUser_ShouldReturnFalse_WhenExerciseDoesNotBelongToUser() {
        // Arrange
        when(exerciseRepository.findByIdAndUserIdAndNotDeleted(eq(1L), anyLong()))
                .thenReturn(Optional.empty());

        // Act
        boolean result = exerciseService.exerciseBelongsToUser(1L);

        // Assert
        assertFalse(result);
        verify(exerciseRepository).findByIdAndUserIdAndNotDeleted(eq(1L), anyLong());
    }










}
