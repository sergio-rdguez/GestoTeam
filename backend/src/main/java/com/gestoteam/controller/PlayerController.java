package com.gestoteam.controller;

import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
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
    public ResponseEntity<List<PlayerResponse>> getAllPlayers(@RequestHeader String audit) {
        return ResponseEntity.ok(playerService.getAllPlayers(audit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id, @RequestHeader String audit) {
        return playerService.getPlayerById(id, audit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPlayer(@RequestBody PlayerRequest playerRequest, @RequestHeader String audit) {
        playerService.createPlayer(playerRequest, audit);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePlayer(@PathVariable Long id, @RequestBody PlayerRequest playerRequest, @RequestHeader String audit) {
        playerService.updatePlayer(id, playerRequest, audit);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable Long id, @RequestHeader String audit) {
        playerService.deletePlayer(id, audit);
    }

    @GetMapping("/team/{teamId}")
    public List<PlayerResponse> getPlayersByTeamId(@PathVariable Long teamId, @RequestHeader("audit") String audit) {
        return playerService.getPlayersByTeamId(teamId, audit);
    }
}
