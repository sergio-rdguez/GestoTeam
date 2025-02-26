package com.gestoteam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "player_id"})
})
@EntityListeners(AuditingEntityListener.class)
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

    @Column(nullable = false)
    private int goals = 0;

    @Max(90)
    @Column(nullable = false)
    private int minutesPlayed = 0;

    @Column(nullable = false)
    private boolean yellowCard = false;

    @Column(nullable = false)
    private boolean doubleYellowCard = false;

    @Column(nullable = false)
    private boolean redCard = false;

    @Column(nullable = false)
    private int goalsConceded = 0;

    @Column(nullable = false)
    private int ownGoals = 0;

    @Column(nullable = false)
    private boolean calledUp = false;

    @Column(nullable = false)
    private boolean starter = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}