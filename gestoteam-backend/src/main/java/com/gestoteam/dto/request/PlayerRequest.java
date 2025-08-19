package com.gestoteam.dto.request;

import com.gestoteam.enums.Foot;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class PlayerRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no puede superar los 50 caracteres")
    private String surnameFirst;

    @Size(max = 50, message = "El segundo apellido no puede superar los 50 caracteres")
    private String surnameSecond;

    @NotNull(message = "La posición es obligatoria")
    private Position position;

    private Foot foot;

    @Positive(message = "El número debe ser un valor positivo")
    private int number;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    @NotNull(message = "El estado es obligatorio")
    private PlayerStatus status;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Long teamId;
}
