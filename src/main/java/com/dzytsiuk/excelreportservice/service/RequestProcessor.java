package com.dzytsiuk.excelreportservice.service;

import com.dzytsiuk.excelreportservice.entity.ReportRequest;

import java.util.List;

public interface RequestProcessor {
    void processReportRequests(List<ReportRequest> reportRequests);
}
