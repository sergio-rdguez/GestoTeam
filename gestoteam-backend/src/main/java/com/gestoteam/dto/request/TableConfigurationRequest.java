package com.gestoteam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableConfigurationRequest {
    
    @NotBlank(message = "El nombre de la tabla es obligatorio")
    @Size(max = 100, message = "El nombre de la tabla no puede exceder 100 caracteres")
    private String tableName;

    @NotNull(message = "El tamaño de página es obligatorio")
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = 1000, message = "El tamaño de página no puede exceder 1000")
    private Integer pageSize;

    @Size(max = 100, message = "La clave de ordenación no puede exceder 100 caracteres")
    private String defaultSortKey;

    @Pattern(regexp = "^(ASC|DESC)$", message = "El orden de clasificación debe ser ASC o DESC")
    @Builder.Default
    private String defaultSortOrder = "ASC";

    @Valid
    @NotEmpty(message = "La configuración de columnas es obligatoria")
    private List<TableColumnConfigurationRequest> columnConfigurations;
}
