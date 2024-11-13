package com.gestoteam.service;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.model.Team;
import com.gestoteam.repository.TeamRepository;
import com.gestoteam.util.AuditUtil;
import com.gestoteam.dto.Audit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TeamService {
    
    private TeamRepository teamRepository;
    private AuditUtil auditUtil;
    

    public List<TeamResponse> getAllTeams(String audit) {
        Audit auditInfo = auditUtil.decryptAudit(audit);
        return teamRepository.findByOwnerIdAndDeletedFalse(auditInfo.getUser())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public Optional<TeamResponse> getTeamById(Long id, String audit) {
        Audit auditInfo = auditUtil.decryptAudit(audit);
        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(this::convertToResponse);
    }

    public TeamResponse createTeam(TeamRequest teamRequest, String audit) {
        Audit auditInfo = auditUtil.decryptAudit(audit);
        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setDescription(teamRequest.getDescription());
        team.setOwnerId(auditInfo.getUser());
        return convertToResponse(teamRepository.save(team));
    }

    public TeamResponse updateTeam(Long id, TeamRequest teamRequest, String audit) {
        Audit auditInfo = auditUtil.decryptAudit(audit);
        return teamRepository.findByIdAndOwnerIdAndDeletedFalse(id, auditInfo.getUser())
                .map(team -> {
                    team.setName(teamRequest.getName());
                    team.setDescription(teamRequest.getDescription());
                    return convertToResponse(teamRepository.save(team));
                })
                .orElseThrow(() -> new RuntimeException("Team not found or you are not the owner"));
    }

    public void deleteTeam(Long id, String audit) {
        Audit auditInfo = auditUtil.decryptAudit(audit);
        teamRepository.findByIdAndOwnerId(id, auditInfo.getUser()).ifPresent(team -> {
            team.setDeleted(true);
            teamRepository.save(team);
        });
    }

    private TeamResponse convertToResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        return response;
    }
}
