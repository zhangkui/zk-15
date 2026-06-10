package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.FeeRecord;

import java.math.BigDecimal;

public interface FeeRecordService extends IService<FeeRecord> {

    Page<FeeRecord> page(PageQuery query);

    boolean add(FeeRecord feeRecord);

    boolean update(FeeRecord feeRecord);

    boolean delete(Long id);

    FeeRecord getDetail(Long id);

    BigDecimal sumThisMonth();
}
