<template>
  <div class="administrators-tab">
    <!-- 搜索表单 -->
    <SearchForm
      v-model="searchParams"
      :fields="searchFields"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    />
    
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button
          type="primary"
          @click="handleCreate"
        >
          <el-icon><Plus /></el-icon>
          新增管理员
        </el-button>
        <el-button
          @click="handleRefresh"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button
          v-if="hasSelection"
          type="danger"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <DataTable
      :data="administrators"
      :columns="tableColumns"
      :loading="loading"
      :pagination="pagination"
      show-selection
      show-batch-delete
      @refresh="handleRefresh"
      @row-click="handleView"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
      @batch-delete="handleBatchDelete"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    >
      <!-- 自定义角色列 -->
      <template #column-roles="{ row }">
        <div class="role-tags">
          <el-tag
            v-for="role in row.roles"
            :key="role.id"
            size="small"
            type="primary"
            class="role-tag"
          >
            {{ role.name }}
          </el-tag>
          <span v-if="!row.roles || row.roles.length === 0" class="no-roles">
            暂无角色
          </span>
        </div>
      </template>
      
      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <StatusTag :status="row.status" type="system" />
      </template>
      
      <!-- 自定义操作列 -->
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleView(row)"
        >
          查看
        </el-button>
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleEdit(row)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="warning"
          size="small"
          @click.stop="handleToggleStatus(row)"
        >
          {{ row.status === 'active' ? '禁用' : '启用' }}
        </el-button>
        <el-button
          link
          type="success"
          size="small"
          @click.stop="handleAssignRoles(row)"
        >
          分配角色
        </el-button>
        <el-button
          link
          type="danger"
          size="small"
          @click.stop="handleDelete(row)"
        >
          删除
        </el-button>
      </template>
    </DataTable>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus, Refresh, Delete } from '@element-plus/icons-vue'
import DataTable from '@/components/admin/DataTable.vue'
import SearchForm from '@/components/admin/SearchForm.vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  administrators: {
    type: Array,
    default: () => []
  },
  roles: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  searchFields: {
    type: Array,
    default: () => []
  },
  tableColumns: {
    type: Array,
    default: () => []
  },
  pagination: {
    type: Object,
    default: () => ({})
  },
  hasSelection: {
    type: Boolean,
    default: false
  },
  selectedAdministratorIds: {
    type: Array,
    default: () => []
  }
})

// Emits 定义
const emit = defineEmits([
  'refresh',
  'create',
  'edit',
  'delete',
  'toggle-status',
  'assign-roles',
  'batch-delete',
  'search',
  'reset',
  'page-change',
  'size-change',
  'sort-change',
  'selection-change'
])

// 搜索参数
const searchParams = reactive({
  page: 1,
  size: 20,
  username: '',
  email: '',
  status: '',
  roleId: ''
})

// 处理刷新
const handleRefresh = () => {
  emit('refresh')
}

// 处理创建
const handleCreate = () => {
  emit('create')
}

// 处理查看
const handleView = (administrator) => {
  emit('view', administrator)
}

// 处理编辑
const handleEdit = (administrator) => {
  emit('edit', administrator)
}

// 处理删除
const handleDelete = (administrator) => {
  emit('delete', administrator)
}

// 处理切换状态
const handleToggleStatus = (administrator) => {
  emit('toggle-status', administrator)
}

// 处理分配角色
const handleAssignRoles = (administrator) => {
  emit('assign-roles', administrator)
}

// 处理批量删除
const handleBatchDelete = () => {
  emit('batch-delete', props.selectedAdministratorIds)
}

// 处理搜索
const handleSearch = (params) => {
  emit('search', params)
}

// 处理重置
const handleReset = () => {
  emit('reset')
}

// 处理分页变化
const handlePageChange = (page) => {
  emit('page-change', page)
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 处理排序变化
const handleSortChange = (sort) => {
  emit('sort-change', sort)
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  emit('selection-change', selection)
}
</script>

<style scoped>
.administrators-tab {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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

.role-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  margin: 0;
}

.no-roles {
  color: #909399;
  font-style: italic;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .administrators-tab {
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
  
  .role-tags {
    flex-direction: column;
    gap: 2px;
  }
}

/* 动画效果 */
.toolbar {
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
