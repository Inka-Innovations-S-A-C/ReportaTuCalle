package com.reportatucalle.modules.report.application.validation;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;

public abstract class ReportValidationHandler {
    protected ReportValidationHandler next;

    public ReportValidationHandler setNext(ReportValidationHandler next) {
        this.next = next;
        return next;
    }

    public void validate(CreateReportRequest request) {
        doValidate(request);
        if (next != null) {
            next.validate(request);
        }
    }

    protected abstract void doValidate(CreateReportRequest request);
}
