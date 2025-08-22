package com.gestoteam.repository;

import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.model.Exercise;
import com.gestoteam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ExerciseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private User testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser = entityManager.persistAndFlush(testUser);

        // Crear ejercicio de prueba
        testExercise = new Exercise();
        testExercise.setTitle("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setTacticalObjectives("Test tactical");
        testExercise.setTechnicalObjectives("Test technical");
        testExercise.setPhysicalObjectives("Test physical");
        testExercise.setMaterials("Test materials");
        testExercise.setCategory(ExerciseCategory.TACTICO);
        testExercise.setUser(testUser);
        testExercise.setDeleted(false);
        testExercise.setCreatedAt(LocalDateTime.now());
        testExercise.setUpdatedAt(LocalDateTime.now());
        testExercise = entityManager.persistAndFlush(testExercise);

        entityManager.clear();
    }

    @Test
    void findAllByUserIdAndNotDeleted_ShouldReturnOnlyNonDeletedExercises() {
        // Act
        List<Exercise> result = exerciseRepository.findAllByUserIdAndNotDeleted(testUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(exercise -> !exercise.getDeleted()));
        assertTrue(result.stream().allMatch(exercise -> exercise.getUser().getId().equals(testUser.getId())));
        
        // Verificar que el ejercicio correcto está en la lista
        Exercise exercise = result.get(0);
        assertEquals("Test Exercise", exercise.getTitle());
        assertEquals(ExerciseCategory.TACTICO, exercise.getCategory());
    }

    @Test
    void findAllByUserIdAndNotDeleted_ShouldReturnEmptyList_WhenUserHasNoExercises() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser = entityManager.persistAndFlush(newUser);

        // Act
        List<Exercise> result = exerciseRepository.findAllByUserIdAndNotDeleted(newUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndCategoryAndNotDeleted_ShouldReturnExercisesByCategory() {
        // Arrange
        ExerciseCategory category = ExerciseCategory.TACTICO;

        // Act
        List<Exercise> result = exerciseRepository.findByUserIdAndCategoryAndNotDeleted(
            testUser.getId(), category);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(exercise -> exercise.getCategory() == category));
        assertTrue(result.stream().allMatch(exercise -> !exercise.getDeleted()));
        assertTrue(result.stream().allMatch(exercise -> exercise.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void findByUserIdAndCategoryAndNotDeleted_ShouldReturnEmptyList_WhenCategoryNotFound() {
        // Arrange
        ExerciseCategory category = ExerciseCategory.FISICO;

        // Act
        List<Exercise> result = exerciseRepository.findByUserIdAndCategoryAndNotDeleted(
            testUser.getId(), category);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndTitleContainingAndNotDeleted_ShouldReturnMatchingExercises() {
        // Arrange
        String searchTerm = "Test";

        // Act
        List<Exercise> result = exerciseRepository.findByUserIdAndTitleContainingAndNotDeleted(
            testUser.getId(), searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(exercise -> exercise.getTitle().toUpperCase().contains(searchTerm.toUpperCase())));
        assertTrue(result.stream().allMatch(exercise -> !exercise.getDeleted()));
        assertTrue(result.stream().allMatch(exercise -> exercise.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void findByUserIdAndTitleContainingAndNotDeleted_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        String searchTerm = "Nonexistent";

        // Act
        List<Exercise> result = exerciseRepository.findByUserIdAndTitleContainingAndNotDeleted(
            testUser.getId(), searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndTitleContainingAndNotDeleted_ShouldBeCaseInsensitive() {
        // Arrange
        String searchTerm = "test";

        // Act
        List<Exercise> result = exerciseRepository.findByUserIdAndTitleContainingAndNotDeleted(
            testUser.getId(), searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        // El término "test" debería encontrar "Test Exercise" (case insensitive)
        assertTrue(result.get(0).getTitle().toLowerCase().contains(searchTerm.toLowerCase()));
    }

    @Test
    void findByIdAndUserIdAndNotDeleted_ShouldReturnExercise_WhenExistsAndBelongsToUser() {
        // Act
        var result = exerciseRepository.findByIdAndUserIdAndNotDeleted(testExercise.getId(), testUser.getId());

        // Assert
        assertTrue(result.isPresent());
        Exercise exercise = result.get();
        assertEquals("Test Exercise", exercise.getTitle());
        assertEquals(testUser.getId(), exercise.getUser().getId());
        assertFalse(exercise.getDeleted());
    }

    @Test
    void findByIdAndUserIdAndNotDeleted_ShouldReturnEmpty_WhenExerciseDoesNotBelongToUser() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPassword("password");
        otherUser = entityManager.persistAndFlush(otherUser);

        // Act
        var result = exerciseRepository.findByIdAndUserIdAndNotDeleted(testExercise.getId(), otherUser.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByIdAndUserIdAndNotDeleted_ShouldReturnEmpty_WhenExerciseIsDeleted() {
        // Arrange
        Exercise exerciseToDelete = entityManager.find(Exercise.class, testExercise.getId());
        exerciseToDelete.setDeleted(true);
        entityManager.persistAndFlush(exerciseToDelete);

        // Act
        var result = exerciseRepository.findByIdAndUserIdAndNotDeleted(testExercise.getId(), testUser.getId());

        // Assert
        assertFalse(result.isPresent());
    }
}
