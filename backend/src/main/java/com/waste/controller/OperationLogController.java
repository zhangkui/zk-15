package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.OperationLog;
import com.waste.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<OperationLog>> list(PageQuery query) {
        Page<OperationLog> page = operationLogService.page(query);
        PageResult<OperationLog> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }

    @PostMapping
    public Result<Void> add(@RequestBody OperationLog log) {
        boolean success = operationLogService.add(log);
        return success ? Result.success() : Result.error("添加失败");
    }
}
