package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
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

    public String login(String username, String rawPassword) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPassword));
        } catch (Exception e) {
            throw new GestoServiceException("Usuario o contraseña incorrectos");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }
}
