package com.gestoteam.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MatchDetailsResponse {
    private Long id;
    private LocalDateTime date;
    private String opponent;
    private String location;
    private String result;
    private boolean won;
    private List<PlayerMatchStatsResponse> playerStats;
}
