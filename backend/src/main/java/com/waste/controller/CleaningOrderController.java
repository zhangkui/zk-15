package com.waste.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.Result;
import com.waste.dto.PageRequest;
import com.waste.entity.CleaningOrder;
import com.waste.entity.WasteItem;
import com.waste.service.CleaningOrderService;
import com.waste.service.WasteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cleaning-order")
@RequiredArgsConstructor
public class CleaningOrderController {

    private final CleaningOrderService cleaningOrderService;
    private final WasteItemService wasteItemService;

    @PostMapping
    public Result<Void> add(@RequestBody CleaningOrder cleaningOrder) {
        cleaningOrder.setOrderNo(generateOrderNo());
        cleaningOrderService.save(cleaningOrder);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody CleaningOrder cleaningOrder) {
        cleaningOrderService.updateById(cleaningOrder);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cleaningOrderService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CleaningOrder> getById(@PathVariable Long id) {
        return Result.success(cleaningOrderService.getById(id));
    }

    @GetMapping("/page")
    public Result<Page<CleaningOrder>> page(PageRequest pageRequest,
                                            @RequestParam(required = false) String orderNo,
                                            @RequestParam(required = false) String status) {
        Page<CleaningOrder> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<CleaningOrder> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(CleaningOrder::getOrderNo, orderNo);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CleaningOrder::getStatus, status);
        }
        wrapper.orderByDesc(CleaningOrder::getCreateTime);
        Page<CleaningOrder> result = cleaningOrderService.page(page, wrapper);
        fillWasteItemName(result.getRecords());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<CleaningOrder>> list() {
        LambdaQueryWrapper<CleaningOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CleaningOrder::getCreateTime);
        List<CleaningOrder> list = cleaningOrderService.list(wrapper);
        fillWasteItemName(list);
        return Result.success(list);
    }

    private void fillWasteItemName(List<CleaningOrder> orders) {
        if (orders == null || orders.isEmpty()) return;
        List<Long> wasteItemIds = orders.stream().map(CleaningOrder::getWasteItemId).distinct().collect(Collectors.toList());
        Map<Long, String> wasteItemMap = wasteItemService.listByIds(wasteItemIds)
                .stream().collect(Collectors.toMap(WasteItem::getId, WasteItem::getName));
        orders.forEach(order -> order.setWasteItemName(wasteItemMap.get(order.getWasteItemId())));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        CleaningOrder cleaningOrder = new CleaningOrder();
        cleaningOrder.setId(id);
        cleaningOrder.setStatus(status);
        if ("已完成".equals(status)) {
            cleaningOrder.setCompleteTime(LocalDateTime.now());
        }
        cleaningOrderService.updateById(cleaningOrder);
        return Result.success();
    }

    @GetMapping("/generate-order-no")
    public Result<String> generateOrderNoApi() {
        return Result.success(generateOrderNo());
    }

    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = IdUtil.simpleUUID().substring(0, 6).toUpperCase();
        return "CL" + datePart + randomPart;
    }
}
