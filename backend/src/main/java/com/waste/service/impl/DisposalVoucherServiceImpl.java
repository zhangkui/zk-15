package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.DisposalVoucher;
import com.waste.entity.OperationLog;
import com.waste.entity.PickupOrder;
import com.waste.entity.WasteInfo;
import com.waste.mapper.DisposalVoucherMapper;
import com.waste.service.DisposalVoucherService;
import com.waste.service.OperationLogService;
import com.waste.service.PickupOrderService;
import com.waste.service.WasteInfoService;
import com.waste.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class DisposalVoucherServiceImpl extends ServiceImpl<DisposalVoucherMapper, DisposalVoucher> implements DisposalVoucherService {

    @Autowired
    private WasteInfoService wasteInfoService;

    @Autowired
    private PickupOrderService pickupOrderService;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public Page<DisposalVoucher> page(PageQuery query) {
        LambdaQueryWrapper<DisposalVoucher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(DisposalVoucher::getVoucherNo, query.getKeyword())
                    .or().like(DisposalVoucher::getVoucherTitle, query.getKeyword())
                    .or().like(DisposalVoucher::getDisposalUnit, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(DisposalVoucher::getCreatedTime, LocalDateTime.parse(query.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(DisposalVoucher::getCreatedTime, LocalDateTime.parse(query.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(DisposalVoucher::getCreatedTime);
        Page<DisposalVoucher> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        for (DisposalVoucher voucher : page.getRecords()) {
            setRelatedInfo(voucher);
        }
        return page;
    }

    @Override
    public boolean add(DisposalVoucher voucher) {
        if (!StringUtils.hasText(voucher.getVoucherNo())) {
            voucher.setVoucherNo(CodeGenerator.generateVoucherNo());
        }
        boolean success = this.save(voucher);
        if (success) {
            saveLog("凭证", voucher.getId(), voucher.getVoucherNo(), "创建", "上传处理凭证: " + voucher.getVoucherTitle());
        }
        return success;
    }

    @Override
    public boolean update(DisposalVoucher voucher) {
        boolean success = this.updateById(voucher);
        if (success) {
            saveLog("凭证", voucher.getId(), voucher.getVoucherNo(), "更新", "更新处理凭证: " + voucher.getVoucherTitle());
        }
        return success;
    }

    @Override
    public boolean delete(Long id) {
        DisposalVoucher voucher = this.getById(id);
        boolean success = this.removeById(id);
        if (success && voucher != null) {
            saveLog("凭证", id, voucher.getVoucherNo(), "删除", "删除处理凭证: " + voucher.getVoucherTitle());
        }
        return success;
    }

    @Override
    public DisposalVoucher getDetail(Long id) {
        DisposalVoucher voucher = this.getById(id);
        if (voucher != null) {
            setRelatedInfo(voucher);
        }
        return voucher;
    }

    private void setRelatedInfo(DisposalVoucher voucher) {
        if (voucher.getWasteId() != null) {
            WasteInfo wasteInfo = wasteInfoService.getById(voucher.getWasteId());
            if (wasteInfo != null) {
                voucher.setWasteName(wasteInfo.getWasteName());
            }
        }
        if (voucher.getOrderId() != null) {
            PickupOrder order = pickupOrderService.getById(voucher.getOrderId());
            if (order != null) {
                voucher.setOrderNo(order.getOrderNo());
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
}
