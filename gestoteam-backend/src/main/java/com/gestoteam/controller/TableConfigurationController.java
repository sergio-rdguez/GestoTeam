package com.gestoteam.controller;

import com.gestoteam.dto.request.TableConfigurationRequest;
import com.gestoteam.dto.response.TableConfigurationResponse;
import com.gestoteam.service.TableConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/table-configurations")
@RequiredArgsConstructor
@Tag(name = "Configuración de Tablas", description = "API para la gestión de configuraciones personalizadas de tablas por usuario")
public class TableConfigurationController {

    private final TableConfigurationService tableConfigurationService;

    @GetMapping("/{tableName}")
    @Operation(summary = "Obtener configuración de tabla", description = "Devuelve la configuración personalizada de una tabla específica para el usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Configuración obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    public ResponseEntity<TableConfigurationResponse> getTableConfiguration(@PathVariable String tableName) {
        TableConfigurationResponse config = tableConfigurationService.getTableConfiguration(tableName);
        return ResponseEntity.ok(config);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las configuraciones", description = "Devuelve todas las configuraciones de tabla del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Configuraciones obtenidas con éxito")
    public ResponseEntity<List<TableConfigurationResponse>> getAllTableConfigurations() {
        List<TableConfigurationResponse> configs = tableConfigurationService.getAllTableConfigurations();
        return ResponseEntity.ok(configs);
    }

    @PostMapping
    @Operation(summary = "Guardar configuración de tabla", description = "Guarda o actualiza la configuración personalizada de una tabla")
    @ApiResponse(responseCode = "200", description = "Configuración guardada con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<TableConfigurationResponse> saveTableConfiguration(@Valid @RequestBody TableConfigurationRequest request) {
        TableConfigurationResponse config = tableConfigurationService.saveTableConfiguration(request);
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/{tableName}")
    @Operation(summary = "Eliminar configuración de tabla", description = "Elimina la configuración personalizada de una tabla")
    @ApiResponse(responseCode = "204", description = "Configuración eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    public ResponseEntity<Void> deleteTableConfiguration(@PathVariable String tableName) {
        tableConfigurationService.deleteTableConfiguration(tableName);
        return ResponseEntity.noContent().build();
    }
}
