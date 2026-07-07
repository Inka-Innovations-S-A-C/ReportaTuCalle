package com.reportatucalle.modules.optimization.infrastructure.persistence.entity;

import com.reportatucalle.modules.optimization.domain.models.RouteHistoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "route_history", schema = "optimization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteHistoryJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "supervisor_id", nullable = false)
    private Long supervisorId;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @Column(name = "route_data_json", columnDefinition = "TEXT", nullable = false)
    private String routeDataJson;
    
    @Column(name = "total_distance_km", nullable = false)
    private Double totalDistanceKm;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RouteHistoryStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RouteHistoryStatus.SAVED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
