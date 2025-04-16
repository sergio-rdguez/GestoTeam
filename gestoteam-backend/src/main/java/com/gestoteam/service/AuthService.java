package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSettingsService userSettingsService;

    public void register(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new GestoServiceException("El nombre de usuario ya está en uso");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        userSettingsService.createDefaultSettings(username);
    }

    public void login(String username, String rawPassword) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new GestoServiceException("Usuario no encontrado o marcado como eliminado"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new GestoServiceException("Contraseña incorrecta");
        }
    }
}
