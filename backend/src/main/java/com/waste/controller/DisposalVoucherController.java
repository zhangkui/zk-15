package com.waste.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.common.PageQuery;
import com.waste.common.PageResult;
import com.waste.common.Result;
import com.waste.entity.DisposalVoucher;
import com.waste.service.DisposalVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/disposalVoucher")
public class DisposalVoucherController {

    @Autowired
    private DisposalVoucherService disposalVoucherService;

    @GetMapping("/list")
    public Result<PageResult<DisposalVoucher>> list(PageQuery query) {
        Page<DisposalVoucher> page = disposalVoucherService.page(query);
        PageResult<DisposalVoucher> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<DisposalVoucher> getById(@PathVariable Long id) {
        DisposalVoucher voucher = disposalVoucherService.getDetail(id);
        return Result.success(voucher);
    }

    @PostMapping
    public Result<Void> add(@RequestBody DisposalVoucher voucher) {
        boolean success = disposalVoucherService.add(voucher);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody DisposalVoucher voucher) {
        boolean success = disposalVoucherService.update(voucher);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = disposalVoucherService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
