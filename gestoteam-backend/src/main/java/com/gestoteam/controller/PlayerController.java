package com.gestoteam.controller;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "Gestión de Jugadores", description = "API para las operaciones CRUD de jugadores")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un jugador por su ID", description = "Devuelve los detalles y estadísticas de un jugador específico.")
    @ApiResponse(responseCode = "200", description = "Jugador encontrado")
    @ApiResponse(responseCode = "404", description = "Jugador no encontrado o no pertenece al usuario")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo jugador", description = "Crea un nuevo jugador y lo asocia a un equipo del usuario.")
    @ApiResponse(responseCode = "201", description = "Jugador creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o límite de jugadores alcanzado")
    public ResponseEntity<Long> createPlayer(@Valid @RequestBody PlayerRequest playerRequest) {
        Long playerId = playerService.createPlayer(playerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(playerId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Actualizar un jugador existente", description = "Actualiza los datos de un jugador.")
    @ApiResponse(responseCode = "204", description = "Jugador actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Jugador no encontrado o no pertenece al usuario")
    public void updatePlayer(@PathVariable Long id, @Valid @RequestBody PlayerRequest playerRequest) {
        playerService.updatePlayer(id, playerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un jugador (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Jugador eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Obtener todos los jugadores de un equipo", description = "Devuelve un resumen de los jugadores de un equipo específico.")
    @ApiResponse(responseCode = "200", description = "Lista de jugadores obtenida")
    @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    public TeamPlayerSummaryResponse getPlayersByTeamId(@PathVariable Long teamId) {
        return playerService.getPlayersByTeamId(teamId);
    }
}