package com.gestoteam.controller;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams(@RequestHeader String audit) {
        return ResponseEntity.ok(teamService.getAllTeams(audit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id, @RequestHeader String audit) {
        return teamService.getTeamById(id, audit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest teamRequest, @RequestHeader String audit) {
        return ResponseEntity.ok(teamService.createTeam(teamRequest, audit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id, @RequestBody TeamRequest teamRequest, @RequestHeader String audit) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamRequest, audit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, @RequestHeader String audit) {
        teamService.deleteTeam(id, audit);
        return ResponseEntity.noContent().build();
    }
}
