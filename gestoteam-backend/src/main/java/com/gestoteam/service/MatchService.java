package com.gestoteam.service;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Match;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.model.Team;
import com.gestoteam.repository.MatchRepository;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.util.GlobalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final OpponentRepository opponentRepository;
    private final ModelMapper modelMapper;
    private final GlobalUtil globalUtil;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private MatchResponse convertToMatchResponse(Match match) {
        MatchResponse response = modelMapper.map(match, MatchResponse.class);
        if (match.getOpponent() != null) {
            response.setOpponent(match.getOpponent().getName());
        }
        return response;
    }


    @Transactional
    public MatchResponse createMatch(MatchRequest request) {
        String username = getCurrentUsername();
        log.info("Creando un partido para el equipo con ID: {} por el usuario: {}", request.getTeamId(), username);

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(request.getTeamId(), username)
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él."));

        Opponent opponent = opponentRepository.findById(request.getOpponentId())
                .orElseThrow(() -> new GestoServiceException("Oponente no encontrado con el ID proporcionado."));

        Match match = new Match();
        match.setTeam(team);
        match.setOpponent(opponent);
        match.setDate(request.getDate());
        match.setLocation(request.getLocation());
        match.setSeason(globalUtil.getCurrentSeason());

        try {
            Match savedMatch = matchRepository.save(match);
            log.info("Partido creado con ID: {} para el equipo: {} por el usuario: {}", savedMatch.getId(), team.getId(), username);
            return convertToMatchResponse(savedMatch);
        } catch (Exception e) {
            log.error("Error al crear el partido para el equipo: {} por el usuario: {}", team.getId(), username, e);
            throw new GestoServiceException("Error al crear el partido. Por favor, inténtelo más tarde.");
        }
    }

    public List<MatchResponse> getMatchesByTeam(Long teamId) {
        String username = getCurrentUsername();
        log.info("Obteniendo partidos para el equipo con ID: {} por el usuario: {}", teamId, username);

        if (!teamRepository.existsByIdAndOwnerIdAndDeletedFalse(teamId, username)) {
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        }

        List<Match> matches = matchRepository.findByTeamIdAndSeason_IdAndDeletedFalse(
                teamId, globalUtil.getCurrentSeason().getId());

        return matches.stream()
                .sorted(Comparator.comparing(Match::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::convertToMatchResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponse updateMatch(Long id, MatchUpdateRequest request) {
        String username = getCurrentUsername();
        log.info("Actualizando el partido con ID: {} por el usuario: {}", id, username);

        Match match = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> m.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él."));

        Opponent opponent = opponentRepository.findById(request.getOpponentId())
                .orElseThrow(() -> new GestoServiceException("Oponente no encontrado con el ID proporcionado."));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;


        match.setOpponent(opponent);
        match.setLocation(request.getLocation());
        match.setDate(LocalDateTime.parse(request.getDate(), formatter));
        match.setFinalized(request.isFinalized());

        if (request.isFinalized()) {
            Integer goalsFor = request.getGoalsFor();
            Integer goalsAgainst = request.getGoalsAgainst();
            match.setGoalsFor(goalsFor);
            match.setGoalsAgainst(goalsAgainst);
            match.setResult(goalsFor + "-" + goalsAgainst);
            match.setWon(goalsFor > goalsAgainst);
            match.setDraw(goalsFor.equals(goalsAgainst));
        } else {
            match.setGoalsFor(null);
            match.setGoalsAgainst(null);
            match.setResult(null);
            match.setWon(false);
            match.setDraw(false);
        }

        try {
            Match savedMatch = matchRepository.save(match);
            log.info("Partido con ID: {} actualizado correctamente por el usuario: {}", id, username);
            return convertToMatchResponse(savedMatch);
        } catch (Exception e) {
            log.error("Error al actualizar el partido con ID: {} por el usuario: {}", id, username, e);
            throw new GestoServiceException("Error al actualizar el partido. Por favor, inténtelo más tarde.");
        }
    }

    @Transactional
    public void deleteMatch(Long id) {
        String username = getCurrentUsername();
        log.info("Eliminando el partido con ID: {} por el usuario: {}", id, username);

        Match existingMatch = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> m.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él."));

        existingMatch.setDeleted(true);
        try {
            matchRepository.save(existingMatch);
            log.info("Partido con ID: {} eliminado correctamente por el usuario: {}", id, username);
        } catch (Exception e) {
            log.error("Error al eliminar el partido con ID: {} por el usuario: {}", id, username, e);
            throw new GestoServiceException("Error al eliminar el partido. Por favor, inténtelo más tarde.");
        }
    }

    @Transactional(readOnly = true)
    public MatchDetailsResponse getMatchDetailsById(Long id) {
        String username = getCurrentUsername();
        log.info("Obteniendo detalles del partido con ID: {} por el usuario: {}", id, username);

        Match match = matchRepository.findByIdAndDeletedFalse(id)
                .filter(m -> m.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Partido no encontrado o no tienes permisos para acceder a él."));

        Team team = match.getTeam();
        List<Player> teamPlayers = playerRepository.findByTeamIdAndDeletedFalse(team.getId());
        List<PlayerMatchStats> existingStats = match.getPlayerStats();
        Set<Long> existingPlayerIds = existingStats.stream()
                .map(stats -> stats.getPlayer().getId())
                .collect(Collectors.toSet());

        for (Player player : teamPlayers) {
            if (!existingPlayerIds.contains(player.getId())) {
                PlayerMatchStats newStats = new PlayerMatchStats();
                newStats.setMatch(match);
                newStats.setPlayer(player);
                existingStats.add(newStats);
            }
        }

        MatchDetailsResponse response = modelMapper.map(match, MatchDetailsResponse.class);
        response.setTeam(modelMapper.map(team, TeamResponse.class));
        response.setOpponent(match.getOpponent().getName());
        response.setOpponentId(match.getOpponent().getId());

        response.setPlayerStats(existingStats.stream().map(stats -> {
            PlayerMatchStatsResponse dto = modelMapper.map(stats, PlayerMatchStatsResponse.class);
            dto.setPlayerId(stats.getPlayer().getId());
            dto.setPlayerFullName(stats.getPlayer().getFullName());
            return dto;
        }).collect(Collectors.toList()));

        log.info("Detalles del partido con ID: {} obtenidos correctamente para el usuario: {}", id, username);
        return response;
    }
}