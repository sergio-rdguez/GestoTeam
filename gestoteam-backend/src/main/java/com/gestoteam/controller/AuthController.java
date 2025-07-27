package com.gestoteam.controller;

import com.gestoteam.dto.request.AuthRequest;
import com.gestoteam.dto.response.AuthResponse;
import com.gestoteam.dto.response.UserResponse;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para registro e inicio de sesión de usuarios")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o el usuario ya existe")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y devuelve un token JWT si las credenciales son correctas.")
    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, devuelve token JWT")
    @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        // Obtenemos los detalles del usuario a partir del token JWT procesado por Spring Security.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Usamos el servicio para obtener la entidad completa.
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));;

        // Mapeamos a un DTO seguro para no exponer la contraseña ni otros datos sensibles.
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity.ok(userResponse);
    }
}