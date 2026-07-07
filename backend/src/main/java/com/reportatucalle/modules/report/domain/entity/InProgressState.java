package com.reportatucalle.modules.report.domain.entity;

public class InProgressState implements ReportStatus {
    @Override
    public String getName() {
        return "IN_PROGRESS";
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
