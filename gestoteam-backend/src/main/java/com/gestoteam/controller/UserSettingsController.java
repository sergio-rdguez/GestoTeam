package com.gestoteam.controller;

import com.gestoteam.model.UserSettings;
import com.gestoteam.service.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
@Tag(name = "Configuración de Usuario", description = "API para gestionar la configuración personalizada del usuario")
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;

    @GetMapping
    @Operation(summary = "Obtener la configuración del usuario", description = "Devuelve la configuración actual del usuario autenticado. Si no existe, crea y devuelve una por defecto.")
    @ApiResponse(responseCode = "200", description = "Configuración obtenida con éxito")
    public UserSettings getSettings() {
        return userSettingsService.getSettings();
    }

    @PutMapping
    @Operation(summary = "Actualizar la configuración del usuario", description = "Actualiza la configuración del usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Configuración actualizada con éxito")
    public UserSettings updateSettings(@RequestBody UserSettings updatedSettings) {
        return userSettingsService.updateSettings(updatedSettings);
    }
}