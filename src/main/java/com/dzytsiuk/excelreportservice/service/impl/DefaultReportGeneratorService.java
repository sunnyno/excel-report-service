package com.dzytsiuk.excelreportservice.service.impl;

import com.dzytsiuk.excelreportservice.entity.*;
import com.dzytsiuk.excelreportservice.exception.UnsupportedReportTypeException;
import com.dzytsiuk.excelreportservice.service.MovieService;
import com.dzytsiuk.excelreportservice.service.ReportGeneratorService;
import com.dzytsiuk.excelreportservice.service.UserService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DefaultReportGeneratorService implements ReportGeneratorService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final int ID_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int GENRES_INDEX = 3;
    private static final int PRICE_INDEX = 4;
    private static final int ADD_DATE_INDEX = 5;
    private static final int MODIFIED_DATE_INDEX = 6;
    private static final int RATING_INDEX = 7;
    private static final int REVIEW_COUNT_INDEX = 8;
    private static final int EMAIL_INDEX = 1;
    private static final int USER_REVIEWS_COUNT_INDEX = 2;
    private static final int AVG_RATING_INDEX = 3;
    private static final short SMALL_FONT_SIZE = 12;
    private static final short LARGE_FONT_SIZE = 16;

    private final MovieService movieService;
    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter;

    @Value("${fetch.size.movie}")
    private Integer moviesFetchSize;

    public DefaultReportGeneratorService(MovieService movieService, UserService userService, DateTimeFormatter dateTimeFormatter) {
        this.movieService = movieService;
        this.userService = userService;
        this.dateTimeFormatter = dateTimeFormatter;
    }


    @Override
    public InputStream generateReport(ReportRequest reportRequest) {
        log.info("Start generating report {}", reportRequest);
        ReportType reportType = reportRequest.getReportType();
        InputStream resultInputStream;
        if (reportType == ReportType.ADDED_DURING_PERIOD || reportType == ReportType.ALL_MOVIES) {
            ReportParameter reportParameter = reportRequest.getReportParameter();
            resultInputStream = generateMovieReport(reportParameter == null ? new ReportParameter() : reportParameter, reportRequest.getId());
        } else if (reportType == ReportType.TOP_ACTIVE_USERS) {
            resultInputStream = generateUserReport(reportRequest.getId());
        } else {
            throw new UnsupportedReportTypeException("Report type " + reportType + " unsupported");
        }
        log.info("Finish generating report {}", reportRequest);
        return resultInputStream;
    }


    private InputStream generateMovieReport(ReportParameter reportParameter, String reportId) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        createMovieTableHeader(wb, sheet);
        CellStyle rowStyle = getCellStyle(wb);
        List<ReportMovie> movies;
        int page = 1;
        for (; (movies = movieService.getMovies(page, moviesFetchSize, reportParameter)).size() == moviesFetchSize; page++) {
            fillMovieRows(movies, page, rowStyle, sheet);
        }
        //process last portion of data
        fillMovieRows(movies, page, rowStyle, sheet);
        log.info("Result fetched. Start forming input stream");
        return getResultInputStream(wb, reportId);
    }

    private void fillMovieRows(List<ReportMovie> movies, int page, CellStyle rowStyle, XSSFSheet sheet) {
        for (int i = 0; i < movies.size(); i++) {
            ReportMovie movie = movies.get(i);
            int rowNumber = ((page - 1) * moviesFetchSize) + i + 1;
            fillMovieRow(movie, rowNumber, rowStyle, sheet);
        }
    }

    private InputStream generateUserReport(String reportId) {
        List<User> users = userService.getTopUsers();
        String reportTypeName = ReportType.TOP_ACTIVE_USERS.getName();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(reportTypeName);
        createUserTableHeader(wb, sheet);
        CellStyle rowStyle = getCellStyle(wb);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            row.setRowStyle(rowStyle);
            fillUserRow(user, row);
        }
        return getResultInputStream(wb, reportId);
    }

    private CellStyle getCellStyle(XSSFWorkbook wb) {
        CellStyle rowStyle = wb.createCellStyle();
        ((XSSFCellStyle) rowStyle).getFont().setFontHeightInPoints(SMALL_FONT_SIZE);
        return rowStyle;
    }

    private void fillUserRow(User user, XSSFRow row) {
        row.createCell(ID_INDEX).setCellValue(user.getId());
        row.createCell(EMAIL_INDEX).setCellValue(user.getEmail());
        row.createCell(USER_REVIEWS_COUNT_INDEX).setCellValue(user.getReviewCount());
        row.createCell(AVG_RATING_INDEX).setCellValue(user.getAverageRating());
    }

    private void createUserTableHeader(XSSFWorkbook wb, XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        styleHeader(wb, header);
        header.createCell(ID_INDEX).setCellValue("User id");
        header.createCell(EMAIL_INDEX).setCellValue("Email");
        header.createCell(USER_REVIEWS_COUNT_INDEX).setCellValue("Reviews Count");
        header.createCell(AVG_RATING_INDEX).setCellValue("Average Rating");
    }

    private void fillMovieRow(ReportMovie movie, Integer rowNumber, CellStyle rowStyle, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(rowNumber);
        row.setRowStyle(rowStyle);
        row.createCell(ID_INDEX).setCellValue(movie.getId());
        row.createCell(TITLE_INDEX).setCellValue(movie.getNameNative());
        row.createCell(DESCRIPTION_INDEX).setCellValue(movie.getDescription());
        row.createCell(GENRES_INDEX).setCellValue(movie.getGenres());
        row.createCell(PRICE_INDEX).setCellValue(nvl(movie.getPrice()));
        row.createCell(ADD_DATE_INDEX).setCellValue(movie.getAddDate().format(dateTimeFormatter));
        row.createCell(MODIFIED_DATE_INDEX).setCellValue(movie.getLastModifiedDate().format(dateTimeFormatter));
        row.createCell(RATING_INDEX).setCellValue(nvl(movie.getRating()));
        row.createCell(REVIEW_COUNT_INDEX).setCellValue(movie.getReviewCount());
    }

    private Double nvl(Double value) {
        return (value == null || value.isNaN()) ? 0.0 : value;
    }


    private void createMovieTableHeader(XSSFWorkbook wb, XSSFSheet sheet) {
        XSSFRow header = sheet.createRow(0);
        styleHeader(wb, header);
        header.createCell(ID_INDEX).setCellValue("Id");
        header.createCell(TITLE_INDEX).setCellValue("Title");
        header.createCell(DESCRIPTION_INDEX).setCellValue("Description");
        header.createCell(GENRES_INDEX).setCellValue("Genre");
        header.createCell(PRICE_INDEX).setCellValue("Price");
        header.createCell(ADD_DATE_INDEX).setCellValue("Add Date");
        header.createCell(MODIFIED_DATE_INDEX).setCellValue("Last Modified Date");
        header.createCell(RATING_INDEX).setCellValue("Rating");
        header.createCell(REVIEW_COUNT_INDEX).setCellValue("Reviews Count");
    }

    private void styleHeader(XSSFWorkbook wb, XSSFRow header) {
        CellStyle cellStyle = wb.createCellStyle();
        ((XSSFCellStyle) cellStyle).getFont().setFontHeightInPoints(LARGE_FONT_SIZE);
        header.setRowStyle(cellStyle);
    }

    private InputStream getResultInputStream(XSSFWorkbook wb, String reportId) {
        // TODO: :(
        String tmpFileName = "tmp/" + reportId + ".xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(tmpFileName);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            wb.write(bufferedOutputStream);
            return new FileInputStream(tmpFileName);
        } catch (IOException e) {
            throw new RuntimeException("Error saving workbook");
        }
    }
}
