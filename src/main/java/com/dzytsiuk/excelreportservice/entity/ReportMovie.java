package com.dzytsiuk.excelreportservice.entity;

import com.dzytsiuk.excelreportservice.config.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportMovie {
    private int id;
    private String nameNative;
    private String nameRussian;
    private String description;
    private Double rating;
    private Double price;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime  addDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime  lastModifiedDate;
    private int reviewCount;
    private String genres;

}
