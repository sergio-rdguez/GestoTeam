package com.gestoteam.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchRequest {
    private String opponent;
    private LocalDateTime date;
    private String location;
    private String result;
    private boolean finalized;
    private Long teamId;
}
