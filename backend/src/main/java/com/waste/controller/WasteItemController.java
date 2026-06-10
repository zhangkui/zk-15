package com.waste.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.Result;
import com.waste.dto.PageRequest;
import com.waste.entity.WasteItem;
import com.waste.entity.WasteCategory;
import com.waste.service.WasteItemService;
import com.waste.service.WasteCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/waste-item")
@RequiredArgsConstructor
public class WasteItemController {

    private final WasteItemService wasteItemService;
    private final WasteCategoryService wasteCategoryService;

    @PostMapping
    public Result<Void> add(@RequestBody WasteItem wasteItem) {
        wasteItemService.save(wasteItem);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody WasteItem wasteItem) {
        wasteItemService.updateById(wasteItem);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wasteItemService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<WasteItem> getById(@PathVariable Long id) {
        return Result.success(wasteItemService.getById(id));
    }

    @GetMapping("/page")
    public Result<Page<WasteItem>> page(PageRequest pageRequest,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) Long categoryId,
                                        @RequestParam(required = false) String status) {
        Page<WasteItem> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<WasteItem> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(WasteItem::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(WasteItem::getCategoryId, categoryId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(WasteItem::getStatus, status);
        }
        wrapper.orderByDesc(WasteItem::getCreateTime);
        Page<WasteItem> result = wasteItemService.page(page, wrapper);
        fillCategoryName(result.getRecords());
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<WasteItem>> list() {
        LambdaQueryWrapper<WasteItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WasteItem::getCreateTime);
        List<WasteItem> list = wasteItemService.list(wrapper);
        fillCategoryName(list);
        return Result.success(list);
    }

    private void fillCategoryName(List<WasteItem> items) {
        if (items == null || items.isEmpty()) return;
        List<Long> categoryIds = items.stream().map(WasteItem::getCategoryId).distinct().collect(Collectors.toList());
        Map<Long, String> categoryMap = wasteCategoryService.listByIds(categoryIds)
                .stream().collect(Collectors.toMap(WasteCategory::getId, WasteCategory::getName));
        items.forEach(item -> item.setCategoryName(categoryMap.get(item.getCategoryId())));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        WasteItem wasteItem = new WasteItem();
        wasteItem.setId(id);
        wasteItem.setStatus(status);
        wasteItemService.updateById(wasteItem);
        return Result.success();
    }
}
