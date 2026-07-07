package com.reportatucalle.modules.report.application.event;

import com.reportatucalle.shared.event.EventListener;
import com.reportatucalle.shared.event.EventPublisher;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@Component
public class ReportEventListener implements EventListener<ReportCreatedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ReportEventListener.class);
    private final ReportEventPublisher eventPublisher;

    public ReportEventListener(ReportEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        eventPublisher.subscribe(this);
    }

    @Override
    public void onEvent(ReportCreatedEvent event) {
        log.info("Report created event received for report ID: {}", event.getReport().getId());
    }
}
