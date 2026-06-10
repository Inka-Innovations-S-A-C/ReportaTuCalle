package com.reportatucalle.modules.auth.application.mapper;

import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import org.springframework.stereotype.Component;

/**
 * Componente responsable de traducir los DTOs de entrada en Entidades de dominio (puros).
 * 
 * DTO → Domain: Convierte requests HTTP a domain POJOs.
 * Domain → JPA: Delegado a AuthMapper/UserMapper en infrastructure.
 */
@Component
public class AuthMapper {

    /**
     * Convierte el DTO de registro en una cuenta de seguridad de dominio puro.
     * Recibe la contraseña ya encriptada desde el servicio.
     */
    public AuthAccount toAuthAccountDomain(RegisterRequest request, String encodedPassword) {
        return AuthAccount.builder()
                .email(request.email())
                .passwordHash(encodedPassword)
                .role(Role.CITIZEN) // Todo registro público es ciudadano por defecto
                .build();
    }

    /**
     * Convierte el DTO de registro en un perfil de ciudadano de dominio puro.
     * Recibe el ID de la cuenta recién creada para mantener el bajo acoplamiento.
     */
    public UserProfile toUserProfileDomain(RegisterRequest request, Long accountId) {
        return UserProfile.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
    }
}