package com.reportatucalle.modules.report.domain.entity;

public class ResolvedState implements ReportStatus {
    @Override
    public String getName() {
        return "RESOLVED";
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
