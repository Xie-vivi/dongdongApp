# XYST社交平台全栈开发计划

## 📋 项目概述
基于现有数据库设计（31个表），创建完整的全栈应用：
- **后台API**: SpringBoot + MySQL + Redis (根目录/back)
- **前端应用**: Vue.js (根目录/front)  
- **后台管理**: Vue.js + Element UI (根目录/front/admin)

## 🏗️ 项目结构
```
xystapp/
├── android/              # 安卓移动端（已完成）
├── database/             # 数据库设计（已完成）
├── back/                 # SpringBoot后台API
│   ├── src/main/java/
│   │   ├── com/xystapp/
│   │   │   ├── config/    # 配置类
│   │   │   ├── controller/ # 控制器
│   │   │   ├── service/   # 服务层
│   │   │   ├── mapper/    # 数据访问层
│   │   │   ├── entity/    # 实体类
│   │   │   ├── dto/       # 数据传输对象
│   │   │   ├── common/    # 公共类
│   │   │   └── utils/     # 工具类
│   │   └── resources/
│   │       ├── application.yml
│   │       └── mapper/    # MyBatis映射文件
│   └── pom.xml
└── front/                # Vue前端应用
    ├── src/
    │   ├── api/          # API接口
    │   ├── components/   # 公共组件
    │   ├── views/        # 页面组件
    │   ├── router/       # 路由配置
    │   ├── store/        # 状态管理
    │   ├── utils/        # 工具函数
    │   └── assets/       # 静态资源
    ├── admin/            # 后台管理界面
    │   ├── src/
    │   │   ├── api/
    │   │   ├── views/
    │   │   ├── components/
    │   │   └── router/
    │   └── package.json
    └── package.json
```

## 🎯 开发优先级
- **P0 (核心基础)**: 用户认证、基础CRUD、文件上传
- **P1 (核心功能)**: 社交互动、消息系统、内容管理
- **P2 (高级功能)**: 后台管理、数据分析、系统配置

## 📊 详细实现计划表

### 1. 用户认证模块 (P0)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 用户注册 | POST /api/auth/register | 注册页面 | 用户管理 | P0 | ✅ | - |
| 用户登录 | POST /api/auth/login | 登录页面 | 登录日志 | P0 | ✅ | - |
| JWT认证 | POST /api/auth/refresh | 令牌刷新 | 会话管理 | P0 | ✅ | - |
| 密码重置 | POST /api/auth/reset | 找回密码 | 密码管理 | P0 | ✅ | - |
| 校园认证 | POST /api/users/school-certify | 校园认证页 | 认证审核 | P0 | ✅ | - |

### 2. 用户管理模块 (P0)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 个人资料 | GET/PUT /api/users/profile | edit-profile | 用户详情 | P0 | ✅ | 认证模块 |
| 用户主页 | GET /api/users/{id} | user-profile | 用户列表 | P0 | ✅ | 认证模块 |
| 用户搜索 | GET /api/users/search | 搜索页面 | 用户搜索 | P0 | ✅ | 认证模块 |
| 头像上传 | POST /api/users/avatar | 头像编辑 | 文件管理 | P0 | ✅ | 认证模块 |
| 背景图上传 | POST /api/users/background | 背景图编辑 | 文件管理 | P0 | ✅ | 认证模块 |

### 3. 内容管理模块 (P0)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 帖子CRUD | GET/POST/PUT/DELETE /api/posts | 帖子详情页 | 帖子管理 | P0 | ✅ | 用户模块 |
| 活动CRUD | GET/POST/PUT/DELETE /api/activities | 活动详情页 | 活动管理 | P0 | ✅ | 用户模块 |
| 内容列表 | GET /api/posts/list | 首页/发现 | 内容审核 | P0 | ✅ | 内容CRUD |
| 内容搜索 | GET /api/posts/search | 搜索页面 | 内容搜索 | P0 | ✅ | 内容CRUD |
| 草稿管理 | GET/POST/PUT/DELETE /api/drafts | 草稿页面 | 草稿管理 | P0 | ✅ | 内容CRUD |

### 4. 社交互动模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 关注系统 | POST/DELETE /api/follow | 关注页面 | 关注管理 | P1 | ✅ | 用户模块 |
| 粉丝列表 | GET /api/follow/followers | 粉丝列表 | 关注统计 | P1 | ✅ | 关注系统 |
| 关注列表 | GET /api/follow/following | 关注列表 | 关注统计 | P1 | ✅ | 关注系统 |
| 点赞功能 | POST/DELETE /api/likes | 帖子/活动页 | 点赞管理 | P1 | ✅ | 内容模块 |
| 收藏功能 | POST/DELETE /api/stars | 帖子/活动页 | 收藏管理 | P1 | ✅ | 内容模块 |

