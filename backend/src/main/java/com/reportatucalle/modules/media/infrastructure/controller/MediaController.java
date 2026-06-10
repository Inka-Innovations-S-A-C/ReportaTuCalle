package com.reportatucalle.modules.media.infrastructure.controller;

import com.reportatucalle.modules.media.application.service.MediaService;
import com.reportatucalle.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * ENDPOINT DE SUBIDA:
 * Expone la funcionalidad a través de HTTP.
 */
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * Endpoint para cargar imágenes.
     * Espera una petición POST tipo form-data con un campo llamado "file".
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        
        // Delega la regla de negocio al servicio
        String imageUrl = mediaService.uploadImage(file);
        
        // Devuelve la URL envuelta en nuestro DTO estándar de respuesta: 
        // { "data": { "url": "http://..." }, "message": "...", "status": 200 }
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("url", imageUrl), "Imagen procesada y almacenada correctamente")
        );
    }
}