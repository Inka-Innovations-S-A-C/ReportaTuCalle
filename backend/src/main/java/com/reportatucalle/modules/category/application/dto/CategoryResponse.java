package com.reportatucalle.modules.category.application.dto;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String markerColor
) {}