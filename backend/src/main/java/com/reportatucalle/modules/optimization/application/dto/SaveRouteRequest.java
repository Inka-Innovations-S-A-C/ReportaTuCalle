package com.reportatucalle.modules.optimization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveRouteRequest(
        @NotNull(message = "El ID del supervisor es obligatorio")
        Long supervisorId,
        @NotNull(message = "El ID de la categoría es obligatorio")
        Long categoryId,
        @NotBlank(message = "Los datos de la ruta no pueden estar vacíos")
        String routeDataJson,
        @NotNull(message = "La distancia total es obligatoria")
        Double totalDistanceKm
) {}
