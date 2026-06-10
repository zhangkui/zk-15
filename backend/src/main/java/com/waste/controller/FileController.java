package com.waste.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.waste.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String newFilename = IdUtil.simpleUUID() + ext;

        File dest = new File(uploadDir, newFilename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        Map<String, String> result = new HashMap<>();
        result.put("url", "/uploads/" + newFilename);
        result.put("fileName", originalFilename);
        result.put("fileSize", String.valueOf(file.getSize()));
        return Result.success(result);
    }
}
