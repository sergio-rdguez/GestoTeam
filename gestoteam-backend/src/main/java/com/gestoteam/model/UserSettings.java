package com.gestoteam.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "max_players_per_team", nullable = false)
    private Integer maxPlayersPerTeam = 25;

    @Column(name = "max_players_per_match", nullable = false)
    private Integer maxPlayersPerMatch = 18;

    @Column(name = "yellow_cards_for_suspension", nullable = false)
    private Integer yellowCardsForSuspension = 5;

    @Column(name = "max_trainings_per_week", nullable = false)
    private Integer maxTrainingsPerWeek = 4;

    @Column(name = "notify_before_match", nullable = false)
    private Boolean notifyBeforeMatch = true;

    @Column(name = "notify_before_training", nullable = false)
    private Boolean notifyBeforeTraining = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
