package com.gestoteam.controller;

import com.gestoteam.dto.response.TacticalDiagramResponse;
import com.gestoteam.service.TacticalDiagramService;
import com.gestoteam.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tactical-diagrams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Diagramas Tácticos", description = "API para gestión de diagramas tácticos")
public class TacticalDiagramController {

    private final TacticalDiagramService tacticalDiagramService;

    private final FileService fileService;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return fileService.getCurrentUserId(username);
    }

    @GetMapping("/user")
    @Operation(
        summary = "Obtener diagramas tácticos del usuario",
        description = "Obtiene todos los diagramas tácticos creados por el usuario actual"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Diagramas obtenidos exitosamente"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<List<TacticalDiagramResponse>> getUserTacticalDiagrams() {
        try {
            Long userId = getCurrentUserId();
            List<TacticalDiagramResponse> diagrams = tacticalDiagramService.getTacticalDiagramsByUser(userId);
            return ResponseEntity.ok(diagrams);
        } catch (Exception e) {
            log.error("Error al obtener diagramas tácticos del usuario: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Obtener diagramas tácticos recientes",
        description = "Obtiene los diagramas tácticos más recientes del usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Diagramas obtenidos exitosamente"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<List<TacticalDiagramResponse>> getRecentTacticalDiagrams(
        @Parameter(description = "Número máximo de diagramas", required = false) @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            Long userId = getCurrentUserId();
            List<TacticalDiagramResponse> diagrams = tacticalDiagramService.getRecentTacticalDiagrams(userId, limit);
            return ResponseEntity.ok(diagrams);
        } catch (Exception e) {
            log.error("Error al obtener diagramas tácticos recientes: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
