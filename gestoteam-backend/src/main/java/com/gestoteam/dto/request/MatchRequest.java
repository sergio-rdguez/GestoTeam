package com.gestoteam.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchRequest {
    @NotNull(message = "El ID del oponente es obligatorio")
    private Long opponentId;

    @NotNull(message = "La fecha del partido es obligatoria")
    @FutureOrPresent(message = "La fecha del partido no puede ser en el pasado")
    private LocalDateTime date;

    private String location;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Long teamId;
}