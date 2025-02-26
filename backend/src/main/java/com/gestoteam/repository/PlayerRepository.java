package com.gestoteam.repository;

import com.gestoteam.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByDeletedFalse();
    Optional<Player> findByIdAndDeletedFalse(Long id);
    List<Player> findByTeamIdAndDeletedFalse(Long teamId);
    long countByTeamIdAndDeletedFalse(Long teamId);
}