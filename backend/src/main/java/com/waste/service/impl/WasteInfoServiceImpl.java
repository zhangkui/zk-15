package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.OperationLog;
import com.waste.entity.WasteCategory;
import com.waste.entity.WasteInfo;
import com.waste.mapper.WasteInfoMapper;
import com.waste.service.OperationLogService;
import com.waste.service.WasteCategoryService;
import com.waste.service.WasteInfoService;
import com.waste.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WasteInfoServiceImpl extends ServiceImpl<WasteInfoMapper, WasteInfo> implements WasteInfoService {

    @Autowired
    private WasteCategoryService wasteCategoryService;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public Page<WasteInfo> page(PageQuery query) {
        LambdaQueryWrapper<WasteInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(WasteInfo::getWasteName, query.getKeyword())
                    .or().like(WasteInfo::getWasteNo, query.getKeyword())
                    .or().like(WasteInfo::getRenterName, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(WasteInfo::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(WasteInfo::getCreatedTime, LocalDateTime.parse(query.getStartTime() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(WasteInfo::getCreatedTime, LocalDateTime.parse(query.getEndTime() + "T23:59:59"));
        }
        wrapper.orderByDesc(WasteInfo::getCreatedTime);
        Page<WasteInfo> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        for (WasteInfo wasteInfo : page.getRecords()) {
            setCategoryInfo(wasteInfo);
        }
        return page;
    }

    @Override
    public boolean add(WasteInfo wasteInfo) {
        if (!StringUtils.hasText(wasteInfo.getWasteNo())) {
            wasteInfo.setWasteNo(CodeGenerator.generateWasteNo());
        }
        if (wasteInfo.getStatus() == null) {
            wasteInfo.setStatus(0);
        }
        boolean success = this.save(wasteInfo);
        if (success) {
            saveLog("废弃物", wasteInfo.getId(), wasteInfo.getWasteNo(), "创建", "登记废弃物信息: " + wasteInfo.getWasteName());
        }
        return success;
    }

    @Override
    public boolean update(WasteInfo wasteInfo) {
        WasteInfo old = this.getById(wasteInfo.getId());
        boolean success = this.updateById(wasteInfo);
        if (success) {
            StringBuilder detail = new StringBuilder("更新废弃物信息: " + wasteInfo.getWasteName());
            if (old != null && old.getStatus() != null && !old.getStatus().equals(wasteInfo.getStatus())) {
                detail.append("，状态变更: ").append(getStatusText(old.getStatus())).append(" -> ").append(getStatusText(wasteInfo.getStatus()));
            }
            saveLog("废弃物", wasteInfo.getId(), wasteInfo.getWasteNo(), "更新", detail.toString());
        }
        return success;
    }

    @Override
    public boolean delete(Long id) {
        WasteInfo wasteInfo = this.getById(id);
        boolean success = this.removeById(id);
        if (success && wasteInfo != null) {
            saveLog("废弃物", id, wasteInfo.getWasteNo(), "删除", "删除废弃物: " + wasteInfo.getWasteName());
        }
        return success;
    }

    @Override
    public WasteInfo getDetail(Long id) {
        WasteInfo wasteInfo = this.getById(id);
        if (wasteInfo != null) {
            setCategoryInfo(wasteInfo);
        }
        return wasteInfo;
    }

    @Override
    public List<WasteInfo> listByCategoryId(Long categoryId) {
        LambdaQueryWrapper<WasteInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WasteInfo::getCategoryId, categoryId);
        return this.list(wrapper);
    }

    @Override
    public long countTotal() {
        return this.count();
    }

    @Override
    public long countByStatus(Integer status) {
        LambdaQueryWrapper<WasteInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WasteInfo::getStatus, status);
        return this.count(wrapper);
    }

    private void setCategoryInfo(WasteInfo wasteInfo) {
        if (wasteInfo.getCategoryId() != null) {
            WasteCategory category = wasteCategoryService.getById(wasteInfo.getCategoryId());
            if (category != null) {
                wasteInfo.setCategoryName(category.getCategoryName());
                wasteInfo.setCategoryCode(category.getCategoryCode());
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
            case 0: return "待登记";
            case 1: return "已登记";
            case 2: return "已委托";
            case 3: return "清运中";
            case 4: return "已完成";
            default: return "未知";
        }
    }
}
