package com.gestoteam.dto.request;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import lombok.Data;

import javax.validation.constraints.*;

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

    @Positive(message = "El número debe ser un valor positivo")
    private int number;

    @Positive(message = "La edad debe ser un valor positivo")
    private int age;

    @NotNull(message = "El estado es obligatorio")
    private PlayerStatus status;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Long teamId;
}
