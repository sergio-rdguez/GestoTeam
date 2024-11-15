package com.gestoteam.dto.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class TeamRequest {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String description;

    @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres")
    private String location;

    @NotNull(message = "La división es obligatoria")
    @Size(max = 30, message = "La división no puede superar los 30 caracteres")
    private String division;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;
}
