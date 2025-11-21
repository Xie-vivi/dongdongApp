<template>
  <div class="admin-permissions">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>权限管理</h2>
      <p>管理系统管理员、角色和权限配置</p>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ administrators.length }}</div>
          <div class="stat-label">管理员</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ roles.length }}</div>
          <div class="stat-label">角色</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Key /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ permissions.length }}</div>
          <div class="stat-label">权限</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Connection /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总记录数</div>
        </div>
      </div>
    </div>
    
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" class="permission-tabs">
      <!-- 管理员管理 -->
      <el-tab-pane label="管理员管理" name="administrators">
        <AdministratorsTab
          :administrators="administrators"
          :roles="roles"
          :loading="loading"
          :search-fields="adminSearchFields"
          :table-columns="adminTableColumns"
          :pagination="pagination"
          :has-selection="hasSelection"
          :selected-administrator-ids="selectedAdministratorIds"
          @refresh="handleRefresh"
          @create="handleCreateAdministrator"
          @edit="handleEditAdministrator"
          @delete="handleDeleteAdministrator"
          @toggle-status="handleToggleAdministratorStatus"
          @assign-roles="handleAssignRoles"
          @batch-delete="handleBatchDeleteAdministrators"
          @search="handleSearchAdministrators"
          @reset="handleResetSearch"
          @page-change="handlePageChange"
          @size-change="handleSizeChange"
          @sort-change="handleSortChange"
          @selection-change="handleSelectionChange"
        />
      </el-tab-pane>
      
      <!-- 角色管理 -->
      <el-tab-pane label="角色管理" name="roles">
        <RolesTab
          :roles="roles"
          :permissions="permissions"
          :loading="loading"
          @refresh="handleRefreshRoles"
          @create="handleCreateRole"
          @edit="handleEditRole"
          @delete="handleDeleteRole"
          @assign-permissions="handleAssignPermissions"
        />
      </el-tab-pane>
      
      <!-- 权限管理 -->
      <el-tab-pane label="权限管理" name="permissions">
        <PermissionsTab
          :permissions="permissions"
          :loading="loading"
          @refresh="handleRefreshPermissions"
        />
      </el-tab-pane>
    </el-tabs>
    
    <!-- 管理员详情对话框 -->
    <AdministratorDetailDialog
      v-model="showAdministratorDetailDialog"
      :administrator="selectedAdministrator"
      :roles="roles"
    />
    
    <!-- 管理员编辑对话框 -->
    <AdministratorEditDialog
      v-model="showAdministratorEditDialog"
      :administrator="editingAdministrator"
      :roles="roles"
      @success="handleAdministratorEditSuccess"
    />
    
    <!-- 角色分配对话框 -->
    <RoleAssignmentDialog
      v-model="showRoleAssignmentDialog"
      :administrator="assigningRolesAdministrator"
      :roles="roles"
      @success="handleRoleAssignmentSuccess"
    />
    
    <!-- 角色详情对话框 -->
    <RoleDetailDialog
      v-model="showRoleDetailDialog"
      :role="selectedRole"
      :permissions="permissions"
    />
    
    <!-- 角色编辑对话框 -->
    <RoleEditDialog
      v-model="showRoleEditDialog"
      :role="editingRole"
      :permissions="permissions"
      @success="handleRoleEditSuccess"
    />
    
    <!-- 权限分配对话框 -->
    <PermissionAssignmentDialog
      v-model="showPermissionAssignmentDialog"
      :role="assigningPermissionsRole"
      :permissions="permissions"
      @success="handlePermissionAssignmentSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { User, UserFilled, Key, Connection } from '@element-plus/icons-vue'
import { usePermissionManagement } from '@/composables/usePermissionManagement'
import AdministratorsTab from './components/AdministratorsTab.vue'
import RolesTab from './components/RolesTab.vue'
import PermissionsTab from './components/PermissionsTab.vue'
import AdministratorDetailDialog from './components/AdministratorDetailDialog.vue'
import AdministratorEditDialog from './components/AdministratorEditDialog.vue'
import RoleAssignmentDialog from './components/RoleAssignmentDialog.vue'
import RoleDetailDialog from './components/RoleDetailDialog.vue'
import RoleEditDialog from './components/RoleEditDialog.vue'
import PermissionAssignmentDialog from './components/PermissionAssignmentDialog.vue'

