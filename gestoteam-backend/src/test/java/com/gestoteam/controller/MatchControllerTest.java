package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.MatchRequest;
import com.gestoteam.dto.request.MatchUpdateRequest;
import com.gestoteam.dto.response.MatchDetailsResponse;
import com.gestoteam.dto.response.MatchResponse;
import com.gestoteam.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchService matchService;

    @Test
    @WithMockUser(username = "testuser")
    void createMatch_ShouldReturnCreated_WithValidData() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setTeamId(1L);
        request.setOpponentId(1L);
        request.setDate(LocalDateTime.now().plusDays(7));

        MatchResponse response = new MatchResponse();
        response.setId(1L);

        given(matchService.createMatch(any(MatchRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/matches")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // El controlador devuelve 200 OK
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getMatchesByTeam_ShouldReturnMatchList() throws Exception {
        MatchResponse response = new MatchResponse();
        response.setId(1L);
        
        given(matchService.getMatchesByTeam(1L)).willReturn(List.of(response));

        mockMvc.perform(get("/api/matches/team/{teamId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMatchDetailsById_ShouldReturnDetails() throws Exception {
        MatchDetailsResponse response = new MatchDetailsResponse();
        response.setId(1L);
        response.setOpponent("Rival Test");
        
        given(matchService.getMatchDetailsById(1L)).willReturn(response);

        mockMvc.perform(get("/api/matches/details/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.opponent", is("Rival Test")));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateMatch_ShouldReturnOk_WithValidData() throws Exception {
        MatchUpdateRequest request = new MatchUpdateRequest();
        request.setLocation("Nuevo Estadio");

        MatchResponse response = new MatchResponse();
        response.setId(1L);
        response.setLocation("Nuevo Estadio");

        given(matchService.updateMatch(eq(1L), any(MatchUpdateRequest.class))).willReturn(response);
        
        mockMvc.perform(put("/api/matches/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is("Nuevo Estadio")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteMatch_ShouldReturnNoContent() throws Exception {
        doNothing().when(matchService).deleteMatch(1L);

        mockMvc.perform(delete("/api/matches/{id}", 1L).with(csrf()))
                .andExpect(status().isNoContent());
    }
}