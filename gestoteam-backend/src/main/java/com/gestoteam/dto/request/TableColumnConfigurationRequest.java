package com.gestoteam.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnConfigurationRequest {
    
    @NotBlank(message = "La clave de columna es obligatoria")
    @Size(max = 100, message = "La clave de columna no puede exceder 100 caracteres")
    private String columnKey;

    @NotBlank(message = "La etiqueta de columna es obligatoria")
    @Size(max = 100, message = "La etiqueta de columna no puede exceder 100 caracteres")
    private String columnLabel;

    @NotNull(message = "La visibilidad de la columna es obligatoria")
    private Boolean visible;

    @NotNull(message = "La capacidad de ordenación de la columna es obligatoria")
    private Boolean sortable;

    @Min(value = 1, message = "El orden de clasificación debe ser al menos 1")
    private Integer sortOrder;

    @Size(max = 50, message = "El ancho de columna no puede exceder 50 caracteres")
    private String width;
}
