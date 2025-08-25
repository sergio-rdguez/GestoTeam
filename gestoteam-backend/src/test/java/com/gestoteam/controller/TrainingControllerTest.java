package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.TrainingRequest;
import com.gestoteam.dto.request.TrainingAttendanceRequest;
import com.gestoteam.dto.response.ExerciseResponse;
import com.gestoteam.dto.response.TrainingResponse;
import com.gestoteam.dto.response.TrainingAttendanceResponse;
import com.gestoteam.enums.AttendanceStatus;
import com.gestoteam.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainingService trainingService;

    private TrainingRequest trainingRequest;
    private TrainingResponse trainingResponse;
    private ExerciseResponse exerciseResponse;

    @BeforeEach
    void setUp() {
        exerciseResponse = new ExerciseResponse();
        exerciseResponse.setId(1L);
        exerciseResponse.setTitle("Test Exercise");
        exerciseResponse.setUserId(1L);

        trainingRequest = new TrainingRequest();
        trainingRequest.setTitle("Test Training");
        trainingRequest.setDate(LocalDateTime.now());
        trainingRequest.setLocation("Test Field");
        trainingRequest.setTrainingType("Technical");
        trainingRequest.setTeamId(1L);
        trainingRequest.setExerciseIds(Arrays.asList(1L));

        trainingResponse = new TrainingResponse();
        trainingResponse.setId(1L);
        trainingResponse.setTitle("Test Training");
        trainingResponse.setDate(LocalDateTime.now());
        trainingResponse.setLocation("Test Field");
        trainingResponse.setTrainingType("Technical");
        trainingResponse.setSessionNumber(1);
        trainingResponse.setUserId(1L);
        trainingResponse.setTeamId(1L);
        trainingResponse.setTeamName("Test Team");
        trainingResponse.setExercises(Arrays.asList(exerciseResponse));
        trainingResponse.setCreatedAt(LocalDateTime.now());
        trainingResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getUserTrainings_ShouldReturnAllTrainings() throws Exception {
        // Arrange
        List<TrainingResponse> trainings = Arrays.asList(trainingResponse);
        when(trainingService.getUserTrainings()).thenReturn(trainings);

        // Act & Assert
        mockMvc.perform(get("/api/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Training"))
                .andExpect(jsonPath("$[0].location").value("Test Field"))
                .andExpect(jsonPath("$[0].trainingType").value("Technical"))
                .andExpect(jsonPath("$[0].sessionNumber").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].teamName").value("Test Team"))
                .andExpect(jsonPath("$[0].exercises").exists())
                .andExpect(jsonPath("$[0].exercises[0].title").value("Test Exercise"));

        verify(trainingService).getUserTrainings();
    }

    @Test
    @WithMockUser
    void getTeamTrainings_ShouldReturnTeamTrainings() throws Exception {
        // Arrange
        List<TrainingResponse> trainings = Arrays.asList(trainingResponse);
        when(trainingService.getTeamTrainings(1L)).thenReturn(trainings);

        // Act & Assert
        mockMvc.perform(get("/api/trainings/team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Training"))
                .andExpect(jsonPath("$[0].teamId").value(1));

        verify(trainingService).getTeamTrainings(1L);
    }

    @Test
    @WithMockUser
    void getTrainingById_ShouldReturnTraining() throws Exception {
        // Arrange
        when(trainingService.getTrainingById(1L)).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(get("/api/trainings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Training"))
                .andExpect(jsonPath("$.location").value("Test Field"))
                .andExpect(jsonPath("$.trainingType").value("Technical"))
                .andExpect(jsonPath("$.sessionNumber").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.teamId").value(1))
                .andExpect(jsonPath("$.teamName").value("Test Team"))
                .andExpect(jsonPath("$.exercises").exists())
                .andExpect(jsonPath("$.exercises[0].title").value("Test Exercise"));

        verify(trainingService).getTrainingById(1L);
    }

    @Test
    @WithMockUser
    void createTraining_ShouldCreateAndReturnTraining() throws Exception {
        // Arrange
        when(trainingService.createTraining(any(TrainingRequest.class))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/trainings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Training"))
                .andExpect(jsonPath("$.location").value("Test Field"))
                .andExpect(jsonPath("$.trainingType").value("Technical"))
                .andExpect(jsonPath("$.sessionNumber").value(1))
                .andExpect(jsonPath("$.teamId").value(1));

        verify(trainingService).createTraining(any(TrainingRequest.class));
    }

    @Test
    @WithMockUser
    void createTraining_ShouldCreateTrainingWithoutExercises_WhenNoExerciseIds() throws Exception {
        // Arrange
        trainingRequest.setExerciseIds(null);
        when(trainingService.createTraining(any(TrainingRequest.class))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(trainingService).createTraining(any(TrainingRequest.class));
    }

    @Test
    @WithMockUser
    void updateTraining_ShouldUpdateAndReturnTraining() throws Exception {
        // Arrange
        when(trainingService.updateTraining(eq(1L), any(TrainingRequest.class))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(put("/api/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Training"))
                .andExpect(jsonPath("$.location").value("Test Field"))
                .andExpect(jsonPath("$.trainingType").value("Technical"));

        verify(trainingService).updateTraining(eq(1L), any(TrainingRequest.class));
    }

    @Test
    @WithMockUser
    void deleteTraining_ShouldDeleteTraining() throws Exception {
        // Arrange
        doNothing().when(trainingService).deleteTraining(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/trainings/1"))
                .andExpect(status().isNoContent());

        verify(trainingService).deleteTraining(1L);
    }

    @Test
    @WithMockUser
    void addExercisesToTraining_ShouldAddExercisesAndReturnTraining() throws Exception {
        // Arrange
        List<Long> exerciseIds = Arrays.asList(1L, 2L);
        when(trainingService.addExercisesToTraining(eq(1L), eq(exerciseIds))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/trainings/1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.exercises").exists());

        verify(trainingService).addExercisesToTraining(eq(1L), eq(exerciseIds));
    }

    @Test
    @WithMockUser
    void removeExercisesFromTraining_ShouldRemoveExercisesAndReturnTraining() throws Exception {
        // Arrange
        List<Long> exerciseIds = Arrays.asList(1L);
        when(trainingService.removeExercisesFromTraining(eq(1L), eq(exerciseIds))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/trainings/1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(trainingService).removeExercisesFromTraining(eq(1L), eq(exerciseIds));
    }

    @Test
    @WithMockUser
    void getTrainingExercises_ShouldReturnExercises() throws Exception {
        // Arrange
        List<ExerciseResponse> exercises = Arrays.asList(exerciseResponse);
        when(trainingService.getTrainingExercises(1L)).thenReturn(exercises);

        // Act & Assert
        mockMvc.perform(get("/api/trainings/1/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Exercise"))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(trainingService).getTrainingExercises(1L);
    }

    @Test
    @WithMockUser
    void createTraining_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        TrainingRequest invalidRequest = new TrainingRequest();
        // Missing required fields: title, date, location, trainingType, teamId

        // Act & Assert
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateTraining_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        TrainingRequest invalidRequest = new TrainingRequest();
        // Missing required fields: title, date, location, trainingType, teamId

        // Act & Assert
        mockMvc.perform(put("/api/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void addExercisesToTraining_ShouldReturnBadRequest_WhenInvalidExerciseIds() throws Exception {
        // Arrange
        List<Long> invalidExerciseIds = Arrays.asList();
        when(trainingService.addExercisesToTraining(eq(1L), eq(invalidExerciseIds))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(post("/api/trainings/1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExerciseIds)))
                .andExpect(status().isOk());

        verify(trainingService).addExercisesToTraining(eq(1L), eq(invalidExerciseIds));
    }

    @Test
    @WithMockUser
    void removeExercisesFromTraining_ShouldReturnBadRequest_WhenInvalidExerciseIds() throws Exception {
        // Arrange
        List<Long> invalidExerciseIds = Arrays.asList();
        when(trainingService.removeExercisesFromTraining(eq(1L), eq(invalidExerciseIds))).thenReturn(trainingResponse);

        // Act & Assert
        mockMvc.perform(delete("/api/trainings/1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExerciseIds)))
                .andExpect(status().isOk());

        verify(trainingService).removeExercisesFromTraining(eq(1L), eq(invalidExerciseIds));
    }

    @Test
    @WithMockUser
    void getTrainingAttendance_ShouldReturnAttendanceList() throws Exception {
        // Arrange
        TrainingAttendanceResponse attendanceResponse = new TrainingAttendanceResponse();
        attendanceResponse.setId(1L);
        attendanceResponse.setPlayerId(1L);
        attendanceResponse.setStatus(AttendanceStatus.PRESENT);
        attendanceResponse.setPlayerName("John");
        attendanceResponse.setPlayerSurname("Doe");
        attendanceResponse.setPlayerFullName("John Doe");
        attendanceResponse.setPhotoPath("/path/to/photo.jpg");
        attendanceResponse.setPosition("DC");
        attendanceResponse.setPositionOrder(10);
        
        List<TrainingAttendanceResponse> attendanceList = Arrays.asList(attendanceResponse);
        when(trainingService.getTrainingAttendance(1L)).thenReturn(attendanceList);

        // Act & Assert
        mockMvc.perform(get("/api/trainings/1/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].playerId").value(1))
                .andExpect(jsonPath("$[0].status").value("PRESENT"))
                .andExpect(jsonPath("$[0].playerName").value("John"))
                .andExpect(jsonPath("$[0].playerSurname").value("Doe"))
                .andExpect(jsonPath("$[0].playerFullName").value("John Doe"))
                .andExpect(jsonPath("$[0].photoPath").value("/path/to/photo.jpg"))
                .andExpect(jsonPath("$[0].position").value("DC"))
                .andExpect(jsonPath("$[0].positionOrder").value(10));

        verify(trainingService).getTrainingAttendance(1L);
    }

    @Test
    @WithMockUser
    void updatePlayerAttendance_ShouldUpdateAttendance() throws Exception {
        // Arrange
        TrainingAttendanceRequest attendanceRequest = new TrainingAttendanceRequest();
        attendanceRequest.setPlayerId(1L);
        attendanceRequest.setStatus(AttendanceStatus.PRESENT);
        attendanceRequest.setNotes("Player arrived on time");

        TrainingAttendanceResponse attendanceResponse = new TrainingAttendanceResponse();
        attendanceResponse.setId(1L);
        attendanceResponse.setPlayerId(1L);
        attendanceResponse.setStatus(AttendanceStatus.PRESENT);
        attendanceResponse.setNotes("Player arrived on time");

        when(trainingService.updatePlayerAttendance(eq(1L), eq(1L), any(TrainingAttendanceRequest.class)))
                .thenReturn(attendanceResponse);

        // Act & Assert
        mockMvc.perform(put("/api/trainings/1/attendance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.playerId").value(1))
                .andExpect(jsonPath("$.status").value("PRESENT"))
                .andExpect(jsonPath("$.notes").value("Player arrived on time"));

        verify(trainingService).updatePlayerAttendance(eq(1L), eq(1L), any(TrainingAttendanceRequest.class));
    }

    @Test
    @WithMockUser
    void getPlayerAbsentTrainings_ShouldReturnAbsentTrainings() throws Exception {
        // Arrange
        List<TrainingResponse> absentTrainings = Arrays.asList(trainingResponse);
        when(trainingService.getPlayerAbsentTrainings(1L)).thenReturn(absentTrainings);

        // Act & Assert
        mockMvc.perform(get("/api/trainings/player/1/absences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Training"));

        verify(trainingService).getPlayerAbsentTrainings(1L);
    }
}
