package com.dzytsiuk.excelreportservice.service;

import com.dzytsiuk.excelreportservice.entity.ReportMovie;
import com.dzytsiuk.excelreportservice.entity.ReportParameter;

import java.util.List;

public interface MovieService {
    List<ReportMovie> getMovies(Integer page, Integer count, ReportParameter reportParameter);
}
