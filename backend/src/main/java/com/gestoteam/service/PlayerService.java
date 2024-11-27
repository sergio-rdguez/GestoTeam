package com.gestoteam.service;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.model.Player;
import com.gestoteam.model.Team;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.util.AuditUtil;
import com.gestoteam.dto.Audit;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public List<PlayerResponse> getAllPlayers(String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo todos los jugadores para el usuario: {}", auditInfo.getUser());

        return playerRepository.findByDeletedFalse()
                .stream()
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> modelMapper.map(player, PlayerResponse.class))
                .collect(Collectors.toList());
    }

    public Optional<PlayerResponse> getPlayerById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo jugador con id: {} para el usuario: {}", id, auditInfo.getUser());

        return playerRepository.findByIdAndDeletedFalse(id)
                .filter(player -> player.getTeam().getOwnerId().equals(auditInfo.getUser()))
                .map(player -> modelMapper.map(player, PlayerResponse.class));
    }

    public void createPlayer(PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando un nuevo jugador para el usuario: {}", auditInfo.getUser());

        Team team = teamRepository.findById(playerRequest.getTeamId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + playerRequest.getTeamId()));

        validationUtil.validateOwner(auditInfo.getUser(), team);

        Player player = modelMapper.map(playerRequest, Player.class);
        player.setTeam(team);

        playerRepository.save(player);
        log.info("Jugador creado correctamente");
    }

    public void updatePlayer(Long id, PlayerRequest playerRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando jugador con id: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));

        validationUtil.validateOwner(auditInfo.getUser(), player.getTeam());

        Team team = teamRepository.findById(playerRequest.getTeamId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + playerRequest.getTeamId()));

        validationUtil.validateOwner(auditInfo.getUser(), team);

        modelMapper.map(playerRequest, player);
        player.setTeam(team);

        playerRepository.save(player);
        log.info("Jugador actualizado correctamente");
    }

    public void deletePlayer(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Eliminando jugador con id: {} para el usuario: {}", id, auditInfo.getUser());

        Player player = playerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con ID: " + id));

        validationUtil.validateOwner(auditInfo.getUser(), player.getTeam());

        player.setDeleted(true);
        playerRepository.save(player);
        log.info("Jugador eliminado correctamente");
    }

    public List<PlayerResponse> getPlayersByTeamId(Long teamId, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo jugadores del equipo con id: {} para el usuario: {}", teamId, auditInfo.getUser());

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + teamId));
        validationUtil.validateOwner(auditInfo.getUser(), team);

        return playerRepository.findByTeamIdAndDeletedFalse(teamId)
                .stream()
                .sorted(Comparator.comparing(player -> player.getPosition().getOrder()))
                .map(player -> modelMapper.map(player, PlayerResponse.class))
                .collect(Collectors.toList());
    }

}
