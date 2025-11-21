<template>
  <div class="quick-actions">
    <el-card class="actions-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>快捷操作</h3>
            <span class="subtitle">常用功能快速入口</span>
          </div>
        </div>
      </template>
      
      <div class="actions-content">
        <div class="actions-grid">
          <div 
            class="action-item" 
            v-for="action in actions" 
            :key="action.key"
            @click="handleActionClick(action.key)"
            :class="{ 'disabled': action.disabled }"
          >
            <div class="action-icon" :style="{ background: action.gradient }">
              <el-icon size="24">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <div class="action-info">
              <div class="action-title">{{ action.title }}</div>
              <div class="action-desc">{{ action.description }}</div>
              <div class="action-badge" v-if="action.badge">
                <el-tag :type="action.badgeType" size="small">{{ action.badge }}</el-tag>
              </div>
            </div>
            <div class="action-arrow">
              <el-icon>
                <ArrowRight />
              </el-icon>
            </div>
          </div>
        </div>
        
        <!-- 最近访问 -->
        <div class="recent-section" v-if="recentActions.length">
          <div class="section-header">
            <h4>最近访问</h4>
            <el-button type="text" size="small" @click="clearRecentActions">
              清空
            </el-button>
          </div>
          <div class="recent-list">
            <div 
              class="recent-item" 
              v-for="recent in recentActions" 
              :key="recent.key"
              @click="handleActionClick(recent.key)"
            >
              <el-icon size="16">
                <component :is="getActionIcon(recent.key)" />
              </el-icon>
              <span>{{ recent.title }}</span>
              <span class="recent-time">{{ formatRecentTime(recent.time) }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  View,
  Setting,
  DataAnalysis,
  Lock,
  Folder,
  Document,
  Monitor,
  ArrowRight,
  Clock
} from '@element-plus/icons-vue'

const emit = defineEmits(['action'])

// 快捷操作数据
const actions = [
  {
    key: 'userManagement',
    title: '用户管理',
    description: '管理用户账户和权限',
    icon: User,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    disabled: false
  },
  {
    key: 'contentReview',
    title: '内容审核',
    description: '审核用户发布的内容',
    icon: View,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    disabled: false,
    badge: '待审核',
    badgeType: 'warning'
  },
  {
    key: 'systemConfig',
    title: '系统配置',
    description: '管理系统参数和设置',
    icon: Setting,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    disabled: false
  },
  {
    key: 'dataAnalytics',
    title: '数据分析',
    description: '查看系统数据统计',
    icon: DataAnalysis,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    disabled: false
  },
  {
    key: 'permissionManagement',
    title: '权限管理',
    description: '管理角色和权限分配',
    icon: Lock,
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    disabled: false
  },
  {
    key: 'fileManagement',
    title: '文件管理',
    description: '管理文件上传和存储',
    icon: Folder,
    gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    disabled: false
  },
  {
    key: 'operationLogs',
    title: '操作日志',
    description: '查看系统操作记录',
    icon: Document,
    gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    disabled: false
  },
  {
    key: 'systemHealth',
    title: '系统监控',
    description: '监控系统运行状态',
    icon: Monitor,
    gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
    disabled: false
  }
]

// 最近访问记录
const recentActions = ref([
  {
    key: 'userManagement',
    title: '用户管理',
    time: new Date(Date.now() - 5 * 60 * 1000)
  },
  {
    key: 'contentReview',
    title: '内容审核',
    time: new Date(Date.now() - 30 * 60 * 1000)
  },
  {
    key: 'dataAnalytics',
    title: '数据分析',
    time: new Date(Date.now() - 2 * 60 * 60 * 1000)
  }
])

// 获取操作图标
const getActionIcon = (key) => {
  const action = actions.find(item => item.key === key)
  return action ? action.icon : Document
}

// 格式化最近访问时间
const formatRecentTime = (time) => {
  const now = new Date()
  const diff = now - new Date(time)
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  return `${days}天前`
}

// 处理操作点击
const handleActionClick = (actionKey) => {
  const action = actions.find(item => item.key === actionKey)
  if (!action || action.disabled) {
    ElMessage.warning('该功能暂时不可用')
    return
  }
  
  // 添加到最近访问
  addToRecentActions(actionKey)
  
  // 发送事件给父组件
  emit('action', actionKey)
}

// 添加到最近访问
const addToRecentActions = (actionKey) => {
  const action = actions.find(item => item.key === actionKey)
  if (!action) return
  
  // 移除已存在的记录
  recentActions.value = recentActions.value.filter(item => item.key !== actionKey)
  
  // 添加到开头
  recentActions.value.unshift({
    key: actionKey,
    title: action.title,
    time: new Date()
  })
  
  // 最多保留5条记录
  if (recentActions.value.length > 5) {
    recentActions.value = recentActions.value.slice(0, 5)
  }
  
  // 保存到本地存储
  saveRecentActions()
}

// 清空最近访问
const clearRecentActions = () => {
  recentActions.value = []
  localStorage.removeItem('dashboard-recent-actions')
  ElMessage.success('已清空最近访问记录')
}

// 保存最近访问到本地存储
const saveRecentActions = () => {
  try {
    localStorage.setItem('dashboard-recent-actions', JSON.stringify(recentActions.value))
  } catch (error) {
    console.warn('保存最近访问记录失败:', error)
  }
}

// 从本地存储加载最近访问
const loadRecentActions = () => {
  try {
    const saved = localStorage.getItem('dashboard-recent-actions')
    if (saved) {
      const data = JSON.parse(saved)
      // 转换时间字符串为Date对象
      recentActions.value = data.map(item => ({
        ...item,
        time: new Date(item.time)
      }))
    }
  } catch (error) {
    console.warn('加载最近访问记录失败:', error)
  }
}

// 组件挂载时加载最近访问
loadRecentActions()
</script>

<style scoped>
.quick-actions {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.actions-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.actions-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  font-size: 12px;
  color: #909399;
}

.actions-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.actions-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.action-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left-color: #409EFF;
}

.action-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-item.disabled:hover {
  transform: none;
  box-shadow: none;
  border-left-color: transparent;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.action-info {
  flex: 1;
  min-width: 0;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  line-height: 1.4;
}

.action-badge {
  display: inline-block;
}

.action-arrow {
  flex-shrink: 0;
  color: #c0c4cc;
  transition: all 0.3s ease;
}

.action-item:hover .action-arrow {
  color: #409EFF;
  transform: translateX(2px);
}

.recent-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
  color: #606266;
}

.recent-item:hover {
  background: #e9ecef;
  color: #409EFF;
}

.recent-time {
  margin-left: auto;
  font-size: 11px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .actions-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .action-item {
    padding: 12px;
  }
  
  .action-icon {
    width: 40px;
    height: 40px;
  }
  
  .action-title {
    font-size: 13px;
  }
  
  .action-desc {
    font-size: 11px;
  }
  
  .recent-item {
    padding: 6px 10px;
    font-size: 12px;
  }
}

/* 动画效果 */
.quick-actions {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .header-left h3 {
    color: #e4e7ed;
  }
  
  .action-item {
    background: #2d2d2d;
  }
  
  .action-item:hover {
    background: #3a3a3a;
  }
  
  .action-title {
    color: #e4e7ed;
  }
  
  .recent-item {
    background: #2d2d2d;
  }
  
  .recent-item:hover {
    background: #3a3a3a;
  }
  
  .section-header h4 {
    color: #e4e7ed;
  }
}
</style>
