package com.gestoteam.repository;

import com.gestoteam.model.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {
    List<PlayerMatchStats> findByPlayerIdAndMatch_Season_Id(Long playerId, Long seasonId);
}