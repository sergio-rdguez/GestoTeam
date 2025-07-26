package com.gestoteam.controller;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPlayer(@RequestBody PlayerRequest playerRequest) {
        playerService.createPlayer(playerRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePlayer(@PathVariable Long id, @RequestBody PlayerRequest playerRequest) {
        playerService.updatePlayer(id, playerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/team/{teamId}")
    public TeamPlayerSummaryResponse getPlayersByTeamId(@PathVariable Long teamId) {
        return playerService.getPlayersByTeamId(teamId);
    }
}