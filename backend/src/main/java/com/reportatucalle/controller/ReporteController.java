package com.reportatucalle.controller;

import com.reportatucalle.dto.ReporteRequest;
import com.reportatucalle.dto.ReporteResponse;
import com.reportatucalle.service.ReporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {
    
    private final ReporteService reporteService;
    //agregando este metodo para que valide si el usuario dispone de un token de inicio de sesion para acceder a los reportes
    private boolean esTokenValido(String token) {
        return token != null && token.startsWith("token-provisional-valido-");
    }
    
    @PostMapping
    public ResponseEntity<ReporteResponse> crear(@RequestHeader(value = "Authorization", required = false) String token, 
                                        @RequestBody ReporteRequest reporte) {
        if (!esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.crear(reporte));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponse> obtener(
        @RequestHeader(value = "Authorization", required = false) String token, 
        @PathVariable Long id) {
        if (!esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        try {
            ReporteResponse response = reporteService.obtener(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
        // 404
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ReporteResponse>> listar(@RequestHeader(value = "Authorization", required = false) String token) {
        if (!esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        return ResponseEntity.ok(reporteService.listar()); // 200 OK
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReporteResponse> cambiarEstado(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable Long id, 
        @RequestParam String estado) {
        if (!esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }
        try {
            ReporteResponse response = reporteService.cambiarEstado(id, estado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().build(); // 400
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable Long id) {
    
        if (!esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        try {
            reporteService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}
