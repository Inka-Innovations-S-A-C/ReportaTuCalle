package com.reportatucalle.modules.media.infrastructure.adapter;

import com.reportatucalle.modules.media.domain.portsout.StoragePort;
import com.reportatucalle.shared.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ADAPTADOR DE INFRAESTRUCTURA:
 * Implementa el guardado de archivos en una carpeta física del servidor.
 * La anotación @ConditionalOnProperty asegura que Spring solo cargue esta clase
 * si explícitamente se lo pedimos en el archivo de propiedades.
 */
@Component
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true) // matchIfMissing activa este por defecto si olvidas poner la propiedad
public class LocalStorageAdapter implements StoragePort {

    // Define la carpeta donde vivirán los archivos (se creará en la raíz de tu proyecto)
    private final Path rootLocation = Paths.get("uploads");

    // Lee la URL base desde las propiedades, o usa localhost:8080 por defecto
    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Este método se ejecuta automáticamente cuando Spring Boot arranca.
     * Garantiza que la carpeta 'uploads' exista antes de recibir la primera foto.
     */
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error crítico: No se pudo crear el directorio de almacenamiento local", e);
        }
    }

    @Override
    public String uploadFile(String fileName, byte[] fileData) {
        try {
            // Resuelve la ruta absoluta (ej. C:/tu-proyecto/uploads/uuid-foto.jpg)
            Path destinationFile = this.rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
            
            // Verificar que el destino sigue dentro del directorio permitido (previene path traversal)
            if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
                    throw new BusinessException("Nombre de archivo inválido detectado", "INVALID_FILE_PATH");
            }
            
            // Escribe los bytes físicos en el disco duro
            Files.write(destinationFile, fileData);
            
            // Construye y devuelve la URL pública para acceder vía navegador
            return baseUrl + "/uploads/" + fileName;
            
        } catch (IOException e) {
            throw new BusinessException("Fallo de escritura en el disco duro local", "STORAGE_WRITE_ERROR");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // 1. Extraer solo el nombre del archivo de la URL
            // Ejemplo: Pasa de "http://localhost:8080/uploads/mifoto.jpg" a "mifoto.jpg"
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // 2. Construir la ruta física en tu disco duro
            Path fileToDelete = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();

            // 3. Eliminar físicamente (deleteIfExists evita errores si el archivo ya no estaba)
            boolean deleted = Files.deleteIfExists(fileToDelete);
            
            if (!deleted) {
                // Opcional: Podrías hacer un log.warn() aquí si quieres saber cuándo falla un borrado
            }

        } catch (Exception e) {
            throw new BusinessException("Error al intentar eliminar el archivo del almacenamiento local", "STORAGE_DELETE_ERROR");
        }
    }
}