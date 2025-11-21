/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : xystapp

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 18/11/2025 16:24:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `date` date NOT NULL,
  `day` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `field_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '场地ID',
  `participants` int(11) NULL DEFAULT 0,
  `max_participants` int(11) NULL DEFAULT NULL,
  `likes_count` int(11) NULL DEFAULT 0,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('draft','published') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'published',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activities_user_time`(`user_id`, `created_at`) USING BTREE,
  INDEX `idx_activities_date`(`date`) USING BTREE,
  INDEX `idx_activities_status`(`status`) USING BTREE,
  INDEX `idx_activities_tag`(`tag`) USING BTREE,
  INDEX `idx_activities_location`(`location`) USING BTREE,
  INDEX `idx_activities_user_status_date`(`user_id`, `status`, `date`) USING BTREE,
  INDEX `idx_activities_field`(`field_id`) USING BTREE COMMENT '场地ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activities
-- ----------------------------

-- ----------------------------
-- Table structure for activity_likes
-- ----------------------------
DROP TABLE IF EXISTS `activity_likes`;
CREATE TABLE `activity_likes`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `activity_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_likes`(`user_id`, `activity_id`) USING BTREE,
  INDEX `idx_activity_likes_activity`(`activity_id`) USING BTREE,
  INDEX `idx_activity_likes_user`(`user_id`) USING BTREE,
  INDEX `idx_activity_likes_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_likes
-- ----------------------------

-- ----------------------------
-- Table structure for activity_participation_stats
-- ----------------------------
DROP TABLE IF EXISTS `activity_participation_stats`;
CREATE TABLE `activity_participation_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `signup_count` int(11) NULL DEFAULT 0 COMMENT '报名人数',
  `checkin_count` int(11) NULL DEFAULT 0 COMMENT '签到人数',
  `participation_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '参与率（%）',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率（%）',
  `satisfaction_score` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '满意度评分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_date`(`activity_id`, `stat_date`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动参与统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_participation_stats
-- ----------------------------

-- ----------------------------
-- Table structure for activity_signups
-- ----------------------------
DROP TABLE IF EXISTS `activity_signups`;
CREATE TABLE `activity_signups`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `activity_id` bigint(20) UNSIGNED NOT NULL,
  `status` enum('signed_up','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'signed_up',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_signups`(`user_id`, `activity_id`) USING BTREE,
  INDEX `idx_activity_signups_activity`(`activity_id`) USING BTREE,
  INDEX `idx_activity_signups_user`(`user_id`) USING BTREE,
  INDEX `idx_activity_signups_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_signups
-- ----------------------------

-- ----------------------------
-- Table structure for activity_stars
-- ----------------------------
DROP TABLE IF EXISTS `activity_stars`;
CREATE TABLE `activity_stars`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `activity_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_stars`(`user_id`, `activity_id`) USING BTREE,
  INDEX `idx_activity_stars_activity`(`activity_id`) USING BTREE,
  INDEX `idx_activity_stars_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_stars
-- ----------------------------

-- ----------------------------
-- Table structure for admin_login_attempts
-- ----------------------------
DROP TABLE IF EXISTS `admin_login_attempts`;
CREATE TABLE `admin_login_attempts`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `attempt_type` enum('success','failed','blocked') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `failure_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_login_attempts_username`(`username`) USING BTREE,
  INDEX `idx_admin_login_attempts_ip`(`ip_address`) USING BTREE,
  INDEX `idx_admin_login_attempts_created`(`created_at`) USING BTREE,
  INDEX `idx_admin_login_attempts_type`(`attempt_type`) USING BTREE,
  INDEX `idx_admin_login_attempts_username_type`(`username`, `attempt_type`) USING BTREE,
  INDEX `idx_admin_login_attempts_ip_type`(`ip_address`, `attempt_type`) USING BTREE,
  INDEX `idx_admin_login_attempts_recent_failures`(`username`, `attempt_type`, `created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_login_attempts
-- ----------------------------
INSERT INTO `admin_login_attempts` VALUES (1, 'superadmin', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'success', NULL, '2025-11-16 15:01:02');
INSERT INTO `admin_login_attempts` VALUES (2, 'content_admin', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'success', NULL, '2025-11-16 15:01:02');
INSERT INTO `admin_login_attempts` VALUES (3, 'user_admin', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'failed', '密码错误', '2025-11-16 14:01:02');
INSERT INTO `admin_login_attempts` VALUES (4, 'locked_admin', '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'blocked', '账户已锁定', '2025-11-16 13:01:02');
INSERT INTO `admin_login_attempts` VALUES (5, 'unknown', '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'failed', '用户不存在', '2025-11-16 14:31:02');

-- ----------------------------
-- Table structure for admin_logs
-- ----------------------------
DROP TABLE IF EXISTS `admin_logs`;
CREATE TABLE `admin_logs`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) UNSIGNED NOT NULL,
  `action_type` enum('create','update','delete','view','login','logout','export','import','ban','unban') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `target_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `request_data` json NULL,
  `old_data` json NULL,
  `new_data` json NULL,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `response_status` int(11) NULL DEFAULT NULL,
  `execution_time` int(11) NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_logs_admin`(`admin_id`) USING BTREE,
  INDEX `idx_admin_logs_action`(`action_type`) USING BTREE,
  INDEX `idx_admin_logs_target`(`target_type`, `target_id`) USING BTREE,
  INDEX `idx_admin_logs_created`(`created_at`) USING BTREE,
  INDEX `idx_admin_logs_admin_created`(`admin_id`, `created_at`) USING BTREE,
  INDEX `idx_admin_logs_action_created`(`action_type`, `created_at`) USING BTREE,
  INDEX `idx_admin_logs_target_created`(`target_type`, `target_id`, `created_at`) USING BTREE,
  INDEX `idx_admin_logs_ip`(`ip_address`) USING BTREE,
  INDEX `idx_admin_logs_response_status`(`response_status`) USING BTREE,
  INDEX `idx_admin_logs_admin_action_time`(`admin_id`, `action_type`, `created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_logs
-- ----------------------------
INSERT INTO `admin_logs` VALUES (1, 1, 'create', 'admin', 5, '创建管理员账号', '{\"email\": \"lockedadmin@xystapp.com\", \"username\": \"locked_admin\"}', NULL, NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 150, '2025-11-16 15:01:02');
INSERT INTO `admin_logs` VALUES (2, 2, 'delete', 'post', 1, '删除违规帖子', '{\"reason\": \"内容违规\", \"post_id\": 1}', NULL, NULL, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 200, '2025-11-16 15:01:02');
INSERT INTO `admin_logs` VALUES (3, 3, 'update', 'user', 2, '修改用户状态', '{\"status\": \"banned\", \"user_id\": 2}', NULL, NULL, '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 120, '2025-11-16 15:01:02');
INSERT INTO `admin_logs` VALUES (4, 4, 'export', 'data', NULL, '导出用户数据', '{\"type\": \"users\", \"format\": \"excel\"}', NULL, NULL, '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 5000, '2025-11-16 15:01:02');
INSERT INTO `admin_logs` VALUES (5, 1, 'login', NULL, NULL, '超级管理员登录', '{}', NULL, NULL, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 50, '2025-11-16 15:01:02');
INSERT INTO `admin_logs` VALUES (6, 2, 'logout', NULL, NULL, '内容管理员登出', '{}', NULL, NULL, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 200, 30, '2025-11-16 15:01:02');

-- ----------------------------
-- Table structure for admin_operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `admin_operation_logs`;
CREATE TABLE `admin_operation_logs`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) UNSIGNED NOT NULL COMMENT '操作管理员ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源类型',
  `resource_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '资源ID',
  `operation_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作描述',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求URL',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
  `execution_time` int(11) NULL DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` enum('success','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'success' COMMENT '执行状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_operation_logs_admin`(`admin_id`) USING BTREE,
  INDEX `idx_admin_operation_logs_type`(`operation_type`) USING BTREE,
  INDEX `idx_admin_operation_logs_resource`(`resource_type`, `resource_id`) USING BTREE,
  INDEX `idx_admin_operation_logs_status`(`status`) USING BTREE,
  INDEX `idx_admin_operation_logs_created`(`created_at`) USING BTREE,
  INDEX `idx_admin_operation_logs_ip`(`ip_address`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin_operation_logs
-- ----------------------------

-- ----------------------------
-- Table structure for admin_role_assignments
-- ----------------------------
DROP TABLE IF EXISTS `admin_role_assignments`;
CREATE TABLE `admin_role_assignments`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) UNSIGNED NOT NULL,
  `role_id` bigint(20) UNSIGNED NOT NULL,
  `assigned_by` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `assigned_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_role_assignments`(`admin_id`, `role_id`) USING BTREE,
  INDEX `idx_admin_role_assignments_admin`(`admin_id`) USING BTREE,
  INDEX `idx_admin_role_assignments_role`(`role_id`) USING BTREE,
  INDEX `idx_admin_role_assignments_assigned_by`(`assigned_by`) USING BTREE,
  INDEX `idx_admin_role_assignments_expires`(`expires_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role_assignments
-- ----------------------------
INSERT INTO `admin_role_assignments` VALUES (1, 1, 1, 1, '2025-11-16 15:01:02', NULL);
INSERT INTO `admin_role_assignments` VALUES (2, 2, 2, 1, '2025-11-16 15:01:02', NULL);
INSERT INTO `admin_role_assignments` VALUES (3, 3, 3, 1, '2025-11-16 15:01:02', NULL);
INSERT INTO `admin_role_assignments` VALUES (4, 4, 4, 1, '2025-11-16 15:01:02', NULL);
INSERT INTO `admin_role_assignments` VALUES (5, 5, 6, 1, '2025-11-16 15:01:02', NULL);

-- ----------------------------
-- Table structure for admin_roles
-- ----------------------------
DROP TABLE IF EXISTS `admin_roles`;
CREATE TABLE `admin_roles`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) UNSIGNED NOT NULL COMMENT '管理员ID',
  `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `assigned_by` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '分配者ID',
  `assigned_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `expires_at` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_roles`(`admin_id`, `role_id`) USING BTREE,
  INDEX `idx_admin_roles_admin`(`admin_id`) USING BTREE,
  INDEX `idx_admin_roles_role`(`role_id`) USING BTREE,
  INDEX `idx_admin_roles_assigned_by`(`assigned_by`) USING BTREE,
  INDEX `idx_admin_roles_active`(`is_active`) USING BTREE,
  INDEX `idx_admin_roles_expires`(`expires_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin_roles
-- ----------------------------

-- ----------------------------
-- Table structure for admin_sessions
-- ----------------------------
DROP TABLE IF EXISTS `admin_sessions`;
CREATE TABLE `admin_sessions`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) UNSIGNED NOT NULL,
  `session_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `expires_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_activity_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admin_sessions_token`(`session_token`) USING BTREE,
  INDEX `idx_admin_sessions_admin`(`admin_id`) USING BTREE,
  INDEX `idx_admin_sessions_expires`(`expires_at`) USING BTREE,
  INDEX `idx_admin_sessions_admin_active`(`admin_id`, `is_active`) USING BTREE,
  INDEX `idx_admin_sessions_last_activity`(`last_activity_at`) USING BTREE,
  INDEX `idx_admin_sessions_ip`(`ip_address`) USING BTREE,
  INDEX `idx_admin_sessions_admin_active_expires`(`admin_id`, `is_active`, `expires_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_sessions
-- ----------------------------

-- ----------------------------
-- Table structure for admins
-- ----------------------------
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('active','inactive','locked') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  `last_login_at` timestamp NULL DEFAULT NULL,
  `last_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `login_count` int(11) NULL DEFAULT 0,
  `failed_login_count` int(11) NULL DEFAULT 0,
  `locked_until` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_admins_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_admins_email`(`email`) USING BTREE,
  INDEX `idx_admins_status`(`status`) USING BTREE,
  INDEX `idx_admins_last_login`(`last_login_at`) USING BTREE,
  INDEX `idx_admins_created`(`created_at`) USING BTREE,
  INDEX `idx_admins_locked_until`(`locked_until`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admins
-- ----------------------------
INSERT INTO `admins` VALUES (1, 'superadmin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'superadmin@xystapp.com', NULL, '超级管理员', NULL, 'active', '2025-11-16 14:01:03', '192.168.1.100', 15, 0, NULL, NULL, '2025-11-16 15:01:02', '2025-11-16 15:01:03');
INSERT INTO `admins` VALUES (2, 'content_admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'contentadmin@xystapp.com', NULL, '内容管理员', NULL, 'active', '2025-11-16 14:31:03', '192.168.1.101', 8, 0, NULL, NULL, '2025-11-16 15:01:02', '2025-11-16 15:01:03');
INSERT INTO `admins` VALUES (3, 'user_admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'useradmin@xystapp.com', NULL, '用户管理员', NULL, 'active', '2025-11-16 13:01:03', '192.168.1.102', 12, 0, NULL, NULL, '2025-11-16 15:01:02', '2025-11-16 15:01:03');
INSERT INTO `admins` VALUES (4, 'normal_admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'normaladmin@xystapp.com', NULL, '普通管理员', NULL, 'active', NULL, NULL, 0, 0, NULL, NULL, '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `admins` VALUES (5, 'locked_admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'lockedadmin@xystapp.com', NULL, '锁定管理员', NULL, 'active', NULL, NULL, 0, 0, NULL, NULL, '2025-11-16 15:01:02', '2025-11-16 15:01:02');

-- ----------------------------
-- Table structure for chat_members
-- ----------------------------
DROP TABLE IF EXISTS `chat_members`;
CREATE TABLE `chat_members`  (
  `chat_id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `role` enum('owner','admin','member') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'member',
  `joined_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_read_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`chat_id`, `user_id`) USING BTREE,
  INDEX `idx_chat_members_user`(`user_id`) USING BTREE,
  INDEX `idx_chat_members_role`(`role`) USING BTREE,
  INDEX `idx_chat_members_joined`(`joined_at`) USING BTREE,
  INDEX `idx_chat_members_user_role`(`user_id`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_members
-- ----------------------------

-- ----------------------------
-- Table structure for chats
-- ----------------------------
DROP TABLE IF EXISTS `chats`;
CREATE TABLE `chats`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` enum('private','group') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `notice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `activity` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `group_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_chats_type`(`type`) USING BTREE,
  INDEX `idx_chats_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chats
-- ----------------------------

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `post_id` bigint(20) UNSIGNED NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `at_user_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `reply_to_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comments_post_time`(`post_id`, `created_at`) USING BTREE,
  INDEX `idx_comments_user`(`user_id`) USING BTREE,
  INDEX `idx_comments_at_user`(`at_user_id`) USING BTREE,
  INDEX `idx_comments_reply_to`(`reply_to_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------

-- ----------------------------
-- Table structure for content_stats
-- ----------------------------
DROP TABLE IF EXISTS `content_stats`;
CREATE TABLE `content_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容类型：post/activity',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数',
  `share_count` int(11) NULL DEFAULT 0 COMMENT '分享数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `engagement_score` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '互动得分',
  `quality_score` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '质量得分',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_content_date`(`content_id`, `content_type`, `stat_date`) USING BTREE,
  INDEX `idx_content_type_date`(`content_type`, `stat_date`) USING BTREE,
  INDEX `idx_content_id`(`content_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内容统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_stats
-- ----------------------------

-- ----------------------------
-- Table structure for data_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `data_dictionary`;
CREATE TABLE `data_dictionary`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `dict_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `dict_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int(11) NULL DEFAULT 0,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_data_dictionary_type_key`(`dict_type`, `dict_key`) USING BTREE,
  INDEX `idx_data_dictionary_type`(`dict_type`) USING BTREE,
  INDEX `idx_data_dictionary_active`(`is_active`) USING BTREE,
  INDEX `idx_data_dictionary_type_sort`(`dict_type`, `sort_order`) USING BTREE,
  INDEX `idx_data_dictionary_type_active`(`dict_type`, `is_active`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_dictionary
-- ----------------------------
INSERT INTO `data_dictionary` VALUES (1, 'user_status', 'active', '1', '正常', 1, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (2, 'user_status', 'inactive', '0', '禁用', 2, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (3, 'user_status', 'locked', '2', '锁定', 3, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (4, 'content_status', 'published', '1', '已发布', 1, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (5, 'content_status', 'draft', '0', '草稿', 2, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (6, 'content_status', 'deleted', '2', '已删除', 3, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (7, 'notification_type', 'info', 'info', '信息', 1, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (8, 'notification_type', 'warning', 'warning', '警告', 2, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (9, 'notification_type', 'error', 'error', '错误', 3, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `data_dictionary` VALUES (10, 'notification_type', 'success', 'success', '成功', 4, 1, NULL, '2025-11-16 15:01:03', '2025-11-16 15:01:03');

-- ----------------------------
-- Table structure for drafts
-- ----------------------------
DROP TABLE IF EXISTS `drafts`;
CREATE TABLE `drafts`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `type` enum('post','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_drafts_user_type`(`user_id`, `type`) USING BTREE,
  INDEX `idx_drafts_created`(`created_at`) USING BTREE,
  INDEX `idx_drafts_updated`(`updated_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of drafts
-- ----------------------------

-- ----------------------------
-- Table structure for field_tags
-- ----------------------------
DROP TABLE IF EXISTS `field_tags`;
CREATE TABLE `field_tags`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `field_id` bigint(20) UNSIGNED NOT NULL COMMENT '场地ID',
  `tag_id` bigint(20) UNSIGNED NOT NULL COMMENT '标签ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_field_tag`(`field_id`, `tag_id`) USING BTREE COMMENT '场地标签唯一关联',
  INDEX `idx_field_tags_field`(`field_id`) USING BTREE COMMENT '场地ID索引',
  INDEX `idx_field_tags_tag`(`tag_id`) USING BTREE COMMENT '标签ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '场地标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of field_tags
-- ----------------------------

-- ----------------------------
-- Table structure for field_usage_stats
-- ----------------------------
DROP TABLE IF EXISTS `field_usage_stats`;
CREATE TABLE `field_usage_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `field_id` bigint(20) NOT NULL COMMENT '场地ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `usage_hours` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '使用时长（小时）',
  `booking_count` int(11) NULL DEFAULT 0 COMMENT '预订次数',
  `utilization_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '利用率（%）',
  `revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '收入',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_field_date`(`field_id`, `stat_date`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_field_id`(`field_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '场地使用统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of field_usage_stats
-- ----------------------------

-- ----------------------------
-- Table structure for fields
-- ----------------------------
DROP TABLE IF EXISTS `fields`;
CREATE TABLE `fields`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `creator_id` bigint(20) UNSIGNED NOT NULL,
  `members_count` int(11) NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fields_creator`(`creator_id`) USING BTREE,
  INDEX `idx_fields_name`(`name`) USING BTREE,
  INDEX `idx_fields_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fields
-- ----------------------------
INSERT INTO `fields` VALUES (1, 'lol场', 'lol爱好者聚集地，一起开黑上分', '/static/fields/lol.png', 4, 25, '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `fields` VALUES (2, '羽毛球群', '羽毛球爱好者的交流平台', '/static/fields/badminton.png', 1, 15, '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `fields` VALUES (3, '原神集会', '原神玩家聚集地，一起探索提瓦特大陆', '/static/fields/genshin.png', 5, 30, '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `fields` VALUES (4, '鸣潮集会', '鸣潮玩家的专属社区', '/static/fields/wuthering.png', 2, 18, '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `fields` VALUES (5, '锋芒值YDs', '游戏高手聚集地', '/static/fields/gaming.png', 3, 12, '2025-11-16 15:00:26', '2025-11-16 15:00:26');

-- ----------------------------
-- Table structure for file_storage_configs
-- ----------------------------
DROP TABLE IF EXISTS `file_storage_configs`;
CREATE TABLE `file_storage_configs`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_storage_config`(`config_key`) USING BTREE COMMENT '配置键唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件存储配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_storage_configs
-- ----------------------------

-- ----------------------------
-- Table structure for file_thumbnails
-- ----------------------------
DROP TABLE IF EXISTS `file_thumbnails`;
CREATE TABLE `file_thumbnails`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '缩略图ID',
  `file_id` bigint(20) UNSIGNED NOT NULL COMMENT '原文件ID',
  `thumbnail_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '缩略图路径',
  `width` int(10) UNSIGNED NOT NULL COMMENT '缩略图宽度',
  `height` int(10) UNSIGNED NOT NULL COMMENT '缩略图高度',
  `size` bigint(20) UNSIGNED NOT NULL COMMENT '缩略图大小',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_thumbnail_file`(`file_id`) USING BTREE COMMENT '文件ID唯一索引',
  INDEX `idx_thumbnails_created`(`created_at`) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件缩略图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_thumbnails
-- ----------------------------

-- ----------------------------
-- Table structure for file_uploads
-- ----------------------------
DROP TABLE IF EXISTS `file_uploads`;
CREATE TABLE `file_uploads`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件相对路径',
  `file_size` bigint(20) UNSIGNED NOT NULL COMMENT '文件大小(字节)',
  `file_type` enum('image','document','video','audio','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'other' COMMENT '文件类型',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MIME类型',
  `upload_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '上传用户ID',
  `related_type` enum('user_avatar','post_image','activity_image','field_avatar','comment_image','chat_image','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '关联类型',
  `related_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '关联ID',
  `status` enum('uploading','uploaded','failed','deleted') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'uploaded' COMMENT '文件状态',
  `upload_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传IP地址',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_file_path`(`file_path`) USING BTREE COMMENT '文件路径唯一索引',
  INDEX `idx_file_uploads_user`(`upload_user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_file_uploads_related`(`related_type`, `related_id`) USING BTREE COMMENT '关联索引',
  INDEX `idx_file_uploads_type`(`file_type`) USING BTREE COMMENT '文件类型索引',
  INDEX `idx_file_uploads_status`(`status`) USING BTREE COMMENT '状态索引',
  INDEX `idx_file_uploads_created`(`created_at`) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件上传表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_uploads
-- ----------------------------

-- ----------------------------
-- Table structure for message_stats
-- ----------------------------
DROP TABLE IF EXISTS `message_stats`;
CREATE TABLE `message_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `chat_id` bigint(20) NOT NULL COMMENT '聊天ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `message_count` int(11) NULL DEFAULT 0 COMMENT '消息数量',
  `active_user_count` int(11) NULL DEFAULT 0 COMMENT '活跃用户数',
  `avg_response_time_minutes` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '平均响应时间（分钟）',
  `message_type_distribution` json NULL COMMENT '消息类型分布',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_chat_date`(`chat_id`, `stat_date`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_chat_id`(`chat_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_stats
-- ----------------------------

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `chat_id` bigint(20) UNSIGNED NOT NULL,
  `sender_id` bigint(20) UNSIGNED NOT NULL,
  `content_type` enum('text','image') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'text',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_messages_chat_time`(`chat_id`, `created_at`) USING BTREE,
  INDEX `idx_messages_sender_time`(`sender_id`, `created_at`) USING BTREE,
  INDEX `idx_messages_content_type`(`content_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------

-- ----------------------------
-- Table structure for migration_history
-- ----------------------------
DROP TABLE IF EXISTS `migration_history`;
CREATE TABLE `migration_history`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '迁移版本',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '迁移描述',
  `executed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
  `status` enum('started','completed','failed','rolled_back') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'started' COMMENT '状态',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `execution_time_ms` int(11) NULL DEFAULT NULL COMMENT '执行耗时(毫秒)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_version`(`version`) USING BTREE,
  INDEX `idx_executed_at`(`executed_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '迁移历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of migration_history
-- ----------------------------
INSERT INTO `migration_history` VALUES (1, '1.0.0-all-new-tables', '所有新增表结构和修改汇总', '2025-11-16 18:12:22', 'started', NULL, NULL);

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `type` enum('like','star','comment','at','follow','follow_post','follow_activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `related_type` enum('post','activity','comment') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `related_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `sender_id` bigint(20) UNSIGNED NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notifications_user_read_time`(`user_id`, `is_read`, `created_at`) USING BTREE,
  INDEX `idx_notifications_type`(`type`) USING BTREE,
  INDEX `idx_notifications_related`(`related_type`, `related_id`) USING BTREE,
  INDEX `idx_notifications_sender`(`sender_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notifications
-- ----------------------------

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_system` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permissions_name`(`name`) USING BTREE,
  UNIQUE INDEX `uk_permissions_resource_action`(`resource`, `action`) USING BTREE,
  INDEX `idx_permissions_resource`(`resource`) USING BTREE,
  INDEX `idx_permissions_action`(`action`) USING BTREE,
  INDEX `idx_permissions_system`(`is_system`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, 'users:view', '查看用户', 'users', 'view', '查看用户列表和详情', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (2, 'users:create', '创建用户', 'users', 'create', '创建新用户', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (3, 'users:update', '编辑用户', 'users', 'update', '编辑用户信息', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (4, 'users:delete', '删除用户', 'users', 'delete', '删除用户', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (5, 'users:ban', '封禁用户', 'users', 'ban', '封禁或解封用户', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (6, 'users:certify', '用户认证', 'users', 'certify', '审核用户认证申请', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (7, 'posts:view', '查看帖子', 'posts', 'view', '查看帖子列表和详情', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (8, 'posts:delete', '删除帖子', 'posts', 'delete', '删除违规帖子', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (9, 'posts:pin', '置顶帖子', 'posts', 'pin', '置顶或取消置顶帖子', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (10, 'activities:view', '查看活动', 'activities', 'view', '查看活动列表和详情', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (11, 'activities:delete', '删除活动', 'activities', 'delete', '删除违规活动', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (12, 'activities:pin', '置顶活动', 'activities', 'pin', '置顶或取消置顶活动', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (13, 'comments:view', '查看评论', 'comments', 'view', '查看评论列表', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (14, 'comments:delete', '删除评论', 'comments', 'delete', '删除违规评论', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (15, 'comments:audit', '审核评论', 'comments', 'audit', '审核评论内容', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (16, 'fields:view', '查看场地', 'fields', 'view', '查看场地列表', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (17, 'fields:create', '创建场地', 'fields', 'create', '创建新场地', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (18, 'fields:update', '编辑场地', 'fields', 'update', '编辑场地信息', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (19, 'fields:delete', '删除场地', 'fields', 'delete', '删除场地', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (20, 'admins:view', '查看管理员', 'admins', 'view', '查看管理员列表', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (21, 'admins:create', '创建管理员', 'admins', 'create', '创建新管理员', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (22, 'admins:update', '编辑管理员', 'admins', 'update', '编辑管理员信息', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (23, 'admins:delete', '删除管理员', 'admins', 'delete', '删除管理员', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (24, 'roles:view', '查看角色', 'roles', 'view', '查看角色列表', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (25, 'roles:create', '创建角色', 'roles', 'create', '创建新角色', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (26, 'roles:update', '编辑角色', 'roles', 'update', '编辑角色信息', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (27, 'roles:delete', '删除角色', 'roles', 'delete', '删除角色', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (28, 'system:config', '系统配置', 'system', 'config', '修改系统配置', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (29, 'system:log', '查看日志', 'system', 'log', '查看系统日志', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (30, 'system:backup', '系统备份', 'system', 'backup', '执行系统备份', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (31, 'data:export', '数据导出', 'data', 'export', '导出业务数据', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (32, 'data:import', '数据导入', 'data', 'import', '导入业务数据', 1, '2025-11-16 15:01:02');
INSERT INTO `permissions` VALUES (33, 'data:statistics', '数据统计', 'data', 'statistics', '查看数据统计', 1, '2025-11-16 15:01:02');

-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
DROP TABLE IF EXISTS `post_likes`;
CREATE TABLE `post_likes`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `post_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_likes`(`user_id`, `post_id`) USING BTREE,
  INDEX `idx_post_likes_post`(`post_id`) USING BTREE,
  INDEX `idx_post_likes_user`(`user_id`) USING BTREE,
  INDEX `idx_post_likes_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_likes
-- ----------------------------

-- ----------------------------
-- Table structure for post_stars
-- ----------------------------
DROP TABLE IF EXISTS `post_stars`;
CREATE TABLE `post_stars`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `post_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_stars`(`user_id`, `post_id`) USING BTREE,
  INDEX `idx_post_stars_post`(`post_id`) USING BTREE,
  INDEX `idx_post_stars_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_stars
-- ----------------------------

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subtitle` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `likes_count` int(11) NULL DEFAULT 0,
  `stars_count` int(11) NULL DEFAULT 0,
  `comments_count` int(11) NULL DEFAULT 0,
  `status` enum('draft','published') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'published',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_posts_user_time`(`user_id`, `created_at`) USING BTREE,
  INDEX `idx_posts_status`(`status`) USING BTREE,
  INDEX `idx_posts_tag`(`tag`) USING BTREE,
  INDEX `idx_posts_user_status_time`(`user_id`, `status`, `created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of posts
-- ----------------------------

-- ----------------------------
-- Table structure for region_stats
-- ----------------------------
DROP TABLE IF EXISTS `region_stats`;
CREATE TABLE `region_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地区名称',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `user_count` int(11) NULL DEFAULT 0 COMMENT '用户数量',
  `new_user_count` int(11) NULL DEFAULT 0 COMMENT '新增用户数量',
  `active_user_count` int(11) NULL DEFAULT 0 COMMENT '活跃用户数量',
  `post_count` int(11) NULL DEFAULT 0 COMMENT '发帖数量',
  `activity_count` int(11) NULL DEFAULT 0 COMMENT '活动数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_region_date`(`region`, `stat_date`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_region`(`region`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '地域统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of region_stats
-- ----------------------------

-- ----------------------------
-- Table structure for report_generation_log
-- ----------------------------
DROP TABLE IF EXISTS `report_generation_log`;
CREATE TABLE `report_generation_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '报表类型：daily/weekly/monthly/custom',
  `report_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '报表标题',
  `generation_params` json NULL COMMENT '生成参数',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint(20) NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '状态：pending/processing/completed/failed',
  `generated_by` bigint(20) NOT NULL COMMENT '生成人ID',
  `generated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `download_count` int(11) NULL DEFAULT 0 COMMENT '下载次数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_report_type`(`report_type`) USING BTREE,
  INDEX `idx_generated_by`(`generated_by`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_generated_at`(`generated_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '报表生成记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report_generation_log
-- ----------------------------

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) UNSIGNED NOT NULL,
  `permission_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permissions`(`role_id`, `permission_id`) USING BTREE,
  INDEX `idx_role_permissions_role`(`role_id`) USING BTREE,
  INDEX `idx_role_permissions_permission`(`permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES (1, 1, 1, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (2, 1, 2, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (3, 1, 3, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (4, 1, 4, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (5, 1, 5, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (6, 1, 6, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (7, 1, 7, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (8, 1, 8, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (9, 1, 9, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (10, 1, 10, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (11, 1, 11, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (12, 1, 12, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (13, 1, 13, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (14, 1, 14, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (15, 1, 15, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (16, 1, 16, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (17, 1, 17, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (18, 1, 18, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (19, 1, 19, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (20, 1, 20, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (21, 1, 21, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (22, 1, 22, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (23, 1, 23, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (24, 1, 24, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (25, 1, 25, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (26, 1, 26, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (27, 1, 27, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (28, 1, 28, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (29, 1, 29, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (30, 1, 30, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (31, 1, 31, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (32, 1, 32, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (33, 1, 33, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (64, 2, 7, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (65, 2, 8, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (66, 2, 9, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (67, 2, 10, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (68, 2, 11, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (69, 2, 12, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (70, 2, 13, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (71, 2, 14, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (72, 2, 15, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (73, 2, 33, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (79, 3, 1, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (80, 3, 2, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (81, 3, 3, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (82, 3, 4, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (83, 3, 5, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (84, 3, 6, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (85, 3, 16, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (86, 3, 17, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (87, 3, 18, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (88, 3, 19, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (89, 3, 33, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (94, 4, 10, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (95, 4, 11, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (96, 4, 12, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (97, 4, 31, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (98, 4, 32, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (99, 4, 33, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (100, 4, 7, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (101, 4, 8, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (102, 4, 9, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (103, 4, 1, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (104, 4, 2, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (105, 4, 3, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (106, 4, 4, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (107, 4, 5, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (108, 4, 6, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (109, 5, 20, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (110, 5, 21, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (111, 5, 22, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (112, 5, 23, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (113, 5, 24, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (114, 5, 25, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (115, 5, 26, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (116, 5, 27, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (117, 5, 28, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (118, 5, 29, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (119, 5, 30, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (124, 6, 1, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (125, 6, 7, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (126, 6, 10, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (127, 6, 13, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (128, 6, 16, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (129, 6, 20, '2025-11-16 15:01:02');
INSERT INTO `role_permissions` VALUES (130, 6, 24, '2025-11-16 15:01:02');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_system` tinyint(1) NULL DEFAULT 0,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_roles_name`(`name`) USING BTREE,
  INDEX `idx_roles_status`(`status`) USING BTREE,
  INDEX `idx_roles_system`(`is_system`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'super_admin', '超级管理员', '拥有所有权限的超级管理员', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `roles` VALUES (2, 'content_admin', '内容管理员', '负责内容管理和审核', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `roles` VALUES (3, 'user_admin', '用户管理员', '负责用户管理和权限分配', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `roles` VALUES (4, 'data_admin', '数据管理员', '负责数据统计和导出', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `roles` VALUES (5, 'system_admin', '系统管理员', '负责系统配置和维护', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');
INSERT INTO `roles` VALUES (6, 'viewer', '查看员', '只有查看权限', 1, 'active', '2025-11-16 15:01:02', '2025-11-16 15:01:02');

-- ----------------------------
-- Table structure for scheduled_tasks
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_tasks`;
CREATE TABLE `scheduled_tasks`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cron_expression` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `last_run_at` timestamp NULL DEFAULT NULL,
  `next_run_at` timestamp NULL DEFAULT NULL,
  `run_count` int(11) NULL DEFAULT 0,
  `success_count` int(11) NULL DEFAULT 0,
  `failure_count` int(11) NULL DEFAULT 0,
  `timeout_seconds` int(11) NULL DEFAULT 300,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scheduled_tasks_name`(`task_name`) USING BTREE,
  INDEX `idx_scheduled_tasks_active`(`is_active`) USING BTREE,
  INDEX `idx_scheduled_tasks_next_run`(`next_run_at`) USING BTREE,
  INDEX `idx_scheduled_tasks_last_run`(`last_run_at`) USING BTREE,
  INDEX `idx_scheduled_tasks_run_count`(`run_count`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheduled_tasks
-- ----------------------------
INSERT INTO `scheduled_tasks` VALUES (1, '数据备份任务', 'com.xystapp.task.DataBackupTask', '0 0 2 * * ?', '每日凌晨2点执行数据备份', 1, NULL, '2025-11-16 02:00:00', 0, 0, 0, 300, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `scheduled_tasks` VALUES (2, '清理过期会话', 'com.xystapp.task.CleanExpiredSessionsTask', '0 30 * * * ?', '每小时清理过期会话', 1, NULL, '2025-11-16 15:31:03', 0, 0, 0, 300, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `scheduled_tasks` VALUES (3, '统计数据更新', 'com.xystapp.task.StatisticsUpdateTask', '0 0 1 * * ?', '每日凌晨1点更新统计数据', 1, NULL, '2025-11-16 01:00:00', 0, 0, 0, 300, '2025-11-16 15:01:03', '2025-11-16 15:01:03');
INSERT INTO `scheduled_tasks` VALUES (4, '发送系统通知', 'com.xystapp.task.SendNotificationTask', '0 */5 * * * ?', '每5分钟检查并发送待发送通知', 1, NULL, '2025-11-16 15:06:03', 0, 0, 0, 300, '2025-11-16 15:01:03', '2025-11-16 15:01:03');

-- ----------------------------
-- Table structure for search_configs
-- ----------------------------
DROP TABLE IF EXISTS `search_configs`;
CREATE TABLE `search_configs`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_active` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_search_config`(`config_key`) USING BTREE COMMENT '配置键唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '搜索配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of search_configs
-- ----------------------------

-- ----------------------------
-- Table structure for search_history
-- ----------------------------
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
  `user_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '搜索关键词',
  `search_type` enum('all','post','activity','user','field') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'all' COMMENT '搜索类型',
  `result_count` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '搜索结果数量',
  `search_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '搜索IP地址',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_search_history_user`(`user_id`) USING BTREE COMMENT '用户ID索引',
  INDEX `idx_search_history_keyword`(`keyword`) USING BTREE COMMENT '关键词索引',
  INDEX `idx_search_history_type`(`search_type`) USING BTREE COMMENT '搜索类型索引',
  INDEX `idx_search_history_created`(`created_at`) USING BTREE COMMENT '创建时间索引',
  INDEX `idx_search_history_user_keyword`(`user_id`, `keyword`) USING BTREE COMMENT '用户关键词复合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '搜索历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of search_history
-- ----------------------------

-- ----------------------------
-- Table structure for search_stats
-- ----------------------------
DROP TABLE IF EXISTS `search_stats`;
CREATE TABLE `search_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '搜索关键词',
  `search_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'all' COMMENT '搜索类型：post/user/activity/all',
  `search_count` int(11) NULL DEFAULT 0 COMMENT '搜索次数',
  `result_count` int(11) NULL DEFAULT 0 COMMENT '平均结果数量',
  `click_count` int(11) NULL DEFAULT 0 COMMENT '点击次数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_date_keyword_type`(`stat_date`, `keyword`, `search_type`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_keyword`(`keyword`) USING BTREE,
  INDEX `idx_search_type`(`search_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '搜索统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of search_stats
-- ----------------------------

-- ----------------------------
-- Table structure for system_config_audit_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_config_audit_logs`;
CREATE TABLE `system_config_audit_logs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `config_id` bigint(20) NOT NULL COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `old_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '旧值',
  `new_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '新值',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作者ID',
  `operator_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作者IP',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_id`(`config_id`) USING BTREE,
  INDEX `idx_config_key`(`config_key`) USING BTREE,
  INDEX `idx_operator_id`(`operator_id`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config_audit_logs
-- ----------------------------

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置类型',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `is_system` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为系统配置',
  `created_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `updated_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key`) USING BTREE,
  INDEX `idx_config_type`(`config_type`) USING BTREE,
  INDEX `idx_is_enabled`(`is_enabled`) USING BTREE,
  INDEX `idx_is_system`(`is_system`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE,
  INDEX `idx_updated_at`(`updated_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_configs
-- ----------------------------

-- ----------------------------
-- Table structure for system_notifications
-- ----------------------------
DROP TABLE IF EXISTS `system_notifications`;
CREATE TABLE `system_notifications`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('info','warning','error','success') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'info',
  `target_type` enum('all','admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'admin',
  `target_id` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `read_at` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) UNSIGNED NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_system_notifications_target`(`target_type`, `target_id`) USING BTREE,
  INDEX `idx_system_notifications_read`(`is_read`) USING BTREE,
  INDEX `idx_system_notifications_created`(`created_at`) USING BTREE,
  INDEX `idx_system_notifications_type`(`type`) USING BTREE,
  INDEX `idx_system_notifications_expires`(`expires_at`) USING BTREE,
  INDEX `idx_system_notifications_created_by`(`created_by`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_notifications
-- ----------------------------
INSERT INTO `system_notifications` VALUES (1, '系统维护通知', '系统将于今晚22:00-24:00进行维护升级', 'warning', 'all', NULL, 0, NULL, 1, '2025-11-16 15:01:03', NULL);
INSERT INTO `system_notifications` VALUES (2, '新功能上线', '用户认证功能已正式上线', 'success', 'admin', NULL, 0, NULL, 1, '2025-11-16 15:01:03', NULL);
INSERT INTO `system_notifications` VALUES (3, '安全提醒', '请定期更换管理员密码确保安全', 'info', 'admin', NULL, 0, NULL, 1, '2025-11-16 15:01:03', NULL);
INSERT INTO `system_notifications` VALUES (4, '数据备份完成', '昨日数据备份已成功完成', 'success', 'admin', NULL, 0, NULL, 2, '2025-11-16 15:01:03', NULL);

-- ----------------------------
-- Table structure for system_performance_stats
-- ----------------------------
DROP TABLE IF EXISTS `system_performance_stats`;
CREATE TABLE `system_performance_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint(4) NOT NULL COMMENT '统计小时（0-23）',
  `api_call_count` int(11) NULL DEFAULT 0 COMMENT 'API调用次数',
  `avg_response_time_ms` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平均响应时间（毫秒）',
  `error_count` int(11) NULL DEFAULT 0 COMMENT '错误次数',
  `cache_hit_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '缓存命中率（%）',
  `db_query_count` int(11) NULL DEFAULT 0 COMMENT '数据库查询次数',
  `avg_db_query_time_ms` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平均数据库查询时间（毫秒）',
  `cpu_usage_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT 'CPU使用率（%）',
  `memory_usage_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '内存使用率（%）',
  `disk_usage_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '磁盘使用率（%）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_date_hour`(`stat_date`, `stat_hour`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统性能统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_performance_stats
-- ----------------------------

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `color` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签颜色',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签描述',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_name`(`name`) USING BTREE COMMENT '标签名称唯一索引',
  INDEX `idx_tag_sort`(`sort_order`) USING BTREE COMMENT '排序索引',
  INDEX `idx_tag_active`(`is_active`) USING BTREE COMMENT '启用状态索引'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tags
-- ----------------------------
INSERT INTO `tags` VALUES (1, '运动健身', '#FF6B6B', '各类运动健身活动场地', 1, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (2, '文化艺术', '#4ECDC4', '文化艺术相关场地', 2, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (3, '教育培训', '#45B7D1', '教育培训类场地', 3, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (4, '商务办公', '#96CEB4', '商务办公会议场地', 4, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (5, '休闲娱乐', '#FECA57', '休闲娱乐场所', 5, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (6, '户外活动', '#FF9FF3', '户外运动活动场地', 6, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (7, '餐饮聚会', '#54A0FF', '餐饮聚会场所', 7, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');
INSERT INTO `tags` VALUES (8, '科技创新', '#48DBFB', '科技创新相关场地', 8, 1, '2025-11-16 18:26:32', '2025-11-16 18:26:32');

-- ----------------------------
-- Table structure for task_execution_logs
-- ----------------------------
DROP TABLE IF EXISTS `task_execution_logs`;
CREATE TABLE `task_execution_logs`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) UNSIGNED NOT NULL,
  `execution_status` enum('running','success','failed','timeout') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  `duration_seconds` int(11) NULL DEFAULT NULL,
  `output_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_execution_logs_task`(`task_id`) USING BTREE,
  INDEX `idx_task_execution_logs_status`(`execution_status`) USING BTREE,
  INDEX `idx_task_execution_logs_start`(`start_time`) USING BTREE,
  INDEX `idx_task_execution_logs_task_start`(`task_id`, `start_time`) USING BTREE,
  INDEX `idx_task_execution_logs_duration`(`duration_seconds`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_execution_logs
-- ----------------------------
INSERT INTO `task_execution_logs` VALUES (1, 1, 'success', '2025-11-15 15:01:03', '2025-11-15 15:01:03', 120, '数据备份完成，备份文件大小：1.2GB', NULL, '2025-11-16 15:01:03');
INSERT INTO `task_execution_logs` VALUES (2, 2, 'success', '2025-11-16 14:01:03', '2025-11-16 14:01:03', 5, '清理过期会话15个', NULL, '2025-11-16 15:01:03');
INSERT INTO `task_execution_logs` VALUES (3, 3, 'success', '2025-11-16 13:01:03', '2025-11-16 13:01:03', 300, '统计数据更新完成', NULL, '2025-11-16 15:01:03');
INSERT INTO `task_execution_logs` VALUES (4, 4, 'failed', '2025-11-16 14:31:03', '2025-11-16 14:31:03', 10, '邮件服务连接失败', NULL, '2025-11-16 15:01:03');

-- ----------------------------
-- Table structure for user_behavior_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_behavior_stats`;
CREATE TABLE `user_behavior_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `post_count` int(11) NULL DEFAULT 0 COMMENT '发帖数量',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数量',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数量',
  `share_count` int(11) NULL DEFAULT 0 COMMENT '分享数量',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览数量',
  `message_count` int(11) NULL DEFAULT 0 COMMENT '消息数量',
  `search_count` int(11) NULL DEFAULT 0 COMMENT '搜索次数',
  `active_duration_minutes` int(11) NULL DEFAULT 0 COMMENT '活跃时长（分钟）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id`, `stat_date`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户行为统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behavior_stats
-- ----------------------------

-- ----------------------------
-- Table structure for user_fields
-- ----------------------------
DROP TABLE IF EXISTS `user_fields`;
CREATE TABLE `user_fields`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `field_id` bigint(20) UNSIGNED NOT NULL,
  `role` enum('member','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'member',
  `joined_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_fields`(`user_id`, `field_id`) USING BTREE,
  INDEX `idx_user_fields_user`(`user_id`) USING BTREE,
  INDEX `idx_user_fields_field`(`field_id`) USING BTREE,
  INDEX `idx_user_fields_role`(`role`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_fields
-- ----------------------------

-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `follower_id` bigint(20) UNSIGNED NOT NULL,
  `following_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_follows`(`follower_id`, `following_id`) USING BTREE,
  INDEX `idx_user_follows_follower`(`follower_id`) USING BTREE,
  INDEX `idx_user_follows_following`(`following_id`) USING BTREE,
  INDEX `idx_user_follows_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_follows
-- ----------------------------

-- ----------------------------
-- Table structure for user_retention_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_retention_stats`;
CREATE TABLE `user_retention_stats`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `register_date` date NOT NULL COMMENT '注册日期',
  `day1_retained` tinyint(1) NULL DEFAULT 0 COMMENT '第1日留存',
  `day3_retained` tinyint(1) NULL DEFAULT 0 COMMENT '第3日留存',
  `day7_retained` tinyint(1) NULL DEFAULT 0 COMMENT '第7日留存',
  `day30_retained` tinyint(1) NULL DEFAULT 0 COMMENT '第30日留存',
  `last_active_date` date NULL DEFAULT NULL COMMENT '最后活跃日期',
  `total_active_days` int(11) NULL DEFAULT 0 COMMENT '总活跃天数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_register_date`(`register_date`) USING BTREE,
  INDEX `idx_last_active_date`(`last_active_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户留存统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_retention_stats
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `background` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  `is_certified` tinyint(1) NULL DEFAULT 0,
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `school_certified` tinyint(1) NULL DEFAULT 0,
  `uid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `mbti` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `show_gender` tinyint(1) NULL DEFAULT 1,
  `show_age` tinyint(1) NULL DEFAULT 1,
  `show_mbti` tinyint(1) NULL DEFAULT 1,
  `followers_count` int(11) NULL DEFAULT 0,
  `following_count` int(11) NULL DEFAULT 0,
  `field_count` int(11) NULL DEFAULT 0,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_users_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_users_uid`(`uid`) USING BTREE,
  INDEX `idx_users_username`(`username`) USING BTREE,
  INDEX `idx_users_school`(`school`) USING BTREE,
  INDEX `idx_users_mbti`(`mbti`) USING BTREE,
  INDEX `idx_users_location`(`location`) USING BTREE,
  INDEX `idx_users_status`(`status`) USING BTREE,
  INDEX `idx_users_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'xiaoyazhi', '会吃西瓜的小鸭纸', '/static/follow/follow-users-section/Ellipse 11.png', '/static/profile/Rectangle 279.png', '我也不知道简介写什么好\n什么时候才能放假啊啊啊啊啊啊啊啊啊哈哈就是说日子一天也过不下去了\n快放我回家！\n简介只许写五行多了不许写了：）', 'male', '2003-05-15', 21, 1, '中央民族大学', 1, '542312132', '美国', 'INFP', 1, 1, 1, 155, 33, 20, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `users` VALUES (2, 'username2', '假如说名字很长的话十一二三四五六七八九十一二三四', '/static/follow/follow-users-section/Ellipse 2.png', NULL, NULL, 'female', NULL, 19, 0, NULL, 0, '123456789', '北京', 'ESFJ', 0, 1, 1, 89, 45, 15, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `users` VALUES (3, 'user3', '你叫什么名字', '/static/follow/follow-users-section/Ellipse 13.png', NULL, NULL, 'other', NULL, 22, 1, '清华大学', 1, '987654321', '上海', 'INTJ', 1, 0, 1, 234, 67, 8, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `users` VALUES (4, 'grouplord', '群主大人', '/static/follow/follow-users-section/Ellipse 14.png', NULL, NULL, 'male', NULL, 25, 1, '北京大学', 1, '456789123', '深圳', 'ENTP', 1, 1, 1, 567, 123, 25, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `users` VALUES (5, 'activeuser', '不知名用户', '/static/follow/follow-users-section/Ellipse 15.png', NULL, NULL, 'female', NULL, 20, 0, '复旦大学', 0, '789123456', '广州', 'ISFP', 1, 1, 1, 345, 89, 12, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');
INSERT INTO `users` VALUES (6, 'newuser', '新用户', '/static/follow/follow-users-section/Ellipse 16.png', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '111222333', NULL, NULL, 1, 1, 1, 0, 0, 0, 'active', '2025-11-16 15:00:26', '2025-11-16 15:00:26');

-- ----------------------------
-- Table structure for view_history
-- ----------------------------
DROP TABLE IF EXISTS `view_history`;
CREATE TABLE `view_history`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `item_type` enum('post','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_view_history_user`(`user_id`) USING BTREE,
  INDEX `idx_view_history_item`(`item_type`, `item_id`) USING BTREE,
  INDEX `idx_view_history_created`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of view_history
-- ----------------------------

-- ----------------------------
-- View structure for v_activities_with_field_tags
-- ----------------------------
DROP VIEW IF EXISTS `v_activities_with_field_tags`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_activities_with_field_tags` AS select `a`.`id` AS `id`,`a`.`user_id` AS `user_id`,`a`.`title` AS `title`,`a`.`date` AS `date`,`a`.`day` AS `day`,`a`.`time` AS `time`,`a`.`location` AS `location`,`a`.`tag` AS `tag`,`a`.`field_id` AS `field_id`,`a`.`participants` AS `participants`,`a`.`max_participants` AS `max_participants`,`a`.`likes_count` AS `likes_count`,`a`.`image` AS `image`,`a`.`status` AS `status`,`a`.`created_at` AS `created_at`,`a`.`updated_at` AS `updated_at`,`f`.`name` AS `field_name`,group_concat(`t`.`name` order by `t`.`sort_order` ASC separator ',') AS `field_tag_names`,group_concat(`t`.`color` order by `t`.`sort_order` ASC separator ',') AS `field_tag_colors` from (((`activities` `a` left join `fields` `f` on((`a`.`field_id` = `f`.`id`))) left join `field_tags` `ft` on((`f`.`id` = `ft`.`field_id`))) left join `tags` `t` on(((`ft`.`tag_id` = `t`.`id`) and (`t`.`is_active` = 1)))) group by `a`.`id`,`a`.`user_id`,`a`.`title`,`a`.`date`,`a`.`day`,`a`.`time`,`a`.`location`,`a`.`tag`,`a`.`field_id`,`a`.`participants`,`a`.`max_participants`,`a`.`likes_count`,`a`.`image`,`a`.`status`,`a`.`created_at`,`a`.`updated_at`,`f`.`name`;

-- ----------------------------
-- View structure for v_fields_with_tags
-- ----------------------------
DROP VIEW IF EXISTS `v_fields_with_tags`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_fields_with_tags` AS select `f`.`id` AS `id`,`f`.`name` AS `name`,`f`.`description` AS `description`,`f`.`avatar` AS `avatar`,`f`.`creator_id` AS `creator_id`,`f`.`members_count` AS `members_count`,`f`.`created_at` AS `created_at`,`f`.`updated_at` AS `updated_at`,group_concat(`t`.`name` order by `t`.`sort_order` ASC separator ',') AS `tag_names`,group_concat(`t`.`color` order by `t`.`sort_order` ASC separator ',') AS `tag_colors` from ((`fields` `f` left join `field_tags` `ft` on((`f`.`id` = `ft`.`field_id`))) left join `tags` `t` on(((`ft`.`tag_id` = `t`.`id`) and (`t`.`is_active` = 1)))) group by `f`.`id`,`f`.`name`,`f`.`description`,`f`.`avatar`,`f`.`creator_id`,`f`.`members_count`,`f`.`created_at`,`f`.`updated_at`;

SET FOREIGN_KEY_CHECKS = 1;
