package com.gestoteam.dto.response;

import com.gestoteam.enums.PlayerStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TeamPlayerSummaryResponse {
    private int totalPlayers;
    private Map<String, Long> statusCount;
    private String teamName;
    private Long teamId;
    private List<PlayerSummary> players;

    @Data
    public static class PlayerSummary {
        private long id;
        private String fullName;
        private String photoUrl;
        private int number;
        private String position;
        private int positionOrder;
        private String foot;
        private PlayerStatus status;
    }
}
