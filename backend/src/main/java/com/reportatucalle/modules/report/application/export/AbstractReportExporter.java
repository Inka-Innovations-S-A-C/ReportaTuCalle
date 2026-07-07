package com.reportatucalle.modules.report.application.export;

import java.util.List;
import java.util.Map;

/**
 * Template Method Pattern for exporting reports.
 */
public abstract class AbstractReportExporter {

    public final void export() {
        List<Map<String, Object>> data = fetchData();
        String formatted = formatData(data);
        saveFile(formatted);
    }

    protected List<Map<String, Object>> fetchData() {
        // Mock fetching data
        return List.of(Map.of("id", 1, "title", "Pothole"));
    }

    protected abstract String formatData(List<Map<String, Object>> data);

    protected void saveFile(String content) {
        System.out.println("Saving file content: " + content);
    }
}
