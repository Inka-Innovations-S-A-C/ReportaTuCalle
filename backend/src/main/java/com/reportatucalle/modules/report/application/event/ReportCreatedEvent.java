package com.reportatucalle.modules.report.application.event;

import com.reportatucalle.modules.report.domain.entity.Report;

public class ReportCreatedEvent {
    private final Report report;
    public ReportCreatedEvent(Report report) { this.report = report; }
    public Report getReport() { return report; }
}
