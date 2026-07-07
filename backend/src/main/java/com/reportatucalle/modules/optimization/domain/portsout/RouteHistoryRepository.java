package com.reportatucalle.modules.optimization.domain.portsout;

import com.reportatucalle.modules.optimization.domain.models.RouteHistory;
import java.util.List;
import java.util.Optional;

public interface RouteHistoryRepository {
    RouteHistory save(RouteHistory routeHistory);
    Optional<RouteHistory> findById(Long id);
    List<RouteHistory> findBySupervisorId(Long supervisorId);
    List<RouteHistory> findAll();
}
