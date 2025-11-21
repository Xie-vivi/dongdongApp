<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon size="32" color="#409EFF">
            <Setting />
          </el-icon>
          <span v-show="!sidebarCollapsed" class="logo-text">XYST 管理后台</span>
        </div>
        <el-button
          v-show="!sidebarCollapsed"
          text
          size="small"
          @click="toggleSidebar"
        >
          <el-icon><Fold /></el-icon>
        </el-button>
      </div>
      
      <el-menu
        :default-active="$route.path"
        :collapse="sidebarCollapsed"
        class="sidebar-menu"
        router
        unique-opened
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-sub-menu index="users">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">
            <el-icon><List /></el-icon>
            <template #title>用户列表</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/admin/content/posts">
            <el-icon><DocumentCopy /></el-icon>
            <template #title>帖子管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/content/activities">
            <el-icon><Calendar /></el-icon>
            <template #title>活动管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/content/audit">
            <el-icon><View /></el-icon>
            <template #title>内容审核</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/system/config">
            <el-icon><Tools /></el-icon>
            <template #title>系统配置</template>
          </el-menu-item>
          <el-menu-item index="/admin/system/logs">
            <el-icon><DocumentCopy /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="permissions">
          <template #title>
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </template>
          <el-menu-item index="/admin/permissions/administrators">
            <el-icon><UserFilled /></el-icon>
            <template #title>管理员管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/permissions/roles">
            <el-icon><Avatar /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/tags">
          <el-icon><CollectionTag /></el-icon>
          <template #title>标签管理</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/analytics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据分析</template>
        </el-menu-item>
        
        <el-menu-item index="/admin/files">
          <el-icon><Folder /></el-icon>
          <template #title>文件管理</template>
        </el-menu-item>
      </el-menu>
    </div>
    
    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-button
            v-show="sidebarCollapsed"
            text
            size="small"
            @click="toggleSidebar"
          >
            <el-icon><Expand /></el-icon>
          </el-button>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
              首页
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbItems" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="navbar-right">
          <!-- 通知 -->
          <el-badge :value="3" class="notification-badge">
            <el-button circle text>
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
          
          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="adminAvatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ adminName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 页面内容 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting, Fold, Expand, Odometer, User, List, Document,
  DocumentCopy, Calendar, View, Tools, Lock, UserFilled,
  Avatar, CollectionTag, DataAnalysis, Folder, Bell,
  ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

// 侧边栏状态
const sidebarCollapsed = ref(false)

// 管理员信息
const adminName = computed(() => store.getters['admin/adminName'])
const adminAvatar = computed(() => store.getters['admin/adminAvatar'])

// 面包屑导航
const breadcrumbItems = computed(() => {
  const routeMeta = route.meta
  const items = []
  
  if (routeMeta.title && route.path !== '/admin/dashboard') {
    items.push({
      title: routeMeta.title,
      path: route.path
    })
  }
  
  return items
})

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
  localStorage.setItem('admin_sidebar_collapsed', sidebarCollapsed.value)
}

// 处理用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 跳转到个人信息页面
      ElMessage.info('个人信息功能开发中')
      break
    case 'settings':
      // 跳转到账户设置页面
      ElMessage.info('账户设置功能开发中')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 执行登出
        await store.dispatch('admin/logout')
        ElMessage.success('已退出登录')
        router.push('/admin/login')
      } catch (error) {
        // 用户取消登出
      }
      break
  }
}

// 恢复侧边栏状态
const restoreSidebarState = () => {
  const collapsed = localStorage.getItem('admin_sidebar_collapsed')
  if (collapsed !== null) {
    sidebarCollapsed.value = collapsed === 'true'
  }
}

// 监听路由变化，自动展开相关菜单
watch(route, (newRoute) => {
  // 可以在这里添加路由变化的处理逻辑
}, { immediate: true })

// 组件挂载时恢复状态
restoreSidebarState()
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #e6e6e6;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏样式 */
.navbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  margin-right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 内容区域样式 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .username {
    display: none;
  }
}

/* 过渡动画 */
.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #f5f7fa;
}
</style>
