package com.waste.controller;

import com.waste.common.Result;
import com.waste.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalWasteCount", dashboardService.getTotalWasteCount());
        data.put("pickingCount", dashboardService.getPickingCount());
        data.put("completedCount", dashboardService.getCompletedCount());
        data.put("thisMonthOrderCount", dashboardService.getThisMonthOrderCount());
        data.put("thisMonthFeeTotal", dashboardService.getThisMonthFeeTotal());
        return Result.success(data);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalWasteCount", dashboardService.getTotalWasteCount());
        data.put("pickingCount", dashboardService.getPickingCount());
        data.put("completedCount", dashboardService.getCompletedCount());
        data.put("thisMonthOrderCount", dashboardService.getThisMonthOrderCount());
        data.put("thisMonthFeeTotal", dashboardService.getThisMonthFeeTotal());
        data.put("categoryWasteCount", dashboardService.getCategoryWasteCount());
        return Result.success(data);
    }

    @GetMapping("/categoryWasteCount")
    public Result<List<Map<String, Object>>> categoryWasteCount() {
        List<Map<String, Object>> list = dashboardService.getCategoryWasteCount();
        return Result.success(list);
    }
}
