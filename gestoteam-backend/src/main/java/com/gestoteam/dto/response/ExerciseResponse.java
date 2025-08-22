package com.gestoteam.dto.response;

import com.gestoteam.enums.ExerciseCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExerciseResponse {
    private Long id;
    private String title;
    private String description;
    private String tacticalObjectives;
    private String technicalObjectives;
    private String physicalObjectives;
    private String materials;
    private ExerciseCategory category;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Información del diagrama táctico
    private Long tacticalDiagramId;
    private String tacticalDiagramTitle;
    private String tacticalDiagramImageUrl;
}
