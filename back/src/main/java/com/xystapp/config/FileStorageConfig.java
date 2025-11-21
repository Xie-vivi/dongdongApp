package com.xystapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 文件存储配置类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {

    /**
     * 存储类型：local, oss, cos
     */
    private StorageType storageType = StorageType.LOCAL;

    /**
     * 本地存储路径
     */
    private String localPath = "uploads";

    /**
     * 最大文件大小（字节）
     */
    private Long maxFileSize = 10L * 1024 * 1024; // 10MB

    /**
     * 允许的图片类型
     */
    private List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 允许的文档类型
     */
    private List<String> allowedDocumentTypes = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx");

    /**
     * 允许的视频类型
     */
    private List<String> allowedVideoTypes = Arrays.asList("mp4", "avi", "mov", "wmv");

    /**
     * 允许的音频类型
     */
    private List<String> allowedAudioTypes = Arrays.asList("mp3", "wav", "flac", "aac");

    /**
     * 是否启用缩略图
     */
    private Boolean thumbnailEnabled = true;

    /**
     * 缩略图宽度
     */
    private Integer thumbnailWidth = 200;

    /**
     * 缩略图高度
     */
    private Integer thumbnailHeight = 200;

    /**
     * 图片最大宽度
     */
    private Integer imageMaxWidth = 1920;

    /**
     * 图片最大高度
     */
    private Integer imageMaxHeight = 1080;

    /**
     * 图片压缩质量（0.0-1.0）
     */
    private Float imageCompressQuality = 0.8f;

    /**
     * 是否启用图片压缩
     */
    private Boolean imageCompressEnabled = true;

    /**
     * 文件访问URL前缀
     */
    private String urlPrefix = "/uploads";

    /**
     * 是否启用文件内容验证
     */
    private Boolean contentValidationEnabled = true;

    /**
     * 临时文件清理时间（小时）
     */
    private Integer tempFileCleanupHours = 1;

    /**
     * 删除文件保留时间（天）
     */
    private Integer deletedFileRetentionDays = 30;

    /**
     * 存储类型枚举
     */
    public enum StorageType {
        LOCAL("local", "本地存储"),
        OSS("oss", "阿里云OSS"),
        COS("cos", "腾讯云COS");

        private final String code;
        private final String description;

        StorageType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static StorageType fromCode(String code) {
            for (StorageType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return LOCAL;
        }
    }

    /**
     * 获取允许的文件类型列表
     */
    public List<String> getAllowedTypesByCategory(String category) {
        switch (category.toLowerCase()) {
            case "image":
                return allowedImageTypes;
            case "document":
                return allowedDocumentTypes;
            case "video":
                return allowedVideoTypes;
            case "audio":
                return allowedAudioTypes;
            default:
                return Arrays.asList();
        }
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isFileTypeAllowed(String extension, String category) {
        List<String> allowedTypes = getAllowedTypesByCategory(category);
        return allowedTypes.contains(extension.toLowerCase());
    }

    /**
     * 检查文件大小是否超限
     */
    public boolean isFileSizeValid(long fileSize) {
        return fileSize <= maxFileSize;
    }

    /**
     * 获取存储根路径
     */
    public String getStorageRootPath() {
        if (storageType == StorageType.LOCAL) {
            return localPath;
        }
        return localPath; // 默认返回本地路径
    }

    /**
     * 获取完整存储路径
     */
    public String getFullStoragePath() {
        return System.getProperty("user.dir") + "/" + getStorageRootPath();
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String filePath) {
        return urlPrefix + "/" + filePath;
    }

    /**
     * 获取缩略图路径
     */
    public String getThumbnailPath(String originalPath) {
        String directory = originalPath.substring(0, originalPath.lastIndexOf("/"));
        String fileName = originalPath.substring(originalPath.lastIndexOf("/") + 1);
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        
        return directory + "/thumbnail_" + nameWithoutExt + extension;
    }

    /**
     * 验证配置参数
     */
    public boolean validateConfig() {
        if (maxFileSize <= 0) {
            return false;
        }
        if (thumbnailWidth <= 0 || thumbnailHeight <= 0) {
            return false;
        }
        if (imageMaxWidth <= 0 || imageMaxHeight <= 0) {
            return false;
        }
        if (imageCompressQuality < 0.0f || imageCompressQuality > 1.0f) {
            return false;
        }
        return true;
    }

    // ==================== Getter和Setter方法 ====================

    public StorageType getStorageType() { return storageType; }
    public void setStorageType(StorageType storageType) { this.storageType = storageType; }

    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }

    public Long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; }

    public List<String> getAllowedImageTypes() { return allowedImageTypes; }
    public void setAllowedImageTypes(List<String> allowedImageTypes) { this.allowedImageTypes = allowedImageTypes; }

    public List<String> getAllowedDocumentTypes() { return allowedDocumentTypes; }
    public void setAllowedDocumentTypes(List<String> allowedDocumentTypes) { this.allowedDocumentTypes = allowedDocumentTypes; }

    public List<String> getAllowedVideoTypes() { return allowedVideoTypes; }
    public void setAllowedVideoTypes(List<String> allowedVideoTypes) { this.allowedVideoTypes = allowedVideoTypes; }

    public List<String> getAllowedAudioTypes() { return allowedAudioTypes; }
    public void setAllowedAudioTypes(List<String> allowedAudioTypes) { this.allowedAudioTypes = allowedAudioTypes; }

    public Boolean getThumbnailEnabled() { return thumbnailEnabled; }
    public void setThumbnailEnabled(Boolean thumbnailEnabled) { this.thumbnailEnabled = thumbnailEnabled; }

    public Integer getThumbnailWidth() { return thumbnailWidth; }
    public void setThumbnailWidth(Integer thumbnailWidth) { this.thumbnailWidth = thumbnailWidth; }

    public Integer getThumbnailHeight() { return thumbnailHeight; }
    public void setThumbnailHeight(Integer thumbnailHeight) { this.thumbnailHeight = thumbnailHeight; }

    public Integer getImageMaxWidth() { return imageMaxWidth; }
    public void setImageMaxWidth(Integer imageMaxWidth) { this.imageMaxWidth = imageMaxWidth; }

    public Integer getImageMaxHeight() { return imageMaxHeight; }
    public void setImageMaxHeight(Integer imageMaxHeight) { this.imageMaxHeight = imageMaxHeight; }

    public Float getImageCompressQuality() { return imageCompressQuality; }
    public void setImageCompressQuality(Float imageCompressQuality) { this.imageCompressQuality = imageCompressQuality; }

    public Boolean getImageCompressEnabled() { return imageCompressEnabled; }
    public void setImageCompressEnabled(Boolean imageCompressEnabled) { this.imageCompressEnabled = imageCompressEnabled; }

    public String getUrlPrefix() { return urlPrefix; }
    public void setUrlPrefix(String urlPrefix) { this.urlPrefix = urlPrefix; }

    public Boolean getContentValidationEnabled() { return contentValidationEnabled; }
    public void setContentValidationEnabled(Boolean contentValidationEnabled) { this.contentValidationEnabled = contentValidationEnabled; }

    public Integer getTempFileCleanupHours() { return tempFileCleanupHours; }
    public void setTempFileCleanupHours(Integer tempFileCleanupHours) { this.tempFileCleanupHours = tempFileCleanupHours; }

    public Integer getDeletedFileRetentionDays() { return deletedFileRetentionDays; }
    public void setDeletedFileRetentionDays(Integer deletedFileRetentionDays) { this.deletedFileRetentionDays = deletedFileRetentionDays; }
}
