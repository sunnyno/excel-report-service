package com.dzytsiuk.excelreportservice.service.impl;

import com.dzytsiuk.excelreportservice.entity.ReportMovie;
import com.dzytsiuk.excelreportservice.entity.ReportParameter;
import com.dzytsiuk.excelreportservice.service.MovieService;
import com.dzytsiuk.excelreportservice.service.impl.client.MovielandClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultMovieService implements MovieService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MovielandClient movielandClient;

    public DefaultMovieService(MovielandClient movielandClient) {
        this.movielandClient = movielandClient;
    }

    @Override
    public List<ReportMovie>  getMovies(Integer page, Integer count, ReportParameter reportParameter) {
        log.info("Start sending request to get {} movies on page {} with parameters {}",
                count, page, reportParameter);
        List<ReportMovie> movies = movielandClient.getMovies(page, count, reportParameter.getFromDate(), reportParameter.getToDate());
        log.info("Movies on page {} with parameters {} received {}", page, reportParameter, movies);
        return movies;
    }
}
