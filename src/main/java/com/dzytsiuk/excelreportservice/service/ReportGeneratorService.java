package com.dzytsiuk.excelreportservice.service;

import com.dzytsiuk.excelreportservice.entity.ReportRequest;

import java.io.InputStream;

public interface ReportGeneratorService {

    InputStream generateReport(ReportRequest reportRequest);
}