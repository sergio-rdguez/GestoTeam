package com.gestoteam.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private String opponent;
    private String location;
    private String result;
    private boolean deleted = false;
    private boolean won;
    private boolean finalized = false;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerMatchStats> playerStats = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