### 5. 评论系统模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 评论CRUD | GET/POST/PUT/DELETE /api/comments | 帖子详情页 | 评论管理 | P1 | ✅ | 内容模块 |
| @用户功能 | POST /api/comments/mention | 评论输入框 | @用户管理 | P1 | ✅ | 评论CRUD |
| 回复功能 | POST /api/comments/reply | 评论列表 | 回复管理 | P1 | ✅ | 评论CRUD |
| 评论列表 | GET /api/comments/{postId} | 帖子详情页 | 评论审核 | P1 | ✅ | 评论CRUD |

### 6. 消息系统模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 消息列表 | GET /api/messages | 消息页面 | 消息管理 | P1 | ✅ | 用户模块 |
| 聊天详情 | GET /api/messages/{chatId} | chat-detail | 聊天记录 | P1 | ✅ | 消息列表 |
| 发送消息 | POST /api/messages/send | chat-detail | 消息审核 | P1 | ✅ | 聊天详情 |
| 私聊创建 | POST /api/messages/private | 用户主页 | 聊天管理 | P1 | ✅ | 消息列表 |
| 群聊管理 | POST/PUT/DELETE /api/chats | 群聊页面 | 群聊管理 | P1 | ✅ | 消息列表 |
| WebSocket实时消息 | ws://api/ws | 聊天页面 | 连接监控 | P1 | ✅ | 消息系统 |
| 在线状态管理 | Redis状态 | 用户列表 | 在线统计 | P1 | ✅ | WebSocket |
| 输入状态广播 | STOMP消息 | 聊天界面 | 状态监控 | P1 | ✅ | WebSocket |

### 7. 通知系统模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 通知列表 | GET /api/notifications | 消息页面 | 通知管理 | P1 | ✅ | 社交模块 |
| 标记已读 | PUT /api/notifications/read | 通知列表 | 通知统计 | P1 | ✅ | 通知列表 |
| 通知设置 | GET/PUT /api/notifications/settings | 设置页面 | 通知配置 | P1 | ✅ | 通知列表 |

### 8. 场地管理模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 场地列表 | GET /api/fields | 场地页面 | 场地管理 | P1 | ✅ | 用户模块 |
| 场地创建 | POST /api/fields | 创建场地 | 场地审核 | P1 | ✅ | 场地列表 |
| 加入场地 | POST /api/fields/join | 场地详情 | 成员管理 | P1 | ✅ | 场地列表 |
| 场地成员 | GET /api/fields/{id}/members | 场地详情 | 成员列表 | P1 | ✅ | 加入场地 |

### 9. 活动报名模块 (P1)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 活动报名 | POST /api/activities/signup | 活动详情页 | 报名管理 | P1 | ✅ | 活动模块 |
| 取消报名 | DELETE /api/activities/signup | 活动详情页 | 报名管理 | P1 | ✅ | 活动报名 |
| 报名列表 | GET /api/activities/{id}/signups | 活动详情页 | 报名审核 | P1 | ✅ | 活动报名 |
| 参与统计 | GET /api/activities/stats | 活动页面 | 数据统计 | P1 | ✅ | 报名列表 |

### 10. 浏览记录模块 (P2)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 记录浏览 | POST /api/history/track | 所有页面 | 浏览统计 | P2 | ✅ | 用户模块 |
| 浏览历史 | GET /api/history | 个人主页 | 历史记录 | P2 | ✅ | 记录浏览 |
| 热门内容 | GET /api/history/trending | 发现页面 | 热度分析 | P2 | ✅ | 浏览历史 |

### 11. 后台管理模块 (P2)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 管理员登录 | POST /api/admin/auth/login | - | 登录页面 | P2 | ✅ | - |
| 权限管理 | GET/POST/PUT/DELETE /api/admin/permissions | - | 权限管理 | P2 | ✅ | 管理员登录 |
| 角色管理 | GET/POST/PUT/DELETE /api/admin/roles | - | 角色管理 | P2 | ✅ | 权限管理 |
| 用户管理 | GET/POST/PUT/DELETE /api/admin/users | - | 用户管理 | P2 | ✅ | 角色管理 |
| 内容审核 | GET/PUT /api/admin/content/audit | - | 内容审核 | P2 | ✅ | 用户管理 |
| 数据统计 | GET /api/admin/statistics | - | 数据面板 | P2 | ✅ | 内容审核 |
| 系统配置 | GET/PUT /api/admin/config | - | 系统设置 | P2 | ✅ | 数据统计 |
| 配置管理 | GET/POST/PUT/DELETE /system/configs | - | 配置管理 | P2 | ✅ | 系统配置 |
| 配置缓存 | POST /system/configs/refresh-cache | - | 缓存管理 | P2 | ✅ | 配置管理 |
| 配置审计 | GET /system/configs/audit | - | 审计日志 | P2 | ✅ | 配置管理 |

