package com.reportatucalle.modules.report.application.export;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExcelReportExporter extends AbstractReportExporter {

    @Override
    protected String formatData(List<Map<String, Object>> data) {
        // Mock formatting to Excel
        return "Excel Data format: " + data.toString();
    }
}
