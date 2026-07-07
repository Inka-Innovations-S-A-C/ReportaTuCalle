package com.reportatucalle.modules.report.domain.entity;

public class PendingState implements ReportStatus {
    @Override
    public String getName() {
        return "PENDING";
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
