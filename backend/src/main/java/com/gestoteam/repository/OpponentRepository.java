package com.gestoteam.repository;

import com.gestoteam.model.Opponent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OpponentRepository extends JpaRepository<Opponent, Long> {
    List<Opponent> findByTeamId(Long teamId);
}