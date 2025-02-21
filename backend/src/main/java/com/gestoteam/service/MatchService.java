package com.gestoteam.service;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.model.Match;
import com.gestoteam.model.Team;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.util.GlobalUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final GlobalUtil globalUtil;

    public MatchResponse createMatch(MatchRequest request) {
        Match match = modelMapper.map(request, Match.class);
        match.setId(null);
        if (match.getPlayerStats() == null) {
            match.setPlayerStats(new ArrayList<>());
        }
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));
        match.setTeam(team);
        match.setSeason(globalUtil.getCurrentSeason());
        Match savedMatch = matchRepository.save(match);
        return modelMapper.map(savedMatch, MatchResponse.class);
    }

    public List<MatchResponse> getMatchesByTeam(Long teamId) {
        List<Match> matches = matchRepository.findByTeamIdAndSeason_IdAndDeletedFalse(
                teamId, globalUtil.getCurrentSeason().getId());
        return matches.stream()
                .sorted(Comparator.comparing(Match::getDate).reversed())
                .map(match -> modelMapper.map(match, MatchResponse.class))
                .collect(Collectors.toList());
    }

    public MatchResponse updateMatch(Long id, MatchUpdateRequest request) {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        if (request.getDate() != null) {
            existingMatch.setDate(request.getDate());
        }
        if (request.getOpponent() != null) {
            existingMatch.setOpponent(request.getOpponent());
        }
        if (request.getLocation() != null) {
            existingMatch.setLocation(request.getLocation());
        }
        if (request.getResult() != null) {
            existingMatch.setResult(request.getResult());
        }
        existingMatch.setWon(request.isWon());
        Match savedMatch = matchRepository.save(existingMatch);
        return modelMapper.map(savedMatch, MatchResponse.class);
    }


    public void deleteMatch(Long id) {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        existingMatch.setDeleted(true);
        matchRepository.save(existingMatch);
    }
    
    public MatchDetailsResponse getMatchDetailsById(Long id) {
        Match match = matchRepository.findById(id)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new RuntimeException("Match not found"));
        Team team = match.getTeam();
        MatchDetailsResponse response = modelMapper.map(match, MatchDetailsResponse.class);
        TeamResponse teamResponse = modelMapper.map(team, TeamResponse.class);
        response.setTeam(teamResponse);
        response.setPlayerStats(match.getPlayerStats().stream().map(stats -> {
            var dto = modelMapper.map(stats, com.gestoteam.dto.response.PlayerMatchStatsResponse.class);
            dto.setPlayerId(stats.getPlayer().getId());
            dto.setPlayerFullName(stats.getPlayer().getFullName());
            return dto;
        }).collect(Collectors.toList()));
        
        return response;
    }
}
