package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.FeeRecord;
import com.waste.service.FeeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeRecord")
public class FeeRecordController {

    @Autowired
    private FeeRecordService feeRecordService;

    @GetMapping("/list")
    public Result<PageResult<FeeRecord>> list(PageQuery query) {
        Page<FeeRecord> page = feeRecordService.page(query);
        PageResult<FeeRecord> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<FeeRecord> getById(@PathVariable Long id) {
        FeeRecord feeRecord = feeRecordService.getDetail(id);
        return Result.success(feeRecord);
    }

    @PostMapping
    public Result<Void> add(@RequestBody FeeRecord feeRecord) {
        boolean success = feeRecordService.add(feeRecord);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody FeeRecord feeRecord) {
        boolean success = feeRecordService.update(feeRecord);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = feeRecordService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
