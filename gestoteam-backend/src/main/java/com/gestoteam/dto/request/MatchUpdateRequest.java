package com.gestoteam.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchUpdateRequest {
    private LocalDateTime date;
    private Long opponentId;
    private String location;
    private String result;
    private boolean finalized;
    private boolean won;
}