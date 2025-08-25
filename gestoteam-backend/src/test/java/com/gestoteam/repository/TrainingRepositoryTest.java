package com.gestoteam.repository;

import com.gestoteam.enums.Category;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.enums.Foot;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import com.gestoteam.model.Exercise;
import com.gestoteam.model.Team;
import com.gestoteam.model.Training;
import com.gestoteam.model.User;
import com.gestoteam.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@ActiveProfiles("test")
class TrainingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private User testUser;
    private Team testTeam;
    private Exercise testExercise;
    private Training testTraining1;
    private Training testTraining2;
    private Training testTraining3;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos antes de cada test
        entityManager.clear();
        
        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser = entityManager.persistAndFlush(testUser);

        // Crear equipo de prueba
        testTeam = new Team();
        testTeam.setName("Test Team");
        testTeam.setCategory(Category.SENIOR);
        testTeam.setOwnerId(testUser.getId());
        testTeam.setDeleted(false);
        testTeam.setCreatedAt(LocalDateTime.now());
        testTeam.setUpdatedAt(LocalDateTime.now());
        testTeam = entityManager.persistAndFlush(testTeam);

        // Crear jugador de prueba con todos los campos obligatorios
        testPlayer = new Player();
        testPlayer.setName("John");
        testPlayer.setSurnameFirst("Doe");
        testPlayer.setSurnameSecond("Smith");
        testPlayer.setPosition(Position.DC);
        testPlayer.setFoot(Foot.DIESTRO);
        testPlayer.setNumber(10);
        testPlayer.setStatus(PlayerStatus.ACTIVO);
        testPlayer.setBirthDate(LocalDate.of(1995, 5, 15));
        testPlayer.setTeam(testTeam);
        testPlayer.setDeleted(false);
        testPlayer.setCreatedAt(LocalDateTime.now());
        testPlayer.setUpdatedAt(LocalDateTime.now());
        testPlayer = entityManager.persistAndFlush(testPlayer);

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

        // Crear entrenamientos de prueba
        testTraining1 = new Training();
        testTraining1.setTitle("Test Training 1");
        testTraining1.setDate(LocalDateTime.now());
        testTraining1.setLocation("Field 1");
        testTraining1.setTrainingType("Technical");
        testTraining1.setSessionNumber(1);
        testTraining1.setUser(testUser);
        testTraining1.setTeam(testTeam);
        testTraining1.setExercises(List.of(testExercise));
        testTraining1.setDeleted(false);
        testTraining1.setCreatedAt(LocalDateTime.now());
        testTraining1.setUpdatedAt(LocalDateTime.now());
        testTraining1 = entityManager.persistAndFlush(testTraining1);

        testTraining2 = new Training();
        testTraining2.setTitle("Test Training 2");
        testTraining2.setDate(LocalDateTime.now().plusDays(1));
        testTraining2.setLocation("Field 2");
        testTraining2.setTrainingType("Tactical");
        testTraining2.setSessionNumber(2);
        testTraining2.setUser(testUser);
        testTraining2.setTeam(testTeam);
        testTraining2.setExercises(List.of());
        testTraining2.setDeleted(false);
        testTraining2.setCreatedAt(LocalDateTime.now());
        testTraining2.setUpdatedAt(LocalDateTime.now());
        testTraining2 = entityManager.persistAndFlush(testTraining2);

        testTraining3 = new Training();
        testTraining3.setTitle("Test Training 3");
        testTraining3.setDate(LocalDateTime.now().plusDays(2));
        testTraining3.setLocation("Field 3");
        testTraining3.setTrainingType("Physical");
        testTraining3.setSessionNumber(3);
        testTraining3.setUser(testUser);
        testTraining3.setTeam(testTeam);
        testTraining3.setExercises(List.of());
        testTraining3.setDeleted(true); // Este está marcado como eliminado
        testTraining3.setCreatedAt(LocalDateTime.now());
        testTraining3.setUpdatedAt(LocalDateTime.now());
        testTraining3 = entityManager.persistAndFlush(testTraining3);

        entityManager.clear();
    }

    @Test
    void findByUserIdAndDeletedFalse_ShouldReturnOnlyNonDeletedTrainings() {
        // Act
        List<Training> result = trainingRepository.findByUserIdAndDeletedFalse(testUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(training -> !training.getDeleted()));
        assertTrue(result.stream().allMatch(training -> training.getUser().getId().equals(testUser.getId())));
        
        // Verificar que los entrenamientos correctos están en la lista
        List<String> titles = result.stream().map(Training::getTitle).toList();
        assertTrue(titles.contains("Test Training 1"));
        assertTrue(titles.contains("Test Training 2"));
        assertFalse(titles.contains("Test Training 3")); // Este está eliminado
    }

    @Test
    void findByUserIdAndDeletedFalse_ShouldReturnEmptyList_WhenUserHasNoTrainings() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser = entityManager.persistAndFlush(newUser);

        // Act
        List<Training> result = trainingRepository.findByUserIdAndDeletedFalse(newUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndDeletedFalse_ShouldReturnEmptyList_WhenUserHasOnlyDeletedTrainings() {
        // Arrange
        User userWithDeletedTrainings = new User();
        userWithDeletedTrainings.setUsername("deleteduser");
        userWithDeletedTrainings.setPassword("password");
        userWithDeletedTrainings = entityManager.persistAndFlush(userWithDeletedTrainings);

        Team teamForDeletedUser = new Team();
        teamForDeletedUser.setName("Deleted User Team");
        teamForDeletedUser.setCategory(Category.SENIOR);
        teamForDeletedUser.setOwnerId(userWithDeletedTrainings.getId());
        teamForDeletedUser.setDeleted(false);
        teamForDeletedUser.setCreatedAt(LocalDateTime.now());
        teamForDeletedUser.setUpdatedAt(LocalDateTime.now());
        teamForDeletedUser = entityManager.persistAndFlush(teamForDeletedUser);

        Training deletedTraining = new Training();
        deletedTraining.setTitle("Deleted Training");
        deletedTraining.setDate(LocalDateTime.now());
        deletedTraining.setLocation("Deleted Field");
        deletedTraining.setTrainingType("Deleted Type");
        deletedTraining.setSessionNumber(1);
        deletedTraining.setUser(userWithDeletedTrainings);
        deletedTraining.setTeam(teamForDeletedUser);
        deletedTraining.setExercises(List.of());
        deletedTraining.setDeleted(true);
        deletedTraining.setCreatedAt(LocalDateTime.now());
        deletedTraining.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(deletedTraining);

        entityManager.clear();

        // Act
        List<Training> result = trainingRepository.findByUserIdAndDeletedFalse(userWithDeletedTrainings.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndUserIdAndDeletedFalse_ShouldReturnTraining_WhenExistsAndBelongsToUser() {
        // Act
        Optional<Training> result = trainingRepository.findByIdAndUserIdAndDeletedFalse(
                testTraining1.getId(), testUser.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTraining1.getId(), result.get().getId());
        assertEquals("Test Training 1", result.get().getTitle());
        assertEquals("Field 1", result.get().getLocation());
        assertEquals("Technical", result.get().getTrainingType());
        assertEquals(1, result.get().getSessionNumber());
        assertFalse(result.get().getDeleted());
        assertEquals(testUser.getId(), result.get().getUser().getId());
        assertEquals(testTeam.getId(), result.get().getTeam().getId());
    }

    @Test
    void findByIdAndUserIdAndDeletedFalse_ShouldReturnEmpty_WhenTrainingDoesNotBelongToUser() {
        // Arrange
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPassword("password");
        otherUser = entityManager.persistAndFlush(otherUser);

        // Act
        Optional<Training> result = trainingRepository.findByIdAndUserIdAndDeletedFalse(
                testTraining1.getId(), otherUser.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByIdAndUserIdAndDeletedFalse_ShouldReturnEmpty_WhenTrainingIsDeleted() {
        // Act
        Optional<Training> result = trainingRepository.findByIdAndUserIdAndDeletedFalse(
                testTraining3.getId(), testUser.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByIdAndUserIdAndDeletedFalse_ShouldReturnEmpty_WhenTrainingDoesNotExist() {
        // Act
        Optional<Training> result = trainingRepository.findByIdAndUserIdAndDeletedFalse(
                999L, testUser.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByTeamIdAndDeletedFalse_ShouldReturnTeamTrainings() {
        // Act
        List<Training> result = trainingRepository.findByTeamIdAndDeletedFalse(testTeam.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(training -> !training.getDeleted()));
        assertTrue(result.stream().allMatch(training -> training.getTeam().getId().equals(testTeam.getId())));
        
        // Verificar que los entrenamientos están ordenados por fecha
        List<String> titles = result.stream().map(Training::getTitle).toList();
        assertEquals("Test Training 1", titles.get(0));
        assertEquals("Test Training 2", titles.get(1));
    }

    @Test
    void save_ShouldPersistTraining() {
        // Arrange
        Training newTraining = new Training();
        newTraining.setTitle("New Training");
        newTraining.setDate(LocalDateTime.now().plusDays(3));
        newTraining.setLocation("New Field");
        newTraining.setTrainingType("New Type");
        newTraining.setSessionNumber(4);
        newTraining.setUser(testUser);
        newTraining.setTeam(testTeam);
        newTraining.setExercises(List.of(testExercise));
        newTraining.setDeleted(false);
        newTraining.setCreatedAt(LocalDateTime.now());
        newTraining.setUpdatedAt(LocalDateTime.now());

        // Act
        Training savedTraining = trainingRepository.save(newTraining);

        // Assert
        assertNotNull(savedTraining.getId());
        assertEquals("New Training", savedTraining.getTitle());
        assertEquals("New Field", savedTraining.getLocation());
        assertEquals("New Type", savedTraining.getTrainingType());
        assertEquals(4, savedTraining.getSessionNumber());
        assertEquals(testUser.getId(), savedTraining.getUser().getId());
        assertEquals(testTeam.getId(), savedTraining.getTeam().getId());
        assertFalse(savedTraining.getDeleted());
        assertEquals(1, savedTraining.getExercises().size());
        assertEquals(testExercise.getId(), savedTraining.getExercises().get(0).getId());
    }

    @Test
    void save_ShouldUpdateExistingTraining() {
        // Arrange
        String newTitle = "Updated Training";
        testTraining1.setTitle(newTitle);

        // Act
        Training updatedTraining = trainingRepository.save(testTraining1);

        // Assert
        assertEquals(testTraining1.getId(), updatedTraining.getId());
        assertEquals(newTitle, updatedTraining.getTitle());
        assertEquals("Technical", updatedTraining.getTrainingType());
        assertEquals(1, updatedTraining.getSessionNumber());
        assertEquals(testUser.getId(), updatedTraining.getUser().getId());
        assertEquals(testTeam.getId(), updatedTraining.getTeam().getId());
    }

    @Test
    void delete_ShouldRemoveTraining() {
        // Act
        trainingRepository.delete(testTraining1);

        // Assert
        Optional<Training> deletedTraining = trainingRepository.findById(testTraining1.getId());
        assertFalse(deletedTraining.isPresent());
    }

    @Test
    void findByUserIdAndDeletedFalse_ShouldHandleTrainingsWithExercises() {
        // Act
        List<Training> result = trainingRepository.findByUserIdAndDeletedFalse(testUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verificar que el entrenamiento con ejercicios tiene la relación correcta
        Training trainingWithExercises = result.stream()
                .filter(t -> t.getTitle().equals("Test Training 1"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(trainingWithExercises);
        assertEquals(1, trainingWithExercises.getExercises().size());
        assertEquals(testExercise.getId(), trainingWithExercises.getExercises().get(0).getId());
    }

    @Test
    void findByUserIdAndDeletedFalse_ShouldHandleTrainingsWithoutExercises() {
        // Act
        List<Training> result = trainingRepository.findByUserIdAndDeletedFalse(testUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verificar que el entrenamiento sin ejercicios tiene la lista vacía
        Training trainingWithoutExercises = result.stream()
                .filter(t -> t.getTitle().equals("Test Training 2"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(trainingWithoutExercises);
        assertTrue(trainingWithoutExercises.getExercises().isEmpty());
    }

    @Test
    void findAllByUserIdAndNotDeleted_ShouldReturnOnlyNonDeletedExercises() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

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
}
