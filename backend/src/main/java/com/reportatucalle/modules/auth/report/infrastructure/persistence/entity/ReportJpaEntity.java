package com.reportatucalle.modules.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;

import java.time.LocalDateTime;

/**
 * Entidad JPA: Persistencia de reportes ciudadanos.
 * 
 * SOLO para infraestructura: Contiene @Entity, @Table, @Column
 * Convertida DESDE ReportDomain mediante ReportMapper
 * Separada del dominio puro para no contaminarlo
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "citizen_id", nullable = false)
    private Long citizenId;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(nullable = false, length = 150)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;
    
    @Builder.Default
    @Column(name = "report_count", nullable = false)
    private Integer reportCount = 1;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
