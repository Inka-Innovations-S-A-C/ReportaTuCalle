package com.reportatucalle.modules.optimization.infrastructure.persistence.mapper;

import com.reportatucalle.modules.optimization.domain.models.RouteHistory;
import com.reportatucalle.modules.optimization.infrastructure.persistence.entity.RouteHistoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RouteHistoryPersistenceMapper {

    public RouteHistory toDomain(RouteHistoryJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return RouteHistory.builder()
                .id(jpaEntity.getId())
                .supervisorId(jpaEntity.getSupervisorId())
                .categoryId(jpaEntity.getCategoryId())
                .routeDataJson(jpaEntity.getRouteDataJson())
                .totalDistanceKm(jpaEntity.getTotalDistanceKm())
                .status(jpaEntity.getStatus())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }

    public RouteHistoryJpaEntity toJpaEntity(RouteHistory domain) {
        if (domain == null) return null;
        return RouteHistoryJpaEntity.builder()
                .id(domain.getId())
                .supervisorId(domain.getSupervisorId())
                .categoryId(domain.getCategoryId())
                .routeDataJson(domain.getRouteDataJson())
                .totalDistanceKm(domain.getTotalDistanceKm())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
