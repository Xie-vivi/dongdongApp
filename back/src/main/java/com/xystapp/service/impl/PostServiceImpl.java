package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xystapp.dto.PostCreateRequest;
import com.xystapp.dto.PostQueryRequest;
import com.xystapp.dto.PostUpdateRequest;
import com.xystapp.entity.Post;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.PostMapper;
import com.xystapp.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int MAX_IMAGES_COUNT = 9;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post createPost(PostCreateRequest request, Long userId) {
        log.info("创建帖子请求: userId={}, title={}", userId, request.getTitle());

        // 参数验证
        validatePostRequest(request);

        // 创建帖子对象
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setSubtitle(request.getSubtitle());
        post.setContent(request.getContent());
        post.setTag(request.getTag());
        post.setStatus(request.getStatus() != null ? request.getStatus() : "draft");
        
        // 处理图片列表
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > MAX_IMAGES_COUNT) {
                throw new BusinessException(400, "图片数量不能超过" + MAX_IMAGES_COUNT + "张");
            }
            post.setImages(convertImagesToJson(request.getImages()));
        }

        // 设置初始统计信息
        post.setLikesCount(0);
        post.setStarsCount(0);
        post.setCommentsCount(0);

        // 保存到数据库
        int result = postMapper.insert(post);
        if (result <= 0) {
            throw new BusinessException(500, "创建帖子失败");
        }

        log.info("帖子创建成功: id={}, userId={}", post.getId(), userId);
        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post updatePost(Long id, PostUpdateRequest request, Long userId) {
        log.info("更新帖子请求: id={}, userId={}", id, userId);

        // 检查帖子是否存在和权限
        Post existingPost = postMapper.selectPostWithUserById(id);
        if (existingPost == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 管理员操作检查（userId为null表示管理员操作）
        if (userId != null && !existingPost.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限修改此帖子");
        }

        // 参数验证
        validatePostRequest(request);

        // 更新帖子信息
        existingPost.setTitle(request.getTitle());
        existingPost.setSubtitle(request.getSubtitle());
        existingPost.setContent(request.getContent());
        existingPost.setTag(request.getTag());
        
        if (request.getStatus() != null) {
            existingPost.setStatus(request.getStatus());
        }

        // 处理图片列表
        if (request.getImages() != null) {
            if (request.getImages().size() > MAX_IMAGES_COUNT) {
                throw new BusinessException(400, "图片数量不能超过" + MAX_IMAGES_COUNT + "张");
            }
            existingPost.setImages(convertImagesToJson(request.getImages()));
        }

        // 更新数据库
        int result = postMapper.updateById(existingPost);
        if (result <= 0) {
            throw new BusinessException(500, "更新帖子失败");
        }

        log.info("帖子更新成功: id={}, userId={}", id, userId);
        return existingPost;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id, Long userId) {
        log.info("删除帖子请求: id={}, userId={}", id, userId);

        // 检查帖子是否存在和权限
        Post existingPost = postMapper.selectPostWithUserById(id);
        if (existingPost == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 管理员操作检查（userId为null表示管理员操作）
        if (userId != null && !existingPost.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此帖子");
        }

        // 删除帖子
        int result = postMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(500, "删除帖子失败");
        }

        log.info("帖子删除成功: id={}, userId={}", id, userId);
    }

    @Override
    public Post getPostById(Long id) {
        log.info("获取帖子详情: id={}", id);

        Post post = postMapper.selectPostWithUserById(id);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 处理图片数据
        if (StringUtils.hasText(post.getImages())) {
            post.setImages(post.getImages()); // 保持JSON格式，前端解析
        }

        return post;
    }

    @Override
    public IPage<Post> getPostList(PostQueryRequest query) {
        log.info("查询帖子列表: keyword={}, userId={}, tag={}, status={}", 
                query.getKeyword(), query.getUserId(), query.getTag(), query.getStatus());

        // 设置默认值
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(20);
        }
        if (query.getSortBy() == null) {
            query.setSortBy("created_at");
        }
        if (query.getSortOrder() == null) {
            query.setSortOrder("desc");
        }

        // 只查询已发布的帖子（除非指定状态）
        if (query.getStatus() == null) {
            query.setStatus("published");
        }

        Page<Post> page = new Page<>(query.getPage(), query.getSize());
        IPage<Post> result = postMapper.selectPostsWithUser(page, query);

        log.info("查询帖子列表成功: 总数={}, 当前页数据={}", result.getTotal(), result.getRecords().size());
        return result;
    }

    @Override
    public IPage<Post> getUserPosts(Long userId, String status, Integer page, Integer size) {
        log.info("获取用户帖子列表: userId={}, status={}", userId, status);

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Post> pageParam = new Page<>(page, size);
        IPage<Post> result = postMapper.selectPostsByUserId(pageParam, userId, status);

        log.info("获取用户帖子列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post publishDraft(Long id, Long userId) {
        log.info("发布草稿帖子: id={}, userId={}", id, userId);

        // 检查权限
        if (!isPostOwner(id, userId)) {
            throw new BusinessException(403, "无权限操作此帖子");
        }

        Post post = postMapper.selectPostWithUserById(id);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        if (!"draft".equals(post.getStatus())) {
            throw new BusinessException(400, "只能发布草稿状态的帖子");
        }

        post.setStatus("published");
        int result = postMapper.updateById(post);
        if (result <= 0) {
            throw new BusinessException(500, "发布帖子失败");
        }

        log.info("草稿帖子发布成功: id={}, userId={}", id, userId);
        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post setAsDraft(Long id, Long userId) {
        log.info("将帖子设为草稿: id={}, userId={}", id, userId);

        // 检查权限
        if (!isPostOwner(id, userId)) {
            throw new BusinessException(403, "无权限操作此帖子");
        }

        Post post = postMapper.selectPostWithUserById(id);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        post.setStatus("draft");
        int result = postMapper.updateById(post);
        if (result <= 0) {
            throw new BusinessException(500, "设置草稿失败");
        }

        log.info("帖子设为草稿成功: id={}, userId={}", id, userId);
        return post;
    }

    @Override
    public List<Post> getPostsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        log.info("批量获取帖子信息: ids={}", ids);
        return postMapper.selectPostsByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePostStats(Long postId, Integer likesCount, Integer starsCount, Integer commentsCount) {
        log.info("更新帖子统计信息: postId={}, likes={}, stars={}, comments={}", 
                postId, likesCount, starsCount, commentsCount);

        int result = postMapper.updatePostStats(postId, likesCount, starsCount, commentsCount);
        if (result <= 0) {
            throw new BusinessException(500, "更新统计信息失败");
        }
    }

    @Override
    public boolean isPostOwner(Long postId, Long userId) {
        // 管理员操作检查（userId为null表示管理员操作）
        if (userId == null) {
            return true;
        }
        return postMapper.isPostOwner(postId, userId);
    }

    /**
     * 验证帖子请求参数
     */
    private void validatePostRequest(Object request) {
        // 基础验证由@Valid注解处理
        // 这里可以添加额外的业务验证逻辑
    }

    /**
     * 将图片列表转换为JSON字符串
     */
    private String convertImagesToJson(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            log.error("图片列表JSON转换失败", e);
            throw new BusinessException(500, "图片数据处理失败");
        }
    }
}
