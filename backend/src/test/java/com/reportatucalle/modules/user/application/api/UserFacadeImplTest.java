package com.reportatucalle.modules.user.application.api;

import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFacadeImplTest {

    private final UserProfileRepository repository = mock(UserProfileRepository.class);
    private final UserFacadeImpl facade = new UserFacadeImpl(repository);

    @Test
    void returnsUserPublicInformation() {
        UserProfile user = UserProfile.builder().id(5L).accountId(9L).firstName("Luis").lastName("Ramos").build();
        when(repository.findById(5L)).thenReturn(Optional.of(user));
        when(repository.findByAccountId(9L)).thenReturn(Optional.of(user));
        when(repository.existsById(5L)).thenReturn(true);
        when(repository.existsByAccountId(9L)).thenReturn(true);

        assertEquals(Optional.of("Luis Ramos"), facade.getFullNameForUser(5L));
        assertEquals(Optional.of(5L), facade.getUserIdByAccountId(9L));
        assertTrue(facade.userExists(5L));
        assertTrue(facade.userExistsByAccountId(9L));
    }
}
