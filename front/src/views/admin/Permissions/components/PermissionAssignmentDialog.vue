<template>
  <el-dialog
    v-model="visible"
    title="分配权限"
    width="700px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="role" class="permission-assignment">
      <!-- 角色信息 -->
      <div class="role-info">
        <div class="role-avatar">
          <el-icon class="role-icon"><UserFilled /></el-icon>
        </div>
        <div class="role-details">
          <div class="role-name">{{ role.name }}</div>
          <div class="role-description">{{ role.description || '暂无描述' }}</div>
        </div>
        <StatusTag :status="role.status" type="system" />
      </div>
      
      <!-- 权限选择 -->
      <div class="permission-selection">
        <div class="selection-header">
          <h4>选择权限</h4>
          <div class="selection-actions">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleExpandAll"
            >
              全部展开
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleCollapseAll"
            >
              全部折叠
            </el-button>
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
        
        <div class="permission-tree">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTreeData"
            :props="treeProps"
            :default-checked-keys="selectedPermissionIds"
            :default-expanded-keys="expandedKeys"
            show-checkbox
            node-key="id"
            @check="handlePermissionCheck"
          >
            <template #default="{ node, data }">
              <div class="permission-node">
                <el-icon v-if="data.icon" class="node-icon">
                  <component :is="data.icon" />
                </el-icon>
                <span class="node-name">{{ data.name }}</span>
                <el-tag
                  v-if="data.type && data.type !== 'module'"
                  size="small"
                  :type="getPermissionTypeTagType(data.type)"
                  class="node-type"
                >
                  {{ getPermissionTypeText(data.type) }}
                </el-tag>
                <span v-if="data.code" class="node-code">{{ data.code }}</span>
              </div>
            </template>
          </el-tree>
        </div>
      </div>
      
      <!-- 选择统计 -->
      <div class="selection-stats">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ selectedPermissions.length }}</div>
            <div class="stat-label">已选权限</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ selectedModules.length }}</div>
            <div class="stat-label">覆盖模块</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalPermissions }}</div>
            <div class="stat-label">总权限数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ selectionPercentage }}%</div>
            <div class="stat-label">选择比例</div>
          </div>
        </div>
      </div>
      
      <!-- 权限预览 -->
      <div v-if="selectedPermissions.length > 0" class="permission-preview">
        <div class="preview-header">
          <h4>权限预览</h4>
          <el-button
            link
            type="primary"
            size="small"
            @click="handleTogglePreview"
          >
            {{ showPreview ? '收起' : '展开' }}
          </el-button>
        </div>
        
        <el-collapse-transition>
          <div v-show="showPreview" class="preview-content">
            <div
              v-for="module in selectedPermissionModules"
              :key="module.key"
              class="preview-module"
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
        </el-collapse-transition>
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
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'
import { assignPermissions } from '@/api/admin'

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
const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 加载状态
const loading = ref(false)

// 权限树引用
const permissionTreeRef = ref(null)

// 展开的节点键
const expandedKeys = ref([])

// 选中的权限ID
const selectedPermissionIds = ref([])

// 是否显示预览
const showPreview = ref(false)

// 权限树数据
const permissionTreeData = computed(() => {
  return buildPermissionTree(props.permissions)
})

// 树形组件属性
const treeProps = {
  children: 'children',
  label: 'name'
}

// 选中的权限
const selectedPermissions = computed(() => {
  return props.permissions.filter(p => selectedPermissionIds.value.includes(p.id))
})

// 选中的模块
const selectedModules = computed(() => {
  const modules = new Set()
  selectedPermissions.value.forEach(permission => {
    if (permission.module) {
      modules.add(permission.module)
    }
  })
  return Array.from(modules)
})

// 总权限数
const totalPermissions = computed(() => {
  return props.permissions.filter(p => p.type !== 'module').length
})

// 选择比例
const selectionPercentage = computed(() => {
  if (totalPermissions.value === 0) return 0
  return Math.round((selectedPermissions.value.length / totalPermissions.value) * 100)
})

