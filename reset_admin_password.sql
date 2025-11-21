-- 重置管理员密码脚本
-- 密码: 123456 (BCrypt加密)

USE xystapp;

-- 重置超级管理员密码为 123456
UPDATE admins SET password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE username = 'superadmin';

-- 重置内容管理员密码为 123456  
UPDATE admins SET password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE username = 'content_admin';

-- 重置用户管理员密码为 123456
UPDATE admins SET password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE username = 'user_admin';

-- 重置普通管理员密码为 123456
UPDATE admins SET password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE username = 'normal_admin';

-- 解锁并重置锁定管理员密码为 123456
UPDATE admins SET 
    password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    status = 'active',
    locked_until = NULL,
    failed_login_count = 0
WHERE username = 'locked_admin';

-- 显示更新结果
SELECT username, email, real_name, status FROM admins WHERE status = 'active';

COMMIT;
