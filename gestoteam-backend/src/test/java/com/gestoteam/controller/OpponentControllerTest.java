package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.request.OpponentRequest;
import com.gestoteam.dto.response.OpponentResponse;
import com.gestoteam.service.OpponentService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@WithMockUser(username = "testuser")
class OpponentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpponentService opponentService;

    @Test
    void createOpponent_ShouldReturnOpponent() throws Exception {
        OpponentRequest request = new OpponentRequest();
        request.setName("Rival Test");
        request.setField("Campo Test");
        request.setObservations("Observaciones del rival");
        request.setTeamId(1L);

        OpponentResponse response = new OpponentResponse();
        response.setId(1L);
        response.setName("Rival Test");
        response.setField("Campo Test");
        response.setObservations("Observaciones del rival");
        response.setTeamId(1L);

        when(opponentService.createOpponent(any(OpponentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/opponents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Rival Test")));
    }

    @Test
    void getOpponentsByTeam_ShouldReturnOpponentList() throws Exception {
        OpponentResponse response = new OpponentResponse();
        response.setId(1L);
        response.setName("Rival Test");

        when(opponentService.getOpponentsByTeam(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/opponents/team/{teamId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Rival Test")));
    }

    @Test
    void getOpponentById_ShouldReturnOpponent() throws Exception {
        OpponentResponse response = new OpponentResponse();
        response.setId(1L);
        response.setName("Rival Test");
        response.setField("Campo Test");
        response.setObservations("Observaciones del rival");
        response.setTeamId(1L);

        when(opponentService.getOpponentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/opponents/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Rival Test")));
    }

    @Test
    void updateOpponent_ShouldReturnUpdatedOpponent() throws Exception {
        OpponentRequest request = new OpponentRequest();
        request.setName("Rival Actualizado");
        request.setField("Campo Nuevo");
        request.setObservations("Nuevas observaciones");
        request.setTeamId(1L);

        OpponentResponse response = new OpponentResponse();
        response.setId(1L);
        response.setName("Rival Actualizado");
        response.setField("Campo Nuevo");
        response.setObservations("Nuevas observaciones");

        when(opponentService.updateOpponent(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/opponents/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Rival Actualizado")));
    }

    @Test
    void deleteOpponent_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/opponents/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}