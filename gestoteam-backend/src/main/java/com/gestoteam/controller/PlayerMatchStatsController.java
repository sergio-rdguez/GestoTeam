package com.gestoteam.controller;

import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.service.PlayerMatchStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player-match-stats")
@RequiredArgsConstructor
public class PlayerMatchStatsController {

    private final PlayerMatchStatsService playerMatchStatsService;

    @GetMapping("/{id}")
    public PlayerMatchStatsResponse getPlayerMatchStats(@PathVariable Long id, @RequestHeader("Audit") String audit) {
        return playerMatchStatsService.getPlayerMatchStatsById(id, audit);
    }

    @PostMapping
    public PlayerMatchStatsResponse createPlayerMatchStats(@RequestBody PlayerMatchStatsRequest request, @RequestHeader("Audit") String audit) {
        return playerMatchStatsService.createPlayerMatchStats(request, audit);
    }

    @PutMapping("/{id}")
    public PlayerMatchStatsResponse updatePlayerMatchStats(@PathVariable Long id, @RequestBody PlayerMatchStatsRequest request, @RequestHeader("Audit") String audit) {
        return playerMatchStatsService.updatePlayerMatchStats(id, request, audit);
    }
}