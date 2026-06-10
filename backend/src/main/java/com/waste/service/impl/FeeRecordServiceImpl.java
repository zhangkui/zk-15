package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.FeeRecord;
import com.waste.entity.OperationLog;
import com.waste.entity.PickupOrder;
import com.waste.entity.WasteInfo;
import com.waste.mapper.FeeRecordMapper;
import com.waste.service.FeeRecordService;
import com.waste.service.OperationLogService;
import com.waste.service.PickupOrderService;
import com.waste.service.WasteInfoService;
import com.waste.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class FeeRecordServiceImpl extends ServiceImpl<FeeRecordMapper, FeeRecord> implements FeeRecordService {

    @Autowired
    private WasteInfoService wasteInfoService;

    @Autowired
    private PickupOrderService pickupOrderService;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public Page<FeeRecord> page(PageQuery query) {
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(FeeRecord::getFeeNo, query.getKeyword())
                    .or().like(FeeRecord::getPayerName, query.getKeyword())
                    .or().like(FeeRecord::getInvoiceNo, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(FeeRecord::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(FeeRecord::getCreatedTime, LocalDateTime.parse(query.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(FeeRecord::getCreatedTime, LocalDateTime.parse(query.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(FeeRecord::getCreatedTime);
        Page<FeeRecord> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        for (FeeRecord feeRecord : page.getRecords()) {
            setRelatedInfo(feeRecord);
        }
        return page;
    }

    @Override
    public boolean add(FeeRecord feeRecord) {
        if (!StringUtils.hasText(feeRecord.getFeeNo())) {
            feeRecord.setFeeNo(CodeGenerator.generateFeeNo());
        }
        if (feeRecord.getStatus() == null) {
            feeRecord.setStatus(0);
        }
        boolean success = this.save(feeRecord);
        if (success) {
            saveLog("费用", feeRecord.getId(), feeRecord.getFeeNo(), "创建", "创建费用记录，金额: " + feeRecord.getTotalAmount());
        }
        return success;
    }

    @Override
    public boolean update(FeeRecord feeRecord) {
        FeeRecord old = this.getById(feeRecord.getId());
        boolean success = this.updateById(feeRecord);
        if (success) {
            StringBuilder detail = new StringBuilder("更新费用记录");
            if (old != null && old.getStatus() != null && !old.getStatus().equals(feeRecord.getStatus())) {
                detail.append("，状态变更: ").append(getStatusText(old.getStatus())).append(" -> ").append(getStatusText(feeRecord.getStatus()));
            }
            saveLog("费用", feeRecord.getId(), feeRecord.getFeeNo(), "更新", detail.toString());
        }
        return success;
    }

    @Override
    public boolean delete(Long id) {
        FeeRecord feeRecord = this.getById(id);
        boolean success = this.removeById(id);
        if (success && feeRecord != null) {
            saveLog("费用", id, feeRecord.getFeeNo(), "删除", "删除费用记录，金额: " + feeRecord.getTotalAmount());
        }
        return success;
    }

    @Override
    public FeeRecord getDetail(Long id) {
        FeeRecord feeRecord = this.getById(id);
        if (feeRecord != null) {
            setRelatedInfo(feeRecord);
        }
        return feeRecord;
    }

    @Override
    public BigDecimal sumThisMonth() {
        LocalDate firstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(FeeRecord::getCreatedTime, firstDay.atStartOfDay());
        wrapper.le(FeeRecord::getCreatedTime, lastDay.atTime(23, 59, 59));
        wrapper.eq(FeeRecord::getStatus, 1);
        List<FeeRecord> list = this.list(wrapper);
        return list.stream()
                .map(FeeRecord::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setRelatedInfo(FeeRecord feeRecord) {
        if (feeRecord.getWasteId() != null) {
            WasteInfo wasteInfo = wasteInfoService.getById(feeRecord.getWasteId());
            if (wasteInfo != null) {
                feeRecord.setWasteName(wasteInfo.getWasteName());
            }
        }
        if (feeRecord.getOrderId() != null) {
            PickupOrder order = pickupOrderService.getById(feeRecord.getOrderId());
            if (order != null) {
                feeRecord.setOrderNo(order.getOrderNo());
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
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已退款";
            default: return "未知";
        }
    }
}
