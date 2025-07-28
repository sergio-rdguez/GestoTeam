package com.gestoteam.dto.request;

import lombok.Data;

@Data
public class PlayerMatchStatsRequest {
    private Long matchId;
    private Long playerId;
    private int goals;
    private int assists;
    private int minutesPlayed;
    private boolean yellowCard;
    private boolean doubleYellowCard;
    private boolean redCard;
    private int goalsConceded;
    private int ownGoals;
    private boolean calledUp;
    private boolean starter;
}
