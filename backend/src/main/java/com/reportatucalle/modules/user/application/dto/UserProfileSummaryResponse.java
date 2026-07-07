package com.reportatucalle.modules.user.application.dto;

import java.time.LocalDateTime;

public record UserProfileSummaryResponse(
        Long id,
        Long accountId,
        String fullName,
        String firstName,
        String lastName,
        String phone,
        Integer civicScore,
        LocalDateTime createdAt
) {}
