package com.gestoteam.repository;

import com.gestoteam.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTeamIdAndSeason_IdAndDeletedFalse(Long teamId, Long seasonId);

}
