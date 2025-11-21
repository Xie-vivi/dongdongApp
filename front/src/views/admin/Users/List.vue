<template>
  <div class="admin-users-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <p>管理系统中的所有用户账户</p>
    </div>
    
    <!-- 搜索表单 -->
    <SearchForm
      v-model="searchParams"
      :fields="searchFields"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    />
    
    <!-- 数据表格 -->
    <DataTable
      :data="users"
      :columns="tableColumns"
      :loading="loading"
      :pagination="pagination"
      show-selection
      show-batch-delete
      @refresh="handleRefresh"
      @row-click="handleViewUser"
      @view="handleViewUser"
      @edit="handleEditUser"
      @delete="handleDeleteUser"
      @batch-delete="handleBatchDelete"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    >
      <!-- 自定义操作列 -->
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleViewUser(row)"
        >
          查看
        </el-button>
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleEditUser(row)"
        >
          编辑
        </el-button>
        <el-dropdown @command="(command) => handleStatusCommand(command, row)">
          <el-button link type="warning" size="small">
            状态 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="active" :disabled="row.status === 'active'">
                <el-icon><Check /></el-icon>
                启用
              </el-dropdown-item>
              <el-dropdown-item command="inactive" :disabled="row.status === 'inactive'">
                <el-icon><Close /></el-icon>
                禁用
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button
          link
          type="danger"
          size="small"
          @click.stop="handleDeleteUser(row)"
        >
          删除
        </el-button>
      </template>
      
      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <StatusTag :status="row.status" type="user" />
      </template>
      
      <!-- 自定义头像列 -->
      <template #column-avatar="{ row }">
        <el-avatar :size="40" :src="row.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
      </template>
    </DataTable>
    
    <!-- 用户详情对话框 -->
    <UserDetailDialog
      v-model="showDetailDialog"
      :user="selectedUser"
      @edit="handleEditUser"
    />
    
    <!-- 用户编辑对话框 -->
    <UserEditDialog
      v-model="showEditDialog"
      :user="editingUser"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, Check, Close, User } from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'
import DataTable from '@/components/admin/DataTable.vue'
import SearchForm from '@/components/admin/SearchForm.vue'
import StatusTag from '@/components/admin/StatusTag.vue'
import UserDetailDialog from './components/UserDetailDialog.vue'
import UserEditDialog from './components/UserEditDialog.vue'

const router = useRouter()

// 使用用户管理组合式函数
const {
  loading,
  users,
  total,
  searchParams,
  selectedUsers,
  searchFields,
  tableColumns,
  hasSelection,
  selectedUserIds,
  fetchUsers,
  updateUserData,
  updateUserStatusData,
  deleteUserData,
  batchDeleteUsers,
  batchUpdateUserStatus,
  searchUsers,
  resetSearch,
  handlePageChange: onPageChange,
  handleSizeChange: onSizeChange,
  handleSortChange: onSortChange,
  handleSelectionChange: onSelectionChange
} = useUserManagement()

// 对话框状态
const showDetailDialog = ref(false)
const showEditDialog = ref(false)
const selectedUser = ref(null)
const editingUser = ref(null)

// 分页配置
const pagination = computed(() => ({
  current: searchParams.page,
  size: searchParams.size,
  total: total
}))

// 处理搜索
const handleSearch = (params) => {
  searchUsers(params)
}

// 处理重置
const handleReset = () => {
  resetSearch()
}

// 处理刷新
const handleRefresh = () => {
  fetchUsers()
}

// 处理查看用户
const handleViewUser = (user) => {
  selectedUser.value = user
  showDetailDialog.value = true
}

// 处理编辑用户
const handleEditUser = (user) => {
  editingUser.value = { ...user }
  showEditDialog.value = true
}

// 处理删除用户
const handleDeleteUser = (user) => {
  deleteUserData(user.id)
}

// 处理批量删除
const handleBatchDelete = (userIds) => {
  batchDeleteUsers(userIds)
}

// 处理状态命令
const handleStatusCommand = async (command, user) => {
  await updateUserStatusData(user.id, command)
}

// 处理编辑成功
const handleEditSuccess = () => {
  showEditDialog.value = false
  editingUser.value = null
  fetchUsers()
}

// 处理分页变化
const handlePageChange = (page) => {
  onPageChange(page)
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  onSizeChange(size)
}

// 处理排序变化
const handleSortChange = (sort) => {
  onSortChange(sort)
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  onSelectionChange(selection)
}

// 批量操作
const handleBatchEnable = () => {
  batchUpdateUserStatus(selectedUserIds.value, 'active')
}

const handleBatchDisable = () => {
  batchUpdateUserStatus(selectedUserIds.value, 'inactive')
}
</script>

<style scoped>
.admin-users-list {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    text-align: center;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
}
</style>
