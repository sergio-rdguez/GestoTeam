package com.gestoteam.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class TeamRequest {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @NotNull(message = "El campo es obligatorio")
    @Size(max = 30, message = "El campo no puede superar los 30 caracteres")
    private String field;

    @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres")
    private String location;

    @NotNull(message = "La división es obligatoria")
    @Size(max = 30, message = "La división no puede superar los 30 caracteres")
    private String division;

    @NotNull(message = "La Categoría es obligatoria")
    private String category;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String description;
}
