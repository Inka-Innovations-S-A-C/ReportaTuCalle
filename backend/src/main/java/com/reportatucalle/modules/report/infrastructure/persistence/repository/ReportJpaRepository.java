package com.reportatucalle.modules.report.infrastructure.persistence.repository;

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
    List<ReportJpaEntity> findByStatus(String status);
    
    /**
     * Busca reportes por ciudadano.
     */
    List<ReportJpaEntity> findByCitizenId(Long citizenId);
    
    /**
     * Busca reportes por supervisor asignado.
     */
    List<ReportJpaEntity> findByAssignedToUserId(Long assignedToUserId);
    
    /**
     * Busca reportes asignados a un supervisor filtrando por múltiples estados.
     */
    List<ReportJpaEntity> findByAssignedToUserIdAndStatusIn(Long assignedToUserId, List<String> statuses);
    
    /**
     * Busca los últimos 30 reportes resueltos asignados a un supervisor.
     */
    List<ReportJpaEntity> findTop30ByAssignedToUserIdAndStatusOrderByCreatedAtDesc(Long assignedToUserId, String status);
    
    /**
     * Busca reportes dentro de un radio geográfico usando PostGIS.
     */
    @Query(value = "SELECT * FROM report.reports r WHERE ST_DWithin(CAST(r.location AS geography), CAST(:center AS geography), :radiusInMeters)", 
           nativeQuery = true)
    List<ReportJpaEntity> findReportsWithinRadius(
            @Param("center") Point center,
            @Param("radiusInMeters") double radiusInMeters
    );
    
    /**
     * Busca un reporte activo duplicado dentro de un radio.
     * Consolidación inteligente: no duplicar reportes del mismo incidente.
     */
    @Query(value = "SELECT * FROM report.reports r WHERE r.category_id = :categoryId " +
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
