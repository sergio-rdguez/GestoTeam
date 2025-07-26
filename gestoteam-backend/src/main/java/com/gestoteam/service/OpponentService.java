package com.gestoteam.service;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Team;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpponentService {

    private final OpponentRepository opponentRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public OpponentResponse createOpponent(OpponentRequest request) {
        String username = getCurrentUsername();
        log.info("Creando un oponente para el equipo con ID: {} por el usuario: {}", request.getTeamId(), username);

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(request.getTeamId(), username)
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él."));

        Opponent opponent = modelMapper.map(request, Opponent.class);
        opponent.setTeam(team);

        try {
            Opponent savedOpponent = opponentRepository.save(opponent);
            log.info("Oponente creado con ID: {} para el equipo: {} por el usuario: {}", savedOpponent.getId(), team.getId(), username);
            return modelMapper.map(savedOpponent, OpponentResponse.class);
        } catch (Exception e) {
            log.error("Error al crear el oponente para el equipo: {} por el usuario: {}", team.getId(), username, e);
            throw new GestoServiceException("Error al crear el oponente. Por favor, inténtelo más tarde.");
        }
    }

    public List<OpponentResponse> getOpponentsByTeam(Long teamId) {
        String username = getCurrentUsername();
        log.info("Obteniendo oponentes para el equipo con ID: {} por el usuario: {}", teamId, username);

        if (!teamRepository.existsByIdAndOwnerIdAndDeletedFalse(teamId, username)) {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, username);
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        }

        List<Opponent> opponents = opponentRepository.findByTeamId(teamId);
        return opponents.stream()
                .map(opponent -> modelMapper.map(opponent, OpponentResponse.class))
                .collect(Collectors.toList());
    }
}