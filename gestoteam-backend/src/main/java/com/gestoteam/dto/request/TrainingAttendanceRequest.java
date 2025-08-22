package com.gestoteam.dto.request;

import com.gestoteam.enums.AttendanceStatus;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class TrainingAttendanceRequest {
    
    @NotNull(message = "El ID del jugador es obligatorio")
    private Long playerId;
    
    @NotNull(message = "El estado de asistencia es obligatorio")
    private AttendanceStatus status;
    
    private String notes; // Notas adicionales sobre la asistencia
}

@Data
class BulkAttendanceRequest {
    
    @NotNull(message = "La lista de asistencias es obligatoria")
    private List<TrainingAttendanceRequest> attendanceList;
}
