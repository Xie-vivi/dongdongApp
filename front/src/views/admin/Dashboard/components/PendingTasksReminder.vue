<template>
  <div class="pending-tasks-reminder">
    <el-card class="tasks-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>待处理事项</h3>
            <span class="subtitle">需要关注的重要事项</span>
          </div>
          <div class="header-right">
            <el-badge :value="totalCount" :max="99" class="task-badge">
              <el-button size="small" @click="refreshTasks">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </el-badge>
          </div>
        </div>
      </template>
      
      <div class="tasks-content">
        <!-- 任务分类标签 -->
        <div class="task-tabs">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane 
              v-for="category in taskCategories" 
              :key="category.key"
              :label="category.label" 
              :name="category.key"
            >
              <template #label>
                <div class="tab-label">
                  <el-icon size="14">
                    <component :is="category.icon" />
                  </el-icon>
                  <span>{{ category.label }}</span>
                  <el-badge 
                    v-if="category.count > 0" 
                    :value="category.count" 
                    :max="99" 
                    class="tab-badge"
                  />
                </div>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
        
        <!-- 任务列表 -->
        <div class="tasks-list" v-if="filteredTasks.length">
          <div 
            class="task-item" 
            v-for="task in filteredTasks" 
            :key="task.id"
            :class="[task.priority, task.type]"
          >
            <div class="task-icon">
              <el-icon size="16" :color="getTaskColor(task.priority)">
                <component :is="getTaskIcon(task.type)" />
              </el-icon>
            </div>
            <div class="task-content">
              <div class="task-header">
                <div class="task-title">{{ task.title }}</div>
                <div class="task-priority">
                  <el-tag 
                    :type="getPriorityType(task.priority)" 
                    size="small"
                  >
                    {{ getPriorityText(task.priority) }}
                  </el-tag>
                </div>
              </div>
              <div class="task-description">{{ task.description }}</div>
              <div class="task-meta">
                <div class="task-info">
                  <span class="task-type">{{ getTaskTypeText(task.type) }}</span>
                  <span class="task-time">{{ formatTime(task.createdAt) }}</span>
                </div>
                <div class="task-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleTaskAction('review', task)"
                  >
                    处理
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small" 
                    @click="handleTaskAction('ignore', task)"
                  >
                    忽略
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div class="empty-state" v-else>
          <el-empty 
            :description="getEmptyDescription()" 
            :image-size="100"
          >
            <el-button type="primary" @click="refreshTasks">
              刷新数据
            </el-button>
          </el-empty>
        </div>
        
        <!-- 快速操作 -->
        <div class="quick-actions" v-if="filteredTasks.length">
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="batchProcess('review')"
              :disabled="!hasSelectedTasks"
            >
              批量处理
            </el-button>
            <el-button 
              size="small" 
              @click="batchProcess('ignore')"
              :disabled="!hasSelectedTasks"
            >
              批量忽略
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              @click="selectAll"
            >
              {{ allSelected ? '取消全选' : '全选' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Document,
  Calendar,
  Message,
  Warning,
  InfoFilled,
  View,
  User
} from '@element-plus/icons-vue'

const props = defineProps({
  pendingTasks: {
    type: Array,
    default: () => []
  },
  totalCount: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['task-action'])

// 当前激活的标签页
const activeTab = ref('all')

// 选中的任务
const selectedTasks = ref(new Set())

// 任务分类
const taskCategories = computed(() => [
  {
    key: 'all',
    label: '全部',
    icon: InfoFilled,
    count: props.totalCount
  },
  {
    key: 'post',
    label: '帖子审核',
    icon: Document,
    count: props.pendingTasks.filter(task => task.type === 'post').length
  },
  {
    key: 'activity',
    label: '活动审核',
    icon: Calendar,
    count: props.pendingTasks.filter(task => task.type === 'activity').length
  },
  {
    key: 'comment',
    label: '评论审核',
    icon: Message,
    count: props.pendingTasks.filter(task => task.type === 'comment').length
  },
  {
    key: 'report',
    label: '用户举报',
    icon: Warning,
    count: props.pendingTasks.filter(task => task.type === 'report').length
  },
  {
    key: 'alert',
    label: '系统告警',
    icon: Warning,
    count: props.pendingTasks.filter(task => task.type === 'alert').length
  }
])

// 过滤后的任务
const filteredTasks = computed(() => {
  if (activeTab.value === 'all') {
    return props.pendingTasks
  }
  return props.pendingTasks.filter(task => task.type === activeTab.value)
})

// 是否有选中的任务
const hasSelectedTasks = computed(() => selectedTasks.value.size > 0)

// 是否全选
const allSelected = computed(() => 
  filteredTasks.value.length > 0 && selectedTasks.value.size === filteredTasks.value.length
)

// 获取任务颜色
const getTaskColor = (priority) => {
  const colorMap = {
    high: '#f56c6c',
    medium: '#e6a23c',
    low: '#409eff'
  }
  return colorMap[priority] || '#909399'
}

// 获取任务图标
const getTaskIcon = (type) => {
  const iconMap = {
    post: Document,
    activity: Calendar,
    comment: Message,
    report: Warning,
    alert: Warning,
    user: User
  }
  return iconMap[type] || InfoFilled
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return typeMap[priority] || 'info'
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const textMap = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级'
  }
  return textMap[priority] || '普通'
}

// 获取任务类型文本
const getTaskTypeText = (type) => {
  const textMap = {
    post: '帖子审核',
    activity: '活动审核',
    comment: '评论审核',
    report: '用户举报',
    alert: '系统告警',
    user: '用户处理'
  }
  return textMap[type] || '其他'
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

// 获取空状态描述
const getEmptyDescription = () => {
  const descriptions = {
    all: '暂无待处理事项',
    post: '暂无待审核帖子',
    activity: '暂无待审核活动',
    comment: '暂无待审核评论',
    report: '暂无用户举报',
    alert: '暂无系统告警'
  }
  return descriptions[activeTab.value] || '暂无数据'
}

// 处理标签页变化
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  selectedTasks.value.clear()
}

// 处理任务操作
const handleTaskAction = (action, task) => {
  emit('task-action', action, task)
}

// 刷新任务
const refreshTasks = () => {
  ElMessage.success('待处理事项已刷新')
}

// 全选/取消全选
const selectAll = () => {
  if (allSelected.value) {
    selectedTasks.value.clear()
  } else {
    filteredTasks.value.forEach(task => {
      selectedTasks.value.add(task.id)
    })
  }
}

// 批量处理
const batchProcess = async (action) => {
  if (selectedTasks.value.size === 0) {
    ElMessage.warning('请先选择要处理的任务')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要${action === 'review' ? '处理' : '忽略'}选中的 ${selectedTasks.value.size} 个任务吗？`,
      '批量操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 执行批量操作
    const tasks = filteredTasks.value.filter(task => selectedTasks.value.has(task.id))
    tasks.forEach(task => {
      handleTaskAction(action, task)
    })
    
    selectedTasks.value.clear()
    ElMessage.success(`批量${action === 'review' ? '处理' : '忽略'}成功`)
  } catch {
    // 用户取消操作
  }
}
</script>

<style scoped>
.pending-tasks-reminder {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tasks-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tasks-card :deep(.el-card__body) {
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

.header-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.task-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}

.tasks-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  font-size: 10px;
  height: 16px;
  line-height: 16px;
  min-width: 16px;
  padding: 0 4px;
}

.tasks-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
  max-height: 400px;
}

.task-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
  transition: all 0.3s ease;
  cursor: pointer;
}

.task-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.task-item.high {
  border-left-color: #f56c6c;
  background: #fef0f0;
}

.task-item.medium {
  border-left-color: #e6a23c;
  background: #fdf6ec;
}

.task-item.low {
  border-left-color: #409eff;
  background: #ecf5ff;
}

.task-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.task-content {
  flex: 1;
  min-width: 0;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.task-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  flex: 1;
  margin-right: 12px;
}

.task-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  margin-bottom: 12px;
}

.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.quick-actions {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tasks-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .task-item {
    padding: 12px;
  }
  
  .task-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .task-title {
    margin-right: 0;
  }
  
  .task-meta {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .task-actions {
    align-self: flex-end;
  }
  
  .action-buttons {
    flex-wrap: wrap;
    gap: 8px;
  }
}

/* 动画效果 */
.pending-tasks-reminder {
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
  
  .task-item {
    background: #2d2d2d;
  }
  
  .task-item.high {
    background: #4a2e2e;
  }
  
  .task-item.medium {
    background: #4a3e2e;
  }
  
  .task-item.low {
    background: #2e3e4a;
  }
  
  .task-title {
    color: #e4e7ed;
  }
  
  .task-description {
    color: #c0c4cc;
  }
}
</style>
