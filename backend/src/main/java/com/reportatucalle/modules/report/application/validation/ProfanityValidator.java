package com.reportatucalle.modules.report.application.validation;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.shared.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfanityValidator extends ReportValidationHandler {

    private final List<String> badWords = List.of("idiota", "estúpido", "basura");

    @Override
    protected void doValidate(CreateReportRequest request) {
        if (request.title() != null) {
            boolean hasProfanity = badWords.stream().anyMatch(word -> request.title().toLowerCase().contains(word));
            if (hasProfanity) {
                throw new BusinessException("El título contiene lenguaje inapropiado", "PROFANITY_DETECTED");
            }
        }
        if (request.description() != null) {
            boolean hasProfanity = badWords.stream().anyMatch(word -> request.description().toLowerCase().contains(word));
            if (hasProfanity) {
                throw new BusinessException("La descripción contiene lenguaje inapropiado", "PROFANITY_DETECTED");
            }
        }
    }
}
