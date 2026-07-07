package com.reportatucalle.modules.report.domain.entity;

public class AssignedState implements ReportStatus {
    @Override
    public String getName() {
        return "ASSIGNED";
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
