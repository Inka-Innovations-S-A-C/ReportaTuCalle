package com.reportatucalle.shared.websocket;

public record SupervisorLocationMessage(
    Long supervisorId,
    double latitude,
    double longitude
) {}
