package com.gestoteam.service;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Opponent;
import com.gestoteam.model.Team;
import com.gestoteam.repository.OpponentRepository;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.dto.Audit;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpponentService {

    private final OpponentRepository opponentRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public OpponentResponse createOpponent(OpponentRequest request, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Creando un oponente para el equipo con ID: {} por el usuario: {}", request.getTeamId(), auditInfo.getUser());

        Team team = teamRepository.findByIdAndOwnerIdAndDeletedFalse(request.getTeamId(), auditInfo.getUser())
                .orElseThrow(() -> {
                    log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", request.getTeamId(), auditInfo.getUser());
                    return new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
                });

        Opponent opponent = modelMapper.map(request, Opponent.class);
        opponent.setTeam(team);

        try {
            Opponent savedOpponent = opponentRepository.save(opponent);
            log.info("Oponente creado con ID: {} para el equipo: {} por el usuario: {}", savedOpponent.getId(), team.getId(), auditInfo.getUser());
            return modelMapper.map(savedOpponent, OpponentResponse.class);
        } catch (Exception e) {
            log.error("Error al crear el oponente para el equipo: {} por el usuario: {}", team.getId(), auditInfo.getUser(), e);
            throw new GestoServiceException("Error al crear el oponente. Por favor, inténtelo más tarde.");
        }
    }

    public List<OpponentResponse> getOpponentsByTeam(Long teamId, String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo oponentes para el equipo con ID: {} por el usuario: {}", teamId, auditInfo.getUser());

        if (!teamRepository.existsByIdAndOwnerIdAndDeletedFalse(teamId, auditInfo.getUser())) {
            log.warn("Equipo con ID: {} no encontrado o no pertenece al usuario: {}", teamId, auditInfo.getUser());
            throw new GestoServiceException("Equipo no encontrado o no tienes permisos para acceder a él.");
        }

        List<Opponent> opponents = opponentRepository.findByTeamId(teamId);
        List<OpponentResponse> response = opponents.stream()
                .map(opponent -> modelMapper.map(opponent, OpponentResponse.class))
                .collect(Collectors.toList());

        log.info("Se encontraron {} oponentes para el equipo: {} y usuario: {}", response.size(), teamId, auditInfo.getUser());
        return response;
    }
}