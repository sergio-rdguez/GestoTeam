package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.ExerciseRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExerciseService exerciseService;

    private ExerciseRequest exerciseRequest;
    private ExerciseResponse exerciseResponse;

    @BeforeEach
    void setUp() {

        exerciseRequest = new ExerciseRequest();
        exerciseRequest.setTitle("Test Exercise");
        exerciseRequest.setDescription("Test Description");
        exerciseRequest.setCategory(ExerciseCategory.TACTICO);
        exerciseRequest.setTacticalObjectives("Improve tactics");
        exerciseRequest.setTechnicalObjectives("Improve technique");
        exerciseRequest.setPhysicalObjectives("Improve fitness");
        exerciseRequest.setMaterials("Cones, balls");
        exerciseRequest.setTacticalDiagramId(1L);

        exerciseResponse = new ExerciseResponse();
        exerciseResponse.setId(1L);
        exerciseResponse.setTitle("Test Exercise");
        exerciseResponse.setDescription("Test Description");
        exerciseResponse.setCategory(ExerciseCategory.TACTICO);
        exerciseResponse.setUserId(1L);
        exerciseResponse.setCreatedAt(LocalDateTime.now());
        exerciseResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getUserExercises_ShouldReturnAllExercises() throws Exception {
        // Arrange
        List<ExerciseResponse> exercises = Arrays.asList(exerciseResponse);
        when(exerciseService.getAllUserExercises()).thenReturn(exercises);

        // Act & Assert
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Exercise"))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(exerciseService).getAllUserExercises();
    }

    @Test
    @WithMockUser
    void getUserExercisesByCategory_ShouldReturnFilteredExercises() throws Exception {
        // Arrange
        ExerciseCategory category = ExerciseCategory.TACTICO;
        List<ExerciseResponse> exercises = Arrays.asList(exerciseResponse);
        when(exerciseService.getUserExercisesByCategory(eq(category)))
                .thenReturn(exercises);

        // Act & Assert
        mockMvc.perform(get("/api/exercises/category/TACTICO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("TACTICO"));

        verify(exerciseService).getUserExercisesByCategory(eq(category));
    }

    @Test
    @WithMockUser
    void searchUserExercises_ShouldReturnMatchingExercises() throws Exception {
        // Arrange
        String searchTerm = "Test";
        List<ExerciseResponse> exercises = Arrays.asList(exerciseResponse);
        when(exerciseService.searchUserExercises(eq(searchTerm)))
                .thenReturn(exercises);

        // Act & Assert
        mockMvc.perform(get("/api/exercises/search")
                        .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Exercise"));

        verify(exerciseService).searchUserExercises(eq(searchTerm));
    }

    @Test
    @WithMockUser
    void getExerciseById_ShouldReturnExercise() throws Exception {
        // Arrange
        when(exerciseService.getExerciseById(1L)).thenReturn(exerciseResponse);

        // Act & Assert
        mockMvc.perform(get("/api/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Exercise"))
                .andExpect(jsonPath("$.category").value("TACTICO"));

        verify(exerciseService).getExerciseById(1L);
    }

    @Test
    @WithMockUser
    void createExercise_ShouldCreateAndReturnExercise() throws Exception {
        // Arrange
        when(exerciseService.createExercise(any(ExerciseRequest.class))).thenReturn(exerciseResponse);

        // Act & Assert
        mockMvc.perform(post("/api/exercises")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Exercise"));

        verify(exerciseService).createExercise(any(ExerciseRequest.class));
    }

    // TODO: Test temporalmente deshabilitado - requiere configuración adicional de mocks
    /*
    @Test
    @WithMockUser("testuser")
    void createExerciseAndLinkToTraining_ShouldCreateAndLinkExercise() throws Exception {
        // Arrange
        Long trainingId = 1L;
        when(exerciseService.createExercise(any(ExerciseRequest.class)))
                .thenReturn(exerciseResponse);

        // Act & Assert
        mockMvc.perform(post("/api/exercises/create-and-link")
                        .with(csrf())
                        .param("trainingId", trainingId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Exercise"));

        verify(exerciseService).createExercise(any(ExerciseRequest.class));
    }
    */

    @Test
    @WithMockUser
    void updateExercise_ShouldUpdateAndReturnExercise() throws Exception {
        // Arrange
        when(exerciseService.updateExercise(eq(1L), any(ExerciseRequest.class))).thenReturn(exerciseResponse);

        // Act & Assert
        mockMvc.perform(put("/api/exercises/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Exercise"));

        verify(exerciseService).updateExercise(eq(1L), any(ExerciseRequest.class));
    }

    @Test
    @WithMockUser
    void deleteExercise_ShouldDeleteExercise() throws Exception {
        // Arrange
        doNothing().when(exerciseService).deleteExercise(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/exercises/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Ejercicio eliminado con éxito"));

        verify(exerciseService).deleteExercise(1L);
    }

    @Test
    @WithMockUser
    void getExerciseCategories_ShouldReturnAllCategories() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/exercises/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithMockUser
    void createExercise_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        ExerciseRequest invalidRequest = new ExerciseRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/exercises")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateExercise_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        ExerciseRequest invalidRequest = new ExerciseRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(put("/api/exercises/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void assignTacticalDiagram_ShouldAssignDiagramToExercise() throws Exception {
        // Arrange
        doNothing().when(exerciseService).assignTacticalDiagram(1L, 1L);

        // Act & Assert
        mockMvc.perform(post("/api/exercises/1/assign-diagram/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Diagrama táctico asignado con éxito"));

        verify(exerciseService).assignTacticalDiagram(1L, 1L);
    }

    @Test
    @WithMockUser
    void removeTacticalDiagram_ShouldRemoveDiagramFromExercise() throws Exception {
        // Arrange
        doNothing().when(exerciseService).removeTacticalDiagram(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/exercises/1/remove-diagram")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Diagrama táctico removido con éxito"));

        verify(exerciseService).removeTacticalDiagram(1L);
    }
}
