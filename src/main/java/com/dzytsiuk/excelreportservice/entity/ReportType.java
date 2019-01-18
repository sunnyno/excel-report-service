package com.dzytsiuk.excelreportservice.entity;

public enum ReportType {
    ALL_MOVIES("allMovies"), ADDED_DURING_PERIOD("addedDuringPeriod"), TOP_ACTIVE_USERS("topActiveUsers");

    private String name;

    ReportType(String name) {
        this.name = name;
    }

    public static ReportType getReportTypeByName(String name) {
        for (ReportType reportType : ReportType.values()) {
            if (name.equalsIgnoreCase(reportType.name)) {
                return reportType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
