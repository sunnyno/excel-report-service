package com.dzytsiuk.excelreportservice.service.impl;

import com.dzytsiuk.excelreportservice.entity.ReportRequest;
import com.dzytsiuk.excelreportservice.entity.ReportType;
import com.dzytsiuk.excelreportservice.service.FtpService;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DefaultFtpService implements FtpService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private Integer port;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;


    @Override
    @SneakyThrows
    public void saveAndEnrichWithUrl(InputStream inputStream, ReportRequest reportRequest) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            String filename = getFileName(reportRequest);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            boolean saved = ftpClient.storeFile(filename, inputStream);
            if (!saved) {
                throw new RuntimeException("Cannot save file " + filename + " to FTP");
            }
            log.info("File {} sent to Ftp", filename);
            reportRequest.setFtpUrl("ftp://" + username + "@" + host + "/" + filename);
            ftpClient.logout();
        } catch (IOException e) {
            throw new RuntimeException("Error saving report " + reportRequest.getId() + " to ftp");
        } finally {
            inputStream.close();
            Files.delete(Paths.get("tmp/" + reportRequest.getId() + ".xlsx"));
            ftpClient.disconnect();
        }
    }

    private String getFileName(ReportRequest reportRequest) {
        String filename = "/report";
        ReportType reportType = reportRequest.getReportType();
        if (reportType == ReportType.ADDED_DURING_PERIOD || reportType == ReportType.ALL_MOVIES) {
            filename += "/movie/";
        } else if (reportType == ReportType.TOP_ACTIVE_USERS) {
            filename += "/user/";
        }
        return filename + reportType.getName() + "_" + reportRequest.getId() + ".xlsx";
    }
}
