// 管理后台布局组件
const AdminLayout = () => import('@/views/admin/Layout.vue')

// 管理后台路由配置
const adminRoutes = [
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdminAuth: true },
    children: [
      // 仪表盘
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard/index.vue'),
        meta: { 
          title: '仪表盘',
          icon: 'Odometer',
          requiresAdminAuth: true,
          permission: 'dashboard:view'
        }
      },
      
      // 用户管理
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users/index.vue'),
        meta: { 
          title: '用户管理',
          icon: 'User',
          requiresAdminAuth: true,
          permission: 'user:view'
        }
      },
      {
        path: 'users/:id',
        name: 'AdminUserDetail',
        component: () => import('@/views/admin/Users/components/UserDetail.vue'),
        meta: { 
          title: '用户详情',
          requiresAdminAuth: true,
          permission: 'user:view'
        }
      },
      {
        path: 'users/:id/edit',
        name: 'AdminUserEdit',
        component: () => import('@/views/admin/Users/components/UserEdit.vue'),
        meta: { 
          title: '编辑用户',
          requiresAdminAuth: true,
          permission: 'user:update'
        }
      },
      {
        path: 'users/:id/behavior',
        name: 'AdminUserBehavior',
        component: () => import('@/views/admin/Users/components/UserBehavior.vue'),
        meta: { 
          title: '用户行为分析',
          requiresAdminAuth: true,
          permission: 'user:behavior:view'
        }
      },
      {
        path: 'users/:id/analytics',
        name: 'AdminUserAnalytics',
        component: () => import('@/views/admin/Users/components/UserAnalytics.vue'),
        meta: { 
          title: '用户数据分析',
          requiresAdminAuth: true,
          permission: 'user:analytics:view'
        }
      },
      
      // 内容审核
      {
        path: 'content',
        name: 'AdminContent',
        redirect: '/admin/content/posts'
      },
      {
        path: 'content/posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/Content/Posts.vue'),
        meta: { 
          title: '帖子管理',
          icon: 'Document',
          requiresAdminAuth: true,
          permission: 'content:view'
        }
      },
      {
        path: 'content/activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/Content/Activities.vue'),
        meta: { 
          title: '活动管理',
          icon: 'Calendar',
          requiresAdminAuth: true,
          permission: 'content:view'
        }
      },
      {
        path: 'content/audit',
        name: 'AdminAudit',
        component: () => import('@/views/admin/Content/Audit.vue'),
        meta: { 
          title: '内容审核',
          icon: 'View',
          requiresAdminAuth: true,
          permission: 'content:audit'
        }
      },
      
      // 系统配置
      {
        path: 'system',
        name: 'AdminSystem',
        redirect: '/admin/system/config'
      },
      {
        path: 'system/config',
        name: 'AdminConfig',
        component: () => import('@/views/admin/System/Config.vue'),
        meta: { 
          title: '系统配置',
          icon: 'Setting',
          requiresAdminAuth: true,
          permission: 'system:config:view'
        }
      },
      {
        path: 'system/logs',
        name: 'AdminLogs',
        component: () => import('@/views/admin/System/Logs.vue'),
        meta: { 
          title: '操作日志',
          icon: 'DocumentCopy',
          requiresAdminAuth: true,
          permission: 'system:log:view'
        }
      },
      
      // 权限管理
      {
        path: 'permissions',
        name: 'AdminPermissions',
        redirect: '/admin/permissions/administrators'
      },
      {
        path: 'permissions/administrators',
        name: 'AdminAdministrators',
        component: () => import('@/views/admin/Permissions/Administrators.vue'),
        meta: { 
          title: '管理员管理',
          icon: 'UserFilled',
          requiresAdminAuth: true,
          permission: 'admin:view'
        }
      },
      {
        path: 'permissions/roles',
        name: 'AdminRoles',
        component: () => import('@/views/admin/Permissions/Roles.vue'),
        meta: { 
          title: '角色管理',
          icon: 'Avatar',
          requiresAdminAuth: true,
          permission: 'role:view'
        }
      },
      
      // 标签管理
      {
        path: 'tags',
        name: 'AdminTags',
        component: () => import('@/views/admin/Tags/List.vue'),
        meta: { 
          title: '标签管理',
          icon: 'CollectionTag',
          requiresAdminAuth: true,
          permission: 'tag:view'
        }
      },
      
      // 数据分析
      {
        path: 'analytics',
        name: 'AdminAnalytics',
        component: () => import('@/views/admin/Analytics/index.vue'),
        meta: { 
          title: '数据分析',
          icon: 'DataAnalysis',
          requiresAdminAuth: true,
          permission: 'analytics:view'
        }
      },
      
      // 文件管理
      {
        path: 'files',
        name: 'AdminFiles',
        component: () => import('@/views/admin/Files/List.vue'),
        meta: { 
          title: '文件管理',
          icon: 'Folder',
          requiresAdminAuth: true,
          permission: 'file:view'
        }
      }
    ]
  },
  
  // 权限管理模块
  {
    path: '/permissions',
    name: 'AdminPermissions',
    component: () => import('@/views/admin/Permissions/index.vue'),
    meta: { requiresAdminAuth: true, title: '权限管理' }
  },
  
  // 数据分析模块
  {
    path: '/analytics',
    name: 'AdminAnalytics',
    component: () => import('@/views/admin/Analytics/index.vue'),
    meta: { requiresAdminAuth: true, title: '数据分析' }
  },
  {
    path: '/analytics/user-behavior',
    name: 'AdminUserBehavior',
    component: () => import('@/views/admin/Analytics/UserBehavior.vue'),
    meta: { requiresAdminAuth: true, title: '用户行为统计' }
  },
  {
    path: '/analytics/content-analysis',
    name: 'AdminContentAnalysis',
    component: () => import('@/views/admin/Analytics/ContentAnalysis.vue'),
    meta: { requiresAdminAuth: true, title: '内容数据分析' }
  },
  {
    path: '/analytics/system-performance',
    name: 'AdminSystemPerformance',
    component: () => import('@/views/admin/Analytics/SystemPerformance.vue'),
    meta: { requiresAdminAuth: true, title: '系统性能监控' }
  },
  {
    path: '/analytics/reports',
    name: 'AdminReports',
    component: () => import('@/views/admin/Analytics/Reports.vue'),
    meta: { requiresAdminAuth: true, title: '报表管理' }
  },
  
  // 文件管理模块
  {
    path: '/file-manager',
    name: 'FileManager',
    component: () => import('@/views/admin/FileManager/index.vue'),
    meta: { requiresAdminAuth: true, title: '文件管理' }
  },
  {
    path: '/file-manager/upload',
    name: 'FileUpload',
    component: () => import('@/views/admin/FileManager/components/FileUpload.vue'),
    meta: { requiresAdminAuth: true, title: '文件上传' }
  },
  {
    path: '/file-manager/files',
    name: 'FileList',
    component: () => import('@/views/admin/FileManager/components/FileList.vue'),
    meta: { requiresAdminAuth: true, title: '文件列表' }
  },
  {
    path: '/file-manager/storage-config',
    name: 'StorageConfig',
    component: () => import('@/views/admin/FileManager/StorageConfig.vue'),
    meta: { requiresAdminAuth: true, title: '存储配置' }
  },
  
  // 操作日志模块
  {
    path: '/operation-logs',
    name: 'OperationLogs',
    component: () => import('@/views/admin/OperationLogs/index.vue'),
    meta: { requiresAdminAuth: true, title: '操作日志' }
  },
  
  // 管理员登录页面
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: { requiresAdminAuth: false }
  }
]

export default adminRoutes
