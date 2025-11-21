package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件上传服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface FileUploadService {

    // ==================== 文件上传 ====================

    /**
     * 上传单个文件
     */
    FileUpload uploadFile(MultipartFile file, String relatedType, Long relatedId);

    /**
     * 上传用户头像
     */
    FileUpload uploadUserAvatar(MultipartFile file, Long userId);

    /**
     * 上传帖子图片
     */
    FileUpload uploadPostImage(MultipartFile file, Long postId);

    /**
     * 上传活动图片
     */
    FileUpload uploadActivityImage(MultipartFile file, Long activityId);

    /**
     * 上传场地头像
     */
    FileUpload uploadFieldAvatar(MultipartFile file, Long fieldId);

    /**
     * 批量上传文件
     */
    List<FileUpload> uploadFiles(MultipartFile[] files, String relatedType, Long relatedId);

    // ==================== 文件查询 ====================

    /**
     * 根据ID获取文件信息
     */
    FileUpload getFileById(Long fileId);

    /**
     * 获取文件详情（包含用户信息）
     */
    FileUpload getFileDetail(Long fileId);

    /**
     * 获取用户上传的文件列表
     */
    IPage<FileUpload> getUserFiles(Long userId, Integer page, Integer size, String fileType);

    /**
     * 获取关联类型的文件列表
     */
    IPage<FileUpload> getRelatedFiles(String relatedType, Long relatedId, Integer page, Integer size);

    /**
     * 获取文件列表（管理员用）
     */
    IPage<FileUpload> getFileList(Integer page, Integer size, String keyword, String fileType, String status);

    // ==================== 文件管理 ====================

    /**
     * 删除文件
     */
    boolean deleteFile(Long fileId, Long operatorId);

    /**
     * 批量删除文件
     */
    boolean batchDeleteFiles(List<Long> fileIds, Long operatorId);

    /**
     * 更新文件关联信息
     */
    FileUpload updateFileRelation(Long fileId, String relatedType, Long relatedId);

    /**
     * 移动文件到其他关联
     */
    FileUpload moveFile(Long fileId, String newRelatedType, Long newRelatedId, Long operatorId);

    // ==================== 文件下载 ====================

    /**
     * 下载文件
     */
    void downloadFile(Long fileId, HttpServletResponse response);

    /**
     * 获取文件流
     */
    java.io.InputStream getFileStream(Long fileId);

    /**
     * 获取文件字节数组
     */
    byte[] getFileBytes(Long fileId);

    // ==================== 图片处理 ====================

    /**
     * 生成缩略图
     */
    FileUpload generateThumbnail(Long fileId);

    /**
     * 获取图片信息（宽高、尺寸等）
     */
    FileUpload getImageInfo(Long fileId);

    /**
     * 裁剪图片
     */
    FileUpload cropImage(Long fileId, Integer x, Integer y, Integer width, Integer height);

    /**
     * 压缩图片
     */
    FileUpload compressImage(Long fileId, Float quality);

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否有权限访问文件
     */
    boolean hasFileAccess(Long fileId, Long userId);

    /**
     * 检查用户是否有权限删除文件
     */
    boolean hasFileDeletePermission(Long fileId, Long userId);

    /**
     * 检查用户是否有权限管理文件
     */
    boolean hasFileManagePermission(Long fileId, Long userId);

    // ==================== 文件验证 ====================

    /**
     * 验证文件类型
     */
    boolean validateFileType(MultipartFile file, String... allowedTypes);

    /**
     * 验证文件大小
     */
    boolean validateFileSize(MultipartFile file, Long maxSize);

    /**
     * 验证图片尺寸
     */
    boolean validateImageDimensions(MultipartFile file, Integer maxWidth, Integer maxHeight);

    /**
     * 验证文件内容
     */
    boolean validateFileContent(MultipartFile file);

    // ==================== 统计分析 ====================

    /**
     * 获取用户文件统计
     */
    UserFileStats getUserFileStats(Long userId);

    /**
     * 获取系统文件统计
     */
    SystemFileStats getSystemFileStats();

    /**
     * 获取文件类型统计
     */
    List<FileTypeStats> getFileTypeStats();

    /**
     * 获取文件上传统计
     */
    List<FileUploadTrendStats> getFileUploadTrend(Integer days);

    // ==================== 存储管理 ====================

    /**
     * 获取存储使用情况
     */
    StorageUsageStats getStorageUsageStats();

    /**
     * 清理无效文件
     */
    int cleanupInvalidFiles();

    /**
     * 清理临时文件
     */
    int cleanupTempFiles();

    /**
     * 备份文件
     */
    boolean backupFile(Long fileId);

    /**
     * 恢复文件
     */
    boolean restoreFile(Long fileId);

    // ==================== 业务查询 ====================

    /**
     * 获取用户头像
     */
    FileUpload getUserAvatar(Long userId);

    /**
     * 获取帖子图片列表
     */
    List<FileUpload> getPostImages(Long postId);

    /**
     * 获取活动图片列表
     */
    List<FileUpload> getActivityImages(Long activityId);

    /**
     * 获取场地头像
     */
    FileUpload getFieldAvatar(Long fieldId);

    /**
     * 获取最近上传的文件
     */
    List<FileUpload> getRecentFiles(Long userId, Integer limit);

    // ==================== 统计类定义 ====================

    /**
     * 用户文件统计类
     */
    class UserFileStats {
        private Long totalFiles;
        private Long imageFiles;
        private Long documentFiles;
        private Long totalSize;
        private Long todayUploads;

        public UserFileStats(Long totalFiles, Long imageFiles, Long documentFiles, 
                           Long totalSize, Long todayUploads) {
            this.totalFiles = totalFiles;
            this.imageFiles = imageFiles;
            this.documentFiles = documentFiles;
            this.totalSize = totalSize;
            this.todayUploads = todayUploads;
        }

        public Long getTotalFiles() { return totalFiles; }
        public Long getImageFiles() { return imageFiles; }
        public Long getDocumentFiles() { return documentFiles; }
        public Long getTotalSize() { return totalSize; }
        public Long getTodayUploads() { return todayUploads; }
    }

    /**
     * 系统文件统计类
     */
    class SystemFileStats {
        private Long totalFiles;
        private Long totalSize;
        private Long todayUploads;
        private Long activeFiles;
        private Long deletedFiles;

        public SystemFileStats(Long totalFiles, Long totalSize, Long todayUploads, 
                             Long activeFiles, Long deletedFiles) {
            this.totalFiles = totalFiles;
            this.totalSize = totalSize;
            this.todayUploads = todayUploads;
            this.activeFiles = activeFiles;
            this.deletedFiles = deletedFiles;
        }

        public Long getTotalFiles() { return totalFiles; }
        public Long getTotalSize() { return totalSize; }
        public Long getTodayUploads() { return todayUploads; }
        public Long getActiveFiles() { return activeFiles; }
        public Long getDeletedFiles() { return deletedFiles; }
    }

    /**
     * 文件类型统计类
     */
    class FileTypeStats {
        private String fileType;
        private String description;
        private Long count;
        private Long totalSize;

        public FileTypeStats(String fileType, String description, Long count, Long totalSize) {
            this.fileType = fileType;
            this.description = description;
            this.count = count;
            this.totalSize = totalSize;
        }

        public String getFileType() { return fileType; }
        public String getDescription() { return description; }
        public Long getCount() { return count; }
        public Long getTotalSize() { return totalSize; }
    }

    /**
     * 文件上传统计类
     */
    class FileUploadTrendStats {
        private String date;
        private Long uploadCount;
        private Long totalSize;

        public FileUploadTrendStats(String date, Long uploadCount, Long totalSize) {
            this.date = date;
            this.uploadCount = uploadCount;
            this.totalSize = totalSize;
        }

        public String getDate() { return date; }
        public Long getUploadCount() { return uploadCount; }
        public Long getTotalSize() { return totalSize; }
    }

    /**
     * 存储使用情况统计类
     */
    class StorageUsageStats {
        private Long totalStorage;
        private Long usedStorage;
        private Long availableStorage;
        private Float usagePercentage;

        public StorageUsageStats(Long totalStorage, Long usedStorage, 
                               Long availableStorage, Float usagePercentage) {
            this.totalStorage = totalStorage;
            this.usedStorage = usedStorage;
            this.availableStorage = availableStorage;
            this.usagePercentage = usagePercentage;
        }

        public Long getTotalStorage() { return totalStorage; }
        public Long getUsedStorage() { return usedStorage; }
        public Long getAvailableStorage() { return availableStorage; }
        public Float getUsagePercentage() { return usagePercentage; }
    }
}
