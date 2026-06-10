package com.reportatucalle.modules.auth.application.mapper;

import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperTest {

    private final AuthMapper mapper = new AuthMapper();

    @Test
    void mapsRegisterRequestToDomainObjects() {
        RegisterRequest request = new RegisterRequest("Ana", "Torres", "ana@mail.com", "secret123");

        AuthAccount account = mapper.toAuthAccountDomain(request, "encoded");
        UserProfile profile = mapper.toUserProfileDomain(request, 10L);

        assertEquals("ana@mail.com", account.getEmail());
        assertEquals("encoded", account.getPasswordHash());
        assertEquals(Role.CITIZEN, account.getRole());
        assertEquals(10L, profile.getAccountId());
        assertEquals("Ana Torres", profile.getFullName());
    }
}
