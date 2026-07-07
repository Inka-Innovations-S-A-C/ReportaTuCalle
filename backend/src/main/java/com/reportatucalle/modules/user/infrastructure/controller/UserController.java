package com.reportatucalle.modules.user.infrastructure.controller;

import com.reportatucalle.modules.user.application.dto.UpdateProfileRequest;
import com.reportatucalle.modules.user.application.dto.UserProfileResponse;
import com.reportatucalle.modules.user.application.dto.UserProfileSummaryResponse;
import com.reportatucalle.modules.user.application.service.UserService;
import com.reportatucalle.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de entrada web para el módulo de usuario.
 *
 * Separación clara de responsabilidades:
 * - AuthController → identidad (login, registro, token)
 * - UserController → perfil (datos personales, rol, historial)
 *
 * Todos los endpoints requieren autenticación.
 * El rol define qué puede ver cada usuario.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Obtiene el perfil del usuario autenticado.
     * Accesible por todos los roles — cada uno ve su propio perfil.
     *
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CITIZEN', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        UserProfileResponse response = userService.getMyProfile();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Perfil recuperado exitosamente")
        );
    }

    /**
     * Actualiza los datos personales del usuario autenticado.
     * Solo puede editar su propio perfil — nunca el de otro.
     * Email y rol no son editables desde aquí.
     *
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CITIZEN', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Perfil actualizado exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfileSummaryResponse>>> getAllUsers() {
        List<UserProfileSummaryResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response, "Usuarios recuperados exitosamente"));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<List<UserProfileSummaryResponse>>> getLeaderboard() {
        List<UserProfileSummaryResponse> response = userService.getLeaderboard();
        return ResponseEntity.ok(ApiResponse.success(response, "Ranking recuperado exitosamente"));
    }
}