package com.reportatucalle.modules.report.infrastructure.persistence.mapper;

import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportJpaEntity;
import com.reportatucalle.modules.report.domain.entity.ReportStatusFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper: Convierte entre ReportDomain (puro, builder manual) y ReportJpaEntity (JPA).
 * 
 * Barrera arquitectónica que evita contaminación del dominio.
 * Nota: ReportDomain usa Builder manual (Gang of Four), aquí se demuestra su uso.
 */
@Component
public class ReportPersistenceMapper {
    
    /**
     * JPA Entity → Domain POJO puro (usando builder manual).
     */
    public Report toDomain(ReportJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        
        return Report.builder()
                .id(jpaEntity.getId())
                .citizenId(jpaEntity.getCitizenId())
                .categoryId(jpaEntity.getCategoryId())
                .assignedToUserId(jpaEntity.getAssignedToUserId())
                .title(jpaEntity.getTitle())
                .description(jpaEntity.getDescription())
                .imageUrl(jpaEntity.getImageUrl())
                .location(jpaEntity.getLocation())
                .status(ReportStatusFactory.fromString(jpaEntity.getStatus()))
                .reportCount(jpaEntity.getReportCount())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity.
     */
    public ReportJpaEntity toJpaEntity(Report domain) {
        if (domain == null) return null;
        
        return ReportJpaEntity.builder()
                .id(domain.getId())
                .citizenId(domain.getCitizenId())
                .categoryId(domain.getCategoryId())
                .assignedToUserId(domain.getAssignedToUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .imageUrl(domain.getImageUrl())
                .location(domain.getLocation())
                .status(domain.getStatus().getName())
                .reportCount(domain.getReportCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity (crear nuevo, sin ID).
     */
    public ReportJpaEntity toJpaEntityForCreation(Report domain) {
        if (domain == null) return null;
        
        return ReportJpaEntity.builder()
                .citizenId(domain.getCitizenId())
                .categoryId(domain.getCategoryId())
                .assignedToUserId(domain.getAssignedToUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .imageUrl(domain.getImageUrl())
                .location(domain.getLocation())
                .status(domain.getStatus().getName())
                .reportCount(domain.getReportCount())
                .build();
    }
}
