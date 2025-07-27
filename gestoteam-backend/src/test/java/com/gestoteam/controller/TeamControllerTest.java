package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.TeamRequest;
import com.gestoteam.dto.response.TeamResponse;
import com.gestoteam.enums.Category;
import com.gestoteam.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

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
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    private TeamRequest validTeamRequest;

    @BeforeEach
    void setUp() {
        validTeamRequest = new TeamRequest();
        validTeamRequest.setName("New Team");
        validTeamRequest.setCategory(Category.SENIOR.name());
        validTeamRequest.setDivision("Primera Aficionados");
        validTeamRequest.setField("Campo de Pruebas");
        validTeamRequest.setLocation("Madrid");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllTeams_ShouldReturnTeamList() throws Exception {
        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setId(1L);
        teamResponse.setName("Test Team");

        given(teamService.getAllTeams()).willReturn(List.of(teamResponse));

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Team")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTeamById_ShouldReturnTeam_WhenFound() throws Exception {
        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setId(1L);
        teamResponse.setName("Test Team");

        given(teamService.getTeamById(1L)).willReturn(Optional.of(teamResponse));

        mockMvc.perform(get("/api/teams/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Team")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTeamById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        given(teamService.getTeamById(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/teams/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTeam_ShouldReturnCreated_WithValidData() throws Exception {
        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setId(1L);
        teamResponse.setName(validTeamRequest.getName());

        given(teamService.createTeam(any(TeamRequest.class))).willReturn(teamResponse);

        mockMvc.perform(post("/api/teams")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTeamRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Team")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTeam_ShouldReturnOk_WithValidData() throws Exception {
        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setId(1L);
        teamResponse.setName(validTeamRequest.getName());

        given(teamService.updateTeam(eq(1L), any(TeamRequest.class))).willReturn(teamResponse);

        mockMvc.perform(put("/api/teams/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTeamRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Team")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTeam_ShouldReturnNoContent() throws Exception {
        doNothing().when(teamService).deleteTeam(1L);

        mockMvc.perform(delete("/api/teams/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}