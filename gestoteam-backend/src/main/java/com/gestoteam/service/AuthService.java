package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSettingsService userSettingsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

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

// Dentro de la clase AuthService

    public String login(String username, String rawPassword) {
        // Log de inicio
        log.info("Intento de inicio de sesión para el usuario: {}", username);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword)
            );
        } catch (Exception e) {
            // --- LOG CLAVE AÑADIDO ---
            // Aquí registramos el error real que nos da Spring Security
            log.error("Error de autenticación para el usuario '{}': {}", username, e.getMessage());

            // Mantenemos el mensaje genérico para el usuario final
            throw new GestoServiceException("Usuario o contraseña incorrectos");
        }

        // Log de éxito
        log.info("Autenticación exitosa para {}. Generando token.", username);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }
}
