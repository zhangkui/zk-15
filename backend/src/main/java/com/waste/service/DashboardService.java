package com.waste.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {

    long getTotalWasteCount();

    long getPickingCount();

    long getCompletedCount();

    long getThisMonthOrderCount();

    BigDecimal getThisMonthFeeTotal();

    List<Map<String, Object>> getCategoryWasteCount();
}
