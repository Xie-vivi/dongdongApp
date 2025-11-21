package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.config.FileStorageConfig;
import com.xystapp.entity.FileUpload;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.FileUploadMapper;
import com.xystapp.service.FileUploadService;
import com.xystapp.utils.FileValidator;
import com.xystapp.utils.SecurityUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Autowired
    private FileUploadMapper fileUploadMapper;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Autowired
    private FileValidator fileValidator;

    // ==================== 文件上传 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUpload uploadFile(MultipartFile file, String relatedType, Long relatedId) {
        log.info("开始上传文件: originalName={}, relatedType={}, relatedId={}", 
                file.getOriginalFilename(), relatedType, relatedId);

        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 验证文件
        String category = getFileCategoryFromRelatedType(relatedType);
        FileValidator.ValidationResult validationResult = fileValidator.validateFile(
                file, category, fileStorageConfig.getMaxFileSize());
        
        if (!validationResult.isValid()) {
            throw new BusinessException(400, validationResult.getMessage());
        }

        try {
            // 生成文件名和路径
            String fileName = generateFileName(file);
            String relativePath = generateFilePath(fileName);
            String fullPath = getFullStoragePath(relativePath);

            // 确保目录存在
            createDirectoryIfNotExists(fullPath);

            // 保存文件
            file.transferTo(new File(fullPath));

            // 创建文件记录
            FileUpload fileUpload = createFileUploadRecord(file, fileName, relativePath, 
                    relatedType, relatedId, currentUserId);

            // 保存到数据库
            int result = fileUploadMapper.insert(fileUpload);
            if (result <= 0) {
                // 删除已保存的文件
                new File(fullPath).delete();
                throw new BusinessException(500, "保存文件记录失败");
            }

            // 生成缩略图（如果是图片）
            if (fileUpload.getIsImage() && fileStorageConfig.getThumbnailEnabled()) {
                generateThumbnail(fileUpload.getId());
            }

            // 压缩图片（如果需要）
            if (fileUpload.getIsImage() && fileStorageConfig.getImageCompressEnabled()) {
                compressImage(fileUpload.getId(), 0.8f);
            }

            log.info("文件上传成功: fileId={}, fileName={}", fileUpload.getId(), fileName);
            return fileUpload;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(500, "文件上传失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUpload uploadUserAvatar(MultipartFile file, Long userId) {
        // 验证权限：用户只能上传自己的头像，管理员可以上传任何人的头像
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(userId) && !SecurityUtils.isAdmin()) {
            throw new BusinessException(403, "没有权限上传该用户头像");
        }

        // 删除旧头像
        FileUpload oldAvatar = getUserAvatar(userId);
        if (oldAvatar != null) {
            deleteFile(oldAvatar.getId(), currentUserId);
        }

        return uploadFile(file, "user_avatar", userId);
    }

    @Override
    public FileUpload uploadPostImage(MultipartFile file, Long postId) {
        // TODO: 验证帖子权限
        return uploadFile(file, "post_image", postId);
    }

    @Override
    public FileUpload uploadActivityImage(MultipartFile file, Long activityId) {
        // TODO: 验证活动权限
        return uploadFile(file, "activity_image", activityId);
    }

    @Override
    public FileUpload uploadFieldAvatar(MultipartFile file, Long fieldId) {
        // TODO: 验证场地权限
        return uploadFile(file, "field_avatar", fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileUpload> uploadFiles(MultipartFile[] files, String relatedType, Long relatedId) {
        if (files == null || files.length == 0) {
            throw new BusinessException(400, "请选择要上传的文件");
        }

        List<FileUpload> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                uploadedFiles.add(uploadFile(file, relatedType, relatedId));
            }
        }

        return uploadedFiles;
    }

    // ==================== 文件查询 ====================

    @Override
    public FileUpload getFileById(Long fileId) {
        if (fileId == null) {
            throw new BusinessException(400, "文件ID不能为空");
        }

        FileUpload fileUpload = fileUploadMapper.selectById(fileId);
        if (fileUpload == null) {
            throw new BusinessException(404, "文件不存在");
        }

        return fileUpload;
    }

    @Override
    public FileUpload getFileDetail(Long fileId) {
        FileUpload fileUpload = fileUploadMapper.selectFileDetail(fileId);
        if (fileUpload == null) {
            throw new BusinessException(404, "文件不存在");
        }

        return fileUpload;
    }

    @Override
    public IPage<FileUpload> getUserFiles(Long userId, Integer page, Integer size, String fileType) {
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Page<FileUpload> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        return fileUploadMapper.selectUserFiles(pageParam, userId, fileType);
    }

    @Override
    public IPage<FileUpload> getRelatedFiles(String relatedType, Long relatedId, Integer page, Integer size) {
        Page<FileUpload> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        return fileUploadMapper.selectRelatedFiles(pageParam, relatedType, relatedId);
    }

    @Override
    public IPage<FileUpload> getFileList(Integer page, Integer size, String keyword, String fileType, String status) {
        // 只有管理员可以查看所有文件
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException(403, "没有权限查看文件列表");
        }

        Page<FileUpload> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        return fileUploadMapper.selectFileList(pageParam, keyword, fileType, status);
    }

    // ==================== 文件管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long operatorId) {
        FileUpload fileUpload = getFileById(fileId);

        // 权限检查
        if (!hasFileDeletePermission(fileId, operatorId)) {
            throw new BusinessException(403, "没有权限删除该文件");
        }

        try {
            // 删除物理文件
            String fullPath = getFullStoragePath(fileUpload.getFilePath());
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }

            // 删除缩略图
            if (fileUpload.getThumbnailPath() != null) {
                String thumbnailPath = getFullStoragePath(fileUpload.getThumbnailPath());
                File thumbnailFile = new File(thumbnailPath);
                if (thumbnailFile.exists()) {
                    thumbnailFile.delete();
                }
            }

            // 更新数据库状态
            fileUpload.setStatus(FileUpload.Status.DELETED.getCode());
            int result = fileUploadMapper.updateById(fileUpload);

            log.info("文件删除成功: fileId={}, fileName={}", fileId, fileUpload.getFileName());
            return result > 0;

        } catch (Exception e) {
            log.error("文件删除失败: fileId={}", fileId, e);
            throw new BusinessException(500, "文件删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFiles(List<Long> fileIds, Long operatorId) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BusinessException(400, "文件ID列表不能为空");
        }

        boolean allSuccess = true;
        for (Long fileId : fileIds) {
            try {
                deleteFile(fileId, operatorId);
            } catch (Exception e) {
                log.error("批量删除文件失败: fileId={}", fileId, e);
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUpload updateFileRelation(Long fileId, String relatedType, Long relatedId) {
        FileUpload fileUpload = getFileById(fileId);

        // 权限检查
        if (!hasFileManagePermission(fileId, SecurityUtils.getCurrentUserId())) {
            throw new BusinessException(403, "没有权限修改该文件");
        }

        fileUpload.setRelatedType(relatedType);
        fileUpload.setRelatedId(relatedId);
        fileUploadMapper.updateById(fileUpload);

        return fileUpload;
    }

    @Override
    public FileUpload moveFile(Long fileId, String newRelatedType, Long newRelatedId, Long operatorId) {
        return updateFileRelation(fileId, newRelatedType, newRelatedId);
    }

    // ==================== 文件下载 ====================

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) {
        FileUpload fileUpload = getFileById(fileId);

        // 权限检查
        if (!hasFileAccess(fileId, SecurityUtils.getCurrentUserId())) {
            throw new BusinessException(403, "没有权限访问该文件");
        }

        try {
            String fullPath = getFullStoragePath(fileUpload.getFilePath());
            File file = new File(fullPath);

            if (!file.exists()) {
                throw new BusinessException(404, "文件不存在");
            }

            // 设置响应头
            response.setContentType(fileUpload.getMimeType());
            response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + fileUpload.getOriginalName() + "\"");
            response.setContentLengthLong(fileUpload.getFileSize());

            // 写入响应流
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

        } catch (IOException e) {
            log.error("文件下载失败: fileId={}", fileId, e);
            throw new BusinessException(500, "文件下载失败");
        }
    }

    @Override
    public InputStream getFileStream(Long fileId) {
        FileUpload fileUpload = getFileById(fileId);

        // 权限检查
        if (!hasFileAccess(fileId, SecurityUtils.getCurrentUserId())) {
            throw new BusinessException(403, "没有权限访问该文件");
        }

        try {
            String fullPath = getFullStoragePath(fileUpload.getFilePath());
            return new FileInputStream(fullPath);
        } catch (FileNotFoundException e) {
            throw new BusinessException(404, "文件不存在");
        }
    }

    @Override
    public byte[] getFileBytes(Long fileId) {
        try (InputStream is = getFileStream(fileId);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "读取文件失败");
        }
    }

    // ==================== 图片处理 ====================

    @Override
    public FileUpload generateThumbnail(Long fileId) {
        FileUpload fileUpload = getFileById(fileId);

        if (!fileUpload.getIsImage()) {
            throw new BusinessException(400, "该文件不是图片，无法生成缩略图");
        }

        try {
            String originalPath = getFullStoragePath(fileUpload.getFilePath());
            String thumbnailRelativePath = fileStorageConfig.getThumbnailPath(fileUpload.getFilePath());
            String thumbnailFullPath = getFullStoragePath(thumbnailRelativePath);

            // 确保目录存在
            createDirectoryIfNotExists(thumbnailFullPath);

            // 生成缩略图
            Thumbnails.of(originalPath)
                    .size(fileStorageConfig.getThumbnailWidth(), fileStorageConfig.getThumbnailHeight())
                    .keepAspectRatio(true)
                    .toFile(thumbnailFullPath);

            // 更新记录
            fileUpload.setThumbnailPath(thumbnailRelativePath);
            fileUploadMapper.updateById(fileUpload);

            log.info("缩略图生成成功: fileId={}, thumbnailPath={}", fileId, thumbnailRelativePath);
            return fileUpload;

        } catch (IOException e) {
            log.error("缩略图生成失败: fileId={}", fileId, e);
            throw new BusinessException(500, "缩略图生成失败");
        }
    }

    @Override
    public FileUpload getImageInfo(Long fileId) {
        FileUpload fileUpload = getFileDetail(fileId);
        
        if (!fileUpload.getIsImage()) {
            throw new BusinessException(400, "该文件不是图片");
        }

        // TODO: 使用ImageIO读取图片信息
        return fileUpload;
    }

    @Override
    public FileUpload cropImage(Long fileId, Integer x, Integer y, Integer width, Integer height) {
        // TODO: 实现图片裁剪
        throw new BusinessException(501, "功能暂未实现");
    }

    @Override
    public FileUpload compressImage(Long fileId, Float quality) {
        FileUpload fileUpload = getFileById(fileId);

        if (!fileUpload.getIsImage()) {
            throw new BusinessException(400, "该文件不是图片，无法压缩");
        }

        try {
            String originalPath = getFullStoragePath(fileUpload.getFilePath());
            String compressedRelativePath = generateCompressedFileName(fileUpload.getFilePath());
            String compressedFullPath = getFullStoragePath(compressedRelativePath);

            // 确保目录存在
            createDirectoryIfNotExists(compressedFullPath);

            // 压缩图片
            Thumbnails.of(originalPath)
                    .outputQuality(quality != null ? quality : fileStorageConfig.getImageCompressQuality())
                    .toFile(compressedFullPath);

            // 更新记录
            fileUpload.setFilePath(compressedRelativePath);
            fileUploadMapper.updateById(fileUpload);

            log.info("图片压缩成功: fileId={}, quality={}", fileId, quality);
            return fileUpload;

        } catch (IOException e) {
            log.error("图片压缩失败: fileId={}", fileId, e);
            throw new BusinessException(500, "图片压缩失败");
        }
    }

    // ==================== 权限验证 ====================

    @Override
    public boolean hasFileAccess(Long fileId, Long userId) {
        FileUpload fileUpload = fileUploadMapper.selectById(fileId);
        if (fileUpload == null) {
            return false;
        }

        // 用户可以访问自己的文件
        if (fileUpload.getUploadUserId().equals(userId)) {
            return true;
        }

        // 管理员可以访问所有文件
        if (SecurityUtils.isAdmin()) {
            return true;
        }

        // 公开文件（如用户头像、帖子图片等）可以访问
        return isPublicFile(fileUpload);
    }

    @Override
    public boolean hasFileDeletePermission(Long fileId, Long userId) {
        FileUpload fileUpload = fileUploadMapper.selectById(fileId);
        if (fileUpload == null) {
            return false;
        }

        // 用户可以删除自己的文件
        if (fileUpload.getUploadUserId().equals(userId)) {
            return true;
        }

        // 管理员可以删除所有文件
        return SecurityUtils.isAdmin();
    }

    @Override
    public boolean hasFileManagePermission(Long fileId, Long userId) {
        return hasFileDeletePermission(fileId, userId);
    }

    // ==================== 文件验证 ====================

    @Override
    public boolean validateFileType(MultipartFile file, String... allowedTypes) {
        String extension = fileValidator.getFileExtension(file.getOriginalFilename());
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateFileSize(MultipartFile file, Long maxSize) {
        return file.getSize() <= maxSize;
    }

    @Override
    public boolean validateImageDimensions(MultipartFile file, Integer maxWidth, Integer maxHeight) {
        // TODO: 实现图片尺寸验证
        return true;
    }

    @Override
    public boolean validateFileContent(MultipartFile file) {
        String extension = fileValidator.getFileExtension(file.getOriginalFilename());
        return fileValidator.validateFileContent(file, extension);
    }

    // ==================== 统计分析 ====================

    @Override
    public UserFileStats getUserFileStats(Long userId) {
        Long totalFiles = fileUploadMapper.countUserFiles(userId, null);
        Long imageFiles = fileUploadMapper.countUserFiles(userId, "image");
        Long documentFiles = fileUploadMapper.countUserFiles(userId, "document");
        Long totalSize = fileUploadMapper.sumUserFileSize(userId, null);
        Long todayUploads = fileUploadMapper.countTodayUploads(userId);

        return new UserFileStats(totalFiles, imageFiles, documentFiles, totalSize, todayUploads);
    }

    @Override
    public SystemFileStats getSystemFileStats() {
        Long totalFiles = fileUploadMapper.countSystemFiles(null, "uploaded");
        Long totalSize = fileUploadMapper.sumSystemFileSize(null, "uploaded");
        Long todayUploads = fileUploadMapper.countTodayUploads(null);
        Long activeFiles = fileUploadMapper.countSystemFiles(null, "uploaded");
        Long deletedFiles = fileUploadMapper.countSystemFiles(null, "deleted");

        return new SystemFileStats(totalFiles, totalSize, todayUploads, activeFiles, deletedFiles);
    }

    @Override
    public List<FileTypeStats> getFileTypeStats() {
        List<FileUploadMapper.FileTypeStats> mapperStats = fileUploadMapper.selectFileTypeStats();
        return mapperStats.stream()
                .map(stat -> new FileTypeStats(stat.getFileType(), "文件类型", stat.getCount(), stat.getTotalSize()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<FileUploadTrendStats> getFileUploadTrend(Integer days) {
        List<FileUploadMapper.FileUploadTrendStats> mapperStats = fileUploadMapper.selectFileUploadTrend(days != null ? days : 7);
        return mapperStats.stream()
                .map(stat -> new FileUploadTrendStats(stat.getDate(), stat.getUploadCount(), stat.getTotalSize()))
                .collect(java.util.stream.Collectors.toList());
    }

    // ==================== 存储管理 ====================

    @Override
    public StorageUsageStats getStorageUsageStats() {
        FileUploadMapper.StorageUsageStats stats = fileUploadMapper.selectStorageUsageStats();
        return new StorageUsageStats(
                stats.getTotalFiles(),
                stats.getTotalSize(),
                stats.getTodayUploads(),
                (float) stats.getActiveFiles() / stats.getTotalFiles()
        );
    }

    @Override
    public int cleanupInvalidFiles() {
        return fileUploadMapper.cleanupInvalidFiles();
    }

    @Override
    public int cleanupTempFiles() {
        return fileUploadMapper.cleanupTempFiles(fileStorageConfig.getTempFileCleanupHours());
    }

    @Override
    public boolean backupFile(Long fileId) {
        // TODO: 实现文件备份
        throw new BusinessException(501, "功能暂未实现");
    }

    @Override
    public boolean restoreFile(Long fileId) {
        // TODO: 实现文件恢复
        throw new BusinessException(501, "功能暂未实现");
    }

    // ==================== 业务查询 ====================

    @Override
    public FileUpload getUserAvatar(Long userId) {
        return fileUploadMapper.selectUserAvatar(userId);
    }

    @Override
    public List<FileUpload> getPostImages(Long postId) {
        return fileUploadMapper.selectPostImages(postId);
    }

    @Override
    public List<FileUpload> getActivityImages(Long activityId) {
        return fileUploadMapper.selectActivityImages(activityId);
    }

    @Override
    public FileUpload getFieldAvatar(Long fieldId) {
        return fileUploadMapper.selectFieldAvatar(fieldId);
    }

    @Override
    public List<FileUpload> getRecentFiles(Long userId, Integer limit) {
        return fileUploadMapper.selectRecentFiles(userId, limit);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 生成文件名
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = fileValidator.getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        return uuid + "_" + timestamp + "." + extension;
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String fileName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return datePath + "/" + fileName;
    }

    /**
     * 获取完整存储路径
     */
    private String getFullStoragePath(String relativePath) {
        return fileStorageConfig.getFullStoragePath() + "/" + relativePath;
    }

    /**
     * 创建目录
     */
    private void createDirectoryIfNotExists(String filePath) {
        try {
            Path path = Paths.get(filePath).getParent();
            if (path != null && !Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new BusinessException(500, "创建目录失败：" + e.getMessage());
        }
    }

    /**
     * 创建文件上传记录
     */
    private FileUpload createFileUploadRecord(MultipartFile file, String fileName, 
                                            String relativePath, String relatedType, 
                                            Long relatedId, Long userId) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setOriginalName(file.getOriginalFilename());
        fileUpload.setFileName(fileName);
        fileUpload.setFilePath(relativePath);
        fileUpload.setFileSize(file.getSize());
        fileUpload.setFileType(getFileTypeFromFile(file));
        fileUpload.setMimeType(file.getContentType());
        fileUpload.setUploadUserId(userId);
        fileUpload.setRelatedType(relatedType);
        fileUpload.setRelatedId(relatedId);
        fileUpload.setStatus(FileUpload.Status.UPLOADED.getCode());
        fileUpload.setUploadIp(SecurityUtils.getClientIp());
        fileUpload.setUserAgent(SecurityUtils.getUserAgent());

        return fileUpload;
    }

    /**
     * 根据关联类型获取文件分类
     */
    private String getFileCategoryFromRelatedType(String relatedType) {
        switch (relatedType) {
            case "user_avatar":
            case "field_avatar":
                return "image";
            case "post_image":
            case "activity_image":
            case "comment_image":
            case "chat_image":
                return "image";
            default:
                return "other";
        }
    }

    /**
     * 根据文件获取文件类型
     */
    private String getFileTypeFromFile(MultipartFile file) {
        String extension = fileValidator.getFileExtension(file.getOriginalFilename());
        return fileValidator.getFileCategory(extension);
    }

    /**
     * 判断是否为公开文件
     */
    private boolean isPublicFile(FileUpload fileUpload) {
        return "user_avatar".equals(fileUpload.getRelatedType()) ||
               "post_image".equals(fileUpload.getRelatedType()) ||
               "activity_image".equals(fileUpload.getRelatedType()) ||
               "field_avatar".equals(fileUpload.getRelatedType());
    }

    /**
     * 生成压缩文件名
     */
    private String generateCompressedFileName(String originalPath) {
        String directory = originalPath.substring(0, originalPath.lastIndexOf("/"));
        String fileName = originalPath.substring(originalPath.lastIndexOf("/") + 1);
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        
        return directory + "/compressed_" + nameWithoutExt + extension;
    }
}
