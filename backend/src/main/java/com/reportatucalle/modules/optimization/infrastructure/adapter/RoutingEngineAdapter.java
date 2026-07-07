package com.reportatucalle.modules.optimization.infrastructure.adapter;

import org.springframework.stereotype.Component;

/**
 * Pure Adapter Pattern wrapping an external Routing Engine API (e.g. OSRM)
 */
@Component
public class RoutingEngineAdapter {

    private final OsrmApiAdaptee osrmApi;

    public RoutingEngineAdapter() {
        this.osrmApi = new OsrmApiAdaptee();
    }

    public String getRoute(double startLat, double startLng, double endLat, double endLng) {
        // Translating the standard interface call to the specific OSRM adaptee call
        return osrmApi.fetchRouteFromOsrm(startLat, startLng, endLat, endLng);
    }
    
    /**
     * Inner adaptee simulating a 3rd-party dependency that we can't or shouldn't change
     */
    private static class OsrmApiAdaptee {
        public String fetchRouteFromOsrm(double lat1, double lng1, double lat2, double lng2) {
            return String.format("OSRM Route from (%f, %f) to (%f, %f)", lat1, lng1, lat2, lng2);
        }
    }
}
