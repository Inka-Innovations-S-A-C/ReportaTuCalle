package com.reportatucalle.modules.optimization.infrastructure.persistence.repository;

import com.reportatucalle.modules.optimization.infrastructure.persistence.entity.RouteHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteHistoryJpaRepository extends JpaRepository<RouteHistoryJpaEntity, Long> {
    List<RouteHistoryJpaEntity> findBySupervisorId(Long supervisorId);
}
