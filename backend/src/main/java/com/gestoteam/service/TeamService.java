package com.gestoteam.service;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.exception.InvalidAuditException;
import com.gestoteam.model.Team;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.util.AuditUtil;
import com.gestoteam.dto.Audit;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public List<TeamResponse> getAllTeams(String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo todos los equipos para el usuario: {}", auditInfo.getUser());

        return teamRepository.findByOwnerIdAndDeletedFalse(auditInfo.getUser())
                .stream()
                .map(team -> {
                    TeamResponse response = new TeamResponse();
                    response.setId(team.getId());
                    response.setName(team.getName());
                    response.setDescription(team.getDescription());
                    response.setLocation(team.getLocation());
                    response.setDivision(team.getDivision());
                    response.setCategoryName(team.getCategory().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Optional<TeamResponse> getTeamById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo el equipo con id: {} para el usuario: {}", id, auditInfo.getUser());

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(team -> {
                    TeamResponse response = new TeamResponse();
                    response.setId(team.getId());
                    response.setName(team.getName());
                    response.setDescription(team.getDescription());
                    response.setLocation(team.getLocation());
                    response.setDivision(team.getDivision());
                    response.setCategoryName(team.getCategory().getName());
                    return response;
                });
    }

    public TeamResponse createTeam(TeamRequest teamRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando un equipo para el usuario: {}", auditInfo.getUser());

        Team team = modelMapper.map(teamRequest, Team.class);
        team.setOwnerId(auditInfo.getUser());

        Team savedTeam = teamRepository.save(team);
        log.info("Equipo creado con id: {}", savedTeam.getId());
        return modelMapper.map(savedTeam, TeamResponse.class);
    }

    public TeamResponse updateTeam(Long id, TeamRequest teamRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando el equipo con id: {} para el usuario: {}", id, auditInfo.getUser());

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(team -> {
                    modelMapper.map(teamRequest, team);
                    Team updatedTeam = teamRepository.save(team);
                    log.info("Equipo actualizado con id: {}", updatedTeam.getId());
                    return modelMapper.map(updatedTeam, TeamResponse.class);
                })
                .orElseThrow(() -> {
                    log.warn("Equipo no encontrado o acceso no autorizado para el id de equipo: {}", id);
                    return new RuntimeException("Equipo no encontrado o no tienes permisos para acceder a él");
                });
    }

    public void deleteTeam(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Eliminando el equipo con id: {} para el usuario: {}", id, auditInfo.getUser());

        teamRepository.findByIdAndOwnerId(id, auditInfo.getUser()).ifPresentOrElse(team -> {
            team.setDeleted(true);
            teamRepository.save(team);
            log.info("Equipo con id: {} marcado como eliminado", id);
        }, () -> {
            log.warn("Equipo con id: {} no encontrado o acceso no autorizado", id);
            throw new RuntimeException("Equipo no encontrado o no tienes permisos para acceder a él");
        });
    }
}
