<template>
  <div class="roles-tab">
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button
          type="primary"
          @click="handleCreate"
        >
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
        <el-button
          @click="handleRefresh"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>
    
    <!-- 角色卡片列表 -->
    <div class="role-cards">
      <div
        v-for="role in roles"
        :key="role.id"
        class="role-card"
      >
        <div class="role-header">
          <div class="role-info">
            <h3 class="role-name">{{ role.name }}</h3>
            <p class="role-description">{{ role.description || '暂无描述' }}</p>
          </div>
          <div class="role-status">
            <StatusTag :status="role.status" type="system" />
          </div>
        </div>
        
        <div class="role-stats">
          <div class="stat-item">
            <span class="stat-number">{{ role.permissions ? role.permissions.length : 0 }}</span>
            <span class="stat-label">权限</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ role.administratorsCount || 0 }}</span>
            <span class="stat-label">管理员</span>
          </div>
          <div class="stat-item">
            <span class="stat-date">{{ formatDate(role.createdAt) }}</span>
            <span class="stat-label">创建时间</span>
          </div>
        </div>
        
        <div class="role-permissions">
          <div class="permissions-header">
            <span>权限预览</span>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewPermissions(role)"
            >
              查看全部
            </el-button>
          </div>
          <div class="permissions-preview">
            <el-tag
              v-for="permission in getPreviewPermissions(role.permissions)"
              :key="permission.id"
              size="small"
              class="permission-tag"
            >
              {{ permission.name }}
            </el-tag>
            <span
              v-if="role.permissions && role.permissions.length > 6"
              class="more-permissions"
            >
              +{{ role.permissions.length - 6 }} 更多
            </span>
          </div>
        </div>
        
        <div class="role-actions">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleView(role)"
          >
            查看详情
          </el-button>
          <el-button
            link
            type="primary"
            size="small"
            @click="handleEdit(role)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="success"
            size="small"
            @click="handleAssignPermissions(role)"
          >
            分配权限
          </el-button>
          <el-button
            link
            type="danger"
            size="small"
            @click="handleDelete(role)"
          >
            删除
          </el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="roles.length === 0" class="empty-state">
        <el-empty description="暂无角色数据">
          <el-button type="primary" @click="handleCreate">
            新增角色
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Plus, Refresh } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  roles: {
    type: Array,
    default: () => []
  },
  permissions: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits 定义
const emit = defineEmits([
  'refresh',
  'create',
  'edit',
  'delete',
  'assign-permissions',
  'view'
])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString()
}

// 获取预览权限（最多显示6个）
const getPreviewPermissions = (permissions) => {
  if (!permissions || permissions.length === 0) return []
  return permissions.slice(0, 6)
}

// 处理刷新
const handleRefresh = () => {
  emit('refresh')
}

// 处理创建
const handleCreate = () => {
  emit('create')
}

// 处理查看
const handleView = (role) => {
  emit('view', role)
}

// 处理编辑
const handleEdit = (role) => {
  emit('edit', role)
}

// 处理删除
const handleDelete = (role) => {
  emit('delete', role)
}

// 处理分配权限
const handleAssignPermissions = (role) => {
  emit('assign-permissions', role)
}

// 处理查看权限
const handleViewPermissions = (role) => {
  emit('view', role)
}
</script>

<style scoped>
.roles-tab {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.toolbar-left {
  display: flex;
  gap: 12px;
}

.role-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.role-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.role-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.role-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.role-info {
  flex: 1;
}

.role-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.role-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.role-status {
  margin-left: 12px;
}

.role-stats {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.stat-date {
  font-size: 12px;
  color: #606266;
}

.role-permissions {
  margin-bottom: 16px;
}

.permissions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.permissions-header span {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.permissions-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.permission-tag {
  margin: 0;
  font-size: 12px;
}

.more-permissions {
  font-size: 12px;
  color: #909399;
  font-style: italic;
}

.role-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.role-actions .el-button {
  margin: 0;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .roles-tab {
    padding: 16px;
  }
  
  .role-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .role-card {
    padding: 16px;
  }
  
  .role-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .role-status {
    margin-left: 0;
    align-self: flex-start;
  }
  
  .role-stats {
    flex-direction: column;
    gap: 12px;
  }
  
  .stat-item {
    flex-direction: row;
    justify-content: space-between;
  }
  
  .role-actions {
    flex-direction: column;
    gap: 8px;
    align-items: stretch;
  }
}

/* 动画效果 */
.toolbar {
  animation: fadeInUp 0.3s ease-out;
}

.role-card {
  animation: fadeInUp 0.3s ease-out;
}

.role-card:nth-child(1) { animation-delay: 0.1s; }
.role-card:nth-child(2) { animation-delay: 0.2s; }
.role-card:nth-child(3) { animation-delay: 0.3s; }
.role-card:nth-child(4) { animation-delay: 0.4s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
