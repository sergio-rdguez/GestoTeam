package com.gestoteam.controller;

import com.gestoteam.dto.request.AuthRequest;
import com.gestoteam.dto.response.AuthResponse;
import com.gestoteam.dto.response.UserResponse;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import com.gestoteam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
            summary = "Obtener el perfil del usuario autenticado",
            description = "Devuelve la información del perfil del usuario que realiza la petición, basado en su token JWT."
    )
    @ApiResponse(responseCode = "200", description = "Perfil del usuario obtenido con éxito")
    @ApiResponse(responseCode = "401", description = "No autorizado, el token no es válido o no se ha proporcionado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> getUserProfile() {
        UserResponse userProfile = authService.getUserProfile();
        return ResponseEntity.ok(userProfile);
    }
}