package com.gestoteam.controller;

import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.service.OpponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opponents")
public class OpponentController {

    private final OpponentService opponentService;

    @PostMapping
    public OpponentResponse createOpponent(@RequestBody OpponentRequest request, @RequestHeader("Audit") String audit) {
        return opponentService.createOpponent(request, audit);
    }

    @GetMapping("/team/{teamId}")
    public List<OpponentResponse> getOpponentsByTeam(@PathVariable Long teamId, @RequestHeader("Audit") String audit) {
        return opponentService.getOpponentsByTeam(teamId, audit);
    }
}