package com.reportatucalle.modules.auth.infrastructure.controller;

import com.reportatucalle.modules.auth.application.dto.AuthResponse;
import com.reportatucalle.modules.auth.application.dto.LoginRequest;
import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.application.service.AuthService;
import com.reportatucalle.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada (Driving Adapter) para gestionar el acceso web al sistema.
 * Solo se encarga de recibir peticiones HTTP y delegar el trabajo a la capa de Aplicación.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Ciudadano registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Inicio de sesión exitoso"));
    }
}