-- 数据库检查脚本
-- 请在MySQL客户端或数据库管理工具中执行

-- 1. 检查数据库是否存在
SHOW DATABASES LIKE 'xystapp';

-- 2. 使用数据库
USE xystapp;

-- 3. 检查表是否存在
SHOW TABLES;

-- 4. 检查关键表的记录数
SELECT 'users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'admins' as table_name, COUNT(*) as record_count FROM admins
UNION ALL
SELECT 'activities' as table_name, COUNT(*) as record_count FROM activities
UNION ALL
SELECT 'posts' as table_name, COUNT(*) as record_count FROM posts;

-- 5. 检查管理员账号
SELECT id, username, email, real_name, status FROM admins;
