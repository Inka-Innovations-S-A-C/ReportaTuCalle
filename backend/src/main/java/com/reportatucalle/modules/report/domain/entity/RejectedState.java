package com.reportatucalle.modules.report.domain.entity;

public class RejectedState implements ReportStatus {
    @Override
    public String getName() {
        return "REJECTED";
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
