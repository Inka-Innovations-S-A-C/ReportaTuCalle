package com.reportatucalle.modules.user.application.service;

import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.user.application.dto.UpdateProfileRequest;
import com.reportatucalle.modules.user.application.dto.UserProfileResponse;
import com.reportatucalle.modules.user.application.dto.UserProfileSummaryResponse;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserProfileRepository repository;
    private UserService service;
    private AuthAccountJpaEntity account;

    @BeforeEach
    void setUp() {
        repository = mock(UserProfileRepository.class);
        service = new UserService(repository);
        account = AuthAccountJpaEntity.builder().id(9L).email("ana@mail.com").password("x").role(Role.CITIZEN).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(account, null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyProfile_returnsProfileForAuthenticatedAccount() {
        LocalDateTime created = LocalDateTime.now();
        UserProfile profile = UserProfile.builder().id(1L).accountId(9L).firstName("Ana").lastName("Torres")
                .phone("999999999").createdAt(created).build();
        when(repository.findByAccountId(9L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = service.getMyProfile();

        assertEquals(1L, response.id());
        assertEquals("Ana Torres", response.fullName());
        assertEquals("ana@mail.com", response.email());
        assertEquals("CITIZEN", response.role());
    }

    @Test
    void updateMyProfile_savesUpdatedImmutableProfile() {
        UserProfile existing = UserProfile.builder().id(1L).accountId(9L).firstName("Ana").lastName("Torres").build();
        UserProfile saved = UserProfile.builder().id(1L).accountId(9L).firstName("Ana Maria").lastName("Rojas")
                .phone("988888888").build();
        when(repository.findByAccountId(9L)).thenReturn(Optional.of(existing));
        when(repository.save(any(UserProfile.class))).thenReturn(saved);

        UserProfileResponse response = service.updateMyProfile(new UpdateProfileRequest("Ana Maria", "Rojas", "988888888"));

        assertEquals("Ana Maria Rojas", response.fullName());
        assertEquals("988888888", response.phone());
        verify(repository).save(any(UserProfile.class));
    }

    @Test
    void getMyProfile_whenProfileMissing_throwsBusinessException() {
        when(repository.findByAccountId(9L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.getMyProfile());

        assertEquals("PROFILE_NOT_FOUND", ex.getErrorCode());
    }

    @Test
    void getAllUsers_returnsSummaryResponses() {
        UserProfile p1 = UserProfile.builder().id(1L).accountId(10L).firstName("A").lastName("B").phone("123").build();
        UserProfile p2 = UserProfile.builder().id(2L).accountId(20L).firstName("C").lastName("D").phone("456").build();
        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<UserProfileSummaryResponse> result = service.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("A B", result.get(0).fullName());
        assertEquals("C D", result.get(1).fullName());
        assertEquals(10L, result.get(0).accountId());
    }
}
