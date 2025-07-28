package com.gestoteam.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchUpdateRequest {

    @NotNull(message = "El ID del oponente es obligatorio")
    private Long opponentId;

    private String location;
    
    @NotNull(message = "La fecha es obligatoria")
    private String date;

    @Min(value = 0, message = "Los goles a favor no pueden ser negativos")
    private Integer goalsFor;

    @Min(value = 0, message = "Los goles en contra no pueden ser negativos")
    private Integer goalsAgainst;

    private boolean finalized;
}