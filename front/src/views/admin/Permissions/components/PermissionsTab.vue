<template>
  <div class="permissions-tab">
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button
          @click="handleRefresh"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button
          @click="handleExpandAll"
        >
          <el-icon><Expand /></el-icon>
          全部展开
        </el-button>
        <el-button
          @click="handleCollapseAll"
        >
          <el-icon><Fold /></el-icon>
          全部折叠
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索权限..."
          style="width: 200px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- 权限树形表格 -->
    <div class="permissions-tree">
      <el-table
        :data="filteredPermissions"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
        :expand-row-keys="expandedKeys"
        @expand-change="handleExpandChange"
        v-loading="loading"
        element-loading-text="加载权限数据..."
      >
        <el-table-column
          prop="name"
          label="权限名称"
          min-width="200"
        >
          <template #default="{ row }">
            <div class="permission-name">
              <el-icon v-if="row.icon" class="permission-icon">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.name }}</span>
              <el-tag
                v-if="row.type"
                size="small"
                :type="getPermissionTypeTagType(row.type)"
                class="permission-type-tag"
              >
                {{ getPermissionTypeText(row.type) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="code"
          label="权限代码"
          min-width="180"
        >
          <template #default="{ row }">
            <code class="permission-code">{{ row.code }}</code>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="description"
          label="描述"
          min-width="250"
          show-overflow-tooltip
        />
        
        <el-table-column
          prop="module"
          label="所属模块"
          width="120"
        >
          <template #default="{ row }">
            <el-tag
              size="small"
              :type="getModuleTagType(row.module)"
            >
              {{ getModuleText(row.module) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <StatusTag :status="row.status" type="system" />
          </template>
        </el-table-column>
        
        <el-table-column
          prop="createdAt"
          label="创建时间"
          width="160"
          sortable
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column
          label="使用情况"
          width="120"
        >
          <template #default="{ row }">
            <div class="usage-info">
              <el-icon><User /></el-icon>
              <span>{{ row.rolesCount || 0 }} 个角色</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 权限统计 -->
    <div class="permissions-stats">
      <div class="stats-card">
        <div class="stats-title">权限统计</div>
        <div class="stats-content">
          <div class="stat-item">
            <span class="stat-label">总权限数：</span>
            <span class="stat-value">{{ permissions.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">模块数：</span>
            <span class="stat-value">{{ moduleCount }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已启用：</span>
            <span class="stat-value active">{{ activeCount }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已禁用：</span>
            <span class="stat-value inactive">{{ inactiveCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Refresh, Expand, Fold, Search, User } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
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
const emit = defineEmits(['refresh'])

// 搜索关键词
const searchKeyword = ref('')

// 展开的节点键
const expandedKeys = ref([])

// 过滤后的权限
const filteredPermissions = computed(() => {
  if (!searchKeyword.value) {
    return props.permissions
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return filterPermissions(props.permissions, keyword)
})

// 模块数量
const moduleCount = computed(() => {
  const modules = new Set()
  props.permissions.forEach(permission => {
    if (permission.module) {
      modules.add(permission.module)
    }
  })
  return modules.size
})

// 启用数量
const activeCount = computed(() => {
  return props.permissions.filter(p => p.status === 'active').length
})

// 禁用数量
const inactiveCount = computed(() => {
  return props.permissions.filter(p => p.status === 'inactive').length
})

// 递归过滤权限
const filterPermissions = (permissions, keyword) => {
  return permissions.reduce((result, permission) => {
    const matchesSearch = 
      permission.name.toLowerCase().includes(keyword) ||
      permission.code.toLowerCase().includes(keyword) ||
      (permission.description && permission.description.toLowerCase().includes(keyword))
    
    let filteredChildren = []
    if (permission.children && permission.children.length > 0) {
      filteredChildren = filterPermissions(permission.children, keyword)
    }
    
    if (matchesSearch || filteredChildren.length > 0) {
      result.push({
        ...permission,
        children: filteredChildren
      })
    }
    
    return result
  }, [])
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

// 获取模块标签类型
const getModuleTagType = (module) => {
  const moduleMap = {
    dashboard: 'primary',
    users: 'success',
    content: 'warning',
    system: 'danger',
    permissions: 'info'
  }
  return moduleMap[module] || 'info'
}

// 获取模块文本
const getModuleText = (module) => {
  const moduleMap = {
    dashboard: '仪表盘',
    users: '用户管理',
    content: '内容管理',
    system: '系统管理',
    permissions: '权限管理'
  }
  return moduleMap[module] || module
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString()
}

// 处理刷新
const handleRefresh = () => {
  emit('refresh')
}

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

// 处理全部展开
const handleExpandAll = () => {
  const allKeys = []
  const collectKeys = (permissions) => {
    permissions.forEach(permission => {
      allKeys.push(permission.id)
      if (permission.children && permission.children.length > 0) {
        collectKeys(permission.children)
      }
    })
  }
  collectKeys(props.permissions)
  expandedKeys.value = allKeys
}

// 处理全部折叠
const handleCollapseAll = () => {
  expandedKeys.value = []
}

// 处理展开变化
const handleExpandChange = (row, expanded) => {
  if (expanded) {
    if (!expandedKeys.value.includes(row.id)) {
      expandedKeys.value.push(row.id)
    }
  } else {
    const index = expandedKeys.value.indexOf(row.id)
    if (index > -1) {
      expandedKeys.value.splice(index, 1)
    }
  }
}

// 组件挂载时自动展开第一层
onMounted(() => {
  if (props.permissions.length > 0) {
    expandedKeys.value = props.permissions.map(p => p.id)
  }
})
</script>

<style scoped>
.permissions-tab {
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

.toolbar-right {
  display: flex;
  gap: 12px;
}

.permissions-tree {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.permission-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.permission-icon {
  font-size: 16px;
  color: #409eff;
}

.permission-type-tag {
  margin: 0;
}

.permission-code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  color: #e83e8c;
}

.usage-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.permissions-stats {
  margin-top: 20px;
}

.stats-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.stats-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.stat-value.active {
  color: #67c23a;
}

.stat-value.inactive {
  color: #f56c6c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .permissions-tab {
    padding: 16px;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  .stats-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-item {
    padding: 10px;
  }
  
  .stat-value {
    font-size: 16px;
  }
}

/* 动画效果 */
.toolbar {
  animation: fadeInUp 0.3s ease-out;
}

.permissions-tree {
  animation: fadeInUp 0.3s ease-out 0.1s both;
}

.permissions-stats {
  animation: fadeInUp 0.3s ease-out 0.2s both;
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
