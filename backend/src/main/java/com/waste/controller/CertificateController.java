package com.waste.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.waste.common.Result;
import com.waste.entity.Certificate;
import com.waste.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public Result<Void> add(@RequestBody Certificate certificate) {
        certificate.setUploadTime(LocalDateTime.now());
        certificateService.save(certificate);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Certificate certificate) {
        certificateService.updateById(certificate);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        certificateService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Certificate> getById(@PathVariable Long id) {
        return Result.success(certificateService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    public Result<List<Certificate>> listByOrderId(@PathVariable Long orderId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getOrderId, orderId);
        wrapper.orderByDesc(Certificate::getUploadTime);
        return Result.success(certificateService.list(wrapper));
    }
}
