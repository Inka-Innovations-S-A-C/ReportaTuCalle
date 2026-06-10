package com.reportatucalle.modules.media.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entidad de Dominio pura: Archivo multimedia.
 * 
 * PURO: Sin @Entity, @Table, @Column, sin JPA, sin Spring
 * Solo lógica de dominio relacionada con metadatos de archivos
 * Separado: No depende de infraestructura (S3, Local, etc.)
 */
@Getter
@Builder
public class Media {
    
    private final Long id;
    private final String fileName;
    private final String fileUrl; // URL pública o ruta
    private final String contentType;
    private final Long fileSizeBytes;
    private final String fileExtension;
    private final Long uploadedByCitizenId;
    private final LocalDateTime uploadedAt;
    
    /**
     * Valida si el media es válido según reglas de negocio.
     */
    public boolean isValid() {
        return fileName != null && !fileName.isBlank()
                && fileUrl != null && !fileUrl.isBlank()
                && contentType != null && !contentType.isBlank()
                && fileSizeBytes != null && fileSizeBytes > 0
                && uploadedByCitizenId != null;
    }
    
    /**
     * Verifica si el tipo de contenido es una imagen.
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * Obtiene la extensión del archivo.
     */
    public String getFileExtensionFromUrl() {
        if (fileUrl == null) return "";
        int lastDot = fileUrl.lastIndexOf('.');
        return lastDot > 0 ? fileUrl.substring(lastDot + 1) : "";
    }
}
