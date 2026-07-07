package com.reportatucalle.modules.report.application.event;

import com.reportatucalle.shared.event.EventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ReportEventPublisher extends EventPublisher<ReportCreatedEvent> {
}
