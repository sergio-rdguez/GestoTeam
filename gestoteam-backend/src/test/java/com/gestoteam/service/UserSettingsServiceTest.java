package com.gestoteam.service;

import com.gestoteam.model.UserSettings;
import com.gestoteam.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserSettingsService userSettingsService;

    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USERNAME);
    }

    @Test
    void getSettings_ShouldReturnExistingSettings_WhenFound() {
        UserSettings existingSettings = new UserSettings();
        existingSettings.setUserId(USERNAME);
        when(userSettingsRepository.findByUserId(USERNAME)).thenReturn(Optional.of(existingSettings));

        UserSettings result = userSettingsService.getSettings();

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(USERNAME);
    }

    @Test
    void getSettings_ShouldCreateDefaultSettings_WhenNotFound() {
        UserSettings defaultSettings = new UserSettings();
        defaultSettings.setUserId(USERNAME);
        when(userSettingsRepository.findByUserId(USERNAME)).thenReturn(Optional.empty());
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(defaultSettings);

        UserSettings result = userSettingsService.getSettings();

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(USERNAME);
        verify(userSettingsRepository).save(any(UserSettings.class));
    }

    @Test
    void updateSettings_ShouldUpdateAndReturnSettings() {
        UserSettings existingSettings = new UserSettings();
        existingSettings.setUserId(USERNAME);
        existingSettings.setMaxPlayersPerTeam(20);

        UserSettings updatedSettingsDto = new UserSettings();
        updatedSettingsDto.setMaxPlayersPerTeam(25);
        updatedSettingsDto.setMaxPlayersPerMatch(18);
        updatedSettingsDto.setYellowCardsForSuspension(5);
        updatedSettingsDto.setMaxTrainingsPerWeek(3);
        updatedSettingsDto.setNotifyBeforeMatch(false);
        updatedSettingsDto.setNotifyBeforeTraining(false);

        when(userSettingsRepository.findByUserId(USERNAME)).thenReturn(Optional.of(existingSettings));
        when(userSettingsRepository.save(any(UserSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSettings result = userSettingsService.updateSettings(updatedSettingsDto);

        assertThat(result.getMaxPlayersPerTeam()).isEqualTo(25);
        assertThat(result.getMaxTrainingsPerWeek()).isEqualTo(3);
        assertThat(result.getNotifyBeforeMatch()).isFalse();
        verify(userSettingsRepository).save(existingSettings);
    }
}