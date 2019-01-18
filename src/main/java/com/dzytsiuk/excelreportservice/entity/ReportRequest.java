package com.dzytsiuk.excelreportservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportRequest {
    private String id;
    private ReportType reportType;
    private ReportParameter reportParameter;
    private ReportFormat reportFormat;
    private ReportStatus reportStatus;
    private String ftpUrl;
    private LocalDateTime dateRequested;
    private User user;
}
