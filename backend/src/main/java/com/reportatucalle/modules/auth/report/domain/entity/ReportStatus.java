package com.reportatucalle.modules.report.domain.entity;

/**
 * Ciclo de vida de una incidencia urbana en el sistema.
 */
public enum ReportStatus {
    PENDING,        // Reportado por el ciudadano, esperando revisión
    IN_PROGRESS,    // La municipalidad/supervisor está trabajando en ello
    RESOLVED,       // Incidencia solucionada
    REJECTED        // Reporte falso, duplicado o fuera de jurisdicción
}