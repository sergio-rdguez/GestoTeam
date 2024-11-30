package com.gestoteam.service;

import com.gestoteam.dto.Audit;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.UserSettings;
import com.gestoteam.repository.UserSettingsRepository;
import com.gestoteam.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final ValidationUtil validationUtil;

    public UserSettings getSettings(String audit) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Obteniendo configuraciones para el usuario: {}", auditInfo.getUser());

        try {
            return userSettingsRepository.findByUserId(auditInfo.getUser())
                    .orElseGet(() -> {
                        log.info("No se encontraron configuraciones para el usuario: {}. Creando configuraciones por defecto.", auditInfo.getUser());
                        return createDefaultSettings(auditInfo.getUser());
                    });
        } catch (Exception e) {
            log.error("Error al obtener configuraciones para el usuario: {}", auditInfo.getUser(), e);
            throw new GestoServiceException("Error al obtener las configuraciones del usuario.");
        }
    }

    private UserSettings createDefaultSettings(String userId) {
        log.info("Creando configuraciones por defecto para el usuario: {}", userId);

        UserSettings defaultSettings = new UserSettings();
        defaultSettings.setUserId(userId);
        defaultSettings.setMaxPlayersPerTeam(25);
        defaultSettings.setMaxPlayersPerMatch(18);
        defaultSettings.setYellowCardsForSuspension(5);
        defaultSettings.setMaxTrainingsPerWeek(4);
        defaultSettings.setNotifyBeforeMatch(true);
        defaultSettings.setNotifyBeforeTraining(true);

        try {
            UserSettings savedSettings = userSettingsRepository.save(defaultSettings);
            log.info("Configuraciones por defecto creadas correctamente para el usuario: {}", userId);
            return savedSettings;
        } catch (Exception e) {
            log.error("Error al crear configuraciones por defecto para el usuario: {}", userId, e);
            throw new GestoServiceException("Error al crear configuraciones por defecto.");
        }
    }

    public UserSettings updateSettings(String audit, UserSettings updatedSettings) {
        Audit auditInfo = validationUtil.validateAudit(audit);
        log.info("Actualizando configuraciones para el usuario: {}", auditInfo.getUser());

        try {
            UserSettings settings = getSettings(audit);

            settings.setMaxPlayersPerTeam(updatedSettings.getMaxPlayersPerTeam());
            settings.setMaxPlayersPerMatch(updatedSettings.getMaxPlayersPerMatch());
            settings.setYellowCardsForSuspension(updatedSettings.getYellowCardsForSuspension());
            settings.setMaxTrainingsPerWeek(updatedSettings.getMaxTrainingsPerWeek());
            settings.setNotifyBeforeMatch(updatedSettings.getNotifyBeforeMatch());
            settings.setNotifyBeforeTraining(updatedSettings.getNotifyBeforeTraining());

            UserSettings savedSettings = userSettingsRepository.save(settings);
            log.info("Configuraciones actualizadas correctamente para el usuario: {}", auditInfo.getUser());
            return savedSettings;
        } catch (Exception e) {
            log.error("Error al actualizar configuraciones para el usuario: {}", auditInfo.getUser(), e);
            throw new GestoServiceException("Error al actualizar las configuraciones del usuario.");
        }
    }
}
