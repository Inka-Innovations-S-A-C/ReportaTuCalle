package com.reportatucalle.modules.report.application.validation;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.shared.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class LocationValidator extends ReportValidationHandler {

    @Override
    protected void doValidate(CreateReportRequest request) {
        if (request.latitude() < -90 || request.latitude() > 90) {
            throw new BusinessException("Latitud inválida", "INVALID_LOCATION");
        }
        if (request.longitude() < -180 || request.longitude() > 180) {
            throw new BusinessException("Longitud inválida", "INVALID_LOCATION");
        }
    }
}
