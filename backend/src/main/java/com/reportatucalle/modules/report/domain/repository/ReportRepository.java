package com.reportatucalle.modules.report.domain.repository;

import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (Outbound Port): Contrato de persistencia para reportes.
 * 
 * PURO: Sin @Repository, sin extends JpaRepository, sin Spring
 * Definido en domain: Independiente de infraestructura
 * Implementado por: ReportPersistenceAdapter en infrastructure
 * Trabajar con ReportDomain (puro), no con JPA entities
 */
public interface ReportRepository {
    
    /**
     * Guarda un nuevo reporte.
     */
    Report save(Report report);
    
    /**
     * Busca un reporte por ID.
     */
    Optional<Report> findById(Long id);
    
    /**
     * Elimina un reporte por ID.
     */
    void deleteById(Long id);
    
    /**
     * Busca todos los reportes con un estado específico.
     */
    List<Report> findByStatus(ReportStatus status);
    
    /**
     * Busca todos los reportes creados por un ciudadano.
     */
    List<Report> findByCitizenId(Long citizenId);
    
    /**
     * Busca todos los reportes asignados a un supervisor.
     */
    List<Report> findByAssignedToUserId(Long assignedToUserId);
    
    /**
     * Busca reportes asignados a un supervisor filtrando por múltiples estados.
     */
    List<Report> findByAssignedToUserIdAndStatusIn(Long assignedToUserId, List<ReportStatus> statuses);
    
    /**
     * Busca los últimos 30 reportes resueltos asignados a un supervisor.
     */
    List<Report> findTop30ByAssignedToUserIdAndStatusOrderByCreatedAtDesc(Long assignedToUserId, ReportStatus status);
    
    /**
     * Busca todos los reportes.
     */
    List<Report> findAll();
    
    /**
     * Busca reportes dentro de un radio geográfico usando PostGIS.
     */
    List<Report> findReportsWithinRadius(Point center, double radiusInMeters);
    
    /**
     * Busca un reporte existente duplicado dentro de un radio.
     * Usado para consolidación inteligente (no duplicar reportes).
     */
    Optional<Report> findExistingDuplicate(Long categoryId, Point location, double radiusInMeters);
    
    /**
     * Busca multiples reportes por sus IDs.
     */
    List<Report> findAllById(List<Long> ids);
}