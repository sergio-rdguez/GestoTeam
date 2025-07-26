package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Match;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerMatchStatsService {

    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public PlayerMatchStatsResponse getPlayerMatchStatsById(Long id) {
        String username = getCurrentUsername();
        log.info("Obteniendo estadísticas de jugador-partido con ID: {} por el usuario: {}", id, username);

        PlayerMatchStats stats = playerMatchStatsRepository.findById(id)
                .filter(s -> s.getMatch().getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Estadísticas no encontradas o no tienes permisos para acceder a ellas."));

        return modelMapper.map(stats, PlayerMatchStatsResponse.class);
    }

    public PlayerMatchStatsResponse createPlayerMatchStats(PlayerMatchStatsRequest request) {
        String username = getCurrentUsername();
        log.info("Creando estadísticas para el partido ID: {} y jugador ID: {} por el usuario: {}",
                request.getMatchId(), request.getPlayerId(), username);

        Match match = matchRepository.findByIdAndDeletedFalse(request.getMatchId())
                .filter(m -> m.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él."));

        Player player = playerRepository.findByIdAndDeletedFalse(request.getPlayerId())
                .filter(p -> p.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él."));

        PlayerMatchStats stats = modelMapper.map(request, PlayerMatchStats.class);
        stats.setMatch(match);
        stats.setPlayer(player);

        try {
            PlayerMatchStats savedStats = playerMatchStatsRepository.save(stats);
            log.info("Estadísticas creadas con ID: {}", savedStats.getId());
            return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
        } catch (Exception e) {
            log.error("Error al crear las estadísticas: {}", e.getMessage());
            throw new GestoServiceException("Error al crear las estadísticas. Por favor, inténtelo más tarde.");
        }
    }

    public PlayerMatchStatsResponse updatePlayerMatchStats(Long id, PlayerMatchStatsRequest request) {
        String username = getCurrentUsername();
        log.info("Actualizando estadísticas de jugador con ID: {} por el usuario: {}", id, username);

        PlayerMatchStats existingStats = playerMatchStatsRepository.findById(id)
                .filter(stats -> stats.getMatch().getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Estadísticas no encontradas o no tienes permisos para acceder a ellas."));

        // Actualizamos los campos necesarios. ModelMapper podría usarse aquí también.
        existingStats.setGoals(request.getGoals());
        existingStats.setMinutesPlayed(request.getMinutesPlayed());
        existingStats.setYellowCard(request.isYellowCard());
        existingStats.setDoubleYellowCard(request.isDoubleYellowCard());
        existingStats.setRedCard(request.isRedCard());
        existingStats.setGoalsConceded(request.getGoalsConceded());
        existingStats.setOwnGoals(request.getOwnGoals());
        existingStats.setCalledUp(request.isCalledUp());
        existingStats.setStarter(request.isStarter());

        try {
            PlayerMatchStats savedStats = playerMatchStatsRepository.save(existingStats);
            log.info("Estadísticas con ID: {} actualizadas correctamente por el usuario: {}", id, username);
            return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
        } catch (Exception e) {
            log.error("Error al actualizar las estadísticas con ID: {} por el usuario: {}", id, username, e);
            throw new GestoServiceException("Error al actualizar las estadísticas. Por favor, inténtelo más tarde.");
        }
    }
}