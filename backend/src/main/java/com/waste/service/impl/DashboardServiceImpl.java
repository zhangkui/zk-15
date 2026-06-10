package com.waste.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.waste.entity.WasteCategory;
import com.waste.entity.WasteInfo;
import com.waste.service.DashboardService;
import com.waste.service.FeeRecordService;
import com.waste.service.PickupOrderService;
import com.waste.service.WasteCategoryService;
import com.waste.service.WasteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private WasteInfoService wasteInfoService;

    @Autowired
    private PickupOrderService pickupOrderService;

    @Autowired
    private FeeRecordService feeRecordService;

    @Autowired
    private WasteCategoryService wasteCategoryService;

    @Override
    public long getTotalWasteCount() {
        return wasteInfoService.countTotal();
    }

    @Override
    public long getPickingCount() {
        return wasteInfoService.countByStatus(3);
    }

    @Override
    public long getCompletedCount() {
        return wasteInfoService.countByStatus(4);
    }

    @Override
    public long getThisMonthOrderCount() {
        return pickupOrderService.countThisMonth();
    }

    @Override
    public BigDecimal getThisMonthFeeTotal() {
        return feeRecordService.sumThisMonth();
    }

    @Override
    public List<Map<String, Object>> getCategoryWasteCount() {
        List<WasteCategory> categories = wasteCategoryService.listAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (WasteCategory category : categories) {
            LambdaQueryWrapper<WasteInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WasteInfo::getCategoryId, category.getId());
            long count = wasteInfoService.count(wrapper);
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", category.getId());
            item.put("categoryName", category.getCategoryName());
            item.put("categoryCode", category.getCategoryCode());
            item.put("count", count);
            result.add(item);
        }
        return result;
    }
}
