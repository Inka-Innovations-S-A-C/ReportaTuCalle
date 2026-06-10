package com.reportatucalle.modules.auth.application.api;

import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.auth.domain.repository.AuthAccountRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthFacadeImplTest {

    private final AuthAccountRepository repository = mock(AuthAccountRepository.class);
    private final AuthFacadeImpl facade = new AuthFacadeImpl(repository);

    @Test
    void returnsAccountPublicInformation() {
        AuthAccount account = AuthAccount.builder().id(1L).email("admin@mail.com").passwordHash("hash").role(Role.ADMIN).build();
        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(repository.existsById(1L)).thenReturn(true);

        assertEquals(Optional.of("admin@mail.com"), facade.getEmailForAccount(1L));
        assertEquals(Optional.of("ADMIN"), facade.getRoleForAccount(1L));
        assertTrue(facade.accountExists(1L));
    }

    @Test
    void returnsEmptyWhenAccountDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        when(repository.existsById(99L)).thenReturn(false);

        assertEquals(Optional.empty(), facade.getEmailForAccount(99L));
        assertEquals(Optional.empty(), facade.getRoleForAccount(99L));
        assertFalse(facade.accountExists(99L));
    }
}
