package com.gestoteam.service;

import com.gestoteam.dto.request.TableConfigurationRequest;
import com.gestoteam.dto.request.TableColumnConfigurationRequest;
import com.gestoteam.dto.response.TableColumnConfigurationResponse;
import com.gestoteam.dto.response.TableConfigurationResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.TableConfiguration;
import com.gestoteam.model.TableColumnConfiguration;
import com.gestoteam.repository.TableConfigurationRepository;
import com.gestoteam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableConfigurationServiceTest {

    @Mock
    private TableConfigurationRepository tableConfigurationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TableConfigurationService tableConfigurationService;

    private TableConfiguration mockTableConfiguration;
    private TableConfigurationRequest mockRequest;
    private TableConfigurationResponse mockResponse;
    private List<TableColumnConfigurationResponse> mockColumnResponses;

    @BeforeEach
    void setUp() {
        // Configurar mocks de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");
        
        // Configurar mock de usuario
        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(new com.gestoteam.model.User() {{
                setId(1L);
            }}));

        // Configurar mock de configuración
        mockTableConfiguration = TableConfiguration.builder()
            .id(1L)
            .userId(1L)
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .columnConfigurations(Arrays.asList(
                TableColumnConfiguration.builder()
                    .id(1L)
                    .columnKey("name")
                    .columnLabel("Nombre")
                    .visible(true)
                    .sortable(true)
                    .sortOrder(1)
                    .build()
            ))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        // Configurar mock de request
        mockRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(25)
            .defaultSortKey("category")
            .defaultSortOrder("DESC")
            .columnConfigurations(Arrays.asList(
                TableColumnConfigurationRequest.builder()
                    .columnKey("name")
                    .columnLabel("Nombre")
                    .visible(true)
                    .sortable(true)
                    .sortOrder(1)
                    .build()
            ))
            .build();

        // Configurar mock de response
        mockResponse = TableConfigurationResponse.builder()
            .id(1L)
            .tableName("teams")
            .pageSize(25)
            .defaultSortKey("category")
            .defaultSortOrder("DESC")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        // Configurar mock de columnas response
        mockColumnResponses = Arrays.asList(
            TableColumnConfigurationResponse.builder()
                .columnKey("name")
                .columnLabel("Nombre")
                .visible(true)
                .sortable(true)
                .sortOrder(1)
                .build()
        );


    }

    @Test
    void getTableConfiguration_ExistingConfiguration_ReturnsConfiguration() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenReturn(Optional.of(mockTableConfiguration));

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("teams");

        // Assert
        assertNotNull(result);
        assertEquals("teams", result.getTableName());
        verify(tableConfigurationRepository).findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams");
    }

    @Test
    void getTableConfiguration_NonExistingConfiguration_CreatesDefault() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenReturn(Optional.empty());
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("teams");

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void saveTableConfiguration_NewConfiguration_SavesSuccessfully() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenReturn(Optional.empty());
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.saveTableConfiguration(mockRequest);

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void saveTableConfiguration_ExistingConfiguration_UpdatesSuccessfully() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenReturn(Optional.of(mockTableConfiguration));
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.saveTableConfiguration(mockRequest);

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void getAllTableConfigurations_ReturnsList() {
        // Arrange
        List<TableConfiguration> configs = Arrays.asList(mockTableConfiguration);
        when(tableConfigurationRepository.findByUserIdOrderByTableNameAsc(1L))
            .thenReturn(configs);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        List<TableConfigurationResponse> result = tableConfigurationService.getAllTableConfigurations();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tableConfigurationRepository).findByUserIdOrderByTableNameAsc(1L);
    }

    @Test
    void deleteTableConfiguration_DeletesSuccessfully() {
        // Arrange
        doNothing().when(tableConfigurationRepository).deleteByUserIdAndTableName(1L, "teams");

        // Act
        tableConfigurationService.deleteTableConfiguration("teams");

        // Assert
        verify(tableConfigurationRepository).deleteByUserIdAndTableName(1L, "teams");
    }

    @Test
    void getTableConfiguration_WithException_ThrowsGestoServiceException() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(GestoServiceException.class, () -> {
            tableConfigurationService.getTableConfiguration("teams");
        });
    }

    @Test
    void saveTableConfiguration_WithException_ThrowsGestoServiceException() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "teams"))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(GestoServiceException.class, () -> {
            tableConfigurationService.saveTableConfiguration(mockRequest);
        });
    }

    @Test
    void createDefaultConfiguration_ForTeams_CreatesCorrectConfiguration() {
        // Arrange
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("teams");

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void createDefaultConfiguration_ForPlayers_CreatesCorrectConfiguration() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "players"))
            .thenReturn(Optional.empty());
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("players");

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void createDefaultConfiguration_ForMatches_CreatesCorrectConfiguration() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "matches"))
            .thenReturn(Optional.empty());
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("matches");

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }

    @Test
    void createDefaultConfiguration_ForUnknownTable_CreatesGenericConfiguration() {
        // Arrange
        when(tableConfigurationRepository.findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "unknown"))
            .thenReturn(Optional.empty());
        when(tableConfigurationRepository.save(any(TableConfiguration.class)))
            .thenReturn(mockTableConfiguration);

        when(modelMapper.map(mockTableConfiguration, TableConfigurationResponse.class))
            .thenReturn(mockResponse);
        when(modelMapper.map(any(TableColumnConfiguration.class), eq(TableColumnConfigurationResponse.class)))
            .thenReturn(mockColumnResponses.get(0));

        // Act
        TableConfigurationResponse result = tableConfigurationService.getTableConfiguration("unknown");

        // Assert
        assertNotNull(result);
        verify(tableConfigurationRepository).save(any(TableConfiguration.class));
    }
}
