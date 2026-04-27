package com.reportatucalle.service;

import com.reportatucalle.dto.UsuarioRequest;
import com.reportatucalle.dto.UsuarioResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService {
    
    private final Map<Long, UsuarioResponse> usuarios = new HashMap<>();
    private final Map<String,String> credenciales = new HashMap<>();//agregado variable credenciales
    private Long nextId = 1L;
    
    public UsuarioResponse registrar(UsuarioRequest request) {
        // Validaciones básicas
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email es requerido");
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password debe tener mínimo 8 caracteres");
        }
        
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre es requerido");
        }
        
        // Email único -> Reemplacé la logica del metodo para que use las credenciales
        if (credenciales.containsKey(request.getEmail())) {  
            throw new IllegalArgumentException("Email ya existe");
        }
        
        // Validar que si es supervisor/admin, requiere teléfono
        if (("supervisor".equals(request.getTipo()) || "admin".equals(request.getTipo())) &&
            (request.getTelefono() == null || request.getTelefono().isBlank())) {
            throw new IllegalArgumentException("Teléfono es obligatorio para supervisores y admins");
        }
        
        Long id = nextId++;
        UsuarioResponse response = new UsuarioResponse();
        response.setId(id);
        response.setEmail(request.getEmail());
        response.setNombre(request.getNombre());
        response.setTipo(request.getTipo() != null ? request.getTipo() : "ciudadano");
        response.setTelefono(request.getTelefono());
        response.setFechaRegistro(LocalDateTime.now());
        
        usuarios.put(id, response);
        credenciales.put(request.getEmail(),request.getPassword());
        return response;
    }
    //FRAGMENTO DE CODIGO AGREGADO (LOGIN)
    public String login(String email, String password) {
        if (!credenciales.containsKey(email)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }

        if (!credenciales.get(email).equals(password)) {
            throw new SecurityException("Contraseña incorrecta");
        }

        return "token-provisional-valido-" + email;
    }
    
    public UsuarioResponse obtener(Long id) {
        UsuarioResponse usuario = usuarios.get(id);
        if (usuario == null) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        return usuario;
    }
    
    public List<UsuarioResponse> listar() {
        return new ArrayList<>(usuarios.values());
    }
    
    public UsuarioResponse obtenerPorEmail(String email) {
        return usuarios.values().stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }
}
