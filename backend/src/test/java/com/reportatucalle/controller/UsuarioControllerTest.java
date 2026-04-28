package com.reportatucalle.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.reportatucalle.dto.UsuarioRequest;
import com.reportatucalle.dto.UsuarioResponse;
import com.reportatucalle.service.UsuarioService;

class UsuarioControllerTest {

    @Test
    void registrarDevuelveCreated() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        UsuarioController controller = new UsuarioController(usuarioService);

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("ana@test.com");
        request.setPassword("12345678");
        request.setNombre("Ana");
        request.setTipo("ciudadano");

        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setEmail("ana@test.com");
        usuarioResponse.setNombre("Ana");
        usuarioResponse.setTipo("ciudadano");

        when(usuarioService.registrar(request)).thenReturn(usuarioResponse);

        ResponseEntity<UsuarioResponse> response = controller.registrar(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("ana@test.com", response.getBody().getEmail());
        assertEquals("Ana", response.getBody().getNombre());

        verify(usuarioService).registrar(request);
    }

    @Test
    void loginDevuelveToken() {
        UsuarioService usuarioService = mock(UsuarioService.class);
        UsuarioController controller = new UsuarioController(usuarioService);

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("ana@test.com");
        request.setPassword("12345678");

        when(usuarioService.login("ana@test.com", "12345678"))
                .thenReturn("token-provisional-valido-ana@test.com");

        ResponseEntity<Map<String, String>> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("token-provisional-valido-ana@test.com", response.getBody().get("token"));

        verify(usuarioService).login("ana@test.com", "12345678");
    }
}