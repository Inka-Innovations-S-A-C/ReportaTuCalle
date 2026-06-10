package com.reportatucalle.modules.report.infrastructure.persistence.adapter;

import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportJpaEntity;
import com.reportatucalle.modules.report.infrastructure.persistence.mapper.ReportPersistenceMapper;
import com.reportatucalle.modules.report.infrastructure.persistence.repository.ReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Adaptador de persistencia: Implementa el puerto ReportRepository.
 * 
 * Responsabilidades:
 * - Traducir llamadas del puerto a operaciones JPA (via mapper)
 * - Delegar a ReportJpaRepository para DB
 * - Retornar entidades de dominio (ReportDomain), no JPA
 */

@Component
@RequiredArgsConstructor
public class ReportPersistenceAdapter implements ReportRepository {

    private final ReportJpaRepository jpaRepository;
    private final ReportPersistenceMapper mapper;

    /**
     * Guarda o actualiza un reporte.
     *
     * FIX: Antes siempre llamaba a toJpaEntityForCreation() (sin id),
     * lo que hacía que Hibernate intentara un INSERT incluso cuando era
     * una actualización (reporte duplicado con reportCount incrementado),
     * causando un 500 por violación de constraint o id nulo en UPDATE.
     *
     * Ahora distingue: si el dominio trae id != null → usa toJpaEntity()
     * (con id) para que Hibernate haga un UPDATE correcto.
     */
    @Override
    public Report save(Report report) {
        ReportJpaEntity jpaEntity = (report.getId() != null)
                ? mapper.toJpaEntity(report)           // UPDATE: preserva el id
                : mapper.toJpaEntityForCreation(report); // INSERT: sin id, lo genera la BD
        ReportJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Report> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByCitizenId(Long citizenId) {
        return jpaRepository.findByCitizenId(citizenId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findReportsWithinRadius(Point center, double radiusInMeters) {
        return jpaRepository.findReportsWithinRadius(center, radiusInMeters)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Report> findExistingDuplicate(Long categoryId, Point location, double radiusInMeters) {
        return jpaRepository.findExistingDuplicate(categoryId, location, radiusInMeters)
                .map(mapper::toDomain);
    }
}
