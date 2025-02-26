package com.gestoteam.service;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Match;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.model.Team;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.dto.Audit;
import com.gestoteam.util.GlobalUtil;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final ModelMapper modelMapper;
    private final GlobalUtil globalUtil;
    private final ValidationUtil validationUtil;


    public MatchResponse createMatch(MatchRequest request, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando un partido para el equipo con ID: {} por el usuario: {}", request.getTeamId(), auditInfo.getUser());

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(request.getTeamId(), auditInfo.getUser())
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", request.getTeamId(), auditInfo.getUser());
                    return new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });

        Match match = modelMapper.map(request, Match.class);
        match.setId(null);
        if (match.getPlayerStats() == null) {
            match.setPlayerStats(new ArrayList<>());
        }
        match.setTeam(team);
        match.setSeason(globalUtil.getCurrentSeason());
        match.setDeleted(false); // Aseguramos que se cree como no eliminado

        try {
            Match savedMatch = matchRepository.save(match);
            log.info("Partido creado con ID: {} para el equipo: {} por el usuario: {}", savedMatch.getId(), team.getId(), auditInfo.getUser());
            return modelMapper.map(savedMatch, MatchResponse.class);
        } catch (Exception e) {
            log.error("Error al crear el partido para el equipo: {} por el usuario: {}", team.getId(), auditInfo.getUser(), e);
            throw new GestoServiceException("Error al crear el partido. Por favor, inténtelo más tarde.");
        }
    }

    public List<MatchResponse> getMatchesByTeam(Long teamId, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo partidos para el equipo con ID: {} por el usuario: {}", teamId, auditInfo.getUser());

        if (!teamRepository.existsByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())) {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, auditInfo.getUser());
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        }

        List<Match> matches = matchRepository.findByTeamIdAndSeason_IdAndDeletedFalse(
                teamId, globalUtil.getCurrentSeason().getId());

        List<MatchResponse> response = matches.stream()
                .sorted(Comparator.comparing(Match::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(match -> modelMapper.map(match, MatchResponse.class))
                .collect(Collectors.toList());

        log.info("Se encontraron {} partidos para el equipo: {} y usuario: {}", response.size(), teamId, auditInfo.getUser());
        return response;
    }

    public MatchResponse updateMatch(Long id, MatchUpdateRequest request, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando el partido con ID: {} por el usuario: {}", id, auditInfo.getUser());

        Match existingMatch = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(m.getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Partido con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él.");
                });

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

        try {
            Match savedMatch = matchRepository.save(existingMatch);
            log.info("Partido con ID: {} actualizado correctamente por el usuario: {}", id, auditInfo.getUser());
            return modelMapper.map(savedMatch, MatchResponse.class);
        } catch (Exception e) {
            log.error("Error al actualizar el partido con ID: {} por el usuario: {}", id, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al actualizar el partido. Por favor, inténtelo más tarde.");
        }
    }

    public void deleteMatch(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Eliminando el partido con ID: {} por el usuario: {}", id, auditInfo.getUser());

        Match existingMatch = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(m.getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Partido con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él.");
                });

        existingMatch.setDeleted(true);
        try {
            matchRepository.save(existingMatch);
            log.info("Partido con ID: {} eliminado correctamente por el usuario: {}", id, auditInfo.getUser());
        } catch (Exception e) {
            log.error("Error al eliminar el partido con ID: {} por el usuario: {}", id, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al eliminar el partido. Por favor, inténtelo más tarde.");
        }
    }

    public MatchDetailsResponse getMatchDetailsById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo detalles del partido con ID: {} por el usuario: {}", id, auditInfo.getUser());

        // Obtener el partido y validar permisos
        Match match = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> teamRepository.existsByIdAndOwnerIdAndDeletedFalse(m.getTeam().getId(), auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Partido con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él.");
                });

        // Obtener el equipo del partido
        Team team = match.getTeam();

        // Obtener todos los jugadores activos del equipo
        List<Player> teamPlayers = playerRepository.findByTeamIdAndDeletedFalse(team.getId());

        // Obtener las estadísticas existentes del partido
        List<PlayerMatchStats> existingStats = match.getPlayerStats();

        // Crear un conjunto de IDs de jugadores que ya tienen estadísticas
        Set<Long> existingPlayerIds = existingStats.stream()
                .map(stats -> stats.getPlayer().getId())
                .collect(Collectors.toSet());

        // Crear estadísticas para los jugadores que no tienen entrada
        for (Player player : teamPlayers) {
            if (!existingPlayerIds.contains(player.getId())) {
                PlayerMatchStats newStats = new PlayerMatchStats();
                newStats.setMatch(match);
                newStats.setPlayer(player);
                newStats.setGoals(0);
                newStats.setMinutesPlayed(0);
                newStats.setYellowCard(false);
                newStats.setDoubleYellowCard(false);
                newStats.setRedCard(false);
                newStats.setGoalsConceded(0);
                newStats.setOwnGoals(0);
                newStats.setCalledUp(false);
                newStats.setStarter(false);
                existingStats.add(newStats);
                // Guardar la nueva estadística en la base de datos
                playerMatchStatsRepository.save(newStats);
            }
        }

        // Mapear la respuesta
        MatchDetailsResponse response = modelMapper.map(match, MatchDetailsResponse.class);
        TeamResponse teamResponse = modelMapper.map(team, TeamResponse.class);
        response.setTeam(teamResponse);
        response.setPlayerStats(existingStats.stream().map(stats -> {
            PlayerMatchStatsResponse dto = modelMapper.map(stats, PlayerMatchStatsResponse.class);
            dto.setPlayerId(stats.getPlayer().getId());
            dto.setPlayerFullName(stats.getPlayer().getFullName());
            return dto;
        }).collect(Collectors.toList()));

        log.info("Detalles del partido con ID: {} obtenidos correctamente para el usuario: {}", id, auditInfo.getUser());
        return response;
    }
}