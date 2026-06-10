package com.reportatucalle.modules.auth.application.service;

import com.reportatucalle.modules.auth.application.dto.AuthResponse;
import com.reportatucalle.modules.auth.application.dto.LoginRequest;
import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.application.mapper.AuthMapper;
import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.repository.AuthAccountRepository;
import com.reportatucalle.modules.auth.infrastructure.persistence.repository.AuthAccountJpaRepository;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import com.reportatucalle.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Orquestador principal de la autenticación.
 * 
 * Coordina:
 * - Puerto AuthAccountRepository (trabaja con AuthAccountDomain)
 * - Puerto UserProfileRepository (trabaja con UserProfileDomain)
 * - JwtService para generación de tokens
 * - PasswordEncoder para encriptación
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthAccountRepository authAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthAccountJpaRepository authAccountJpaRepository; // Para Spring Security (UserDetails)
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        
        // 1. Validar si el correo ya existe
        if (authAccountRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    "El correo ya está registrado en el sistema", 
                    "EMAIL_ALREADY_EXISTS"
            );
        }

        // 2. Crear domain puro y guardar via puerto
        String encodedPassword = passwordEncoder.encode(request.password());
        AuthAccount accountDomain = authMapper.toAuthAccountDomain(request, encodedPassword);
        AuthAccount savedAccount = authAccountRepository.save(accountDomain);

        // 3. Crear perfil de usuario y guardar
        UserProfile profileDomain = authMapper.toUserProfileDomain(request, savedAccount.getId());
        UserProfile savedProfile = userProfileRepository.save(profileDomain);

        // 4. Generar JWT usando JpaEntity (para Spring Security UserDetails)
        // Necesitamos recuperar la JpaEntity para acceder a UserDetails
        var jpaAccount = authAccountJpaRepository.findById(savedAccount.getId())
                .orElseThrow(() -> new BusinessException("Cuenta no encontrada", "ACCOUNT_NOT_FOUND"));
        
        String jwtToken = jwtService.generateToken(
                Map.of("role", savedAccount.getRole().name(), "profileId", savedProfile.getId()), 
                jpaAccount
        );

        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Recuperar cuenta via puerto (retorna domain)
        AuthAccount accountDomain = authAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(
                        "Cuenta de autenticación no encontrada", 
                        "ACCOUNT_NOT_FOUND"
                ));

        // Recuperar perfil via puerto (retorna domain)
        UserProfile profileDomain = userProfileRepository.findByAccountId(accountDomain.getId())
                .orElseThrow(() -> new BusinessException(
                        "Perfil de usuario no encontrado", 
                        "PROFILE_NOT_FOUND"
                ));

        // Generar JWT usando JpaEntity (para Spring Security UserDetails)
        var jpaAccount = authAccountJpaRepository.findById(accountDomain.getId())
                .orElseThrow(() -> new BusinessException("Cuenta no encontrada", "ACCOUNT_NOT_FOUND"));

        String jwtToken = jwtService.generateToken(
                Map.of("role", accountDomain.getRole().name(), "profileId", profileDomain.getId()), 
                jpaAccount
        );

        return new AuthResponse(jwtToken);
    }
}