// 选中的权限模块（用于预览）
const selectedPermissionModules = computed(() => {
  const modules = {}
  
  selectedPermissions.value.forEach(permission => {
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

// 构建权限树
const buildPermissionTree = (permissions) => {
  const moduleMap = {}
  
  // 按模块分组
  permissions.forEach(permission => {
    const moduleKey = permission.module || 'other'
    if (!moduleMap[moduleKey]) {
      moduleMap[moduleKey] = {
        id: `module_${moduleKey}`,
        name: getModuleName(moduleKey),
        icon: getModuleIcon(moduleKey),
        type: 'module',
        children: []
      }
    }
    moduleMap[moduleKey].children.push({
      ...permission,
      icon: getPermissionIcon(permission.type)
    })
  })
  
  return Object.values(moduleMap).sort((a, b) => a.name.localeCompare(b.name))
}

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

// 获取权限图标
const getPermissionIcon = (type) => {
  const iconMap = {
    menu: 'Menu',
    button: 'Pointer',
    api: 'Connection',
    data: 'DataBoard'
  }
  return iconMap[type] || 'Key'
}

// 获取权限类型标签类型
const getPermissionTypeTagType = (type) => {
  const typeMap = {
    menu: 'primary',
    button: 'success',
    api: 'warning',
    data: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取权限类型文本
const getPermissionTypeText = (type) => {
  const typeMap = {
    menu: '菜单',
    button: '按钮',
    api: '接口',
    data: '数据'
  }
  return typeMap[type] || type
}

// 监听角色变化
watch(
  () => props.role,
  (newRole) => {
    if (newRole && newRole.permissions) {
      selectedPermissionIds.value = newRole.permissions.map(p => p.id)
    } else {
      selectedPermissionIds.value = []
    }
    showPreview.value = false
  },
  { immediate: true }
)

// 处理权限选择变化
const handlePermissionCheck = (data, checked) => {
  selectedPermissionIds.value = checked.checkedKeys.filter(id => 
    !id.startsWith('module_')
  )
}

// 处理全部展开
const handleExpandAll = () => {
  const allKeys = []
  const collectKeys = (nodes) => {
    nodes.forEach(node => {
      allKeys.push(node.id)
      if (node.children && node.children.length > 0) {
        collectKeys(node.children)
      }
    })
  }
  collectKeys(permissionTreeData.value)
  expandedKeys.value = allKeys
}

// 处理全部折叠
const handleCollapseAll = () => {
  expandedKeys.value = []
}

// 处理全选
const handleSelectAll = () => {
  const allIds = []
  const collectIds = (nodes) => {
    nodes.forEach(node => {
      if (node.type !== 'module') {
        allIds.push(node.id)
      }
      if (node.children && node.children.length > 0) {
        collectIds(node.children)
      }
    })
  }
  collectIds(permissionTreeData.value)
  selectedPermissionIds.value = allIds
  
  nextTick(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys(allIds)
    }
  })
}

// 处理清空
const handleClearAll = () => {
  selectedPermissionIds.value = []
  
  nextTick(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys([])
    }
  })
}

// 处理切换预览
const handleTogglePreview = () => {
  showPreview.value = !showPreview.value
}

// 处理确认
const handleConfirm = async () => {
  try {
    if (selectedPermissionIds.value.length === 0) {
      ElMessage.warning('请选择至少一个权限')
      return
    }
    
    loading.value = true
    
    // 调用分配权限API
    await assignPermissions(props.role.id, selectedPermissionIds.value)
    
    ElMessage.success('权限分配成功')
    emit('success')
  } catch (error) {
    console.error('分配权限失败:', error)
    ElMessage.error('分配权限失败')
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
.permission-assignment {
  padding: 0;
}

.role-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 24px;
}

.role-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: #409eff;
  border-radius: 8px;
  flex-shrink: 0;
}

.role-icon {
  font-size: 24px;
  color: #fff;
}

.role-details {
  flex: 1;
}

.role-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.role-description {
  font-size: 14px;
  color: #606266;
}

.permission-selection {
  margin-bottom: 24px;
}

.selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
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

.permission-tree {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 8px;
}

.permission-node {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.node-icon {
  font-size: 14px;
  color: #409eff;
}

.node-name {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.node-type {
  margin: 0;
  font-size: 10px;
}

.node-code {
  font-size: 10px;
  color: #909399;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.selection-stats {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.permission-preview {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
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

.preview-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-module {
  margin-bottom: 16px;
}

.preview-module:last-child {
  margin-bottom: 0;
}

.module-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.module-icon {
  font-size: 14px;
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
  font-size: 11px;
}

.dialog-footer {
  text-align: right;
}

/* 滚动条样式 */
.permission-tree::-webkit-scrollbar {
  width: 6px;
}

.permission-tree::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-info {
    padding: 16px;
    flex-direction: column;
    text-align: center;
  }
  
  .selection-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .selection-actions {
    flex-wrap: wrap;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-number {
    font-size: 20px;
  }
}

/* 动画效果 */
.permission-node {
  transition: all 0.3s ease;
}

.permission-node:hover {
  color: #409eff;
}

.stat-item {
  animation: fadeInUp 0.3s ease-out;
}

.stat-item:nth-child(1) { animation-delay: 0.1s; }
.stat-item:nth-child(2) { animation-delay: 0.2s; }
.stat-item:nth-child(3) { animation-delay: 0.3s; }
.stat-item:nth-child(4) { animation-delay: 0.4s; }

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
