package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.model.UserSettings;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.repository.UserSettingsRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserSettingsService extends BaseService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserRepository userRepository, UserSettingsRepository userSettingsRepository) {
        super(userRepository);
        this.userSettingsRepository = userSettingsRepository;
    }



    public UserSettings getSettings() {
        String username = getCurrentUsername();
        log.info("Obteniendo configuraciones para el usuario: {}", username);

        try {
            // Primero obtener el usuario para obtener su ID
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new GestoServiceException("Usuario no encontrado: " + username));
            
            return userSettingsRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        log.info("No se encontraron configuraciones para {}. Creando por defecto.", username);
                        return createDefaultSettings(user.getId());
                    });
        } catch (Exception e) {
            log.error("Error al obtener configuraciones para el usuario: {}", username, e);
            throw new GestoServiceException("Error al obtener las configuraciones del usuario.");
        }
    }

    public UserSettings updateSettings(UserSettings updatedSettings) {
        String username = getCurrentUsername();
        log.info("Actualizando configuraciones para el usuario: {}", username);

        try {
            UserSettings settings = getSettings(); // getSettings ya usa el usuario correcto

            settings.setMaxPlayersPerTeam(updatedSettings.getMaxPlayersPerTeam());
            settings.setMaxPlayersPerMatch(updatedSettings.getMaxPlayersPerMatch());
            settings.setYellowCardsForSuspension(updatedSettings.getYellowCardsForSuspension());
            settings.setMaxTrainingsPerWeek(updatedSettings.getMaxTrainingsPerWeek());
            settings.setNotifyBeforeMatch(updatedSettings.getNotifyBeforeMatch());
            settings.setNotifyBeforeTraining(updatedSettings.getNotifyBeforeTraining());

            UserSettings savedSettings = userSettingsRepository.save(settings);
            log.info("Configuraciones actualizadas correctamente para el usuario: {}", username);
            return savedSettings;
        } catch (Exception e) {
            log.error("Error al actualizar configuraciones para el usuario: {}", username, e);
            throw new GestoServiceException("Error al actualizar las configuraciones del usuario.");
        }
    }

    public UserSettings createDefaultSettings(Long userId) {
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
}
