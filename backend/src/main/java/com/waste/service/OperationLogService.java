package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    Page<OperationLog> page(PageQuery query);

    OperationLog getById(Long id);

    boolean add(OperationLog log);
}
