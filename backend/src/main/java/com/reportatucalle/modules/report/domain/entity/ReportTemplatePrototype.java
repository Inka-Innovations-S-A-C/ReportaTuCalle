package com.reportatucalle.modules.report.domain.entity;

/**
 * Prototype Pattern for duplicating common report data
 */
public class ReportTemplatePrototype implements Cloneable {
    private String category;
    private String defaultDescription;
    private Integer urgencyLevel;

    public ReportTemplatePrototype(String category, String defaultDescription, Integer urgencyLevel) {
        this.category = category;
        this.defaultDescription = defaultDescription;
        this.urgencyLevel = urgencyLevel;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDefaultDescription() { return defaultDescription; }
    public void setDefaultDescription(String defaultDescription) { this.defaultDescription = defaultDescription; }

    public Integer getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(Integer urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    @Override
    public ReportTemplatePrototype clone() {
        try {
            return (ReportTemplatePrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should not happen
        }
    }
}
