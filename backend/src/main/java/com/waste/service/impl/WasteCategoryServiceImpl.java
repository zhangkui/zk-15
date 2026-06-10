package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.common.PageQuery;
import com.waste.entity.OperationLog;
import com.waste.entity.WasteCategory;
import com.waste.mapper.WasteCategoryMapper;
import com.waste.service.OperationLogService;
import com.waste.service.WasteCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WasteCategoryServiceImpl extends ServiceImpl<WasteCategoryMapper, WasteCategory> implements WasteCategoryService {

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public Page<WasteCategory> page(PageQuery query) {
        LambdaQueryWrapper<WasteCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(WasteCategory::getCategoryName, query.getKeyword())
                    .or().like(WasteCategory::getCategoryCode, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(WasteCategory::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(WasteCategory::getSortOrder);
        return this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public boolean add(WasteCategory category) {
        boolean success = this.save(category);
        if (success) {
            saveLog("分类", category.getId(), category.getCategoryCode(), "创建", "新增废弃物分类: " + category.getCategoryName());
        }
        return success;
    }

    @Override
    public boolean update(WasteCategory category) {
        boolean success = this.updateById(category);
        if (success) {
            saveLog("分类", category.getId(), category.getCategoryCode(), "更新", "更新废弃物分类: " + category.getCategoryName());
        }
        return success;
    }

    @Override
    public boolean delete(Long id) {
        WasteCategory category = this.getById(id);
        boolean success = this.removeById(id);
        if (success && category != null) {
            saveLog("分类", id, category.getCategoryCode(), "删除", "删除废弃物分类: " + category.getCategoryName());
        }
        return success;
    }

    @Override
    public WasteCategory getById(Long id) {
        return super.getById(id);
    }

    @Override
    public List<WasteCategory> listAll() {
        LambdaQueryWrapper<WasteCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WasteCategory::getStatus, 1);
        wrapper.orderByAsc(WasteCategory::getSortOrder);
        return this.list(wrapper);
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
