package com.gestoteam.controller;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.service.OpponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opponents")
@Tag(name = "Gestión de Rivales", description = "API para gestionar los equipos rivales")
public class OpponentController {

    private final OpponentService opponentService;

    @PostMapping
    @Operation(summary = "Crear un nuevo rival", description = "Crea un nuevo equipo rival y lo asocia a uno de tus equipos.")
    @ApiResponse(responseCode = "200", description = "Rival creado con éxito")
    @ApiResponse(responseCode = "404", description = "El equipo especificado no se encuentra")
    public OpponentResponse createOpponent(@RequestBody OpponentRequest request) {
        return opponentService.createOpponent(request);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Obtener los rivales de un equipo", description = "Devuelve una lista de todos los rivales asociados a un equipo específico.")
    @ApiResponse(responseCode = "200", description = "Lista de rivales obtenida con éxito")
    @ApiResponse(responseCode = "404", description = "El equipo especificado no se encuentra")
    public List<OpponentResponse> getOpponentsByTeam(@PathVariable Long teamId) {
        return opponentService.getOpponentsByTeam(teamId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un rival por ID", description = "Devuelve los detalles de un rival específico.")
    @ApiResponse(responseCode = "200", description = "Rival obtenido con éxito")
    @ApiResponse(responseCode = "404", description = "Rival no encontrado")
    public OpponentResponse getOpponentById(@PathVariable Long id) {
        return opponentService.getOpponentById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un rival", description = "Actualiza los datos de un rival existente.")
    @ApiResponse(responseCode = "200", description = "Rival actualizado con éxito")
    @ApiResponse(responseCode = "404", description = "Rival no encontrado")
    public OpponentResponse updateOpponent(@PathVariable Long id, @RequestBody OpponentRequest request) {
        return opponentService.updateOpponent(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rival", description = "Elimina un rival existente.")
    @ApiResponse(responseCode = "200", description = "Rival eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Rival no encontrado")
    public void deleteOpponent(@PathVariable Long id) {
        opponentService.deleteOpponent(id);
    }
}