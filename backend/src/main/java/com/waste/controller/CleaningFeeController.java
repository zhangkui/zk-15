package com.waste.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.waste.common.Result;
import com.waste.entity.CleaningFee;
import com.waste.service.CleaningFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cleaning-fee")
@RequiredArgsConstructor
public class CleaningFeeController {

    private final CleaningFeeService cleaningFeeService;

    @PostMapping
    public Result<Void> add(@RequestBody CleaningFee cleaningFee) {
        cleaningFeeService.save(cleaningFee);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody CleaningFee cleaningFee) {
        cleaningFeeService.updateById(cleaningFee);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cleaningFeeService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CleaningFee> getById(@PathVariable Long id) {
        return Result.success(cleaningFeeService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    public Result<List<CleaningFee>> listByOrderId(@PathVariable Long orderId) {
        LambdaQueryWrapper<CleaningFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CleaningFee::getOrderId, orderId);
        wrapper.orderByDesc(CleaningFee::getCreateTime);
        return Result.success(cleaningFeeService.list(wrapper));
    }
}
