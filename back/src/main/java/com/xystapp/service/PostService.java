package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.PostCreateRequest;
import com.xystapp.dto.PostQueryRequest;
import com.xystapp.dto.PostUpdateRequest;
import com.xystapp.entity.Post;

import java.util.List;

/**
 * 帖子服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface PostService {

    /**
     * 创建帖子
     */
    Post createPost(PostCreateRequest request, Long userId);

    /**
     * 更新帖子
     */
    Post updatePost(Long id, PostUpdateRequest request, Long userId);

    /**
     * 删除帖子
     */
    void deletePost(Long id, Long userId);

    /**
     * 根据ID获取帖子详情
     */
    Post getPostById(Long id);

    /**
     * 分页查询帖子列表
     */
    IPage<Post> getPostList(PostQueryRequest query);

    /**
     * 获取用户的帖子列表
     */
    IPage<Post> getUserPosts(Long userId, String status, Integer page, Integer size);

    /**
     * 发布草稿帖子
     */
    Post publishDraft(Long id, Long userId);

    /**
     * 将帖子设为草稿
     */
    Post setAsDraft(Long id, Long userId);

    /**
     * 批量获取帖子信息
     */
    List<Post> getPostsByIds(List<Long> ids);

    /**
     * 更新帖子统计信息
     */
    void updatePostStats(Long postId, Integer likesCount, Integer starsCount, Integer commentsCount);

    /**
     * 检查用户是否是帖子作者
     */
    boolean isPostOwner(Long postId, Long userId);
}
