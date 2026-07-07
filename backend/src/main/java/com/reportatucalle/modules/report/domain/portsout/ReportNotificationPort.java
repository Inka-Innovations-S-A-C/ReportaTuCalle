package com.reportatucalle.modules.report.domain.portsout;

import com.reportatucalle.modules.report.domain.entity.Report;

public interface ReportNotificationPort {
    void notifyReportCreated(Report report);
    void notifyReportStatusUpdated(Report report);
    void notifyReportAssigned(Report report);
}
