package com.reportatucalle.modules.report.application.validation;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidationChain {

    private final LocationValidator locationValidator;
    private final ProfanityValidator profanityValidator;

    @PostConstruct
    public void init() {
        locationValidator.setNext(profanityValidator);
    }

    public void validate(CreateReportRequest request) {
        locationValidator.validate(request);
    }
}
