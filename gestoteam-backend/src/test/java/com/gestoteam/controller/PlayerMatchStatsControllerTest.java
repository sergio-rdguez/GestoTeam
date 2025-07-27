package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.PlayerMatchStatsRequest;
import com.gestoteam.dto.response.PlayerMatchStatsResponse;
import com.gestoteam.service.PlayerMatchStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@WithMockUser(username = "testuser")
class PlayerMatchStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerMatchStatsService playerMatchStatsService;

    @Test
    void getPlayerMatchStats_ShouldReturnStats() throws Exception {
        PlayerMatchStatsResponse response = new PlayerMatchStatsResponse();
        response.setId(1L);
        response.setGoals(1);

        when(playerMatchStatsService.getPlayerMatchStatsById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/player-match-stats/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals", is(1)));
    }

    @Test
    void createPlayerMatchStats_ShouldReturnCreatedStats() throws Exception {
        PlayerMatchStatsRequest request = new PlayerMatchStatsRequest();
        request.setPlayerId(1L);
        request.setMatchId(1L);

        PlayerMatchStatsResponse response = new PlayerMatchStatsResponse();
        response.setId(1L);

        when(playerMatchStatsService.createPlayerMatchStats(any(PlayerMatchStatsRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/player-match-stats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updatePlayerMatchStats_ShouldReturnUpdatedStats() throws Exception {
        PlayerMatchStatsRequest request = new PlayerMatchStatsRequest();
        request.setMinutesPlayed(90);

        PlayerMatchStatsResponse response = new PlayerMatchStatsResponse();
        response.setId(1L);
        response.setMinutesPlayed(90);

        when(playerMatchStatsService.updatePlayerMatchStats(eq(1L), any(PlayerMatchStatsRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/player-match-stats/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minutesPlayed", is(90)));
    }
}