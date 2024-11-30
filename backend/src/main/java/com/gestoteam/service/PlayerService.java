package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Player;
import com.gestoteam.model.Team;
import com.gestoteam.model.UserSettings;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.dto.Audit;
import com.gestoteam.repository.UserSettingsRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public List<PlayerResponse> getAllPlayers(String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la obtención de todos los jugadores para el usuario: {}", auditInfo.getUser());

        List<PlayerResponse> players = playerRepository.findByDeletedFalse()
                .stream()
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> modelMapper.map(player, PlayerResponse.class))
                .collect(Collectors.toList());

        log.info("Se encontraron {} jugadores para el usuario: {}", players.size(), auditInfo.getUser());
        return players;
    }

    public Optional<PlayerResponse> getPlayerById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la búsqueda de jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Optional<PlayerResponse> playerResponse = playerRepository.findByIdAndDeletedFalse(id)
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> modelMapper.map(player, PlayerResponse.class));

        if (playerResponse.isEmpty()) {
            log.warn("Jugador con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
        } else {
            log.info("Jugador con ID: {} encontrado para el usuario: {}", id, auditInfo.getUser());
        }

        return playerResponse;
    }

    public void createPlayer(PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la creación de un jugador para el usuario: {}", auditInfo.getUser());

        UserSettings userSettings = userSettingsRepository.findByUserId(auditInfo.getUser())
                .orElseThrow(() -> new GestoServiceException("Configuraciones no encontradas para el usuario"));

        Long teamId = playerRequest.getTeamId();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado"));

        validationUtil.validateOwner(auditInfo.getUser(), team);

        long activePlayerCount = playerRepository.countByTeamIdAndDeletedFalse(teamId);

        if (activePlayerCount >= userSettings.getMaxPlayersPerTeam()) {
            log.warn("Límite de jugadores alcanzado para el equipo ID: {}. Límite: {}, Actual: {}",
                    teamId, userSettings.getMaxPlayersPerTeam(), activePlayerCount);

            throw new GestoServiceException("No se puede crear más jugadores. Límite alcanzado." );
        }

        Player player = modelMapper.map(playerRequest, Player.class);
        player.setId(null);
        player.setTeam(team);
        playerRepository.save(player);

        log.info("Jugador creado correctamente para el equipo ID: {} por el usuario: {}", teamId, auditInfo.getUser());
    }

    public void updatePlayer(Long id, PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la actualización del jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new GestoServiceException("Jugador no encontrado"));

        validationUtil.validateOwner(auditInfo.getUser(), player.getTeam());

        Team team = teamRepository.findById(playerRequest.getTeamId())
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado"));

        validationUtil.validateOwner(auditInfo.getUser(), team);

        modelMapper.map(playerRequest, player);
        player.setId(id);
        player.setTeam(team);

        playerRepository.save(player);
        log.info("Jugador con ID: {} actualizado correctamente para el usuario: {}", id, auditInfo.getUser());
    }

    public void deletePlayer(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la eliminación del jugador con ID: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new GestoServiceException("Jugador no encontrado"));

        validationUtil.validateOwner(auditInfo.getUser(), player.getTeam());

        player.setDeleted(true);
        playerRepository.save(player);
        log.info("Jugador con ID: {} marcado como eliminado por el usuario: {}", id, auditInfo.getUser());
    }

    public TeamPlayerSummaryResponse getPlayersByTeamId(Long teamId, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Iniciando la obtención de jugadores del equipo ID: {} para el usuario: {}", teamId, auditInfo.getUser());

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado o no pertenece al usuario"));

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
                .collect(Collectors.toList());

        Map<String, Long> statusCount = players.stream()
                .collect(Collectors.groupingBy(player -> player.getStatus().getDescripcion(), Collectors.counting()));

        TeamPlayerSummaryResponse response = new TeamPlayerSummaryResponse();
        response.setPlayers(playerSummaries);
        response.setTotalPlayers(players.size());
        response.setTeamId(teamId);
        response.setTeamName(team.getName());
        response.setStatusCount(statusCount);

        log.info("Se obtuvo correctamente la información de {} jugadores del equipo ID: {}", players.size(), teamId);
        return response;
    }
}
