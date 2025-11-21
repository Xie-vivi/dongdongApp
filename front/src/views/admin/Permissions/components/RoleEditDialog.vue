<template>
  <FormDialog
    v-model="visible"
    :title="isEdit ? '编辑角色' : '新增角色'"
    :fields="formFields"
    :form-data="formData"
    :loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
  >
    <!-- 自定义权限选择字段 -->
    <template #field-permissionIds="{ field }">
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
            :default-checked-keys="formData.permissionIds"
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
                  v-if="data.type"
                  size="small"
                  :type="getPermissionTypeTagType(data.type)"
                  class="node-type"
                >
                  {{ getPermissionTypeText(data.type) }}
                </el-tag>
              </div>
            </template>
          </el-tree>
        </div>
        
        <div class="selection-summary">
          <div class="summary-item">
            <span class="summary-label">已选择权限：</span>
            <span class="summary-value">{{ selectedPermissions.length }} 个</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">覆盖模块：</span>
            <span class="summary-value">{{ selectedModules.length }} 个</span>
          </div>
        </div>
      </div>
    </template>
  </FormDialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import FormDialog from '@/components/admin/FormDialog.vue'
import { createRole, updateRole } from '@/api/admin'

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

// 是否为编辑模式
const isEdit = computed(() => !!props.role?.id)

// 加载状态
const loading = ref(false)

// 权限树引用
const permissionTreeRef = ref(null)

// 展开的节点键
const expandedKeys = ref([])

// 表单数据
const formData = reactive({
  name: '',
  description: '',
  permissionIds: [],
  status: 'active'
})

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
  return props.permissions.filter(p => formData.permissionIds.includes(p.id))
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

// 表单字段配置
const formFields = computed(() => [
  {
    prop: 'name',
    label: '角色名称',
    type: 'input',
    placeholder: '请输入角色名称',
    required: true,
    rules: [
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
    ]
  },
  {
    prop: 'description',
    label: '角色描述',
    type: 'textarea',
    placeholder: '请输入角色描述',
    rows: 3,
    maxlength: 200,
    showWordLimit: true
  },
  {
    prop: 'permissionIds',
    label: '权限',
    type: 'slot',
    required: true,
    rules: [
      { required: true, message: '请选择至少一个权限', trigger: 'change' }
    ]
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 'active' },
      { label: '禁用', value: 'inactive' }
    ]
  }
])

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

// 监听角色数据变化
watch(
  () => props.role,
  (newRole) => {
    if (newRole) {
      // 编辑模式，填充表单数据
      Object.assign(formData, {
        name: newRole.name || '',
        description: newRole.description || '',
        permissionIds: newRole.permissions ? newRole.permissions.map(p => p.id) : [],
        status: newRole.status || 'active'
      })
    } else {
      // 新增模式，重置表单数据
      resetFormData()
    }
  },
  { immediate: true }
)

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    name: '',
    description: '',
    permissionIds: [],
    status: 'active'
  })
}

// 处理权限选择变化
const handlePermissionCheck = (data, checked) => {
  formData.permissionIds = checked.checkedKeys
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
  formData.permissionIds = allIds
  
  nextTick(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys(allIds)
    }
  })
}

// 处理清空
const handleClearAll = () => {
  formData.permissionIds = []
  
  nextTick(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys([])
    }
  })
}

// 处理确认
const handleConfirm = async (data) => {
  try {
    if (data.permissionIds.length === 0) {
      ElMessage.warning('请选择至少一个权限')
      return
    }
    
    loading.value = true
    
    let response
    if (isEdit.value) {
      // 编辑角色
      response = await updateRole(props.role.id, data)
      ElMessage.success('更新角色成功')
    } else {
      // 新增角色
      response = await createRole(data)
      ElMessage.success('创建角色成功')
    }
    
    emit('success', response.data)
  } catch (error) {
    console.error('保存角色失败:', error)
    ElMessage.error(error.message || '保存角色失败')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.permission-selection {
  width: 100%;
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
  font-size: 14px;
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
  margin-bottom: 16px;
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

.selection-summary {
  display: flex;
  gap: 24px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.summary-label {
  font-size: 12px;
  color: #606266;
}

.summary-value {
  font-size: 12px;
  font-weight: 600;
  color: #409eff;
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
  .selection-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .selection-actions {
    flex-wrap: wrap;
  }
  
  .selection-summary {
    flex-direction: column;
    gap: 8px;
  }
}

/* 动画效果 */
.permission-node {
  transition: all 0.3s ease;
}

.permission-node:hover {
  color: #409eff;
}
</style>
