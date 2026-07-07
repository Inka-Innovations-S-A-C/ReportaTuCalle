package com.reportatucalle.modules.auth.application.dto;

import java.time.LocalDateTime;

public record AuthAccountResponse(
        Long id,
        String email,
        String role,
        LocalDateTime createdAt
) {}
