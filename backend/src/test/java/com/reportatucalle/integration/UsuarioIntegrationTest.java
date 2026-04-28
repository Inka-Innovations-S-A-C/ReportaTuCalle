package com.reportatucalle.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reportatucalle.dto.UsuarioRequest;
import com.reportatucalle.service.UsuarioService;

@SpringBootTest
class UsuarioIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void registerYLoginFuncionan() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("integracion@test.com");
        request.setPassword("12345678");
        request.setNombre("Usuario Test");
        request.setTipo("ciudadano");

        usuarioService.registrar(request);

        String token = usuarioService.login(
                "integracion@test.com",
                "12345678"
        );

        assertNotNull(token);
        assertTrue(
                token.startsWith(
                        "token-provisional-valido-"
                )
        );
    }
}