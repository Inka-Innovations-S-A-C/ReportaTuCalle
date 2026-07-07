import re

with open('src/test/java/com/reportatucalle/modules/report/infrastructure/controller/ReportControllerTest.java', 'r') as f:
    content = f.read()

# Fix ReportResponse constructor calls
content = re.sub(
    r'new ReportResponse\(\s*1L,\s*".*?",\s*".*?",\s*-12\.0,\s*-77\.0,\s*.*?\s*\)',
    r'new ReportResponse(1L, 1L, 1L, 1L, "Title", "Desc", "url", "url", "PENDING", 1, -12.0, -77.0, null)',
    content,
    flags=re.DOTALL
)

# Fix OptimizedRoute
content = content.replace('new OptimizedRoute(Collections.emptyList(), 0.0)', 'new OptimizedRoute(Collections.emptyList(), 0.0, java.util.Collections.emptyMap())')

with open('src/test/java/com/reportatucalle/modules/report/infrastructure/controller/ReportControllerTest.java', 'w') as f:
    f.write(content)

