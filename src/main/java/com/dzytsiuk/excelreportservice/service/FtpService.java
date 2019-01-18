package com.dzytsiuk.excelreportservice.service;

import com.dzytsiuk.excelreportservice.entity.ReportRequest;

import java.io.InputStream;

public interface FtpService {
    void saveAndEnrichWithUrl(InputStream inputStream, ReportRequest reportRequest);
}
