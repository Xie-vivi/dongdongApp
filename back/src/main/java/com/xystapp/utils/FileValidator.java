package com.xystapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件验证工具类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Component
public class FileValidator {

    private static final Logger log = LoggerFactory.getLogger(FileValidator.class);

    // 文件魔数映射
    private static final Map<String, byte[]> FILE_MAGIC_NUMBERS = new HashMap<>();

    static {
        // 图片格式魔数
        FILE_MAGIC_NUMBERS.put("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        FILE_MAGIC_NUMBERS.put("jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
        FILE_MAGIC_NUMBERS.put("png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A});
        FILE_MAGIC_NUMBERS.put("gif", new byte[]{0x47, 0x49, 0x46, 0x38});
        FILE_MAGIC_NUMBERS.put("webp", new byte[]{0x52, 0x49, 0x46, 0x46}); // RIFF
        FILE_MAGIC_NUMBERS.put("bmp", new byte[]{0x42, 0x4D});

        // 文档格式魔数
        FILE_MAGIC_NUMBERS.put("pdf", new byte[]{0x25, 0x50, 0x44, 0x46}); // %PDF
        FILE_MAGIC_NUMBERS.put("doc", new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1});
        FILE_MAGIC_NUMBERS.put("docx", new byte[]{0x50, 0x4B, 0x03, 0x04}); // ZIP格式
        FILE_MAGIC_NUMBERS.put("xls", new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1});
        FILE_MAGIC_NUMBERS.put("xlsx", new byte[]{0x50, 0x4B, 0x03, 0x04}); // ZIP格式
        FILE_MAGIC_NUMBERS.put("ppt", new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1});
        FILE_MAGIC_NUMBERS.put("pptx", new byte[]{0x50, 0x4B, 0x03, 0x04}); // ZIP格式

        // 视频格式魔数
        FILE_MAGIC_NUMBERS.put("mp4", new byte[]{0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70});
        FILE_MAGIC_NUMBERS.put("avi", new byte[]{0x52, 0x49, 0x46, 0x46}); // RIFF
        FILE_MAGIC_NUMBERS.put("mov", new byte[]{0x00, 0x00, 0x00, 0x14, 0x66, 0x74, 0x79, 0x70});

        // 音频格式魔数
        FILE_MAGIC_NUMBERS.put("mp3", new byte[]{(byte) 0xFF, (byte) 0xFB}); // 或 ID3
        FILE_MAGIC_NUMBERS.put("wav", new byte[]{0x52, 0x49, 0x46, 0x46}); // RIFF
        FILE_MAGIC_NUMBERS.put("flac", new byte[]{0x66, 0x4C, 0x61, 0x43}); // fLaC
    }

    /**
     * 危险文件扩展名列表
     */
    private static final List<String> DANGEROUS_EXTENSIONS = Arrays.asList(
            "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "sh", "php", "asp", "aspx",
            "jsp", "py", "rb", "pl", "cgi", "dll", "sys", "drv", "msi", "deb", "rpm", "dmg"
    );

    /**
     * 验证文件是否安全
     */
    public ValidationResult validateFile(MultipartFile file, String category, long maxSize) {
        try {
            // 基础验证
            if (file == null || file.isEmpty()) {
                return ValidationResult.error("文件不能为空");
            }

            // 文件大小验证
            if (file.getSize() > maxSize) {
                return ValidationResult.error("文件大小超过限制：" + formatFileSize(maxSize));
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ValidationResult.error("文件名不能为空");
            }

            String extension = getFileExtension(originalFilename);
            if (extension.isEmpty()) {
                return ValidationResult.error("文件类型不能为空");
            }

            // 检查危险文件
            if (isDangerousFile(extension)) {
                return ValidationResult.error("不允许上传此类型文件");
            }

            // 文件类型验证
            if (!isValidFileType(extension, category)) {
                return ValidationResult.error("不支持的文件类型：" + extension);
            }

            // MIME类型验证
            String mimeType = file.getContentType();
            if (!isValidMimeType(mimeType, category)) {
                return ValidationResult.error("MIME类型不匹配：" + mimeType);
            }

            // 文件内容验证（魔数检查）
            if (!validateFileContent(file, extension)) {
                return ValidationResult.error("文件内容与扩展名不匹配");
            }

            return ValidationResult.success();

        } catch (Exception e) {
            log.error("文件验证失败", e);
            return ValidationResult.error("文件验证过程中发生错误");
        }
    }

    /**
     * 验证图片文件
     */
    public ValidationResult validateImageFile(MultipartFile file, long maxSize, int maxWidth, int maxHeight) {
        ValidationResult basicResult = validateFile(file, "image", maxSize);
        if (!basicResult.isValid()) {
            return basicResult;
        }

        try {
            // 检查图片尺寸（如果需要）
            // 这里可以使用ImageIO读取图片信息进行验证
            // 暂时跳过，在Service层处理

            return ValidationResult.success();

        } catch (Exception e) {
            log.error("图片文件验证失败", e);
            return ValidationResult.error("图片文件验证失败");
        }
    }

    /**
     * 获取文件扩展名
     */
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查是否为危险文件
     */
    public boolean isDangerousFile(String extension) {
        return DANGEROUS_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 检查文件类型是否有效
     */
    public boolean isValidFileType(String extension, String category) {
        switch (category.toLowerCase()) {
            case "image":
                return Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp").contains(extension);
            case "document":
                return Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt").contains(extension);
            case "video":
                return Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv").contains(extension);
            case "audio":
                return Arrays.asList("mp3", "wav", "flac", "aac", "ogg").contains(extension);
            default:
                return false;
        }
    }

    /**
     * 检查MIME类型是否有效
     */
    public boolean isValidMimeType(String mimeType, String category) {
        if (mimeType == null) {
            return false;
        }

        switch (category.toLowerCase()) {
            case "image":
                return mimeType.startsWith("image/");
            case "document":
                return mimeType.startsWith("application/") || mimeType.equals("text/plain");
            case "video":
                return mimeType.startsWith("video/");
            case "audio":
                return mimeType.startsWith("audio/");
            default:
                return false;
        }
    }

    /**
     * 验证文件内容（魔数检查）
     */
    public boolean validateFileContent(MultipartFile file, String extension) {
        try {
            byte[] fileBytes = file.getBytes();
            if (fileBytes.length == 0) {
                return false;
            }

            byte[] expectedMagic = FILE_MAGIC_NUMBERS.get(extension.toLowerCase());
            if (expectedMagic == null) {
                // 如果没有定义魔数，跳过验证
                return true;
            }

            // 检查文件开头是否匹配魔数
            if (fileBytes.length < expectedMagic.length) {
                return false;
            }

            for (int i = 0; i < expectedMagic.length; i++) {
                if (fileBytes[i] != expectedMagic[i]) {
                    return false;
                }
            }

            return true;

        } catch (IOException e) {
            log.error("读取文件内容失败", e);
            return false;
        }
    }

    /**
     * 格式化文件大小
     */
    public String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 获取文件类型分类
     */
    public String getFileCategory(String extension) {
        if (isValidFileType(extension, "image")) {
            return "image";
        } else if (isValidFileType(extension, "document")) {
            return "document";
        } else if (isValidFileType(extension, "video")) {
            return "video";
        } else if (isValidFileType(extension, "audio")) {
            return "audio";
        } else {
            return "other";
        }
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "验证通过");
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
