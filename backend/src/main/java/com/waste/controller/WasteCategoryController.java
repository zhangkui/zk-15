package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.WasteCategory;
import com.waste.service.WasteCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wasteCategory")
public class WasteCategoryController {

    @Autowired
    private WasteCategoryService wasteCategoryService;

    @GetMapping("/list")
    public Result<PageResult<WasteCategory>> list(PageQuery query) {
        Page<WasteCategory> page = wasteCategoryService.page(query);
        PageResult<WasteCategory> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/listAll")
    public Result<List<WasteCategory>> listAll() {
        List<WasteCategory> list = wasteCategoryService.listAll();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<WasteCategory> getById(@PathVariable Long id) {
        WasteCategory category = wasteCategoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<Void> add(@RequestBody WasteCategory category) {
        boolean success = wasteCategoryService.add(category);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody WasteCategory category) {
        boolean success = wasteCategoryService.update(category);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = wasteCategoryService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
