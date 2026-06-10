package com.waste.controller;

import com.waste.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    private static final String[] ALLOWED_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp",
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"
    };

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        if (!isAllowedExtension(extension)) {
            return Result.error("不支持的文件格式，仅支持图片和PDF、Word、Excel、TXT等文档");
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File dir = new File(uploadPath + File.separator + dateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        File targetFile = new File(dir, newFilename);

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }

        String fileUrl = "/uploads/" + dateDir + "/" + newFilename;

        Map<String, Object> data = new HashMap<>();
        data.put("fileName", originalFilename);
        data.put("fileUrl", fileUrl);
        data.put("fileSize", file.getSize());
        data.put("fileType", extension.substring(1));

        return Result.success("上传成功", data);
    }

    @PostMapping("/uploadVoucher")
    public Result<Map<String, Object>> uploadVoucher(@RequestParam("file") MultipartFile file) {
        return upload(file);
    }

    @GetMapping("/download")
    public void download(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        File file = new File(uploadPath + File.separator + filePath.replace("/uploads/", ""));
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = file.getName();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("filePath") String filePath) {
        File file = new File(uploadPath + File.separator + filePath.replace("/uploads/", ""));
        if (file.exists()) {
            file.delete();
        }
        return Result.success();
    }

    private String getExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
