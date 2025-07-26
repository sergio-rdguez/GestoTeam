package com.gestoteam.controller;

import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.service.PlayerMatchStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player-match-stats")
@RequiredArgsConstructor
@Tag(name = "Estadísticas de Jugador por Partido", description = "API para gestionar las estadísticas detalladas de cada jugador en un partido")
public class PlayerMatchStatsController {

    private final PlayerMatchStatsService playerMatchStatsService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una estadística por su ID", description = "Devuelve una única entrada de estadística de jugador-partido por su ID.")
    @ApiResponse(responseCode = "200", description = "Estadística encontrada")
    @ApiResponse(responseCode = "404", description = "Estadística no encontrada")
    public PlayerMatchStatsResponse getPlayerMatchStats(@PathVariable Long id) {
        return playerMatchStatsService.getPlayerMatchStatsById(id);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva estadística", description = "Crea una nueva entrada de estadística para un jugador en un partido específico.")
    @ApiResponse(responseCode = "200", description = "Estadística creada con éxito")
    @ApiResponse(responseCode = "404", description = "El jugador o el partido no se encuentran")
    public PlayerMatchStatsResponse createPlayerMatchStats(@RequestBody PlayerMatchStatsRequest request) {
        return playerMatchStatsService.createPlayerMatchStats(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una estadística existente", description = "Actualiza una entrada de estadística existente por su ID.")
    @ApiResponse(responseCode = "200", description = "Estadística actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Estadística no encontrada")
    public PlayerMatchStatsResponse updatePlayerMatchStats(@PathVariable Long id, @RequestBody PlayerMatchStatsRequest request) {
        return playerMatchStatsService.updatePlayerMatchStats(id, request);
    }
}