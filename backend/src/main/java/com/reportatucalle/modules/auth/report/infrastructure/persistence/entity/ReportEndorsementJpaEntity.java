package com.reportatucalle.modules.report.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa la consolidación ciudadana.
 * Vincula a un ciudadano secundario con un reporte ya existente (Patrón Observer en BD).
 */
@Entity
@Table(name = "report_endorsements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEndorsementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "citizen_id", nullable = false)
    private Long citizenId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}