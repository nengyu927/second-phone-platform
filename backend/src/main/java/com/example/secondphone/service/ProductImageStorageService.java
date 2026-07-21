package com.example.secondphone.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.secondphone.exception.BusinessException;

@Service
public class ProductImageStorageService {
    private static final Map<String, String> EXTENSIONS = Map.of("image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
    private final Path productDirectory;
    public ProductImageStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        productDirectory = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("products");
    }
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("請選擇要上傳的圖片");
        String extension = EXTENSIONS.get(file.getContentType());
        if (extension == null) throw new BusinessException("圖片僅支援 JPG、PNG 或 WebP 格式");
        String filename = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(productDirectory);
            Path target = productDirectory.resolve(filename).normalize();
            if (!target.startsWith(productDirectory)) throw new BusinessException("圖片檔名不合法");
            file.transferTo(target);
            return "/uploads/products/" + filename;
        } catch (IOException exception) {
            throw new BusinessException("圖片儲存失敗，請稍後再試");
        }
    }
}
