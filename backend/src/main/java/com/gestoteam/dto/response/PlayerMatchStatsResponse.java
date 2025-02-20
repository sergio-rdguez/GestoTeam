package com.gestoteam.dto.response;

import lombok.Data;

@Data
public class PlayerMatchStatsResponse {
    private Long id;
    private Long playerId;
    private String playerFullName;
    private int goals;
    private int minutesPlayed;
    private boolean yellowCard;
    private boolean doubleYellowCard;
    private boolean redCard;
    private int goalsConceded;
    private int ownGoals;
    private boolean calledUp;
    private boolean starter;
}
