package com.reportatucalle.modules.report.infrastructure.persistence.repository;

import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportJpaEntity;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository: Operaciones específicas de persistencia JPA.
 * 
 * SOLO infraestructura: Extiende JpaRepository, usa @Repository, @Query nativa
 * Trabaja con ReportJpaEntity (no domain)
 * Implementado por adapter que convierte a domain
 */
@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, Long> {
    
    /**
     * Busca reportes por estado.
     */
    List<ReportJpaEntity> findByStatus(ReportStatus status);
    
    /**
     * Busca reportes por ciudadano.
     */
    List<ReportJpaEntity> findByCitizenId(Long citizenId);
    
    /**
     * Busca reportes dentro de un radio geográfico usando PostGIS.
     */
    @Query(value = "SELECT * FROM reports r WHERE ST_DWithin(CAST(r.location AS geography), CAST(:center AS geography), :radiusInMeters)", 
           nativeQuery = true)
    List<ReportJpaEntity> findReportsWithinRadius(
            @Param("center") Point center,
            @Param("radiusInMeters") double radiusInMeters
    );
    
    /**
     * Busca un reporte activo duplicado dentro de un radio.
     * Consolidación inteligente: no duplicar reportes del mismo incidente.
     */
    @Query(value = "SELECT * FROM reports r WHERE r.category_id = :categoryId " +
                   "AND r.status IN ('PENDING', 'IN_PROGRESS') " +
                   "AND ST_DWithin(CAST(r.location AS geography), CAST(:location AS geography), :radiusInMeters) " +
                   "LIMIT 1",
           nativeQuery = true)
    Optional<ReportJpaEntity> findExistingDuplicate(
            @Param("categoryId") Long categoryId,
            @Param("location") Point location,
            @Param("radiusInMeters") double radiusInMeters
    );
}
