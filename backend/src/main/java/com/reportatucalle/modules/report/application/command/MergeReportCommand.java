package com.reportatucalle.modules.report.application.command;

import com.reportatucalle.modules.report.application.service.ReportService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MergeReportCommand implements AdminActionCommand {
    
    private final ReportService reportService;
    private final Long sourceReportId;
    private final Long targetReportId;

    @Override
    public void execute() {
        // En una implementación real, se llamaría a un método en ReportService para fusionar los reportes
        System.out.println("Merging report " + sourceReportId + " into " + targetReportId);
    }
}
