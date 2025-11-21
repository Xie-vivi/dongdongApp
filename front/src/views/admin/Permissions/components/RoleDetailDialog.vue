<template>
  <el-dialog
    v-model="visible"
    title="角色详情"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="role" class="role-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>角色ID：</label>
            <span>{{ role.id }}</span>
          </div>
          <div class="info-item">
            <label>角色名称：</label>
            <span>{{ role.name }}</span>
          </div>
          <div class="info-item">
            <label>角色描述：</label>
            <span>{{ role.description || '-' }}</span>
          </div>
          <div class="info-item">
            <label>状态：</label>
            <StatusTag :status="role.status" type="system" />
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDateTime(role.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>更新时间：</label>
            <span>{{ formatDateTime(role.updatedAt) }}</span>
          </div>
          <div class="info-item">
            <label>权限数量：</label>
            <span>{{ role.permissions ? role.permissions.length : 0 }}</span>
          </div>
          <div class="info-item">
            <label>管理员数量：</label>
            <span>{{ role.administratorsCount || 0 }}</span>
          </div>
        </div>
      </div>
      
      <!-- 权限列表 -->
      <div class="detail-section">
        <h3 class="section-title">权限列表</h3>
        <div class="permissions-content">
          <div v-if="role.permissions && role.permissions.length > 0" class="permission-modules">
            <div
              v-for="module in permissionModules"
              :key="module.key"
              class="permission-module"
            >
              <div class="module-header">
                <el-icon class="module-icon">
                  <component :is="module.icon" />
                </el-icon>
                <span class="module-name">{{ module.name }}</span>
                <el-badge :value="module.count" type="primary" />
              </div>
              <div class="module-permissions">
                <el-tag
                  v-for="permission in module.permissions"
                  :key="permission.id"
                  size="small"
                  class="permission-tag"
                >
                  <div class="permission-content">
                    <div class="permission-name">{{ permission.name }}</div>
                    <div class="permission-code">{{ permission.code }}</div>
                  </div>
                </el-tag>
              </div>
            </div>
          </div>
          <div v-else class="no-permissions">
            <el-empty description="该角色暂无任何权限" />
          </div>
        </div>
      </div>
      
      <!-- 管理员列表 -->
      <div v-if="role.administrators && role.administrators.length > 0" class="detail-section">
        <h3 class="section-title">管理员列表</h3>
        <div class="administrators-list">
          <div
            v-for="admin in role.administrators"
            :key="admin.id"
            class="admin-item"
          >
            <el-avatar :size="32" :src="admin.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="admin-info">
              <div class="admin-name">{{ admin.username }}</div>
              <div class="admin-email">{{ admin.email }}</div>
            </div>
            <StatusTag :status="admin.status" type="system" />
          </div>
        </div>
      </div>
      
      <!-- 角色统计 -->
      <div class="detail-section">
        <h3 class="section-title">角色统计</h3>
        <div class="statistics-grid">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Key /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ role.permissions ? role.permissions.length : 0 }}</div>
              <div class="stat-label">权限总数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ role.administratorsCount || 0 }}</div>
              <div class="stat-label">管理员数</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ moduleCount }}</div>
              <div class="stat-label">覆盖模块</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ daysSinceCreated }}</div>
              <div class="stat-label">创建天数</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑角色
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { User, Key, Monitor, Clock, Edit } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  role: {
    type: Object,
    default: null
  },
  permissions: {
    type: Array,
    default: () => []
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'edit'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 权限模块分组
const permissionModules = computed(() => {
  if (!props.role || !props.role.permissions) return []
  
  const modules = {}
  props.role.permissions.forEach(permission => {
    const moduleKey = permission.module || 'other'
    if (!modules[moduleKey]) {
      modules[moduleKey] = {
        key: moduleKey,
        name: getModuleName(moduleKey),
        icon: getModuleIcon(moduleKey),
        permissions: []
      }
    }
    modules[moduleKey].permissions.push(permission)
  })
  
  return Object.values(modules).sort((a, b) => a.name.localeCompare(b.name))
})

// 模块数量
const moduleCount = computed(() => {
  return permissionModules.value.length
})

// 创建天数
const daysSinceCreated = computed(() => {
  if (!props.role || !props.role.createdAt) return 0
  const created = new Date(props.role.createdAt)
  const now = new Date()
  const diffTime = Math.abs(now - created)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

// 获取模块名称
const getModuleName = (moduleKey) => {
  const moduleMap = {
    dashboard: '仪表盘',
    users: '用户管理',
    content: '内容管理',
    system: '系统管理',
    permissions: '权限管理',
    other: '其他'
  }
  return moduleMap[moduleKey] || moduleKey
}

// 获取模块图标
const getModuleIcon = (moduleKey) => {
  const iconMap = {
    dashboard: 'Monitor',
    users: 'User',
    content: 'Document',
    system: 'Setting',
    permissions: 'Key',
    other: 'More'
  }
  return iconMap[moduleKey] || 'More'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}

// 处理编辑
const handleEdit = () => {
  emit('edit', props.role)
  handleClose()
}
</script>

<style scoped>
.role-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 12px;
}

.role-detail::-webkit-scrollbar {
  width: 6px;
}

.role-detail::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #409eff;
  border-radius: 2px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
  line-height: 1.5;
}

.info-item span {
  color: #303133;
  line-height: 1.5;
  word-break: break-all;
}

.permissions-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.permission-modules {
  display: grid;
  gap: 16px;
}

.permission-module {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
}

.module-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.module-icon {
  font-size: 16px;
  color: #409eff;
}

.module-name {
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.module-permissions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.permission-tag {
  margin: 0;
  padding: 6px 10px;
  height: auto;
}

.permission-content {
  text-align: left;
}

.permission-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.permission-code {
  font-size: 10px;
  opacity: 0.7;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.no-permissions {
  text-align: center;
  padding: 40px 20px;
}

.administrators-list {
  display: grid;
  gap: 12px;
}

.admin-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.admin-info {
  flex: 1;
}

.admin-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.admin-email {
  font-size: 12px;
  color: #606266;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #409eff;
  border-radius: 8px;
}

.stat-icon .el-icon {
  font-size: 20px;
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-item label {
    min-width: auto;
  }
  
  .statistics-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-card {
    padding: 12px;
  }
  
  .stat-icon {
    width: 32px;
    height: 32px;
  }
  
  .stat-icon .el-icon {
    font-size: 16px;
  }
  
  .stat-number {
    font-size: 20px;
  }
}

/* 动画效果 */
.detail-section {
  animation: fadeInUp 0.3s ease-out;
}

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
