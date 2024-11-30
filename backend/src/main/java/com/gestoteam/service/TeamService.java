package com.gestoteam.service;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Team;
import com.gestoteam.repository.TeamRepository;
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

        List<TeamResponse> teams = teamRepository.findByOwnerIdAndDeletedFalse(auditInfo.getUser())
                .stream()
                .map(team -> {
                    TeamResponse response = new TeamResponse();
                    response.setId(team.getId());
                    response.setName(team.getName());
                    response.setDescription(team.getDescription());
                    response.setLocation(team.getLocation());
                    response.setDivision(team.getDivision());
                    response.setCategory(team.getCategory().getName());
                    return response;
                })
                .collect(Collectors.toList());

        log.info("Se encontraron {} equipos para el usuario: {}", teams.size(), auditInfo.getUser());
        return teams;
    }

    public Optional<TeamResponse> getTeamById(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo el equipo con ID: {} para el usuario: {}", id, auditInfo.getUser());

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(team -> {
                    log.info("Equipo con ID: {} encontrado para el usuario: {}", id, auditInfo.getUser());
                    TeamResponse response = new TeamResponse();
                    response.setId(team.getId());
                    response.setName(team.getName());
                    response.setDescription(team.getDescription());
                    response.setLocation(team.getLocation());
                    response.setDivision(team.getDivision());
                    response.setCategory(team.getCategory().getName());
                    return response;
                });
    }

    public TeamResponse createTeam(TeamRequest teamRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando un equipo para el usuario: {}", auditInfo.getUser());

        Team team = modelMapper.map(teamRequest, Team.class);
        team.setOwnerId(auditInfo.getUser());

        try {
            Team savedTeam = teamRepository.save(team);
            log.info("Equipo creado con ID: {} por el usuario: {}", savedTeam.getId(), auditInfo.getUser());
            return modelMapper.map(savedTeam, TeamResponse.class);
        } catch (Exception e) {
            log.error("Error al crear el equipo para el usuario: {}", auditInfo.getUser(), e);
            throw new GestoServiceException("Error al crear el equipo. Por favor, inténtelo más tarde.");
        }
    }

    public TeamResponse updateTeam(Long id, TeamRequest teamRequest, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando el equipo con ID: {} para el usuario: {}", id, auditInfo.getUser());

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(team -> {
                    modelMapper.map(teamRequest, team);
                    team.setId(id);

                    try {
                        Team updatedTeam = teamRepository.save(team);
                        log.info("Equipo con ID: {} actualizado correctamente para el usuario: {}", id, auditInfo.getUser());
                        return modelMapper.map(updatedTeam, TeamResponse.class);
                    } catch (Exception e) {
                        log.error("Error al actualizar el equipo con ID: {} para el usuario: {}", id, auditInfo.getUser(), e);
                        throw new GestoServiceException("Error al actualizar el equipo. Por favor, inténtelo más tarde.");
                    }
                })
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
                    throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });
    }

    public void deleteTeam(Long id, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Eliminando el equipo con ID: {} para el usuario: {}", id, auditInfo.getUser());

        teamRepository.findByIdAndOwnerId(id, auditInfo.getUser()).ifPresentOrElse(team -> {
            team.setDeleted(true);
            try {
                teamRepository.save(team);
                log.info("Equipo con ID: {} eliminado correctamente por el usuario: {}", id, auditInfo.getUser());
            } catch (Exception e) {
                log.error("Error al eliminar el equipo con ID: {} para el usuario: {}", id, auditInfo.getUser(), e);
                throw new GestoServiceException("Error al eliminar el equipo. Por favor, inténtelo más tarde.");
            }
        }, () -> {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", id, auditInfo.getUser());
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        });
    }
}
