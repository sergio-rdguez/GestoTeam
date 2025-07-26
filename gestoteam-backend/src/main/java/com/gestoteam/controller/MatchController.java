package com.gestoteam.controller;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
@Tag(name = "Gestión de Partidos", description = "API para las operaciones de partidos")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    @Operation(summary = "Crear un nuevo partido")
    @ApiResponse(responseCode = "200", description = "Partido creado con éxito")
    public MatchResponse createMatch(@RequestBody MatchRequest matchRequest) {
        return matchService.createMatch(matchRequest);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Obtener los partidos de un equipo")
    @ApiResponse(responseCode = "200", description = "Lista de partidos obtenida")
    public List<MatchResponse> getMatchesByTeam(@PathVariable Long teamId) {
        return matchService.getMatchesByTeam(teamId);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "Obtener los detalles de un partido")
    @ApiResponse(responseCode = "200", description = "Detalles del partido obtenidos")
    @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    public MatchDetailsResponse getMatchDetailsById(@PathVariable Long id) {
        return matchService.getMatchDetailsById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un partido")
    @ApiResponse(responseCode = "200", description = "Partido actualizado")
    @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    public MatchResponse updateMatch(@PathVariable Long id, @RequestBody MatchUpdateRequest matchRequest) {
        return matchService.updateMatch(id, matchRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un partido (borrado lógico)")
    @ApiResponse(responseCode = "200", description = "Partido eliminado")
    @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
    }
}