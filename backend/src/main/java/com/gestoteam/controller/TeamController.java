package com.gestoteam.controller;

import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<TeamResponse> getAllTeams(@RequestHeader String audit) {
        return teamService.getAllTeams(audit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id, @RequestHeader String audit) {
        return teamService.getTeamById(id, audit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TeamResponse createTeam(@RequestBody TeamRequest teamRequest, @RequestHeader String audit) {
        return teamService.createTeam(teamRequest, audit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @RequestBody TeamRequest teamRequest, @RequestHeader String audit) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamRequest, audit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, @RequestHeader String audit) {
        teamService.deleteTeam(id, audit);
        return ResponseEntity.noContent().build();
    }
}
