package com.dzytsiuk.excelreportservice.entity;

public enum  ReportFormat {
    XLSX("xlsx");
    private String name;

    ReportFormat(String name) {
        this.name = name;
    }

    public static ReportFormat getReportFormatFromName(String name) {
        for (ReportFormat reportFormat : ReportFormat.values()) {
            if (name.equalsIgnoreCase(reportFormat.getName())) {
                return reportFormat;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
