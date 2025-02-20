package com.gestoteam.service;

import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerMatchStatsService {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final ModelMapper modelMapper;

    public PlayerMatchStats getPlayerMatchStatsById(Long id) {
        return playerMatchStatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlayerMatchStats not found"));
    }

    public PlayerMatchStats updatePlayerMatchStats(Long id, PlayerMatchStats updatedStats) {
        PlayerMatchStats existingStats = playerMatchStatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlayerMatchStats not found"));
        modelMapper.map(updatedStats, existingStats);
        return playerMatchStatsRepository.save(existingStats);
    }
}
