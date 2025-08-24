package com.gestoteam.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrainingResponse {
    private Long id;
    private String title;
    private LocalDateTime date;
    private String location;
    private Integer sessionNumber;
    private String trainingType;
    private Long userId;
    private Long teamId;
    private String teamName;
    private List<ExerciseResponse> exercises;
    private List<TrainingAttendanceResponse> attendance;
    private String observations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
