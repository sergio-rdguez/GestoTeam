package com.gestoteam.controller;

import com.gestoteam.service.EnumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@WithMockUser(username = "testuser")
class EnumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnumService enumService;

    @Test
    void getPlayerStatuses_ShouldReturnStatuses() throws Exception {
        List<EnumService.EnumResponse> statuses = List.of(new EnumService.EnumResponse("ACTIVO", "Activo"));
        when(enumService.getPlayerStatuses()).thenReturn(statuses);

        mockMvc.perform(get("/api/enums/player-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("ACTIVO")));
    }

    @Test
    void getPositions_ShouldReturnPositions() throws Exception {
        List<EnumService.EnumResponse> positions = List.of(new EnumService.EnumResponse("DC", "Delantero Centro"));
        when(enumService.getPositions()).thenReturn(positions);

        mockMvc.perform(get("/api/enums/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("DC")));
    }

    @Test
    void getCategories_ShouldReturnCategories() throws Exception {
        List<EnumService.EnumResponse> categories = List.of(new EnumService.EnumResponse("SENIOR", "Senior"));
        when(enumService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/enums/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("SENIOR")));
    }
}