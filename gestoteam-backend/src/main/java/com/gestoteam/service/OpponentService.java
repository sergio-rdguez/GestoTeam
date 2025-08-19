package com.gestoteam.service;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Team;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpponentService extends BaseService {

    private final OpponentRepository opponentRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    public OpponentService(UserRepository userRepository, OpponentRepository opponentRepository, TeamRepository teamRepository, ModelMapper modelMapper) {
        super(userRepository);
        this.opponentRepository = opponentRepository;
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    public OpponentResponse createOpponent(OpponentRequest request) {
        Long userId = getCurrentUserId();
        log.info("Creando un oponente para el equipo con ID: {} por el usuario: {}", request.getTeamId(), userId);

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(request.getTeamId(), userId)
                .orElseThrow(() -> new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él."));

        Opponent opponent = modelMapper.map(request, Opponent.class);
        opponent.setTeam(team);

        try {
            Opponent savedOpponent = opponentRepository.save(opponent);
            log.info("Oponente creado con ID: {} para el equipo: {} por el usuario: {}", savedOpponent.getId(), team.getId(), userId);
            return mapToOpponentResponse(savedOpponent);
        } catch (Exception e) {
            log.error("Error al crear el oponente para el equipo: {} por el usuario: {}", team.getId(), userId, e);
            throw new GestoServiceException("Error al crear el oponente. Por favor, inténtelo más tarde.");
        }
    }

    public List<OpponentResponse> getOpponentsByTeam(Long teamId) {
        Long userId = getCurrentUserId();
        log.info("Obteniendo oponentes para el equipo con ID: {} por el usuario: {}", teamId, userId);

        if (!teamRepository.existsByIdAndOwnerIdAndDeletedFalse(teamId, userId)) {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, userId);
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        }

        List<Opponent> opponents = opponentRepository.findByTeamId(teamId);
        return opponents.stream()
                .map(this::mapToOpponentResponse)
                .collect(Collectors.toList());
    }

    public OpponentResponse getOpponentById(Long id) {
        Long userId = getCurrentUserId();
        log.info("Obteniendo oponente con ID: {} por el usuario: {}", id, userId);

        Opponent opponent = opponentRepository.findById(id)
                .orElseThrow(() -> new GestoServiceException("Oponente no encontrado."));

        // Verificar que el usuario tenga acceso al equipo del oponente
        if (!opponent.getTeam().getOwnerId().equals(userId)) {
            log.warn("Usuario {} intentó acceder al oponente {} sin permisos", userId, id);
            throw new GestoServiceException("No tienes permisos para acceder a este oponente.");
        }

        return mapToOpponentResponse(opponent);
    }

    public OpponentResponse updateOpponent(Long id, OpponentRequest request) {
        Long userId = getCurrentUserId();
        log.info("Actualizando oponente con ID: {} por el usuario: {}", id, userId);

        Opponent opponent = opponentRepository.findById(id)
                .orElseThrow(() -> new GestoServiceException("Oponente no encontrado."));

        // Verificar que el usuario tenga acceso al equipo del oponente
        if (!opponent.getTeam().getOwnerId().equals(userId)) {
            log.warn("Usuario {} intentó actualizar el oponente {} sin permisos", userId, id);
            throw new GestoServiceException("No tienes permisos para actualizar este oponente.");
        }

        // Actualizar campos
        opponent.setName(request.getName());
        opponent.setField(request.getField());
        opponent.setObservations(request.getObservations());

        try {
            Opponent updatedOpponent = opponentRepository.save(opponent);
            log.info("Oponente actualizado con ID: {} por el usuario: {}", id, userId);
            return mapToOpponentResponse(updatedOpponent);
        } catch (Exception e) {
            log.error("Error al actualizar el oponente con ID: {} por el usuario: {}", id, userId, e);
            throw new GestoServiceException("Error al actualizar el oponente. Por favor, inténtelo más tarde.");
        }
    }

    public void deleteOpponent(Long id) {
        Long userId = getCurrentUserId();
        log.info("Eliminando oponente con ID: {} por el usuario: {}", id, userId);

        Opponent opponent = opponentRepository.findById(id)
                .orElseThrow(() -> new GestoServiceException("Oponente no encontrado."));

        // Verificar que el usuario tenga acceso al equipo del oponente
        if (!opponent.getTeam().getOwnerId().equals(userId)) {
            log.warn("Usuario {} intentó eliminar el oponente {} sin permisos", userId, id);
            throw new GestoServiceException("No tienes permisos para eliminar este oponente.");
        }

        try {
            opponentRepository.delete(opponent);
            log.info("Oponente eliminado con ID: {} por el usuario: {}", id, userId);
        } catch (Exception e) {
            log.error("Error al eliminar el oponente con ID: {} por el usuario: {}", id, userId, e);
            throw new GestoServiceException("Error al eliminar el oponente. Por favor, inténtelo más tarde.");
        }
    }

    private OpponentResponse mapToOpponentResponse(Opponent opponent) {
        OpponentResponse response = modelMapper.map(opponent, OpponentResponse.class);
        response.setTeamId(opponent.getTeam().getId());
        return response;
    }
}