package com.waste.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.waste.common.Result;
import com.waste.entity.Archive;
import com.waste.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    @PostMapping
    public Result<Void> add(@RequestBody Archive archive) {
        archiveService.save(archive);
        return Result.success();
    }

    @GetMapping("/order/{orderId}")
    public Result<List<Archive>> listByOrderId(@PathVariable Long orderId) {
        LambdaQueryWrapper<Archive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Archive::getOrderId, orderId);
        wrapper.orderByDesc(Archive::getCreateTime);
        return Result.success(archiveService.list(wrapper));
    }
}
