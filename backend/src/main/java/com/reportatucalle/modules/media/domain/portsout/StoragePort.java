package com.reportatucalle.modules.media.domain.portsout;

/**
 * Puerto de Salida (Outbound Port) — Arquitectura Hexagonal.
 * 
 * Define el contrato hacia el almacenamiento externo.
 * El dominio NO sabe si por debajo hay disco local, S3, Cloudinary, etc.
 * 
 * Patrón Adapter: Cada adaptador implementa este puerto.
 * Patrón Strategy: Spring elige el adaptador según storage.provider en application.yml.
 */
public interface StoragePort {

    /**
     * Almacena un archivo y retorna su URL pública de acceso.
     *
     * @param fileName nombre único del archivo (UUID generado por MediaService)
     * @param fileData bytes crudos del archivo
     * @return URL pública absoluta para acceder al archivo
     */
    String uploadFile(String fileName, byte[] fileData);

    /**
     * Elimina un archivo del almacenamiento.
     *
     * @param fileUrl URL completa del archivo a eliminar
     */
    void deleteFile(String fileUrl);
}