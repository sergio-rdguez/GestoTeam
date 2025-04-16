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
    public MatchResponse createMatch(@RequestBody MatchRequest matchRequest,@RequestHeader String audit) {
        return matchService.createMatch(matchRequest,audit);
    }

    @GetMapping("/team/{teamId}")
    public List<MatchResponse> getMatchesByTeam(@PathVariable Long teamId,@RequestHeader String audit) {
        return matchService.getMatchesByTeam(teamId,audit);
    }

    @GetMapping("/details/{id}")
    public MatchDetailsResponse getMatchDetailsById(@PathVariable Long id,@RequestHeader String audit) {
        return matchService.getMatchDetailsById(id,audit);
    }

    @PutMapping("/{id}")
    public MatchResponse updateMatch(@PathVariable Long id, @RequestBody MatchUpdateRequest matchRequest,@RequestHeader String audit) {
        return matchService.updateMatch(id, matchRequest,audit);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id,@RequestHeader String audit) {
        matchService.deleteMatch(id,audit);
    }
}
