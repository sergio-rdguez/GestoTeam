package com.gestoteam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.model.UserSettings;
import com.gestoteam.service.UserSettingsService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@WithMockUser(username = "testuser")
class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserSettingsService userSettingsService;

    @Test
    void getSettings_ShouldReturnUserSettings() throws Exception {
        UserSettings settings = new UserSettings();
        settings.setId(1L);
        settings.setUserId("testuser");
        settings.setMaxPlayersPerTeam(25);

        when(userSettingsService.getSettings()).thenReturn(settings);

        mockMvc.perform(get("/api/user-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxPlayersPerTeam", is(25)));
    }

    @Test
    void updateSettings_ShouldReturnUpdatedSettings() throws Exception {
        UserSettings updatedSettings = new UserSettings();
        updatedSettings.setId(1L);
        updatedSettings.setUserId("testuser");
        updatedSettings.setMaxPlayersPerTeam(30);

        when(userSettingsService.updateSettings(any(UserSettings.class))).thenReturn(updatedSettings);

        mockMvc.perform(put("/api/user-settings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSettings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxPlayersPerTeam", is(30)));
    }
}