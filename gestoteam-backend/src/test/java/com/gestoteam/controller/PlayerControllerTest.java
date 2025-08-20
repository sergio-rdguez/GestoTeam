package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.PlayerRequest;
import com.gestoteam.dto.response.PlayerResponse;
import com.gestoteam.dto.response.TeamPlayerSummaryResponse;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import com.gestoteam.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayerService playerService;

    @Test
    @WithMockUser(username = "testuser")
    void getPlayerById_ShouldReturnPlayer_WhenFound() throws Exception {
        PlayerResponse playerResponse = new PlayerResponse();
        playerResponse.setId(1L);
        playerResponse.setName("Test Player");

        given(playerService.getPlayerById(1L)).willReturn(Optional.of(playerResponse));

        mockMvc.perform(get("/api/players/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Player"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlayerById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        given(playerService.getPlayerById(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/players/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPlayer_ShouldReturnCreated_WithValidData() throws Exception {
        PlayerRequest playerRequest = new PlayerRequest();
        playerRequest.setName("New Player");
        playerRequest.setSurnameFirst("Surname");
        playerRequest.setTeamId(1L);
        playerRequest.setPosition(Position.DC);
        playerRequest.setStatus(PlayerStatus.ACTIVO);
        playerRequest.setBirthDate(LocalDate.now().minusYears(20));
        playerRequest.setNumber(9);

        // Mock del servicio para que devuelva el ID del jugador creado
        given(playerService.createPlayer(any(PlayerRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/players")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createPlayer_ShouldReturnCorrectPlayerId() throws Exception {
        PlayerRequest playerRequest = new PlayerRequest();
        playerRequest.setName("Another Player");
        playerRequest.setSurnameFirst("Another");
        playerRequest.setTeamId(2L);
        playerRequest.setPosition(Position.MC);
        playerRequest.setStatus(PlayerStatus.ACTIVO);
        playerRequest.setBirthDate(LocalDate.now().minusYears(22));
        playerRequest.setNumber(10);

        // Mock del servicio para que devuelva un ID diferente
        given(playerService.createPlayer(any(PlayerRequest.class))).willReturn(42L);

        mockMvc.perform(post("/api/players")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(42L));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updatePlayer_ShouldReturnNoContent_WithValidData() throws Exception {
        PlayerRequest playerRequest = new PlayerRequest();
        playerRequest.setName("Updated Player");
        playerRequest.setSurnameFirst("Surname");
        playerRequest.setTeamId(1L);
        playerRequest.setPosition(Position.DC);
        playerRequest.setStatus(PlayerStatus.ACTIVO);
        playerRequest.setBirthDate(LocalDate.now().minusYears(20));
        playerRequest.setNumber(9);

        doNothing().when(playerService).updatePlayer(eq(1L), any(PlayerRequest.class));

        mockMvc.perform(put("/api/players/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deletePlayer_ShouldReturnNoContent() throws Exception {
        doNothing().when(playerService).deletePlayer(1L);

        mockMvc.perform(delete("/api/players/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPlayersByTeamId_ShouldReturnSummary() throws Exception {
        TeamPlayerSummaryResponse summaryResponse = new TeamPlayerSummaryResponse();
        summaryResponse.setTeamId(1L);
        summaryResponse.setTotalPlayers(1);
        summaryResponse.setPlayers(Collections.emptyList());

        given(playerService.getPlayersByTeamId(1L)).willReturn(summaryResponse);

        mockMvc.perform(get("/api/players/team/{teamId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.teamId").value(1L))
                .andExpect(jsonPath("$.totalPlayers").value(1));
    }
}