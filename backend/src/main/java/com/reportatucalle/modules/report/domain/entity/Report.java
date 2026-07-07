package com.reportatucalle.modules.report.domain.entity;

import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * Entidad de Dominio pura: Reporte ciudadano de un incidente.
 * 
 * PURO: Sin @Entity, @Table, @Column, sin JPA, sin Spring, sin Jackson
 * Builder MANUAL (Gang of Four): Implementación de referencia para comparación arquitectónica.
 *    Este patrón demuestra el control explícito de construcción sin anotaciones.
 *    Contrasta con Lombok @Builder usado en otras entidades para mostrar ambos enfoques.
 * Separado: ReportJpaEntity (infrastructure) maneja persistencia
 */
@Getter
public class Report {
    
    private final Long id;
    private final Long citizenId; // Referencia blanda a UserProfile
    private final Long categoryId; // Referencia blanda a Category
    private final Long assignedToUserId; // Referencia blanda a UserProfile del supervisor asignado
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String resolutionImageUrl; // Foto del problema resuelto
    private final Point location;
    private final ReportStatus status;
    private final Integer reportCount; // Severidad: cuántas personas reportaron lo mismo
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    /**
     * Constructor privado: Solo accesible a través del Builder.
     * Patrón Gang of Four Builder para control explícito.
     */
    private Report(Builder builder) {
        this.id = builder.id;
        this.citizenId = builder.citizenId;
        this.categoryId = builder.categoryId;
        this.assignedToUserId = builder.assignedToUserId;
        this.title = builder.title;
        this.description = builder.description;
        this.imageUrl = builder.imageUrl;
        this.resolutionImageUrl = builder.resolutionImageUrl;
        this.location = builder.location;
        this.status = builder.status;
        this.reportCount = builder.reportCount;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }
    
    /**
     * Validaciones de dominio: ¿Este reporte es válido?
     */
    public boolean isValid() {
        return citizenId != null
                && categoryId != null
                && title != null && !title.isBlank()
                && description != null && !description.isBlank()
                && location != null
                && status != null;
    }
    
    /**
     * Lógica de dominio: ¿Este reporte es activo?
     */
    public boolean isActive() {
        return status.isActive();
    }
    
    /**
     * Lógica de dominio: ¿Se puede actualizar este reporte?
     */
    public boolean canUpdate() {
        return status.canUpdate();
    }
    
    /**
     * Calcula la severidad relativa (impacto).
     * En negocio: más reportes = más grave.
     */
    public String getSeverity() {
        if (reportCount >= 5) return "CRITICA";
        if (reportCount >= 3) return "ALTA";
        if (reportCount >= 2) return "MEDIA";
        return "BAJA";
    }
    
    /**
     * Builder Pattern - Gang of Four: Implementación manual y explícita.
     * 
     * Ventajas sobre Lombok @Builder:
     * - Control total del flujo de construcción
     * - Validaciones durante construcción
     * - Claridad arquitectónica (demuestra intención de patrón)
     * - Independencia de anotaciones de generación de código
     * 
     * Desventajas:
     * - Más verboso
     * - Mantener manualmente
     * - Más código boilerplate
     */
    public static class Builder {
        
        // Campos requeridos
        private Long citizenId;
        private Long categoryId;
        private String title;
        private String description;
        private Point location;
        
        // Campos opcionales con valores por defecto
        private Long id;
        private Long assignedToUserId;
        private String imageUrl;
        private String resolutionImageUrl;
        private ReportStatus status = new PendingState();
        private Integer reportCount = 1;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        
        // Setter fluido para citizenId
        public Builder citizenId(Long citizenId) {
            this.citizenId = citizenId;
            return this;
        }
        
        // Setter fluido para categoryId
        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }
        
        // Setter fluido para title
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        // Setter fluido para description
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        // Setter fluido para location
        public Builder location(Point location) {
            this.location = location;
            return this;
        }
        
        // Setter fluido para id (opcional)
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        // Setter fluido para assignedToUserId (opcional)
        public Builder assignedToUserId(Long assignedToUserId) {
            this.assignedToUserId = assignedToUserId;
            return this;
        }
        
        // Setter fluido para imageUrl (opcional)
        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        
        // Setter fluido para resolutionImageUrl (opcional)
        public Builder resolutionImageUrl(String resolutionImageUrl) {
            this.resolutionImageUrl = resolutionImageUrl;
            return this;
        }
        
        // Setter fluido para status (opcional)
        public Builder status(ReportStatus status) {
            this.status = status;
            return this;
        }
        
        // Setter fluido para reportCount (opcional)
        public Builder reportCount(Integer reportCount) {
            this.reportCount = reportCount != null ? reportCount : 1;
            return this;
        }
        
        // Setter fluido para createdAt (opcional)
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        // Setter fluido para updatedAt (opcional)
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        /**
         * Validación durante construcción (Strategy: fail-fast).
         * Si los requeridos faltan, lanza excepción antes de crear la instancia.
         */
        private void validate() {
            if (citizenId == null) {
                throw new IllegalArgumentException("citizenId es requerido");
            }
            if (categoryId == null) {
                throw new IllegalArgumentException("categoryId es requerido");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("title es requerido");
            }
            if (description == null || description.isBlank()) {
                throw new IllegalArgumentException("description es requerido");
            }
            if (location == null) {
                throw new IllegalArgumentException("location es requerido");
            }
        }
        
        /**
         * Construye la instancia de ReportDomain.
         * Ejecuta validaciones antes de crear el objeto.
         */
        public Report build() {
            validate();
            return new Report(this);
        }
    }
    
    /**
     * Crea un nuevo Builder para construir ReportDomain.
     */
    public static Builder builder() {
        return new Builder();
    }
}
