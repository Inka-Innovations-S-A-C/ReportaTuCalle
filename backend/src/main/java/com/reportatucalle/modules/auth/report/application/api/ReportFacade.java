package com.reportatucalle.modules.report.application.api;

import java.util.List;
import java.util.Optional;

/**
 * Façade Pública del módulo Report.
 *
 * Contrato que otros módulos pueden usar para interactuar con Report.
 * NO expone repositorios o detalles internos.
 * Mantiene límite arquitectónico entre módulos.
 */
public interface ReportFacade {

    /**
     * Obtiene un reporte por ID.
     */
    Optional<Long> getReportIdIfExists(Long reportId);

    /**
     * Cuenta reportes activos de un ciudadano.
     */
    long countReportsByCitizen(Long citizenId);

    /**
     * Cuenta reportes de una categoría.
     */
    long countReportsByCategory(Long categoryId);

    /**
     * Verifica si una categoría tiene reportes activos.
     */
    boolean hasCategoryReports(Long categoryId);

    /**
     * Busca reportes dentro de un radio (para mapas).
     */
    List<Long> getReportIdsNearby(double latitude, double longitude, double radiusInMeters);
}