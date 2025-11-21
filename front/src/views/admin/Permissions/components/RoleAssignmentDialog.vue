<template>
  <el-dialog
    v-model="visible"
    title="分配角色"
    width="600px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="administrator" class="role-assignment">
      <!-- 管理员信息 -->
      <div class="admin-info">
        <div class="admin-avatar">
          <el-avatar :size="50" :src="administrator.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
        <div class="admin-details">
          <div class="admin-name">{{ administrator.username }}</div>
          <div class="admin-email">{{ administrator.email }}</div>
        </div>
      </div>
      
      <!-- 角色选择 -->
      <div class="role-selection">
        <div class="selection-header">
          <h4>选择角色</h4>
          <div class="selection-actions">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleSelectAll"
            >
              全选
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleClearAll"
            >
              清空
            </el-button>
          </div>
        </div>
        
        <div class="role-list">
          <div
            v-for="role in roles"
            :key="role.id"
            class="role-item"
            :class="{ selected: selectedRoleIds.includes(role.id) }"
            @click="handleToggleRole(role.id)"
          >
            <div class="role-checkbox">
              <el-checkbox
                :model-value="selectedRoleIds.includes(role.id)"
                @change="handleToggleRole(role.id)"
              />
            </div>
            <div class="role-content">
              <div class="role-header">
                <div class="role-name">{{ role.name }}</div>
                <StatusTag :status="role.status" type="system" />
              </div>
              <div class="role-description">{{ role.description || '暂无描述' }}</div>
              <div class="role-stats">
                <span class="stat-item">
                  <el-icon><Key /></el-icon>
                  {{ role.permissions ? role.permissions.length : 0 }} 个权限
                </span>
                <span class="stat-item">
                  <el-icon><User /></el-icon>
                  {{ role.administratorsCount || 0 }} 个管理员
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="roles.length === 0" class="empty-roles">
          <el-empty description="暂无可分配的角色" />
        </div>
      </div>
      
      <!-- 权限预览 -->
      <div v-if="selectedRoles.length > 0" class="permission-preview">
        <div class="preview-header">
          <h4>权限预览</h4>
          <span class="preview-count">共 {{ allPermissions.length }} 个权限</span>
        </div>
        
        <div class="permission-modules">
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
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleConfirm"
        >
          确认分配
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Key } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'
import { assignRoles } from '@/api/admin'

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
const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 加载状态
const loading = ref(false)

// 选中的角色ID
const selectedRoleIds = ref([])

// 选中的角色
const selectedRoles = computed(() => {
  return props.roles.filter(role => selectedRoleIds.value.includes(role.id))
})

// 所有权限
const allPermissions = computed(() => {
  const permissions = new Set()
  selectedRoles.value.forEach(role => {
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

// 监听管理员变化
watch(
  () => props.administrator,
  (newAdministrator) => {
    if (newAdministrator && newAdministrator.roles) {
      selectedRoleIds.value = newAdministrator.roles.map(role => role.id)
    } else {
      selectedRoleIds.value = []
    }
  },
  { immediate: true }
)

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

// 处理切换角色选择
const handleToggleRole = (roleId) => {
  const index = selectedRoleIds.value.indexOf(roleId)
  if (index > -1) {
    selectedRoleIds.value.splice(index, 1)
  } else {
    selectedRoleIds.value.push(roleId)
  }
}

// 处理全选
const handleSelectAll = () => {
  selectedRoleIds.value = props.roles.map(role => role.id)
}

// 处理清空
const handleClearAll = () => {
  selectedRoleIds.value = []
}

// 处理确认
const handleConfirm = async () => {
  try {
    if (selectedRoleIds.value.length === 0) {
      ElMessage.warning('请至少选择一个角色')
      return
    }
    
    loading.value = true
    
    // 调用分配角色API
    await assignRoles(props.administrator.id, selectedRoleIds.value)
    
    ElMessage.success('角色分配成功')
    emit('success')
  } catch (error) {
    console.error('分配角色失败:', error)
    ElMessage.error('分配角色失败')
  } finally {
    loading.value = false
  }
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}
</script>

<style scoped>
.role-assignment {
  padding: 0;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 24px;
}

.admin-avatar {
  flex-shrink: 0;
}

.admin-details {
  flex: 1;
}

.admin-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.admin-email {
  font-size: 14px;
  color: #606266;
}

.role-selection {
  margin-bottom: 24px;
}

.selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.selection-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.selection-actions {
  display: flex;
  gap: 8px;
}

.role-list {
  display: grid;
  gap: 12px;
}

.role-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.role-item.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.role-checkbox {
  margin-top: 2px;
}

.role-content {
  flex: 1;
}

.role-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.role-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.role-description {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.4;
}

.role-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.empty-roles {
  text-align: center;
  padding: 40px 20px;
}

.permission-preview {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.preview-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.preview-count {
  font-size: 14px;
  color: #606266;
}

.permission-modules {
  display: grid;
  gap: 12px;
}

.permission-module {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 12px;
}

.module-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.module-icon {
  font-size: 14px;
  color: #409eff;
}

.module-name {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.module-permissions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.permission-tag {
  margin: 0;
  font-size: 11px;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-info {
    padding: 16px;
  }
  
  .selection-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .role-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .preview-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

/* 动画效果 */
.role-item {
  animation: fadeInUp 0.3s ease-out;
}

.role-item:nth-child(1) { animation-delay: 0.1s; }
.role-item:nth-child(2) { animation-delay: 0.2s; }
.role-item:nth-child(3) { animation-delay: 0.3s; }

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
