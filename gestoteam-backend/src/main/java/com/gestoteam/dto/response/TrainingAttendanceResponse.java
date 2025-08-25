package com.gestoteam.dto.response;

import com.gestoteam.enums.AttendanceStatus;
import com.gestoteam.enums.Position;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingAttendanceResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private String playerSurname;
    private String playerFullName;
    private String photoPath;
    private String position;
    private int positionOrder;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
