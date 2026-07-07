package com.reportatucalle.modules.report.application.command;

import com.reportatucalle.modules.report.application.service.ReportService;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeStatusCommand implements AdminActionCommand {

    private final ReportService reportService;
    private final Long reportId;
    private final String newStatus;

    @Override
    public void execute() {
        reportService.updateReportStatus(reportId, new UpdateReportStatusRequest(newStatus, null));
    }
}
