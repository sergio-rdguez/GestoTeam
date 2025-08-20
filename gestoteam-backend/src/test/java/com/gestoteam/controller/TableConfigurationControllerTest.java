package com.gestoteam.controller;

import com.gestoteam.dto.request.TableConfigurationRequest;
import com.gestoteam.dto.request.TableColumnConfigurationRequest;
import com.gestoteam.dto.response.TableConfigurationResponse;
import com.gestoteam.service.TableConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TableConfigurationControllerTest {

    @Mock
    private TableConfigurationService tableConfigurationService;

    @InjectMocks
    private TableConfigurationController tableConfigurationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TableConfigurationRequest mockRequest;
    private TableConfigurationResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tableConfigurationController).build();
        objectMapper = new ObjectMapper();

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
            .columnConfigurations(Arrays.asList(
                com.gestoteam.dto.response.TableColumnConfigurationResponse.builder()
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
    }

    @Test
    void getTableConfiguration_ValidTableName_ReturnsConfiguration() throws Exception {
        // Arrange
        when(tableConfigurationService.getTableConfiguration("teams"))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/table-configurations/teams")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tableName").value("teams"))
            .andExpect(jsonPath("$.pageSize").value(25))
            .andExpect(jsonPath("$.defaultSortKey").value("category"))
            .andExpect(jsonPath("$.defaultSortOrder").value("DESC"));

        verify(tableConfigurationService).getTableConfiguration("teams");
    }

    @Test
    void getAllTableConfigurations_ReturnsList() throws Exception {
        // Arrange
        List<TableConfigurationResponse> configs = Arrays.asList(mockResponse);
        when(tableConfigurationService.getAllTableConfigurations())
            .thenReturn(configs);

        // Act & Assert
        mockMvc.perform(get("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tableName").value("teams"))
            .andExpect(jsonPath("$[0].pageSize").value(25));

        verify(tableConfigurationService).getAllTableConfigurations();
    }

    @Test
    void saveTableConfiguration_ValidRequest_ReturnsSavedConfiguration() throws Exception {
        // Arrange
        when(tableConfigurationService.saveTableConfiguration(any(TableConfigurationRequest.class)))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tableName").value("teams"))
            .andExpect(jsonPath("$.pageSize").value(25));

        verify(tableConfigurationService).saveTableConfiguration(any(TableConfigurationRequest.class));
    }

    @Test
    void saveTableConfiguration_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("") // Nombre vacío
            .pageSize(0)   // Tamaño de página inválido
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveTableConfiguration_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            // pageSize faltante
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTableConfiguration_ValidTableName_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(tableConfigurationService).deleteTableConfiguration("teams");

        // Act & Assert
        mockMvc.perform(delete("/api/table-configurations/teams")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(tableConfigurationService).deleteTableConfiguration("teams");
    }

    @Test
    void saveTableConfiguration_InvalidSortOrder_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .defaultSortOrder("INVALID") // Orden inválido
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

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveTableConfiguration_InvalidPageSize_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(1001) // Tamaño de página muy grande
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

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveTableConfiguration_EmptyColumnConfigurations_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .columnConfigurations(Arrays.asList()) // Lista vacía
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void saveTableConfiguration_InvalidColumnConfiguration_ReturnsBadRequest() throws Exception {
        // Arrange
        TableConfigurationRequest invalidRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .columnConfigurations(Arrays.asList(
                TableColumnConfigurationRequest.builder()
                    .columnKey("") // Clave vacía
                    .columnLabel("Nombre")
                    .visible(true)
                    .sortable(true)
                    .sortOrder(1)
                    .build()
            ))
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/table-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
