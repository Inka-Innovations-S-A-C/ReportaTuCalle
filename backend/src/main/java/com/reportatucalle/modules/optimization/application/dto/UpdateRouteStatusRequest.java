package com.reportatucalle.modules.optimization.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRouteStatusRequest(
        @NotBlank(message = "El estado no puede estar vacío")
        String status
) {}
