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
import com.gestoteam.dto.Audit;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ValidationUtil validationUtil;

    public PlayerMatchStatsResponse getPlayerMatchStatsById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo estadísticas de jugador para el partido con ID: {} por el usuario: {}", id, auditInfo.getUser());

        PlayerMatchStats playerMatchStats = playerMatchStatsRepository.findById(id)
                .filter(stats -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(stats.getMatch().getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Estadísticas con ID: {} no encontradas o no pertenecen al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Estadísticas no encontradas o no tienes permisos para acceder a ellas.");
                });

        log.info("Estadísticas con ID: {} obtenidas correctamente para el usuario: {}", id, auditInfo.getUser());
        return modelMapper.map(playerMatchStats, PlayerMatchStatsResponse.class);
    }

    public PlayerMatchStatsResponse updatePlayerMatchStats(Long id, PlayerMatchStatsRequest request, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando estadísticas de jugador con ID: {} por el usuario: {}", id, auditInfo.getUser());

        PlayerMatchStats existingStats = playerMatchStatsRepository.findById(id)
                .filter(stats -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(stats.getMatch().getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Estadísticas con ID: {} no encontradas o no pertenecen al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Estadísticas no encontradas o no tienes permisos para acceder a ellas.");
                });

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
            log.info("Estadísticas con ID: {} actualizadas correctamente por el usuario: {}", id, auditInfo.getUser());
            return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
        } catch (Exception e) {
            log.error("Error al actualizar las estadísticas con ID: {} por el usuario: {}", id, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al actualizar las estadísticas. Por favor, inténtelo más tarde.");
        }
    }

    public PlayerMatchStatsResponse createPlayerMatchStats(PlayerMatchStatsRequest request, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando estadísticas de jugador para el partido con ID: {} y jugador con ID: {} por el usuario: {}",
                request.getMatchId(), request.getPlayerId(), auditInfo.getUser());

        Match match = matchRepository.findByIdAndDeletedFalse(request.getMatchId())
                .filter(m -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(m.getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Partido con ID: {} no encontrado o no pertenece al usuario: {}", request.getMatchId(), auditInfo.getUser());
                    return new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él.");
                });

        Player player = playerRepository.findById(request.getPlayerId())
                .filter(p -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(p.getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Jugador con ID: {} no encontrado o no pertenece al usuario: {}", request.getPlayerId(), auditInfo.getUser());
                    return new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él.");
                });

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
        stats.setMatch(match);
        stats.setPlayer(player);

        try {
            PlayerMatchStats savedStats = playerMatchStatsRepository.save(stats);
            log.info("Estadísticas creadas con ID: {} para el partido: {} y jugador: {} por el usuario: {}",
                    savedStats.getId(), match.getId(), player.getId(), auditInfo.getUser());
            return modelMapper.map(savedStats, PlayerMatchStatsResponse.class);
        } catch (Exception e) {
            log.error("Error al crear las estadísticas para el partido: {} y jugador: {} por el usuario: {}",
                    request.getMatchId(), request.getPlayerId(), auditInfo.getUser(), e);
            throw new GestoServiceException("Error al crear las estadísticas. Por favor, inténtelo más tarde.");
        }
    }
}