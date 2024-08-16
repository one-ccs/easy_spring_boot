package com.one_ccs.easy_spring_boot.entity.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class BasePageQuery {

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDatetime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDatetime;

    private String query;
}
