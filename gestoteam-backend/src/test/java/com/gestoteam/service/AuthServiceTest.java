package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserSettingsService userSettingsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
    }

    @Test
    void register_ShouldSaveUser_WhenUsernameIsNew() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        // Simular que el usuario se guarda y se le asigna un ID
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        authService.register("testuser", "password");

        verify(userRepository).save(any(User.class));
        verify(userSettingsService).createDefaultSettings(1L);
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("testuser", "password"))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("El nombre de usuario ya está en uso");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-token");

        String token = authService.login("testuser", "password");

        assertThat(token).isEqualTo("test-token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login("testuser", "wrongpassword"))
                .isInstanceOf(GestoServiceException.class)
                .hasMessage("Usuario o contraseña incorrectos");
    }
}