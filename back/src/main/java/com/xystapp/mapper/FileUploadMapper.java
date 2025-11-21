package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.FileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件上传Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface FileUploadMapper extends BaseMapper<FileUpload> {

    /**
     * 根据ID获取文件详情（包含用户信息）
     */
    FileUpload selectFileDetail(@Param("fileId") Long fileId);

    /**
     * 获取用户上传的文件列表（包含用户信息）
     */
    IPage<FileUpload> selectUserFiles(Page<FileUpload> page, @Param("userId") Long userId, 
                                      @Param("fileType") String fileType);

    /**
     * 获取关联类型的文件列表（包含用户信息）
     */
    IPage<FileUpload> selectRelatedFiles(Page<FileUpload> page, @Param("relatedType") String relatedType, 
                                         @Param("relatedId") Long relatedId);

    /**
     * 获取文件列表（管理员用，包含用户信息）
     */
    IPage<FileUpload> selectFileList(Page<FileUpload> page, @Param("keyword") String keyword, 
                                     @Param("fileType") String fileType, @Param("status") String status);

    // ==================== 统计相关 ====================

    /**
     * 统计用户文件数量
     */
    Long countUserFiles(@Param("userId") Long userId, @Param("fileType") String fileType);

    /**
     * 统计用户文件总大小
     */
    Long sumUserFileSize(@Param("userId") Long userId, @Param("fileType") String fileType);

    /**
     * 统计系统文件数量
     */
    Long countSystemFiles(@Param("fileType") String fileType, @Param("status") String status);

    /**
     * 统计系统文件总大小
     */
    Long sumSystemFileSize(@Param("fileType") String fileType, @Param("status") String status);

    /**
     * 统计今日上传数量
     */
    Long countTodayUploads(@Param("userId") Long userId);

    /**
     * 统计今日上传大小
     */
    Long sumTodayUploadSize(@Param("userId") Long userId);

    /**
     * 按文件类型统计
     */
    List<FileTypeStats> selectFileTypeStats();

    /**
     * 获取文件上传统计
     */
    List<FileUploadTrendStats> selectFileUploadTrend(@Param("days") Integer days);

    // ==================== 业务查询 ====================

    /**
     * 获取用户头像
     */
    FileUpload selectUserAvatar(@Param("userId") Long userId);

    /**
     * 获取帖子图片列表
     */
    List<FileUpload> selectPostImages(@Param("postId") Long postId);

    /**
     * 获取活动图片列表
     */
    List<FileUpload> selectActivityImages(@Param("activityId") Long activityId);

    /**
     * 获取场地头像
     */
    FileUpload selectFieldAvatar(@Param("fieldId") Long fieldId);

    /**
     * 获取最近上传的文件
     */
    List<FileUpload> selectRecentFiles(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据文件路径获取文件信息
     */
    FileUpload selectByFilePath(@Param("filePath") String filePath);

    /**
     * 获取用户指定类型的文件
     */
    List<FileUpload> selectUserFilesByType(@Param("userId") Long userId, @Param("relatedType") String relatedType);

    // ==================== 批量操作 ====================

    /**
     * 批量获取文件信息
     */
    List<FileUpload> selectFilesByIds(@Param("fileIds") List<Long> fileIds);

    /**
     * 批量获取用户文件
     */
    List<FileUpload> selectUserFilesByIds(@Param("fileIds") List<Long> fileIds, @Param("userId") Long userId);

    /**
     * 根据关联ID删除文件记录
     */
    int deleteByRelatedId(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);

    /**
     * 根据用户ID删除文件记录
     */
    int deleteByUserId(@Param("userId") Long userId);

    // ==================== 数据维护 ====================

    /**
     * 清理无效文件记录（文件不存在）
     */
    int cleanupInvalidFiles();

    /**
     * 清理临时文件记录
     */
    int cleanupTempFiles(@Param("hours") Integer hours);

    /**
     * 获取需要清理的文件数量
     */
    Long countFilesToCleanup(@Param("days") Integer days);

    /**
     * 获取孤立文件记录（用户不存在）
     */
    List<FileUpload> selectOrphanedFiles();

    // ==================== 存储相关 ====================

    /**
     * 获取存储使用统计
     */
    StorageUsageStats selectStorageUsageStats();

    /**
     * 获取用户存储使用统计
     */
    List<UserStorageStats> selectUserStorageStats(@Param("limit") Integer limit);

    /**
     * 获取热门文件统计
     */
    List<FileUpload> selectPopularFiles(@Param("limit") Integer limit);

    // ==================== 时间相关查询 ====================

    /**
     * 获取指定时间段内的文件
     */
    List<FileUpload> selectFilesByTimeRange(@Param("startTime") java.time.LocalDateTime startTime,
                                           @Param("endTime") java.time.LocalDateTime endTime,
                                           @Param("fileType") String fileType);

    /**
     * 获取用户指定时间段内的文件
     */
    List<FileUpload> selectUserFilesByTimeRange(@Param("userId") Long userId,
                                               @Param("startTime") java.time.LocalDateTime startTime,
                                               @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取即将过期的文件
     */
    List<FileUpload> selectExpiringFiles(@Param("days") Integer days);

    // ==================== 权限相关 ====================

    /**
     * 检查用户是否拥有文件
     */
    boolean isUserFile(@Param("fileId") Long fileId, @Param("userId") Long userId);

    /**
     * 获取用户可访问的文件ID列表
     */
    List<Long> selectUserAccessibleFileIds(@Param("userId") Long userId);

    /**
     * 获取用户管理的文件ID列表
     */
    List<Long> selectUserManagedFileIds(@Param("userId") Long userId);

    // ==================== 统计类定义 ====================

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
     * 存储使用统计类
     */
    class StorageUsageStats {
        private Long totalFiles;
        private Long totalSize;
        private Long todayUploads;
        private Long activeFiles;
        private Long deletedFiles;

        public StorageUsageStats(Long totalFiles, Long totalSize, Long todayUploads, 
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
     * 用户存储统计类
     */
    class UserStorageStats {
        private Long userId;
        private String username;
        private String nickname;
        private Long fileCount;
        private Long totalSize;

        public UserStorageStats(Long userId, String username, String nickname, 
                              Long fileCount, Long totalSize) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
            this.fileCount = fileCount;
            this.totalSize = totalSize;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public Long getFileCount() { return fileCount; }
        public Long getTotalSize() { return totalSize; }
    }
}
