package com.gestoteam.controller;

import com.gestoteam.service.EnumService;
import com.gestoteam.service.EnumService.EnumResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enums")
@Tag(name = "Enumeraciones", description = "API para obtener valores de enumeraciones utilizadas en la aplicación")
public class EnumController {

    private final EnumService enumService;

    @GetMapping("/player-status")
    @Operation(summary = "Obtener los posibles estados de un jugador")
    public List<EnumResponse> getPlayerStatuses() {
        return enumService.getPlayerStatuses();
    }

    @GetMapping("/positions")
    @Operation(summary = "Obtener las posibles posiciones de un jugador")
    public List<EnumResponse> getPositions() {
        return enumService.getPositions();
    }

    @GetMapping("/categories")
    @Operation(summary = "Obtener las posibles categorías de un equipo")
    public List<EnumResponse> getCategories() {
        return enumService.getCategories();
    }

    @GetMapping("/foots")
    @Operation(summary = "Obtener los posibles tipos de pie de un jugador")
    public List<EnumResponse> getFoots() {
        return enumService.getFoots();
    }
}