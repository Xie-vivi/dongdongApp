package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.FileUpload;
import com.xystapp.service.FileUploadService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文件上传控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;

    // ==================== 文件上传 ====================

    /**
     * 上传单个文件
     */
    @ApiOperation("上传单个文件")
    @PostMapping("/upload")
    public Result<FileUpload> uploadFile(
            @ApiParam("文件") @RequestParam("file") @NotNull MultipartFile file,
            @ApiParam("关联类型") @RequestParam(required = false, defaultValue = "system") String relatedType,
            @ApiParam("关联ID") @RequestParam(required = false) Long relatedId) {
        
        log.info("用户上传文件: originalName={}, relatedType={}, relatedId={}", 
                file.getOriginalFilename(), relatedType, relatedId);

        FileUpload uploadedFile = fileUploadService.uploadFile(file, relatedType, relatedId);
        return Result.success(uploadedFile);
    }

    /**
     * 批量上传文件
     */
    @ApiOperation("批量上传文件")
    @PostMapping("/upload/batch")
    public Result<List<FileUpload>> uploadFiles(
            @ApiParam("文件列表") @RequestParam("files") @NotNull MultipartFile[] files,
            @ApiParam("关联类型") @RequestParam(required = false, defaultValue = "system") String relatedType,
            @ApiParam("关联ID") @RequestParam(required = false) Long relatedId) {
        
        log.info("用户批量上传文件: count={}, relatedType={}, relatedId={}", 
                files.length, relatedType, relatedId);

        List<FileUpload> uploadedFiles = fileUploadService.uploadFiles(files, relatedType, relatedId);
        return Result.success(uploadedFiles);
    }

    /**
     * 上传用户头像
     */
    @ApiOperation("上传用户头像")
    @PostMapping("/upload/avatar")
    public Result<FileUpload> uploadUserAvatar(
            @ApiParam("头像文件") @RequestParam("avatar") @NotNull MultipartFile file,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        
        Long targetUserId = userId != null ? userId : SecurityUtils.getCurrentUserId();
        log.info("用户上传头像: originalName={}, userId={}", file.getOriginalFilename(), targetUserId);

        FileUpload uploadedFile = fileUploadService.uploadUserAvatar(file, targetUserId);
        return Result.success(uploadedFile);
    }

    /**
     * 上传帖子图片
     */
    @ApiOperation("上传帖子图片")
    @PostMapping("/upload/post")
    public Result<FileUpload> uploadPostImage(
            @ApiParam("图片文件") @RequestParam("image") @NotNull MultipartFile file,
            @ApiParam("帖子ID") @RequestParam @NotNull Long postId) {
        
        log.info("用户上传帖子图片: originalName={}, postId={}", file.getOriginalFilename(), postId);

        FileUpload uploadedFile = fileUploadService.uploadPostImage(file, postId);
        return Result.success(uploadedFile);
    }

    /**
     * 上传活动图片
     */
    @ApiOperation("上传活动图片")
    @PostMapping("/upload/activity")
    public Result<FileUpload> uploadActivityImage(
            @ApiParam("图片文件") @RequestParam("image") @NotNull MultipartFile file,
            @ApiParam("活动ID") @RequestParam @NotNull Long activityId) {
        
        log.info("用户上传活动图片: originalName={}, activityId={}", file.getOriginalFilename(), activityId);

        FileUpload uploadedFile = fileUploadService.uploadActivityImage(file, activityId);
        return Result.success(uploadedFile);
    }

    /**
     * 上传场地头像
     */
    @ApiOperation("上传场地头像")
    @PostMapping("/upload/field")
    public Result<FileUpload> uploadFieldAvatar(
            @ApiParam("头像文件") @RequestParam("avatar") @NotNull MultipartFile file,
            @ApiParam("场地ID") @RequestParam @NotNull Long fieldId) {
        
        log.info("用户上传场地头像: originalName={}, fieldId={}", file.getOriginalFilename(), fieldId);

        FileUpload uploadedFile = fileUploadService.uploadFieldAvatar(file, fieldId);
        return Result.success(uploadedFile);
    }

    // ==================== 文件查询 ====================

    /**
     * 获取文件详情
     */
    @ApiOperation("获取文件详情")
    @GetMapping("/{fileId}")
    public Result<FileUpload> getFileDetail(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId) {
        
        log.info("获取文件详情: fileId={}", fileId);

        FileUpload fileUpload = fileUploadService.getFileDetail(fileId);
        return Result.success(fileUpload);
    }

    /**
     * 获取用户文件列表
     */
    @ApiOperation("获取用户文件列表")
    @GetMapping("/user/{userId}")
    public Result<IPage<FileUpload>> getUserFiles(
            @ApiParam("用户ID") @PathVariable @NotNull Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("文件类型") @RequestParam(required = false) String fileType) {
        
        log.info("获取用户文件列表: userId={}, page={}, size={}, fileType={}", 
                userId, page, size, fileType);

        // 权限检查：用户只能查看自己的文件，管理员可以查看所有用户的文件
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(userId) && !SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看该用户文件");
        }

        IPage<FileUpload> filePage = fileUploadService.getUserFiles(userId, page, size, fileType);
        return Result.success(filePage);
    }

    /**
     * 获取关联文件列表
     */
    @ApiOperation("获取关联文件列表")
    @GetMapping("/related")
    public Result<IPage<FileUpload>> getRelatedFiles(
            @ApiParam("关联类型") @RequestParam @NotNull String relatedType,
            @ApiParam("关联ID") @RequestParam(required = false) Long relatedId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取关联文件列表: relatedType={}, relatedId={}, page={}, size={}", 
                relatedType, relatedId, page, size);

        IPage<FileUpload> filePage = fileUploadService.getRelatedFiles(relatedType, relatedId, page, size);
        return Result.success(filePage);
    }

    /**
     * 获取文件列表（管理员用）
     */
    @ApiOperation("获取文件列表（管理员用）")
    @GetMapping("/list")
    public Result<IPage<FileUpload>> getFileList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("文件类型") @RequestParam(required = false) String fileType,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        
        log.info("获取文件列表: page={}, size={}, keyword={}, fileType={}, status={}", 
                page, size, keyword, fileType, status);

        IPage<FileUpload> filePage = fileUploadService.getFileList(page, size, keyword, fileType, status);
        return Result.success(filePage);
    }

    // ==================== 文件管理 ====================

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/{fileId}")
    public Result<Boolean> deleteFile(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId) {
        
        log.info("删除文件: fileId={}", fileId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean result = fileUploadService.deleteFile(fileId, currentUserId);
        
        return Result.success(result);
    }

    /**
     * 批量删除文件
     */
    @ApiOperation("批量删除文件")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteFiles(
            @ApiParam("文件ID列表") @RequestBody @NotNull List<Long> fileIds) {
        
        log.info("批量删除文件: count={}", fileIds.size());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean result = fileUploadService.batchDeleteFiles(fileIds, currentUserId);
        
        return Result.success(result);
    }

    /**
     * 更新文件关联信息
     */
    @ApiOperation("更新文件关联信息")
    @PutMapping("/{fileId}/relation")
    public Result<FileUpload> updateFileRelation(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId,
            @ApiParam("关联类型") @RequestParam @NotNull String relatedType,
            @ApiParam("关联ID") @RequestParam(required = false) Long relatedId) {
        
        log.info("更新文件关联信息: fileId={}, relatedType={}, relatedId={}", 
                fileId, relatedType, relatedId);

        FileUpload fileUpload = fileUploadService.updateFileRelation(fileId, relatedType, relatedId);
        return Result.success(fileUpload);
    }

    // ==================== 文件下载 ====================

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/{fileId}/download")
    public void downloadFile(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId,
            HttpServletResponse response) {
        
        log.info("下载文件: fileId={}", fileId);

        fileUploadService.downloadFile(fileId, response);
    }

    /**
     * 获取文件流
     */
    @ApiOperation("获取文件流")
    @GetMapping(value = "/{fileId}/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getFileStream(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId) {
        
        log.info("获取文件流: fileId={}", fileId);

        return fileUploadService.getFileBytes(fileId);
    }

    // ==================== 图片处理 ====================

    /**
     * 生成缩略图
     */
    @ApiOperation("生成缩略图")
    @PostMapping("/{fileId}/thumbnail")
    public Result<FileUpload> generateThumbnail(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId) {
        
        log.info("生成缩略图: fileId={}", fileId);

        FileUpload fileUpload = fileUploadService.generateThumbnail(fileId);
        return Result.success(fileUpload);
    }

    /**
     * 获取图片信息
     */
    @ApiOperation("获取图片信息")
    @GetMapping("/{fileId}/image-info")
    public Result<FileUpload> getImageInfo(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId) {
        
        log.info("获取图片信息: fileId={}", fileId);

        FileUpload fileUpload = fileUploadService.getImageInfo(fileId);
        return Result.success(fileUpload);
    }

    /**
     * 压缩图片
     */
    @ApiOperation("压缩图片")
    @PostMapping("/{fileId}/compress")
    public Result<FileUpload> compressImage(
            @ApiParam("文件ID") @PathVariable @NotNull Long fileId,
            @ApiParam("压缩质量") @RequestParam(required = false) Float quality) {
        
        log.info("压缩图片: fileId={}, quality={}", fileId, quality);

        FileUpload fileUpload = fileUploadService.compressImage(fileId, quality);
        return Result.success(fileUpload);
    }

    // ==================== 统计分析 ====================

    /**
     * 获取用户文件统计
     */
    @ApiOperation("获取用户文件统计")
    @GetMapping("/stats/user/{userId}")
    public Result<FileUploadService.UserFileStats> getUserFileStats(
            @ApiParam("用户ID") @PathVariable @NotNull Long userId) {
        
        log.info("获取用户文件统计: userId={}", userId);

        // 权限检查：用户只能查看自己的统计，管理员可以查看所有用户的统计
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(userId) && !SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看该用户统计");
        }

        FileUploadService.UserFileStats stats = fileUploadService.getUserFileStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取系统文件统计
     */
    @ApiOperation("获取系统文件统计")
    @GetMapping("/stats/system")
    public Result<FileUploadService.SystemFileStats> getSystemFileStats() {
        
        log.info("获取系统文件统计");

        // 只有管理员可以查看系统统计
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看系统统计");
        }

        FileUploadService.SystemFileStats stats = fileUploadService.getSystemFileStats();
        return Result.success(stats);
    }

    /**
     * 获取文件类型统计
     */
    @ApiOperation("获取文件类型统计")
    @GetMapping("/stats/types")
    public Result<List<FileUploadService.FileTypeStats>> getFileTypeStats() {
        
        log.info("获取文件类型统计");

        // 只有管理员可以查看文件类型统计
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看文件类型统计");
        }

        List<FileUploadService.FileTypeStats> stats = fileUploadService.getFileTypeStats();
        return Result.success(stats);
    }

    /**
     * 获取文件上传统计
     */
    @ApiOperation("获取文件上传统计")
    @GetMapping("/stats/trend")
    public Result<List<FileUploadService.FileUploadTrendStats>> getFileUploadTrend(
            @ApiParam("统计天数") @RequestParam(defaultValue = "7") Integer days) {
        
        log.info("获取文件上传统计: days={}", days);

        // 只有管理员可以查看上传统计
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看上传统计");
        }

        List<FileUploadService.FileUploadTrendStats> stats = fileUploadService.getFileUploadTrend(days);
        return Result.success(stats);
    }

    /**
     * 获取存储使用情况
     */
    @ApiOperation("获取存储使用情况")
    @GetMapping("/stats/storage")
    public Result<FileUploadService.StorageUsageStats> getStorageUsageStats() {
        
        log.info("获取存储使用情况");

        // 只有管理员可以查看存储统计
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限查看存储统计");
        }

        FileUploadService.StorageUsageStats stats = fileUploadService.getStorageUsageStats();
        return Result.success(stats);
    }

    // ==================== 业务查询 ====================

    /**
     * 获取用户头像
     */
    @ApiOperation("获取用户头像")
    @GetMapping("/avatar/{userId}")
    public Result<FileUpload> getUserAvatar(
            @ApiParam("用户ID") @PathVariable @NotNull Long userId) {
        
        log.info("获取用户头像: userId={}", userId);

        FileUpload avatar = fileUploadService.getUserAvatar(userId);
        return Result.success(avatar);
    }

    /**
     * 获取帖子图片列表
     */
    @ApiOperation("获取帖子图片列表")
    @GetMapping("/post/{postId}/images")
    public Result<List<FileUpload>> getPostImages(
            @ApiParam("帖子ID") @PathVariable @NotNull Long postId) {
        
        log.info("获取帖子图片列表: postId={}", postId);

        List<FileUpload> images = fileUploadService.getPostImages(postId);
        return Result.success(images);
    }

    /**
     * 获取活动图片列表
     */
    @ApiOperation("获取活动图片列表")
    @GetMapping("/activity/{activityId}/images")
    public Result<List<FileUpload>> getActivityImages(
            @ApiParam("活动ID") @PathVariable @NotNull Long activityId) {
        
        log.info("获取活动图片列表: activityId={}", activityId);

        List<FileUpload> images = fileUploadService.getActivityImages(activityId);
        return Result.success(images);
    }

    /**
     * 获取场地头像
     */
    @ApiOperation("获取场地头像")
    @GetMapping("/field/{fieldId}/avatar")
    public Result<FileUpload> getFieldAvatar(
            @ApiParam("场地ID") @PathVariable @NotNull Long fieldId) {
        
        log.info("获取场地头像: fieldId={}", fieldId);

        FileUpload avatar = fileUploadService.getFieldAvatar(fieldId);
        return Result.success(avatar);
    }

    /**
     * 获取最近上传的文件
     */
    @ApiOperation("获取最近上传的文件")
    @GetMapping("/recent")
    public Result<List<FileUpload>> getRecentFiles(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取最近上传的文件: userId={}, limit={}", userId, limit);

        List<FileUpload> files = fileUploadService.getRecentFiles(userId, limit);
        return Result.success(files);
    }

    // ==================== 存储管理 ====================

    /**
     * 清理无效文件
     */
    @ApiOperation("清理无效文件")
    @PostMapping("/cleanup/invalid")
    public Result<Integer> cleanupInvalidFiles() {
        
        log.info("清理无效文件");

        // 只有管理员可以清理文件
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限清理文件");
        }

        int cleanedCount = fileUploadService.cleanupInvalidFiles();
        return Result.success(cleanedCount);
    }

    /**
     * 清理临时文件
     */
    @ApiOperation("清理临时文件")
    @PostMapping("/cleanup/temp")
    public Result<Integer> cleanupTempFiles() {
        
        log.info("清理临时文件");

        // 只有管理员可以清理文件
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("没有权限清理文件");
        }

        int cleanedCount = fileUploadService.cleanupTempFiles();
        return Result.success(cleanedCount);
    }
}
