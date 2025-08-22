package com.gestoteam.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrainingRequest {
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime date;
    
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(min = 3, max = 255, message = "La ubicación debe tener entre 3 y 255 caracteres")
    private String location;
    
    @NotBlank(message = "El tipo de entrenamiento es obligatorio")
    @Size(min = 3, max = 255, message = "El tipo de entrenamiento debe tener entre 3 y 255 caracteres")
    private String trainingType;
    
    @NotNull(message = "El equipo es obligatorio")
    private Long teamId;
    
    private List<Long> exerciseIds;
    
    private String observations;
}
