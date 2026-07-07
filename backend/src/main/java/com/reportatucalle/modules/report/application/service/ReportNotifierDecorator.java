package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;

public class ReportNotifierDecorator extends ReportServiceDecorator {

    public ReportNotifierDecorator(IReportService wrapped) {
        super(wrapped);
    }

    @Override
    public ReportResponse createReport(CreateReportRequest request) {
        ReportResponse response = super.createReport(request);
        // Lógica del decorador para enviar notificaciones personalizadas post-creación
        System.out.println("Decorator Notification: A new report has been created with ID " + response.id());
        return response;
    }
}
