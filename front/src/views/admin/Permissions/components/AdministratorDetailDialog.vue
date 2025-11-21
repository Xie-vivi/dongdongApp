<template>
  <el-dialog
    v-model="visible"
    title="管理员详情"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="administrator" class="administrator-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>管理员ID：</label>
            <span>{{ administrator.id }}</span>
          </div>
          <div class="info-item">
            <label>用户名：</label>
            <span>{{ administrator.username }}</span>
          </div>
          <div class="info-item">
            <label>昵称：</label>
            <span>{{ administrator.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <label>邮箱：</label>
            <span>{{ administrator.email }}</span>
          </div>
          <div class="info-item">
            <label>手机号：</label>
            <span>{{ administrator.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <label>状态：</label>
            <StatusTag :status="administrator.status" type="system" />
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDateTime(administrator.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>最后登录：</label>
            <span>{{ formatDateTime(administrator.lastLoginAt) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 角色信息 -->
      <div class="detail-section">
        <h3 class="section-title">角色信息</h3>
        <div class="role-info">
          <div v-if="administrator.roles && administrator.roles.length > 0" class="role-list">
            <el-tag
              v-for="role in administrator.roles"
              :key="role.id"
              type="primary"
              size="large"
              class="role-tag"
            >
              <div class="role-content">
                <div class="role-name">{{ role.name }}</div>
                <div class="role-description">{{ role.description || '暂无描述' }}</div>
              </div>
            </el-tag>
          </div>
          <div v-else class="no-roles">
            <el-empty description="该管理员暂未分配任何角色" />
          </div>
        </div>
      </div>
      
      <!-- 权限概览 -->
      <div class="detail-section">
        <h3 class="section-title">权限概览</h3>
        <div class="permission-overview">
          <div v-if="allPermissions.length > 0" class="permission-modules">
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
                  {{ permission.name }}
                </el-tag>
              </div>
            </div>
          </div>
          <div v-else class="no-permissions">
            <el-empty description="该管理员暂无任何权限" />
          </div>
        </div>
      </div>
      
      <!-- 登录历史 -->
      <div v-if="administrator.loginHistory && administrator.loginHistory.length > 0" class="detail-section">
        <h3 class="section-title">登录历史</h3>
        <div class="login-history">
          <el-timeline>
            <el-timeline-item
              v-for="(login, index) in administrator.loginHistory.slice(0, 5)"
              :key="index"
              :timestamp="formatDateTime(login.loginTime)"
              placement="top"
            >
              <div class="login-item">
                <div class="login-info">
                  <span class="login-ip">IP: {{ login.ip }}</span>
                  <span class="login-location">{{ login.location || '未知位置' }}</span>
                  <span class="login-device">{{ login.device || '未知设备' }}</span>
                </div>
                <div class="login-status">
                  <el-tag
                    :type="login.success ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ login.success ? '成功' : '失败' }}
                  </el-tag>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑管理员
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  administrator: {
    type: Object,
    default: null
  },
  roles: {
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

// 所有权限
const allPermissions = computed(() => {
  if (!props.administrator || !props.administrator.roles) return []
  
  const permissions = new Set()
  props.administrator.roles.forEach(role => {
    if (role.permissions) {
      role.permissions.forEach(permission => {
        permissions.add(permission)
      })
    }
  })
  
  return Array.from(permissions)
})

// 权限模块分组
const permissionModules = computed(() => {
  const modules = {}
  
  allPermissions.value.forEach(permission => {
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
  emit('edit', props.administrator)
  handleClose()
}
</script>

<style scoped>
.administrator-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 12px;
}

.administrator-detail::-webkit-scrollbar {
  width: 6px;
}

.administrator-detail::-webkit-scrollbar-thumb {
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

.role-info {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.role-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.role-tag {
  margin: 0;
  padding: 8px 12px;
  height: auto;
}

.role-content {
  text-align: left;
}

.role-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.role-description {
  font-size: 12px;
  opacity: 0.8;
}

.no-roles {
  text-align: center;
  padding: 40px 20px;
}

.permission-overview {
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
  gap: 6px;
}

.permission-tag {
  margin: 0;
  font-size: 12px;
}

.no-permissions {
  text-align: center;
  padding: 40px 20px;
}

.login-history {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.login-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.login-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.login-ip,
.login-location,
.login-device {
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
  
  .role-list {
    flex-direction: column;
    gap: 8px;
  }
  
  .login-info {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
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
