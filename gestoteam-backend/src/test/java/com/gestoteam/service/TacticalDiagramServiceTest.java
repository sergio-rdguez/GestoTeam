package com.gestoteam.service;

import com.gestoteam.dto.response.TacticalDiagramResponse;
import com.gestoteam.model.TacticalDiagram;
import com.gestoteam.model.User;
import com.gestoteam.repository.TacticalDiagramRepository;
import com.gestoteam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TacticalDiagramServiceTest {

    @Mock
    private TacticalDiagramRepository tacticalDiagramRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TacticalDiagramService tacticalDiagramService;

    private User testUser;
    private TacticalDiagram testDiagram1;
    private TacticalDiagram testDiagram2;
    private TacticalDiagram testDiagram3;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testDiagram1 = new TacticalDiagram();
        testDiagram1.setId(1L);
        testDiagram1.setTitle("Diagram 1");
        testDiagram1.setDescription("First diagram");
        testDiagram1.setFilename("diagram1.png");
        testDiagram1.setFilePath("/uploads/diagram1.png");
        testDiagram1.setCreatedBy(testUser);
        testDiagram1.setCreatedAt(LocalDateTime.now().minusDays(2));
        testDiagram1.setUpdatedAt(LocalDateTime.now().minusDays(2));
        testDiagram1.setDeleted(false);

        testDiagram2 = new TacticalDiagram();
        testDiagram2.setId(2L);
        testDiagram2.setTitle("Diagram 2");
        testDiagram2.setDescription("Second diagram");
        testDiagram2.setFilename("diagram2.png");
        testDiagram2.setFilePath("/uploads/diagram2.png");
        testDiagram2.setCreatedBy(testUser);
        testDiagram2.setCreatedAt(LocalDateTime.now().minusDays(1));
        testDiagram2.setUpdatedAt(LocalDateTime.now().minusDays(1));
        testDiagram2.setDeleted(false);

        testDiagram3 = new TacticalDiagram();
        testDiagram3.setId(3L);
        testDiagram3.setTitle("Diagram 3");
        testDiagram3.setDescription("Third diagram");
        testDiagram3.setFilename("diagram3.png");
        testDiagram3.setFilePath("/uploads/diagram3.png");
        testDiagram3.setCreatedBy(testUser);
        testDiagram3.setCreatedAt(LocalDateTime.now());
        testDiagram3.setUpdatedAt(LocalDateTime.now());
        testDiagram3.setDeleted(false);
    }

    @Test
    void getTacticalDiagramsByUser_ShouldReturnUserDiagrams() {
        // Arrange
        List<TacticalDiagram> diagrams = Arrays.asList(testDiagram1, testDiagram2, testDiagram3);
        when(tacticalDiagramRepository.findByCreatedByIdAndDeletedFalse(1L))
                .thenReturn(diagrams);

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getTacticalDiagramsByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Diagram 1", result.get(0).getTitle());
        assertEquals("Diagram 2", result.get(1).getTitle());
        assertEquals("Diagram 3", result.get(2).getTitle());
        verify(tacticalDiagramRepository).findByCreatedByIdAndDeletedFalse(1L);
    }

    @Test
    void getTacticalDiagramsByUser_ShouldReturnEmptyList_WhenUserHasNoDiagrams() {
        // Arrange
        when(tacticalDiagramRepository.findByCreatedByIdAndDeletedFalse(1L))
                .thenReturn(Arrays.asList());

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getTacticalDiagramsByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(tacticalDiagramRepository).findByCreatedByIdAndDeletedFalse(1L);
    }

    @Test
    void getRecentTacticalDiagrams_ShouldReturnRecentDiagrams() {
        // Arrange
        List<TacticalDiagram> diagrams = Arrays.asList(testDiagram3, testDiagram2, testDiagram1);
        when(tacticalDiagramRepository.findRecentByUser(1L))
                .thenReturn(diagrams);

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getRecentTacticalDiagrams(1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Diagram 3", result.get(0).getTitle()); // Más reciente
        assertEquals("Diagram 2", result.get(1).getTitle()); // Segundo más reciente
        verify(tacticalDiagramRepository).findRecentByUser(1L);
    }

    @Test
    void getRecentTacticalDiagrams_ShouldReturnAllDiagrams_WhenLimitIsGreaterThanAvailable() {
        // Arrange
        List<TacticalDiagram> diagrams = Arrays.asList(testDiagram3, testDiagram2, testDiagram1);
        when(tacticalDiagramRepository.findRecentByUser(1L))
                .thenReturn(diagrams);

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getRecentTacticalDiagrams(1L, 10);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tacticalDiagramRepository).findRecentByUser(1L);
    }

    @Test
    void getRecentTacticalDiagrams_ShouldReturnEmptyList_WhenUserHasNoDiagrams() {
        // Arrange
        when(tacticalDiagramRepository.findRecentByUser(1L))
                .thenReturn(Arrays.asList());

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getRecentTacticalDiagrams(1L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(tacticalDiagramRepository).findRecentByUser(1L);
    }

    @Test
    void mapToResponse_ShouldMapCorrectly() {
        // Arrange
        when(tacticalDiagramRepository.findByCreatedByIdAndDeletedFalse(1L))
                .thenReturn(Arrays.asList(testDiagram1));

        // Act
        List<TacticalDiagramResponse> result = tacticalDiagramService.getTacticalDiagramsByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TacticalDiagramResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals("Diagram 1", response.getTitle());
        assertEquals("First diagram", response.getDescription());
        assertEquals("/uploads/diagram1.png", response.getImageUrl());
        assertEquals("testuser", response.getCreatedByUsername());
        assertEquals(1L, response.getCreatedById());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }
}