### 12. 数据分析模块 (P2)

| 功能模块 | 后端API接口 | 前端页面 | 管理界面 | 优先级 | 状态 | 依赖 |
|---------|------------|---------|---------|-------|------|------|
| 系统概览统计 | GET /api/analytics/overview | - | 数据面板 | P2 | ✅ | 后台管理 |
| 实时数据统计 | GET /api/analytics/realtime | - | 实时监控 | P2 | ✅ | 系统概览 |
| 用户增长分析 | GET /api/analytics/users/growth | - | 用户分析 | P2 | ✅ | 实时统计 |
| 用户活跃度分析 | GET /api/analytics/users/activity | - | 用户分析 | P2 | ✅ | 用户增长 |
| 内容发布趋势 | GET /api/analytics/content/trend | - | 内容分析 | P2 | ✅ | 用户活跃度 |
| 社交互动分析 | GET /api/analytics/social/trend | - | 社交分析 | P2 | ✅ | 内容趋势 |
| 活动参与分析 | GET /api/analytics/activities/participation-trend | - | 活动分析 | P2 | ✅ | 社交互动 |
| 消息发送分析 | GET /api/analytics/messages/trend | - | 消息分析 | P2 | ✅ | 活动参与 |
| 搜索行为分析 | GET /api/analytics/search/behavior | - | 搜索分析 | P2 | ✅ | 消息发送 |
| 系统性能监控 | GET /api/analytics/performance/system | - | 性能监控 | P2 | ✅ | 搜索行为 |
| 业务指标分析 | GET /api/analytics/business/core-metrics | - | 业务分析 | P2 | ✅ | 性能监控 |
| 报表生成 | GET /api/analytics/reports/* | - | 报表管理 | P2 | ✅ | 业务指标 |
| 数据导出 | POST /api/analytics/export/* | - | 数据导出 | P2 | ✅ | 报表生成 |
| 预测分析 | GET /api/analytics/prediction/* | - | 预测分析 | P2 | ✅ | 数据导出 |

### 标签场地活动关联模块
| 功能模块 | API接口 | 前端页面 | 所属模块 | 优先级 | 状态 | 依赖 |
|---------|---------|---------|---------|--------|------|------|
| 标签管理 | GET/POST/PUT/DELETE /api/tags | 标签管理页面 | 数据管理 | P1 | ✅ | 权限系统 |
| 场地标签关联 | POST/DELETE /api/tags/field/{fieldId}/* | 场地标签管理 | 场地管理 | P1 | ✅ | 标签管理 |
| 活动场地关联 | GET/POST/DELETE /api/activities/field/* | 活动场地管理 | 活动管理 | P1 | ✅ | 场地管理 |
| 标签活动查询 | GET /api/activities/field/by-tag/{tagId} | 标签活动列表 | 数据查询 | P1 | ✅ | 活动场地关联 |
| 场地活动查询 | GET /api/activities/field/by-field/{fieldId} | 场地活动列表 | 数据查询 | P1 | ✅ | 活动场地关联 |

## 🚀 开发阶段规划

### 第一阶段：基础架构 (1-2周)
- [ ] 创建SpringBoot项目结构
- [ ] 配置数据库连接和Redis
- [ ] 实现JWT认证机制
- [ ] 创建Vue前端项目结构
- [ ] 配置路由和状态管理

### 第二阶段：核心功能 (2-3周)
- [ ] 用户认证和注册
- [ ] 个人资料管理
- [ ] 帖子和活动CRUD
- [ ] 基础前端页面

### 第三阶段：社交功能 (2-3周)
- [ ] 关注和粉丝系统
- [ ] 点赞和收藏功能
- [ ] 评论和回复系统
- [ ] 消息和通知系统

### 第四阶段：高级功能 (2-3周)
- [ ] 场地管理
- [ ] 活动报名系统
- [ ] 文件上传和管理
- [ ] 搜索功能

### 第五阶段：后台管理 (2-3周)
- [ ] 管理员权限系统
- [ ] 内容审核功能
- [ ] 数据统计面板
- [ ] 系统配置管理

## 📈 进度跟踪

**当前进度**: 0/55 功能模块完成

**已完成**:
- [x] 数据库设计 (31个表)
- [x] 开发计划制定

**进行中**:
- [ ] 项目结构创建

**下一步**: 创建SpringBoot和Vue项目基础结构

---

*最后更新时间: 2025-11-16*
*文档维护者: 开发团队*
