package com.gestoteam.service;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Team;
import com.gestoteam.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<TeamResponse> getAllTeams() {
        String username = getCurrentUsername();
        log.info("Obteniendo todos los equipos para el usuario: {}", username);

        List<TeamResponse> teams = teamRepository.findByOwnerIdAndDeletedFalse(username)
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

        log.info("Se encontraron {} equipos para el usuario: {}", teams.size(), username);
        return teams;
    }

    public Optional<TeamResponse> getTeamById(Long id) {
        String username = getCurrentUsername();
        log.info("Obteniendo el equipo con ID: {} para el usuario: {}", id, username);

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, username)
                .map(team -> {
                    log.info("Equipo con ID: {} encontrado para el usuario: {}", id, username);
                    // ModelMapper puede simplificar esto también, pero lo dejamos así para claridad
                    TeamResponse response = new TeamResponse();
                    response.setId(team.getId());
                    response.setName(team.getName());
                    response.setField(team.getField());
                    response.setDescription(team.getDescription());
                    response.setLocation(team.getLocation());
                    response.setDivision(team.getDivision());
                    response.setCategory(team.getCategory().getName());
                    return response;
                });
    }

    public TeamResponse createTeam(TeamRequest teamRequest) {
        String username = getCurrentUsername();
        log.info("Creando un equipo para el usuario: {}", username);

        Team team = modelMapper.map(teamRequest, Team.class);
        team.setOwnerId(username);

        try {
            Team savedTeam = teamRepository.save(team);
            log.info("Equipo creado con ID: {} por el usuario: {}", savedTeam.getId(), username);
            return modelMapper.map(savedTeam, TeamResponse.class);
        } catch (Exception e) {
            log.error("Error al crear el equipo para el usuario: {}", username, e);
            throw new GestoServiceException("Error al crear el equipo. Por favor, inténtelo más tarde.");
        }
    }

    public TeamResponse updateTeam(Long id, TeamRequest teamRequest) {
        String username = getCurrentUsername();
        log.info("Actualizando el equipo con ID: {} para el usuario: {}", id, username);

        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, username)
                .map(team -> {
                    modelMapper.map(teamRequest, team);
                    team.setId(id); // Aseguramos que el ID no se pierda en el mapeo

                    try {
                        Team updatedTeam = teamRepository.save(team);
                        log.info("Equipo con ID: {} actualizado correctamente para el usuario: {}", id, username);
                        return modelMapper.map(updatedTeam, TeamResponse.class);
                    } catch (Exception e) {
                        log.error("Error al actualizar el equipo con ID: {} para el usuario: {}", id, username, e);
                        throw new GestoServiceException("Error al actualizar el equipo. Por favor, inténtelo más tarde.");
                    }
                })
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", id, username);
                    throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });
    }

    public void deleteTeam(Long id) {
        String username = getCurrentUsername();
        log.info("Eliminando el equipo con ID: {} para el usuario: {}", id, username);

        teamRepository.findByIdAndOwnerId(id, username).ifPresentOrElse(team -> {
            team.setDeleted(true);
            try {
                teamRepository.save(team);
                log.info("Equipo con ID: {} eliminado correctamente por el usuario: {}", id, username);
            } catch (Exception e) {
                log.error("Error al eliminar el equipo con ID: {} para el usuario: {}", id, username, e);
                throw new GestoServiceException("Error al eliminar el equipo. Por favor, inténtelo más tarde.");
            }
        }, () -> {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", id, username);
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        });
    }
}