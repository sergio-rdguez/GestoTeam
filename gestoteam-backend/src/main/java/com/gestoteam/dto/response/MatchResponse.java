package com.gestoteam.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchResponse {
    private Long id;
    private LocalDateTime date;
    private String opponent;
    private String location;
    private String result;
    private boolean won;
    private boolean finalized;
}
