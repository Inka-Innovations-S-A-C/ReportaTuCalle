package com.reportatucalle.modules.report.application.api;

import com.reportatucalle.modules.notification.application.service.NotificationService;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.service.IReportService;
import com.reportatucalle.modules.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Facade Pattern: Aggregates multiple services into a single interface.
 */
@Service
@RequiredArgsConstructor
public class ReportManagementFacade {
    
    private final UserService userService;
    private final IReportService reportService;
    private final NotificationService notificationService;

    public void processUserReport(Long reportId) {
        // High-level operation orchestrating multiple sub-systems
        ReportResponse report = reportService.getReportById(reportId);
        
        // Use user service to fetch something if needed, just demonstrating aggregation
        // ...
        
        // Notify
        notificationService.notifyUser("Your report " + report.id() + " is being processed.", "user@example.com");
    }
}
