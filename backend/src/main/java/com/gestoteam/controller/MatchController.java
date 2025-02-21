package com.gestoteam.controller;

import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.model.Match;
import com.gestoteam.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public MatchResponse createMatch(@RequestBody MatchRequest matchRequest) {
        return matchService.createMatch(matchRequest);
    }

    @GetMapping("/team/{teamId}")
    public List<MatchResponse> getMatchesByTeam(@PathVariable Long teamId) {
        return matchService.getMatchesByTeam(teamId);
    }

    @GetMapping("/details/{id}")
    public MatchDetailsResponse getMatchDetailsById(@PathVariable Long id) {
        return matchService.getMatchDetailsById(id);
    }

    @PutMapping("/{id}")
    public MatchResponse updateMatch(@PathVariable Long id, @RequestBody MatchUpdateRequest matchRequest) {
        return matchService.updateMatch(id, matchRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
    }
}
