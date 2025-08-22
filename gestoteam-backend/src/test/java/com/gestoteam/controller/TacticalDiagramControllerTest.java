package com.gestoteam.controller;

import com.gestoteam.dto.response.TacticalDiagramResponse;
import com.gestoteam.service.TacticalDiagramService;
import com.gestoteam.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TacticalDiagramControllerTest {

    @Mock
    private TacticalDiagramService tacticalDiagramService;

    @Mock
    private FileService fileService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TacticalDiagramController tacticalDiagramController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TacticalDiagramResponse testDiagram1;
    private TacticalDiagramResponse testDiagram2;
    private TacticalDiagramResponse testDiagram3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tacticalDiagramController).build();
        objectMapper = new ObjectMapper();

        testDiagram1 = new TacticalDiagramResponse();
        testDiagram1.setId(1L);
        testDiagram1.setTitle("Diagram 1");
        testDiagram1.setDescription("First diagram");
        testDiagram1.setImageUrl("/uploads/diagram1.png");
        testDiagram1.setCreatedAt(LocalDateTime.now().minusDays(2));
        testDiagram1.setUpdatedAt(LocalDateTime.now().minusDays(2));
        testDiagram1.setCreatedByUsername("testuser");
        testDiagram1.setCreatedById(1L);

        testDiagram2 = new TacticalDiagramResponse();
        testDiagram2.setId(2L);
        testDiagram2.setTitle("Diagram 2");
        testDiagram2.setDescription("Second diagram");
        testDiagram2.setImageUrl("/uploads/diagram2.png");
        testDiagram2.setCreatedAt(LocalDateTime.now().minusDays(1));
        testDiagram2.setUpdatedAt(LocalDateTime.now().minusDays(1));
        testDiagram2.setCreatedByUsername("testuser");
        testDiagram2.setCreatedById(1L);

        testDiagram3 = new TacticalDiagramResponse();
        testDiagram3.setId(3L);
        testDiagram3.setTitle("Diagram 3");
        testDiagram3.setDescription("Third diagram");
        testDiagram3.setImageUrl("/uploads/diagram3.png");
        testDiagram3.setCreatedAt(LocalDateTime.now());
        testDiagram3.setUpdatedAt(LocalDateTime.now());
        testDiagram3.setCreatedByUsername("testuser");
        testDiagram3.setCreatedById(1L);

        // Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        // Mock FileService
        when(fileService.getCurrentUserId("testuser")).thenReturn(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserTacticalDiagrams_ShouldReturnUserDiagrams() throws Exception {
        // Arrange
        List<TacticalDiagramResponse> diagrams = Arrays.asList(testDiagram1, testDiagram2, testDiagram3);
        when(tacticalDiagramService.getTacticalDiagramsByUser(1L)).thenReturn(diagrams);

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Diagram 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Diagram 2"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].title").value("Diagram 3"));

        verify(tacticalDiagramService).getTacticalDiagramsByUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserTacticalDiagrams_ShouldReturnEmptyList_WhenUserHasNoDiagrams() throws Exception {
        // Arrange
        when(tacticalDiagramService.getTacticalDiagramsByUser(1L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(0)));

        verify(tacticalDiagramService).getTacticalDiagramsByUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTacticalDiagrams_ShouldReturnRecentDiagrams() throws Exception {
        // Arrange
        List<TacticalDiagramResponse> diagrams = Arrays.asList(testDiagram3, testDiagram2);
        when(tacticalDiagramService.getRecentTacticalDiagrams(1L, 2)).thenReturn(diagrams);

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/recent")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].title").value("Diagram 3"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Diagram 2"));

        verify(tacticalDiagramService).getRecentTacticalDiagrams(1L, 2);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTacticalDiagrams_ShouldUseDefaultLimit_WhenNoLimitProvided() throws Exception {
        // Arrange
        List<TacticalDiagramResponse> diagrams = Arrays.asList(testDiagram3, testDiagram2, testDiagram1);
        when(tacticalDiagramService.getRecentTacticalDiagrams(1L, 10)).thenReturn(diagrams);

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3)));

        verify(tacticalDiagramService).getRecentTacticalDiagrams(1L, 10);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTacticalDiagrams_ShouldReturnEmptyList_WhenUserHasNoDiagrams() throws Exception {
        // Arrange
        when(tacticalDiagramService.getRecentTacticalDiagrams(1L, 5)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/recent")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(0)));

        verify(tacticalDiagramService).getRecentTacticalDiagrams(1L, 5);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserTacticalDiagrams_ShouldHandleServiceException() throws Exception {
        // Arrange
        when(tacticalDiagramService.getTacticalDiagramsByUser(1L))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/user"))
                .andExpect(status().isInternalServerError());

        verify(tacticalDiagramService).getTacticalDiagramsByUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTacticalDiagrams_ShouldHandleServiceException() throws Exception {
        // Arrange
        when(tacticalDiagramService.getRecentTacticalDiagrams(1L, 10))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/tactical-diagrams/recent"))
                .andExpect(status().isInternalServerError());

        verify(tacticalDiagramService).getRecentTacticalDiagrams(1L, 10);
    }
}
