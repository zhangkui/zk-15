package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.OperationLog;
import com.waste.entity.PickupOrder;
import com.waste.entity.WasteInfo;
import com.waste.mapper.PickupOrderMapper;
import com.waste.service.OperationLogService;
import com.waste.service.PickupOrderService;
import com.waste.service.WasteInfoService;
import com.waste.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class PickupOrderServiceImpl extends ServiceImpl<PickupOrderMapper, PickupOrder> implements PickupOrderService {

    @Autowired
    private WasteInfoService wasteInfoService;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public Page<PickupOrder> page(PageQuery query) {
        LambdaQueryWrapper<PickupOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(PickupOrder::getOrderNo, query.getKeyword())
                    .or().like(PickupOrder::getRenterName, query.getKeyword())
                    .or().like(PickupOrder::getContactPerson, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(PickupOrder::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(PickupOrder::getCreatedTime, LocalDateTime.parse(query.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(PickupOrder::getCreatedTime, LocalDateTime.parse(query.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(PickupOrder::getCreatedTime);
        Page<PickupOrder> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        for (PickupOrder order : page.getRecords()) {
            setWasteInfo(order);
        }
        return page;
    }

    @Override
    public boolean add(PickupOrder order) {
        if (!StringUtils.hasText(order.getOrderNo())) {
            order.setOrderNo(CodeGenerator.generateOrderNo());
        }
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        boolean success = this.save(order);
        if (success) {
            saveLog("委托单", order.getId(), order.getOrderNo(), "创建", "创建清运委托，联系人: " + order.getContactPerson());
        }
        return success;
    }

    @Override
    public boolean update(PickupOrder order) {
        PickupOrder old = this.getById(order.getId());
        boolean success = this.updateById(order);
        if (success) {
            StringBuilder detail = new StringBuilder("更新清运委托");
            if (old != null && old.getStatus() != null && !old.getStatus().equals(order.getStatus())) {
                detail.append("，状态变更: ").append(getStatusText(old.getStatus())).append(" -> ").append(getStatusText(order.getStatus()));
            }
            saveLog("委托单", order.getId(), order.getOrderNo(), "更新", detail.toString());
        }
        return success;
    }

    @Override
    public boolean delete(Long id) {
        PickupOrder order = this.getById(id);
        boolean success = this.removeById(id);
        if (success && order != null) {
            saveLog("委托单", id, order.getOrderNo(), "删除", "删除清运委托单");
        }
        return success;
    }

    @Override
    public PickupOrder getDetail(Long id) {
        PickupOrder order = this.getById(id);
        if (order != null) {
            setWasteInfo(order);
        }
        return order;
    }

    @Override
    public long countThisMonth() {
        LocalDate firstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<PickupOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(PickupOrder::getCreatedTime, firstDay.atStartOfDay());
        wrapper.le(PickupOrder::getCreatedTime, lastDay.atTime(23, 59, 59));
        return this.count(wrapper);
    }

    private void setWasteInfo(PickupOrder order) {
        if (order.getWasteId() != null) {
            WasteInfo wasteInfo = wasteInfoService.getById(order.getWasteId());
            if (wasteInfo != null) {
                order.setWasteName(wasteInfo.getWasteName());
                order.setWasteNo(wasteInfo.getWasteNo());
            }
        }
    }

    private void saveLog(String businessType, Long businessId, String businessNo, String operationType, String operationDetail) {
        OperationLog log = new OperationLog();
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setBusinessNo(businessNo);
        log.setOperationType(operationType);
        log.setOperationDetail(operationDetail);
        log.setOperator("系统管理员");
        operationLogService.add(log);
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待接单";
            case 1: return "已接单";
            case 2: return "清运中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
