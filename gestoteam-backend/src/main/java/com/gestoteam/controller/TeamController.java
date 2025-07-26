package com.gestoteam.controller;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Gestión de Equipos", description = "API para las operaciones CRUD de equipos")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "Obtener todos los equipos del usuario", description = "Devuelve una lista de todos los equipos no eliminados que pertenecen al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de equipos obtenida con éxito")
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un equipo por su ID", description = "Devuelve los detalles de un equipo específico si pertenece al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Equipo encontrado")
    @ApiResponse(responseCode = "404", description = "Equipo no encontrado o no pertenece al usuario")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo equipo", description = "Crea un nuevo equipo y lo asocia al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Equipo creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        return ResponseEntity.ok(teamService.createTeam(teamRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un equipo existente", description = "Actualiza los datos de un equipo existente.")
    @ApiResponse(responseCode = "200", description = "Equipo actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Equipo no encontrado o no pertenece al usuario")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest teamRequest) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un equipo (borrado lógico)", description = "Marca un equipo como eliminado.")
    @ApiResponse(responseCode = "204", description = "Equipo eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Equipo no encontrado o no pertenece al usuario")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}