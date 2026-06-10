package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.WasteInfo;
import com.waste.service.WasteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wasteInfo")
public class WasteInfoController {

    @Autowired
    private WasteInfoService wasteInfoService;

    @GetMapping("/list")
    public Result<PageResult<WasteInfo>> list(PageQuery query) {
        Page<WasteInfo> page = wasteInfoService.page(query);
        PageResult<WasteInfo> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<WasteInfo> getById(@PathVariable Long id) {
        WasteInfo wasteInfo = wasteInfoService.getDetail(id);
        return Result.success(wasteInfo);
    }

    @PostMapping
    public Result<Void> add(@RequestBody WasteInfo wasteInfo) {
        boolean success = wasteInfoService.add(wasteInfo);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody WasteInfo wasteInfo) {
        boolean success = wasteInfoService.update(wasteInfo);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = wasteInfoService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
