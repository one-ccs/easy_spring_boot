package com.one_ccs.easy_spring_boot.entity.vo.request;

import com.one_ccs.easy_spring_boot.entity.vo.BasePageQuery;

public class QueryUserVO extends BasePageQuery {
    /* 0 全部，1 普通用户，2 管理员 */
    private Integer filterFlag = 0;

    public Integer getFilterFlag() {
        return filterFlag;
    }

    public void setFilterFlag(Integer filterFlag) {
        this.filterFlag = filterFlag;
    }
}
