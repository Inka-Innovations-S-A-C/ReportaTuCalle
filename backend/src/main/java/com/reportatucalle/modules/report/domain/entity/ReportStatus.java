package com.reportatucalle.modules.report.domain.entity;

public interface ReportStatus {
    String getName();
    boolean canUpdate();
    boolean isActive();
}