package com.waste.common;

import lombok.Data;

@Data
public class PageQuery {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Integer status;
    private String startTime;
    private String endTime;
}
