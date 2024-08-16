package com.one_ccs.easy_spring_boot.entity.vo.request;

import com.one_ccs.easy_spring_boot.entity.vo.BasePageQuery;

public class QueryUserVO extends BasePageQuery {
    private Integer filterFlag = 0;

    public Integer getFilterFlag() {
        return filterFlag;
    }

    public void setFilterFlag(Integer filterFlag) {
        this.filterFlag = filterFlag;
    }
}
