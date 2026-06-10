package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.PickupOrder;
import com.waste.service.PickupOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pickupOrder")
public class PickupOrderController {

    @Autowired
    private PickupOrderService pickupOrderService;

    @GetMapping("/list")
    public Result<PageResult<PickupOrder>> list(PageQuery query) {
        Page<PickupOrder> page = pickupOrderService.page(query);
        PageResult<PickupOrder> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<PickupOrder> getById(@PathVariable Long id) {
        PickupOrder order = pickupOrderService.getDetail(id);
        return Result.success(order);
    }

    @PostMapping
    public Result<Void> add(@RequestBody PickupOrder order) {
        boolean success = pickupOrderService.add(order);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody PickupOrder order) {
        boolean success = pickupOrderService.update(order);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = pickupOrderService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
