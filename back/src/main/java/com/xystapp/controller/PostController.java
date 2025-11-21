package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.PostCreateRequest;
import com.xystapp.dto.PostQueryRequest;
import com.xystapp.dto.PostUpdateRequest;
import com.xystapp.entity.Post;
import com.xystapp.service.PostService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 帖子控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "帖子管理")
@RestController
@RequestMapping("/posts")
@Validated
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    /**
     * 创建帖子
     */
    @ApiOperation("创建帖子")
    @PostMapping
    public Result<Post> createPost(@Valid @RequestBody PostCreateRequest request) {
        log.info("创建帖子请求: {}", request.getTitle());
        
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.createPost(request, userId);
        
        return Result.success(post);
    }

    /**
     * 更新帖子
     */
    @ApiOperation("更新帖子")
    @PutMapping("/{id}")
    public Result<Post> updatePost(
            @ApiParam("帖子ID") @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request) {
        log.info("更新帖子请求: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.updatePost(id, request, userId);
        
        return Result.success(post);
    }

    /**
     * 删除帖子
     */
    @ApiOperation("删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("删除帖子请求: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        postService.deletePost(id, userId);
        
        return Result.success();
    }

    /**
     * 获取帖子详情
     */
    @ApiOperation("获取帖子详情")
    @GetMapping("/{id}")
    public Result<Post> getPostById(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("获取帖子详情: id={}", id);
        
        Post post = postService.getPostById(id);
        return Result.success(post);
    }

    /**
     * 分页查询帖子列表
     */
    @ApiOperation("分页查询帖子列表")
    @PostMapping("/search")
    public Result<IPage<Post>> getPostList(@Valid @RequestBody PostQueryRequest query) {
        log.info("查询帖子列表: keyword={}, page={}, size={}", 
                query.getKeyword(), query.getPage(), query.getSize());
        
        IPage<Post> result = postService.getPostList(query);
        return Result.success(result);
    }

    /**
     * 获取用户帖子列表
     */
    @ApiOperation("获取用户帖子列表")
    @GetMapping("/user/{userId}")
    public Result<IPage<Post>> getUserPosts(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("帖子状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户帖子列表: userId={}, status={}", userId, status);
        
        IPage<Post> result = postService.getUserPosts(userId, status, page, size);
        return Result.success(result);
    }

    /**
     * 发布草稿帖子
     */
    @ApiOperation("发布草稿帖子")
    @PostMapping("/{id}/publish")
    public Result<Post> publishDraft(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("发布草稿帖子: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.publishDraft(id, userId);
        
        return Result.success(post);
    }

    /**
     * 将帖子设为草稿
     */
    @ApiOperation("将帖子设为草稿")
    @PostMapping("/{id}/draft")
    public Result<Post> setAsDraft(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("将帖子设为草稿: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.setAsDraft(id, userId);
        
        return Result.success(post);
    }

    /**
     * 批量获取帖子信息
     */
    @ApiOperation("批量获取帖子信息")
    @PostMapping("/batch")
    public Result<List<Post>> getPostsByIds(@RequestBody List<Long> ids) {
        log.info("批量获取帖子信息: ids={}", ids);
        
        List<Post> posts = postService.getPostsByIds(ids);
        return Result.success(posts);
    }

    /**
     * 获取我的帖子列表
     */
    @ApiOperation("获取我的帖子列表")
    @GetMapping("/my")
    public Result<IPage<Post>> getMyPosts(
            @ApiParam("帖子状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的帖子列表: status={}", status);
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Post> result = postService.getUserPosts(userId, status, page, size);
        
        return Result.success(result);
    }

    // ==================== 管理员功能 ====================

    /**
     * 管理员删除帖子
     */
    @ApiOperation("管理员删除帖子")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> adminDeletePost(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("管理员删除帖子: id={}, adminId={}", id, SecurityUtils.getCurrentUserId());
        
        // 管理员可以删除任何帖子
        postService.deletePost(id, null); // 传入null表示跳过权限检查，需要服务层支持
        
        return Result.success();
    }

    /**
     * 管理员审核帖子
     */
    @ApiOperation("管理员审核帖子")
    @PostMapping("/admin/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Post> reviewPost(
            @ApiParam("帖子ID") @PathVariable Long id,
            @ApiParam("审核结果") @RequestParam String result,
            @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("管理员审核帖子: id={}, result={}, reason={}", id, result, reason);
        
        // 这里需要实现审核逻辑，暂时返回帖子详情
        Post post = postService.getPostById(id);
        return Result.success(post);
    }

    /**
     * 管理员置顶帖子
     */
    @ApiOperation("管理员置顶帖子")
    @PostMapping("/admin/{id}/pin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> pinPost(
            @ApiParam("帖子ID") @PathVariable Long id,
            @ApiParam("是否置顶") @RequestParam Boolean pinned) {
        log.info("管理员置顶帖子: id={}, pinned={}", id, pinned);
        
        // 这里需要实现置顶逻辑
        return Result.success(pinned);
    }

    /**
     * 管理员获取所有帖子列表（包括草稿和已删除）
     */
    @ApiOperation("管理员获取所有帖子列表")
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Post>> adminGetPostList(
            @ApiParam("查询参数") @Valid PostQueryRequest query) {
        log.info("管理员获取帖子列表: query={}", query);
        
        // 管理员可以看到所有状态的帖子
        IPage<Post> result = postService.getPostList(query);
        return Result.success(result);
    }

    /**
     * 管理员获取草稿列表
     */
    @ApiOperation("管理员获取草稿列表")
    @GetMapping("/admin/drafts")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Post>> adminGetDrafts(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("管理员获取草稿列表: userId={}", userId);
        
        IPage<Post> result = postService.getUserPosts(userId, "draft", page, size);
        return Result.success(result);
    }

    /**
     * 管理员强制发布帖子
     */
    @ApiOperation("管理员强制发布帖子")
    @PostMapping("/admin/{id}/force-publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Post> forcePublishPost(@ApiParam("帖子ID") @PathVariable Long id) {
        log.info("管理员强制发布帖子: id={}", id);
        
        // 管理员可以强制发布任何草稿帖子
        Post post = postService.publishDraft(id, null); // 需要服务层支持管理员操作
        return Result.success(post);
    }

    // ==================== 草稿管理专用接口 ====================

    /**
     * 获取草稿列表（所有用户）
     */
    @ApiOperation("获取草稿列表")
    @GetMapping("/drafts")
    public Result<IPage<Post>> getDrafts(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取草稿列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Post> result = postService.getUserPosts(userId, "draft", page, size);
        return Result.success(result);
    }

    /**
     * 批量删除草稿
     */
    @ApiOperation("批量删除草稿")
    @DeleteMapping("/drafts/batch")
    public Result<Boolean> batchDeleteDrafts(@RequestBody List<Long> ids) {
        log.info("批量删除草稿: ids={}", ids);
        
        Long userId = SecurityUtils.getCurrentUserId();
        for (Long id : ids) {
            postService.deletePost(id, userId);
        }
        
        return Result.success(true);
    }

    // ==================== 统计功能 ====================

    /**
     * 获取帖子统计信息
     */
    @ApiOperation("获取帖子统计信息")
    @GetMapping("/stats")
    public Result<PostStats> getPostStats(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        log.info("获取帖子统计信息: userId={}", userId);
        
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        
        // 这里实现统计逻辑
        PostStats stats = new PostStats();
        stats.setTotalPosts(0);
        stats.setPublishedPosts(0);
        stats.setDraftPosts(0);
        stats.setTotalLikes(0);
        stats.setTotalStars(0);
        stats.setTotalComments(0);
        
        return Result.success(stats);
    }

    /**
     * 管理员获取帖子统计信息
     */
    @ApiOperation("管理员获取帖子统计信息")
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostStats> adminGetPostStats() {
        log.info("管理员获取帖子统计信息");
        
        // 这里实现管理员统计逻辑
        PostStats stats = new PostStats();
        stats.setTotalPosts(0);
        stats.setPublishedPosts(0);
        stats.setDraftPosts(0);
        stats.setTotalLikes(0);
        stats.setTotalStars(0);
        stats.setTotalComments(0);
        
        return Result.success(stats);
    }

    /**
     * 帖子统计信息内部类
     */
    public static class PostStats {
        private Integer totalPosts;
        private Integer publishedPosts;
        private Integer draftPosts;
        private Integer totalLikes;
        private Integer totalStars;
        private Integer totalComments;

        // getters and setters
        public Integer getTotalPosts() { return totalPosts; }
        public void setTotalPosts(Integer totalPosts) { this.totalPosts = totalPosts; }
        public Integer getPublishedPosts() { return publishedPosts; }
        public void setPublishedPosts(Integer publishedPosts) { this.publishedPosts = publishedPosts; }
        public Integer getDraftPosts() { return draftPosts; }
        public void setDraftPosts(Integer draftPosts) { this.draftPosts = draftPosts; }
        public Integer getTotalLikes() { return totalLikes; }
        public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
        public Integer getTotalStars() { return totalStars; }
        public void setTotalStars(Integer totalStars) { this.totalStars = totalStars; }
        public Integer getTotalComments() { return totalComments; }
        public void setTotalComments(Integer totalComments) { this.totalComments = totalComments; }
    }
}
