package com.reportatucalle.modules.report.infrastructure.controller;

import com.reportatucalle.modules.report.infrastructure.sse.SseNotificationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/admin/stream")
@RequiredArgsConstructor
public class ReportSseController {

    private final SseNotificationAdapter sseNotificationAdapter;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public SseEmitter streamReports() {
        return sseNotificationAdapter.createAdminEmitter();
    }
}
