package com.reportatucalle.service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.reportatucalle.dto.UsuarioRequest;
import com.reportatucalle.dto.UsuarioResponse;

class UsuarioServiceExtraTest {

    @Test
    void registrarUsuarioSinTipoUsaCiudadanoPorDefecto() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("sinTipo@test.com");
        request.setPassword("12345678");
        request.setNombre("Sin Tipo");

        UsuarioResponse response = service.registrar(request);

        assertEquals("ciudadano", response.getTipo());
    }

    @Test
    void registrarEmailDuplicadoLanzaError() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("duplicado@test.com");
        request.setPassword("12345678");
        request.setNombre("Usuario");
        request.setTipo("ciudadano");

        service.registrar(request);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.registrar(request));

        assertNotNull(exception);
    }

    @Test
    void registrarSupervisorSinTelefonoLanzaError() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("supervisor@test.com");
        request.setPassword("12345678");
        request.setNombre("Supervisor");
        request.setTipo("supervisor");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.registrar(request));

        assertNotNull(exception);
    }

    @Test
    void loginConPasswordIncorrectoLanzaError() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("login@test.com");
        request.setPassword("12345678");
        request.setNombre("Login");
        request.setTipo("ciudadano");

        service.registrar(request);

        SecurityException exception =
                assertThrows(SecurityException.class, () -> service.login("login@test.com", "incorrecta"));

        assertNotNull(exception);
    }

    @Test
    void obtenerListarYBuscarPorEmailFuncionan() {
        UsuarioService service = new UsuarioService();

        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("buscar@test.com");
        request.setPassword("12345678");
        request.setNombre("Buscar");
        request.setTipo("ciudadano");

        UsuarioResponse creado = service.registrar(request);

        UsuarioResponse obtenido = service.obtener(creado.getId());
        assertEquals("buscar@test.com", obtenido.getEmail());

        List<UsuarioResponse> usuarios = service.listar();
        assertFalse(usuarios.isEmpty());

        UsuarioResponse porEmail = service.obtenerPorEmail("buscar@test.com");
        assertEquals("Buscar", porEmail.getNombre());

        NoSuchElementException exception1 =
                assertThrows(NoSuchElementException.class, () -> service.obtener(999L));

        NoSuchElementException exception2 =
                assertThrows(NoSuchElementException.class, () -> service.obtenerPorEmail("nadie@test.com"));

        assertNotNull(exception1);
        assertNotNull(exception2);
    }
}