package com.reportatucalle.modules.optimization.infrastructure.persistence.adapter;

import com.reportatucalle.modules.optimization.domain.models.RouteHistory;
import com.reportatucalle.modules.optimization.domain.portsout.RouteHistoryRepository;
import com.reportatucalle.modules.optimization.infrastructure.persistence.entity.RouteHistoryJpaEntity;
import com.reportatucalle.modules.optimization.infrastructure.persistence.mapper.RouteHistoryPersistenceMapper;
import com.reportatucalle.modules.optimization.infrastructure.persistence.repository.RouteHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteHistoryPersistenceAdapter implements RouteHistoryRepository {

    private final RouteHistoryJpaRepository jpaRepository;
    private final RouteHistoryPersistenceMapper mapper;

    @Override
    public RouteHistory save(RouteHistory routeHistory) {
        RouteHistoryJpaEntity jpaEntity = mapper.toJpaEntity(routeHistory);
        RouteHistoryJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RouteHistory> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<RouteHistory> findBySupervisorId(Long supervisorId) {
        return jpaRepository.findBySupervisorId(supervisorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteHistory> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
