package com.gestoteam.dto.response;

import com.gestoteam.enums.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingAttendanceResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private String playerSurname;
    private String playerFullName;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
