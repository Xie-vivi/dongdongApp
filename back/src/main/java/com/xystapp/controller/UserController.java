package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.UserProfileUpdateRequest;
import com.xystapp.dto.UserSearchRequest;
import com.xystapp.entity.User;
import com.xystapp.service.UserService;
import com.xystapp.utils.CosUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CosUtils cosUtils;

    /**
     * 获取当前用户资料
     */
    @ApiOperation("获取当前用户资料")
    @GetMapping("/profile")
    public Result<User> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getUserByUsername(username);
        return Result.success(user);
    }

    /**
     * 更新当前用户资料
     */
    @ApiOperation("更新当前用户资料")
    @PutMapping("/profile")
    public Result<User> updateCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        
        String username = userDetails.getUsername();
        User user = userService.updateUserProfile(username, request);
        return Result.success("更新成功", user);
    }

    /**
     * 获取指定用户信息
     */
    @ApiOperation("获取指定用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 根据UID获取用户信息
     */
    @ApiOperation("根据UID获取用户信息")
    @GetMapping("/uid/{uid}")
    public Result<User> getUserByUid(@PathVariable String uid) {
        User user = userService.getUserByUid(uid);
        return Result.success(user);
    }

    /**
     * 搜索用户
     */
    @ApiOperation("搜索用户")
    @PostMapping("/search")
    public Result<IPage<User>> searchUsers(@Valid @RequestBody UserSearchRequest request) {
        IPage<User> users = userService.searchUsers(request);
        return Result.success(users);
    }

    /**
     * 上传头像
     */
    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        
        try {
            String username = userDetails.getUsername();
            String avatarUrl = cosUtils.uploadAvatar(file);
            
            // 更新用户头像
            userService.updateUserAvatar(username, avatarUrl);
            
            return Result.success("头像上传成功", avatarUrl);
        } catch (Exception e) {
            log.error("头像上传失败: {}", e.getMessage());
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传背景图
     */
    @ApiOperation("上传背景图")
    @PostMapping("/background")
    public Result<String> uploadBackground(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        
        try {
            String username = userDetails.getUsername();
            String backgroundUrl = cosUtils.uploadBackground(file);
            
            // 更新用户背景图
            userService.updateUserBackground(username, backgroundUrl);
            
            return Result.success("背景图上传成功", backgroundUrl);
        } catch (Exception e) {
            log.error("背景图上传失败: {}", e.getMessage());
            return Result.error("背景图上传失败: " + e.getMessage());
        }
    }

    /**
     * 校园认证申请
     */
    @ApiOperation("校园认证申请")
    @PostMapping("/school-certify")
    public Result<Void> applySchoolCertification(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("school") String school,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate) {
        
        try {
            String username = userDetails.getUsername();
            userService.applySchoolCertification(username, school, certificate);
            return Result.success();
        } catch (Exception e) {
            log.error("校园认证申请失败: {}", e.getMessage());
            return Result.error("认证申请失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @ApiOperation("获取用户统计信息")
    @GetMapping("/stats/{id}")
    public Result<Object> getUserStats(@PathVariable Long id) {
        Object stats = userService.getUserStats(id);
        return Result.success(stats);
    }

    /**
     * 批量获取用户信息
     */
    @ApiOperation("批量获取用户信息")
    @PostMapping("/batch")
    public Result<List<User>> getUsersByIds(@RequestBody List<Long> ids) {
        List<User> users = userService.getUsersByIds(ids);
        return Result.success(users);
    }
}