// 使用权限管理组合式函数
const {
  loading,
  administrators,
  roles,
  permissions,
  total,
  searchParams,
  selectedAdministratorIds,
  adminSearchFields,
  adminTableColumns,
  hasSelection,
  fetchAdministrators,
  fetchRoles,
  fetchPermissions,
  fetchAdministratorDetail,
  fetchRoleDetail,
  createAdministrator,
  updateAdministrator,
  deleteAdministrator,
  updateAdministratorStatus,
  createRole,
  updateRole,
  deleteRole,
  assignRolesToAdministrator,
  assignPermissionsToRole,
  searchAdministrators,
  resetSearch,
  handlePageChange: onPageChange,
  handleSizeChange: onSizeChange,
  handleSortChange: onSortChange,
  handleSelectionChange: onSelectionChange
} = usePermissionManagement()

// 当前激活的标签页
const activeTab = ref('administrators')

// 对话框状态
const showAdministratorDetailDialog = ref(false)
const showAdministratorEditDialog = ref(false)
const showRoleAssignmentDialog = ref(false)
const showRoleDetailDialog = ref(false)
const showRoleEditDialog = ref(false)
const showPermissionAssignmentDialog = ref(false)

// 选中的数据
const selectedAdministrator = ref(null)
const editingAdministrator = ref(null)
const assigningRolesAdministrator = ref(null)
const selectedRole = ref(null)
const editingRole = ref(null)
const assigningPermissionsRole = ref(null)

// 分页配置
const pagination = computed(() => ({
  current: searchParams.page,
  size: searchParams.size,
  total: total
}))

// 处理刷新管理员列表
const handleRefresh = () => {
  fetchAdministrators()
}

// 处理创建管理员
const handleCreateAdministrator = () => {
  editingAdministrator.value = null
  showAdministratorEditDialog.value = true
}

// 处理编辑管理员
const handleEditAdministrator = (administrator) => {
  editingAdministrator.value = { ...administrator }
  showAdministratorEditDialog.value = true
}

// 处理删除管理员
const handleDeleteAdministrator = (administrator) => {
  deleteAdministrator(administrator.id)
}

// 处理切换管理员状态
const handleToggleAdministratorStatus = (administrator) => {
  const newStatus = administrator.status === 'active' ? 'inactive' : 'active'
  updateAdministratorStatus(administrator.id, newStatus)
}

// 处理分配角色
const handleAssignRoles = (administrator) => {
  assigningRolesAdministrator.value = { ...administrator }
  showRoleAssignmentDialog.value = true
}

// 处理批量删除管理员
const handleBatchDeleteAdministrators = (adminIds) => {
  // 批量删除逻辑
  adminIds.forEach(id => deleteAdministrator(id))
}

// 处理搜索管理员
const handleSearchAdministrators = (params) => {
  searchAdministrators(params)
}

// 处理重置搜索
const handleResetSearch = () => {
  resetSearch()
}

// 处理刷新角色列表
const handleRefreshRoles = () => {
  fetchRoles()
}

// 处理创建角色
const handleCreateRole = () => {
  editingRole.value = null
  showRoleEditDialog.value = true
}

// 处理编辑角色
const handleEditRole = (role) => {
  editingRole.value = { ...role }
  showRoleEditDialog.value = true
}

// 处理删除角色
const handleDeleteRole = (role) => {
  deleteRole(role.id)
}

// 处理分配权限
const handleAssignPermissions = (role) => {
  assigningPermissionsRole.value = { ...role }
  showPermissionAssignmentDialog.value = true
}

// 处理刷新权限列表
const handleRefreshPermissions = () => {
  fetchPermissions()
}

// 处理管理员编辑成功
const handleAdministratorEditSuccess = () => {
  showAdministratorEditDialog.value = false
  editingAdministrator.value = null
  fetchAdministrators()
}

// 处理角色分配成功
const handleRoleAssignmentSuccess = () => {
  showRoleAssignmentDialog.value = false
  assigningRolesAdministrator.value = null
  fetchAdministrators()
}

// 处理角色编辑成功
const handleRoleEditSuccess = () => {
  showRoleEditDialog.value = false
  editingRole.value = null
  fetchRoles()
}

// 处理权限分配成功
const handlePermissionAssignmentSuccess = () => {
  showPermissionAssignmentDialog.value = false
  assigningPermissionsRole.value = null
  fetchRoles()
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
</script>

<style scoped>
.admin-permissions {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: #409eff;
  border-radius: 8px;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.permission-tabs {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

:deep(.el-tabs__header) {
  margin: 0;
  background: #f8f9fa;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-tabs__nav-wrap) {
  padding: 0 20px;
}

:deep(.el-tabs__item) {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  font-weight: 500;
  color: #606266;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  background: #fff;
}

:deep(.el-tabs__content) {
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    margin-right: 12px;
  }
  
  .stat-icon .el-icon {
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .page-header {
    text-align: center;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
}

/* 动画效果 */
.stats-cards {
  animation: fadeInUp 0.3s ease-out;
}

.permission-tabs {
  animation: fadeInUp 0.3s ease-out 0.1s both;
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
