package com.gestoteam.service;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Team;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpponentService {

    private final OpponentRepository opponentRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    public OpponentResponse createOpponent(OpponentRequest request) {
        Opponent opponent = modelMapper.map(request, Opponent.class);
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));
        opponent.setTeam(team);
        Opponent savedOpponent = opponentRepository.save(opponent);
        return modelMapper.map(savedOpponent, OpponentResponse.class);
    }

    public List<OpponentResponse> getOpponentsByTeam(Long teamId) {
        List<Opponent> opponents = opponentRepository.findByTeamId(teamId);
        return opponents.stream()
                .map(opponent -> modelMapper.map(opponent, OpponentResponse.class))
                .collect(Collectors.toList());
    }
}
