package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件上传实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_uploads")
public class FileUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("original_name")
    private String originalName;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("mime_type")
    private String mimeType;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("related_type")
    private String relatedType;

    @TableField("related_id")
    private Long relatedId;

    @TableField("status")
    private String status;

    @TableField("upload_ip")
    private String uploadIp;

    @TableField("user_agent")
    private String userAgent;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 文件访问URL
     */
    @TableField(exist = false)
    private String fileUrl;

    /**
     * 上传用户名
     */
    @TableField(exist = false)
    private String uploadUsername;

    /**
     * 上传用户昵称
     */
    @TableField(exist = false)
    private String uploadUserNickname;

    /**
     * 上传用户头像
     */
    @TableField(exist = false)
    private String uploadUserAvatar;

    /**
     * 文件扩展名
     */
    @TableField(exist = false)
    private String fileExtension;

    /**
     * 文件大小（可读格式）
     */
    @TableField(exist = false)
    private String readableFileSize;

    /**
     * 是否为图片
     */
    @TableField(exist = false)
    private Boolean isImage;

    /**
     * 图片宽度
     */
    @TableField(exist = false)
    private Integer imageWidth;

    /**
     * 图片高度
     */
    @TableField(exist = false)
    private Integer imageHeight;

    /**
     * 缩略图路径
     */
    @TableField(exist = false)
    private String thumbnailPath;

    /**
     * 缩略图URL
     */
    @TableField(exist = false)
    private String thumbnailUrl;

    // ==================== 枚举定义 ====================

    /**
     * 文件类型枚举
     */
    public enum FileType {
        IMAGE("image", "图片"),
        DOCUMENT("document", "文档"),
        VIDEO("video", "视频"),
        AUDIO("audio", "音频"),
        OTHER("other", "其他");

        private final String code;
        private final String description;

        FileType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static FileType fromCode(String code) {
            for (FileType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return OTHER;
        }
    }

    /**
     * 关联类型枚举
     */
    public enum RelatedType {
        USER_AVATAR("user_avatar", "用户头像"),
        POST_IMAGE("post_image", "帖子图片"),
        ACTIVITY_IMAGE("activity_image", "活动图片"),
        FIELD_AVATAR("field_avatar", "场地头像"),
        COMMENT_IMAGE("comment_image", "评论图片"),
        CHAT_IMAGE("chat_image", "聊天图片"),
        SYSTEM("system", "系统文件");

        private final String code;
        private final String description;

        RelatedType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static RelatedType fromCode(String code) {
            for (RelatedType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return SYSTEM;
        }
    }

    /**
     * 文件状态枚举
     */
    public enum Status {
        UPLOADING("uploading", "上传中"),
        UPLOADED("uploaded", "已上传"),
        FAILED("failed", "上传失败"),
        DELETED("deleted", "已删除");

        private final String code;
        private final String description;

        Status(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Status fromCode(String code) {
            for (Status status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取文件扩展名
     */
    public String getFileExtension() {
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 判断是否为图片
     */
    public Boolean getIsImage() {
        if (mimeType != null) {
            return mimeType.startsWith("image/");
        }
        if (fileExtension != null) {
            String ext = getFileExtension();
            return "jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) 
                   || "gif".equals(ext) || "webp".equals(ext) || "bmp".equals(ext);
        }
        return false;
    }

    /**
     * 获取可读的文件大小
     */
    public String getReadableFileSize() {
        if (fileSize == null) {
            return "0 B";
        }
        
        long size = fileSize;
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
     * 获取文件访问URL
     */
    public String getFileUrl() {
        if (filePath != null) {
            return "/uploads/" + filePath;
        }
        return "";
    }

    /**
     * 获取缩略图URL
     */
    public String getThumbnailUrl() {
        if (thumbnailPath != null) {
            return "/uploads/" + thumbnailPath;
        }
        return getFileUrl(); // 如果没有缩略图，返回原图
    }

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getUploadUserId() { return uploadUserId; }
    public void setUploadUserId(Long uploadUserId) { this.uploadUserId = uploadUserId; }

    public String getRelatedType() { return relatedType; }
    public void setRelatedType(String relatedType) { this.relatedType = relatedType; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUploadIp() { return uploadIp; }
    public void setUploadIp(String uploadIp) { this.uploadIp = uploadIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getUploadUsername() { return uploadUsername; }
    public void setUploadUsername(String uploadUsername) { this.uploadUsername = uploadUsername; }

    public String getUploadUserNickname() { return uploadUserNickname; }
    public void setUploadUserNickname(String uploadUserNickname) { this.uploadUserNickname = uploadUserNickname; }

    public String getUploadUserAvatar() { return uploadUserAvatar; }
    public void setUploadUserAvatar(String uploadUserAvatar) { this.uploadUserAvatar = uploadUserAvatar; }

    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }

    public void setReadableFileSize(String readableFileSize) { this.readableFileSize = readableFileSize; }

    public void setIsImage(Boolean isImage) { this.isImage = isImage; }

    public Integer getImageWidth() { return imageWidth; }
    public void setImageWidth(Integer imageWidth) { this.imageWidth = imageWidth; }

    public Integer getImageHeight() { return imageHeight; }
    public void setImageHeight(Integer imageHeight) { this.imageHeight = imageHeight; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}
