package com.waste.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.waste.common.Result;
import com.waste.entity.WasteCategory;
import com.waste.service.WasteCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class WasteCategoryController {

    private final WasteCategoryService wasteCategoryService;

    @PostMapping
    public Result<Void> add(@RequestBody WasteCategory wasteCategory) {
        wasteCategoryService.save(wasteCategory);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody WasteCategory wasteCategory) {
        wasteCategoryService.updateById(wasteCategory);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wasteCategoryService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<WasteCategory> getById(@PathVariable Long id) {
        return Result.success(wasteCategoryService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<WasteCategory>> list() {
        LambdaQueryWrapper<WasteCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(WasteCategory::getSortOrder);
        return Result.success(wasteCategoryService.list(wrapper));
    }
}
