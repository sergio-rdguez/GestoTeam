package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Player;
import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.model.Team;
import com.gestoteam.model.UserSettings;
import com.gestoteam.repository.PlayerMatchStatsRepository;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.dto.Audit;
import com.gestoteam.repository.UserSettingsRepository;
import com.gestoteam.util.GlobalUtil;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final GlobalUtil globalUtil;

    public List<PlayerResponse> getAllPlayers(String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo todos los jugadores para el usuario: {}", auditInfo.getUser());

        List<PlayerResponse> players = playerRepository.findByDeletedFalse()
                .stream()
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> modelMapper.map(player, PlayerResponse.class))
                .collect(toList());

        log.info("Se encontraron {} jugadores para el usuario: {}", players.size(), auditInfo.getUser());
        return players;
    }

    public Optional<PlayerResponse> getPlayerById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Buscando jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Optional<PlayerResponse> playerResponse = playerRepository.findByIdAndDeletedFalse(id)
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> {
                    PlayerResponse response = modelMapper.map(player, PlayerResponse.class);
                    fillSeasonStats(response, id, globalUtil.getCurrentSeason().getId());
                    log.info("Jugador con ID: {} encontrado para el usuario: {}", id, auditInfo.getUser());
                    return response;
                });

        if (playerResponse.isEmpty()) {
            log.warn("Jugador con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
        }

        return playerResponse;
    }

    public void createPlayer(PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando jugador para el equipo con ID: {} por el usuario: {}", playerRequest.getTeamId(), auditInfo.getUser());

        UserSettings userSettings = userSettingsRepository.findByUserId(auditInfo.getUser())
                .orElseThrow(() -> {
                    log.error("Configuraciones no encontradas para el usuario: {}", auditInfo.getUser());
                    return new GestoServiceException("Configuraciones no encontradas para el usuario");
                });

        Long teamId = playerRequest.getTeamId();
        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, auditInfo.getUser());
                    return new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });

        long activePlayerCount = playerRepository.countByTeamIdAndDeletedFalse(teamId);
        if (activePlayerCount >= userSettings.getMaxPlayersPerTeam()) {
            log.warn("Límite de jugadores alcanzado para el equipo ID: {}. Límite: {}, Actual: {}",
                    teamId, userSettings.getMaxPlayersPerTeam(), activePlayerCount);
            throw new GestoServiceException("No se puede crear más jugadores. Límite alcanzado.");
        }

        Player player = modelMapper.map(playerRequest, Player.class);
        player.setId(null);
        player.setTeam(team);
        player.setDeleted(false);

        try {
            playerRepository.save(player);
            log.info("Jugador creado con éxito para el equipo ID: {} por el usuario: {}", teamId, auditInfo.getUser());
        } catch (Exception e) {
            log.error("Error al crear el jugador para el equipo ID: {} por el usuario: {}", teamId, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al crear el jugador. Por favor, inténtelo más tarde.");
        }
    }

    public void updatePlayer(Long id, PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .filter(p -> p.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Jugador con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él.");
                });

        Long newTeamId = playerRequest.getTeamId();
        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(newTeamId, auditInfo.getUser())
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", newTeamId, auditInfo.getUser());
                    return new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });

        modelMapper.map(playerRequest, player);
        player.setId(id);
        player.setTeam(team);

        try {
            playerRepository.save(player);
            log.info("Jugador con ID: {} actualizado con éxito para el usuario: {}", id, auditInfo.getUser());
        } catch (Exception e) {
            log.error("Error al actualizar el jugador con ID: {} para el usuario: {}", id, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al actualizar el jugador. Por favor, inténtelo más tarde.");
        }
    }

    public void deletePlayer(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Eliminando jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .filter(p -> p.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .orElseThrow(() -> {
                    log.warn("Jugador con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    return new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él.");
                });

        player.setDeleted(true);
        try {
            playerRepository.save(player);
            log.info("Jugador con ID: {} marcado como eliminado con éxito por el usuario: {}", id, auditInfo.getUser());
        } catch (Exception e) {
            log.error("Error al eliminar el jugador con ID: {} para el usuario: {}", id, auditInfo.getUser(), e);
            throw new GestoServiceException("Error al eliminar el jugador. Por favor, inténtelo más tarde.");
        }
    }

    public TeamPlayerSummaryResponse getPlayersByTeamId(Long teamId, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo jugadores del equipo con ID: {} para el usuario: {}", teamId, auditInfo.getUser());

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, auditInfo.getUser());
                    return new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });

        List<Player> players = playerRepository.findByTeamIdAndDeletedFalse(teamId);

        List<TeamPlayerSummaryResponse.PlayerSummary> playerSummaries = players.stream()
                .sorted(Comparator.comparing(player -> player.getPosition().getOrder()))
                .map(player -> {
                    TeamPlayerSummaryResponse.PlayerSummary summary = new TeamPlayerSummaryResponse.PlayerSummary();
                    summary.setId(player.getId());
                    summary.setName(player.getFullName());
                    summary.setNumber(player.getNumber());
                    summary.setPosition(player.getPosition().getDescripcion());
                    summary.setStatus(player.getStatus());
                    return summary;
                })
                .collect(toList());

        Map<String, Long> statusCount = players.stream()
                .collect(Collectors.groupingBy(player -> player.getStatus().getDescripcion(), Collectors.counting()));

        TeamPlayerSummaryResponse response = new TeamPlayerSummaryResponse();
        response.setPlayers(playerSummaries);
        response.setTotalPlayers(players.size());
        response.setTeamId(teamId);
        response.setTeamName(team.getName());
        response.setStatusCount(statusCount);

        log.info("Se encontraron {} jugadores para el equipo ID: {} y usuario: {}", players.size(), teamId, auditInfo.getUser());
        return response;
    }

    private void fillSeasonStats(PlayerResponse response, Long playerId, Long seasonId) {
        log.debug("Obteniendo estadísticas de temporada para el jugador ID: {} en la temporada ID: {}", playerId, seasonId);

        List<PlayerMatchStats> statsList = playerMatchStatsRepository.findByPlayerIdAndMatch_Season_Id(playerId, seasonId);

        List<PlayerMatchStats> validStats = statsList.stream()
                .filter(stat -> !stat.getMatch().isDeleted())
                .toList();

        int convoked = (int) validStats.stream().filter(PlayerMatchStats::isCalledUp).count();
        int starterCount = (int) validStats.stream().filter(PlayerMatchStats::isStarter).count();
        int substituteCount = (int) validStats.stream().filter(s -> s.isCalledUp() && !s.isStarter()).count();
        int playedMatches = (int) validStats.stream().filter(s -> s.getMinutesPlayed() > 0).count();
        int totalGoals = validStats.stream().mapToInt(PlayerMatchStats::getGoals).sum();
        double averageGoals = playedMatches > 0 ? (double) totalGoals / playedMatches : 0;
        int yellowCards = (int) validStats.stream().filter(PlayerMatchStats::isYellowCard).count();
        int redCards = (int) validStats.stream().filter(PlayerMatchStats::isRedCard).count();
        int doubleYellowCards = (int) validStats.stream().filter(PlayerMatchStats::isDoubleYellowCard).count();

        PlayerResponse.StatsResponse.MatchesStats matchesStats = new PlayerResponse.StatsResponse.MatchesStats();
        matchesStats.setConvoked(convoked);
        matchesStats.setStarter(starterCount);
        matchesStats.setSubstitute(substituteCount);
        matchesStats.setPlayed(playedMatches);

        PlayerResponse.StatsResponse.GoalsStats goalsStats = new PlayerResponse.StatsResponse.GoalsStats();
        goalsStats.setTotal(totalGoals);
        goalsStats.setAverage(averageGoals);

        PlayerResponse.StatsResponse.CardsStats cardsStats = new PlayerResponse.StatsResponse.CardsStats();
        cardsStats.setYellow(yellowCards);
        cardsStats.setRed(redCards);
        cardsStats.setDoubleYellow(doubleYellowCards);

        PlayerResponse.StatsResponse statsResponse = new PlayerResponse.StatsResponse();
        statsResponse.setMatches(matchesStats);
        statsResponse.setGoals(goalsStats);
        statsResponse.setCards(cardsStats);

        response.setStats(statsResponse);
        log.debug("Estadísticas de temporada calculadas para el jugador ID: {}", playerId);
    }
}