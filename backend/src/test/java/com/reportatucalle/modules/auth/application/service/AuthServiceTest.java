package com.reportatucalle.modules.auth.application.service;

import com.reportatucalle.modules.auth.application.dto.LoginRequest;
import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.application.mapper.AuthMapper;
import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.auth.domain.repository.AuthAccountRepository;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.auth.infrastructure.persistence.repository.AuthAccountJpaRepository;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import com.reportatucalle.shared.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthAccountRepository authRepository;
    private UserProfileRepository userRepository;
    private AuthAccountJpaRepository jpaRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthAccountRepository.class);
        userRepository = mock(UserProfileRepository.class);
        jpaRepository = mock(AuthAccountJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        authService = new AuthService(authRepository, userRepository, jpaRepository,
                passwordEncoder, jwtService, authenticationManager, new AuthMapper());
    }

    @Test
    void register_whenEmailIsNew_createsAccountProfileAndToken() {
        RegisterRequest request = new RegisterRequest("Ana", "Torres", "ana@mail.com", "secret123");
        AuthAccount savedAccount = AuthAccount.builder().id(20L).email("ana@mail.com")
                .passwordHash("encoded").role(Role.CITIZEN).build();
        UserProfile savedProfile = UserProfile.builder().id(30L).accountId(20L).firstName("Ana").lastName("Torres").build();
        AuthAccountJpaEntity jpa = AuthAccountJpaEntity.builder().id(20L).email("ana@mail.com")
                .password("encoded").role(Role.CITIZEN).build();

        when(authRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(authRepository.save(any(AuthAccount.class))).thenReturn(savedAccount);
        when(userRepository.save(any(UserProfile.class))).thenReturn(savedProfile);
        when(jpaRepository.findById(20L)).thenReturn(Optional.of(jpa));
        when(jwtService.generateToken(anyMap(), eq(jpa))).thenReturn("jwt-token");

        assertEquals("jwt-token", authService.register(request).token());
        verify(authRepository).save(any(AuthAccount.class));
        verify(userRepository).save(any(UserProfile.class));
    }

    @Test
    void register_whenEmailExists_throwsBusinessException() {
        RegisterRequest request = new RegisterRequest("Ana", "Torres", "ana@mail.com", "secret123");
        when(authRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals("EMAIL_ALREADY_EXISTS", ex.getErrorCode());
        verify(authRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_authenticatesAndReturnsToken() {
        LoginRequest request = new LoginRequest("ana@mail.com", "secret123");
        AuthAccount account = AuthAccount.builder().id(20L).email("ana@mail.com").passwordHash("encoded").role(Role.CITIZEN).build();
        UserProfile profile = UserProfile.builder().id(30L).accountId(20L).firstName("Ana").lastName("Torres").build();
        AuthAccountJpaEntity jpa = AuthAccountJpaEntity.builder().id(20L).email("ana@mail.com")
                .password("encoded").role(Role.CITIZEN).build();
        when(authRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(account));
        when(userRepository.findByAccountId(20L)).thenReturn(Optional.of(profile));
        when(jpaRepository.findById(20L)).thenReturn(Optional.of(jpa));
        when(jwtService.generateToken(anyMap(), eq(jpa))).thenReturn("login-token");

        assertEquals("login-token", authService.login(request).token());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_whenAccountMissing_throwsBusinessException() {
        LoginRequest request = new LoginRequest("nadie@mail.com", "secret123");
        when(authRepository.findByEmail("nadie@mail.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals("ACCOUNT_NOT_FOUND", ex.getErrorCode());
    }
}
