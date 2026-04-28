package com.reportatucalle.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.reportatucalle.dto.UsuarioRequest;

class UsuarioServiceTest {

    @Test
    void loginConUsuarioRegistradoDevuelveToken() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("ana@test.com");
        request.setPassword("12345678");
        request.setNombre("Ana");
        request.setTipo("ciudadano");

        service.registrar(request);

        String token = service.login("ana@test.com", "12345678");

        assertNotNull(token);
        assertTrue(token.startsWith("token-provisional-valido-"));
    }
}