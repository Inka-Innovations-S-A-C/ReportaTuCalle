package com.reportatucalle.modules.category.application.dto;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;

public record UpdateCategoryRequest(
        String name,
        String description,
        String markerColor,
        AlgorithmType algorithmType,
        Boolean isActive
) {}
