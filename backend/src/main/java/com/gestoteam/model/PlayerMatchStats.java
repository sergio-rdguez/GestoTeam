package com.gestoteam.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerMatchStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private int goals;

    @Max(90)
    private int minutesPlayed;

    private boolean yellowCard;

    private boolean doubleYellowCard;

    private boolean redCard;

    private int goalsConceded;

    private int ownGoals;

    private boolean calledUp;

    private boolean starter;
}
