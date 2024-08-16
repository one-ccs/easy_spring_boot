package com.one_ccs.easy_spring_boot.entity.vo.request;

import java.util.List;

import lombok.Data;

@Data
public class DeleteVO {
    private Integer id = null;
    private List<Integer> ids = null;
}
