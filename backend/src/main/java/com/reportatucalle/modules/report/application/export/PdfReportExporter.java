package com.reportatucalle.modules.report.application.export;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PdfReportExporter extends AbstractReportExporter {

    @Override
    protected String formatData(List<Map<String, Object>> data) {
        // Mock formatting to PDF
        return "PDF Data format: " + data.toString();
    }
}
