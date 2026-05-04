package com.loogingko.ncjd.service;

import com.loogingko.ncjd.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".mp4", ".webm");
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public UploadService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    /**
     * 上传文件到对象存储，返回公网可访问的 URL（须与存储桶读权限配置一致）。
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件！");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过50MB");
        }

        // 校验文件类型
        String fileName = file.getOriginalFilename();
        
        if (fileName == null || fileName.isBlank() || !ALLOWED_EXTENSIONS.contains(fileName.substring(fileName.lastIndexOf('.')))) {
            throw new IllegalArgumentException("文件类型不支持");
        }

        String objectName = buildObjectName(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        // 先落盘再上传：避免 stream + getSize() 与 multipart 实际长度不一致时读流挂死。
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("minio-upload-", ".tmp");
            file.transferTo(tempFile);

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .filename(tempFile.toAbsolutePath().toString())
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new IllegalStateException("文件上传失败: " + e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // best effort
                }
            }
        }

        return buildPublicUrl(objectName);
    }

    private static String buildObjectName(String originalFilename) {
        String suffix = "";
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                suffix = originalFilename.substring(dot);
            }
        }
        return "uploads/" + UUID.randomUUID() + suffix;
    }

    private String buildPublicUrl(String objectName) {
        String base = minioProperties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            base = minioProperties.getEndpoint();
        }
        base = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String bucket = minioProperties.getBucket();
        return base + "/" + bucket + "/" + objectName;
    }
}
