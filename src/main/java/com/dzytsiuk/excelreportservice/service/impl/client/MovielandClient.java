package com.dzytsiuk.excelreportservice.service.impl.client;

import com.dzytsiuk.excelreportservice.entity.ReportMovie;
import com.dzytsiuk.excelreportservice.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(value = "movieland")
public interface MovielandClient {
    @GetMapping("/v1/movie/report/")
    List<ReportMovie> getMovies(@RequestParam("page") Integer page, @RequestParam("count") Integer count,
                                @RequestParam(value = "fromDate", required = false) LocalDateTime fromDate,
                                @RequestParam(value = "toDate", required = false) LocalDateTime toDate);

    @GetMapping("/v1/user/top")
    List<User> getUsers();
}
