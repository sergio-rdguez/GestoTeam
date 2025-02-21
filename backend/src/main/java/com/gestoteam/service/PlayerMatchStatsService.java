package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.model.Match;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerMatchStatsService {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;

    public PlayerMatchStatsResponse getPlayerMatchStatsById(Long id) {
        PlayerMatchStats stats = playerMatchStatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlayerMatchStats not found"));
        return modelMapper.map(stats, PlayerMatchStatsResponse.class);
    }

    public PlayerMatchStatsResponse updatePlayerMatchStats(Long id, PlayerMatchStatsRequest request) {
        PlayerMatchStats existingStats = playerMatchStatsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlayerMatchStats not found"));
        existingStats.setGoals(request.getGoals());
        existingStats.setMinutesPlayed(request.getMinutesPlayed());
        existingStats.setYellowCard(request.isYellowCard());
        existingStats.setDoubleYellowCard(request.isDoubleYellowCard());
        existingStats.setRedCard(request.isRedCard());
        existingStats.setGoalsConceded(request.getGoalsConceded());
        existingStats.setOwnGoals(request.getOwnGoals());
        existingStats.setCalledUp(request.isCalledUp());
        existingStats.setStarter(request.isStarter());
        PlayerMatchStats savedStats = playerMatchStatsRepository.save(existingStats);
        return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
    }

    public PlayerMatchStatsResponse createPlayerMatchStats(PlayerMatchStatsRequest request) {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setGoals(request.getGoals());
        stats.setMinutesPlayed(request.getMinutesPlayed());
        stats.setYellowCard(request.isYellowCard());
        stats.setDoubleYellowCard(request.isDoubleYellowCard());
        stats.setRedCard(request.isRedCard());
        stats.setGoalsConceded(request.getGoalsConceded());
        stats.setOwnGoals(request.getOwnGoals());
        stats.setCalledUp(request.isCalledUp());
        stats.setStarter(request.isStarter());
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found"));
        stats.setMatch(match);
        stats.setPlayer(player);
        PlayerMatchStats savedStats = playerMatchStatsRepository.save(stats);
        return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
    }
}
