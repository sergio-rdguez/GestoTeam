package com.gestoteam.controller;

import com.gestoteam.model.PlayerMatchStats;
import com.gestoteam.service.PlayerMatchStatsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player-match-stats")
public class PlayerMatchStatsController {

    private final PlayerMatchStatsService playerMatchStatsService;

    public PlayerMatchStatsController(PlayerMatchStatsService playerMatchStatsService) {
        this.playerMatchStatsService = playerMatchStatsService;
    }

    @GetMapping("/{id}")
    public PlayerMatchStats getPlayerMatchStats(@PathVariable Long id) {
        return playerMatchStatsService.getPlayerMatchStatsById(id);
    }

    @PutMapping("/{id}")
    public PlayerMatchStats updatePlayerMatchStats(@PathVariable Long id, @RequestBody PlayerMatchStats updatedStats) {
        return playerMatchStatsService.updatePlayerMatchStats(id, updatedStats);
    }
}
