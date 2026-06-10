package com.reportatucalle.modules.media.application.service;

import com.reportatucalle.modules.media.domain.portsout.StoragePort;
import com.reportatucalle.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    // Inyectamos la abstracción. Spring decidirá qué adaptador usar.
    private final StoragePort storagePort;
    
    // Límite estricto de peso: 5 Megabytes (Protege contra ataques de denegación de servicio por llenado de disco)
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; 
    
    // Lista blanca (White-list) de formatos permitidos. Bloquea ejecutables o scripts maliciosos.
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");

    /**
     * Procesa, valida y sube una imagen al proveedor de almacenamiento configurado.
     */
    public String uploadImage(MultipartFile file) {
        
        // 1. Validaciones de Seguridad (Payload Bounding)
        if (file == null || file.isEmpty()) {
            throw new BusinessException("No se ha enviado ningún archivo", "FILE_EMPTY");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("La imagen supera el límite permitido de 5MB", "FILE_TOO_LARGE");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Formato no válido. Solo se permiten imágenes (JPG, PNG, WEBP)", "INVALID_FILE_TYPE");
        }

        // 2. Renombrado Seguro (Previene sobreescritura si dos usuarios suben una foto llamada "bache.jpg")
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Asumimos .jpg por defecto si el archivo viene sin extensión explícita
            extension = ".jpg"; 
        }
        
        // Generamos un identificador único universal (UUID)
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 3. Ejecución del guardado delegando al Adaptador
        try {
            return storagePort.uploadFile(uniqueFileName, file.getBytes());
        } catch (IOException e) {
            throw new BusinessException("Error interno al leer los bytes de la imagen", "FILE_PROCESSING_ERROR");
        }
    }
}