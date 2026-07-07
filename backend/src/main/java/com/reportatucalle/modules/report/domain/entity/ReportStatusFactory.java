package com.reportatucalle.modules.report.domain.entity;

public class ReportStatusFactory {
    public static ReportStatus fromString(String status) {
        if (status == null) return new PendingState();
        return switch (status.toUpperCase()) {
            case "PENDING" -> new PendingState();
            case "ASSIGNED" -> new AssignedState();
            case "IN_PROGRESS" -> new InProgressState();
            case "RESOLVED" -> new ResolvedState();
            case "REJECTED" -> new RejectedState();
            default -> new PendingState();
        };
    }
}
