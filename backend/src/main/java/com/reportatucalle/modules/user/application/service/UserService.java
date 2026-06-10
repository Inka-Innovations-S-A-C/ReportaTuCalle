package com.reportatucalle.modules.user.application.service;

import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.user.application.dto.UserProfileResponse;
import com.reportatucalle.modules.user.application.dto.UpdateProfileRequest;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio del módulo User.
 *
 * Responsabilidades:
 * - Obtener el perfil del ciudadano autenticado
 * - Actualizar datos personales del perfil
 *
 * Separación de responsabilidades:
 * - AuthController maneja identidad (quién eres, tu token)
 * - UserController maneja perfil (tus datos como ciudadano)
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userProfileRepository;

    /**
     * Obtiene el perfil del usuario autenticado actualmente.
     * Extrae el accountId del JWT via SecurityContext.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {

        // Obtener la cuenta autenticada desde el contexto de seguridad
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Buscar el perfil vinculado via referencia blanda (accountId)
        UserProfile profile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        return toResponse(profile, currentAccount);
    }

    /**
     * Actualiza los datos personales del usuario autenticado.
     * Solo puede editar su propio perfil — nunca el de otro.
     */
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {

        // Obtener cuenta autenticada
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // Buscar perfil existente
        UserProfile existing = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        // Construir nueva instancia con datos actualizados (inmutabilidad del dominio)
        UserProfile updated = UserProfile.builder()
                .id(existing.getId())
                .accountId(existing.getAccountId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .createdAt(existing.getCreatedAt())
                .build();

        UserProfile saved = userProfileRepository.save(updated);

        return toResponse(saved, currentAccount);
    }

    /**
     * Convierte UserProfile + AuthAccount a DTO de respuesta.
     * El email viene de AuthAccount porque UserProfile no lo almacena
     * (separación de responsabilidades entre módulos).
     */
    private UserProfileResponse toResponse(UserProfile profile, AuthAccountJpaEntity account) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getFullName(),
                profile.getFirstName(),
                profile.getLastName(),
                account.getEmail(),
                account.getRole().name(),
                profile.getPhone(),
                profile.getCreatedAt()
        );
    }
}