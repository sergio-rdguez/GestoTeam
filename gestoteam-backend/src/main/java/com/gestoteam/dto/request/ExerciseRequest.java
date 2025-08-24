package com.gestoteam.dto.request;

import com.gestoteam.enums.ExerciseCategory;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ExerciseRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
    private String title;
    
    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String description;
    
    @Size(max = 1000, message = "Los objetivos tácticos no pueden exceder los 1000 caracteres")
    private String tacticalObjectives;
    
    @Size(max = 1000, message = "Los objetivos técnicos no pueden exceder los 1000 caracteres")
    private String technicalObjectives;
    
    @Size(max = 1000, message = "Los objetivos físicos no pueden exceder los 1000 caracteres")
    private String physicalObjectives;
    
    @Size(max = 1000, message = "Los materiales no pueden exceder los 1000 caracteres")
    private String materials;
    
    @NotNull(message = "La categoría es obligatoria")
    private ExerciseCategory category;
    

}
