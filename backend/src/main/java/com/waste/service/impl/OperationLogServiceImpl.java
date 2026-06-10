package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.OperationLog;
import com.waste.mapper.OperationLogMapper;
import com.waste.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public Page<OperationLog> page(PageQuery query) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(OperationLog::getBusinessNo, query.getKeyword())
                    .or().like(OperationLog::getOperationType, query.getKeyword())
                    .or().like(OperationLog::getOperator, query.getKeyword())
                    .or().like(OperationLog::getOperationDetail, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(OperationLog::getCreatedTime, LocalDateTime.parse(query.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(OperationLog::getCreatedTime, LocalDateTime.parse(query.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(OperationLog::getCreatedTime);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public OperationLog getById(Long id) {
        return super.getById(id);
    }

    @Override
    public boolean add(OperationLog log) {
        return this.save(log);
    }
}
