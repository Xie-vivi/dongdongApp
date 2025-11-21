<template>
  <div class="user-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>用户管理</h2>
        <p>管理系统用户信息、状态和行为分析</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleExportUsers" :loading="isLoading">
          <el-icon><Download /></el-icon>
          导出用户
        </el-button>
        <el-button type="success" @click="handleRefreshUsers">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="statistics-overview">
      <div class="stat-cards">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatNumber(userAnalytics?.totalUsers || 0) }}</div>
            <div class="stat-label">总用户数</div>
            <div class="stat-trend">较上月 +{{ userAnalytics?.userGrowthRate || 0 }}%</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon active">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatNumber(userAnalytics?.activeUsers || 0) }}</div>
            <div class="stat-label">活跃用户</div>
            <div class="stat-trend">活跃率 {{ userAnalytics?.activityRate || 0 }}%</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon new">
            <el-icon><Plus /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ userAnalytics?.newUsersToday || 0 }}</div>
            <div class="stat-label">今日新增</div>
            <div class="stat-trend">本月新增 {{ userAnalytics?.newUsersThisMonth || 0 }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon verified">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatNumber(userAnalytics?.verifiedUsers || 0) }}</div>
            <div class="stat-label">已认证用户</div>
            <div class="stat-trend">认证率 {{ ((userAnalytics?.verifiedUsers || 0) / (userAnalytics?.totalUsers || 1) * 100).toFixed(1) }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速筛选 -->
    <div class="quick-filters">
      <div class="filter-section">
        <h3>快速筛选</h3>
        <div class="filter-buttons">
          <el-button
            :type="queryParams.status === '' ? 'primary' : 'default'"
            @click="handleFilterByStatus('')"
          >
            全部用户
          </el-button>
          <el-button
            :type="queryParams.status === 'active' ? 'primary' : 'default'"
            @click="handleFilterByStatus('active')"
          >
            正常用户
          </el-button>
          <el-button
            :type="queryParams.status === 'disabled' ? 'primary' : 'default'"
            @click="handleFilterByStatus('disabled')"
          >
            禁用用户
          </el-button>
          <el-button
            :type="queryParams.status === 'pending' ? 'primary' : 'default'"
            @click="handleFilterByStatus('pending')"
          >
            待审核
          </el-button>
          <el-button
            :type="queryParams.verified === true ? 'primary' : 'default'"
            @click="handleFilterByVerified(true)"
          >
            已认证
          </el-button>
          <el-button
            :type="queryParams.verified === false ? 'primary' : 'default'"
            @click="handleFilterByVerified(false)"
          >
            未认证
          </el-button>
        </div>
      </div>

      <div class="filter-section">
        <h3>角色筛选</h3>
        <div class="filter-buttons">
          <el-button
            :type="queryParams.role === '' ? 'primary' : 'default'"
            @click="handleFilterByRole('')"
          >
            全部角色
          </el-button>
          <el-button
            :type="queryParams.role === 'admin' ? 'primary' : 'default'"
            @click="handleFilterByRole('admin')"
          >
            管理员
          </el-button>
          <el-button
            :type="queryParams.role === 'editor' ? 'primary' : 'default'"
            @click="handleFilterByRole('editor')"
          >
            编辑
          </el-button>
          <el-button
            :type="queryParams.role === 'reviewer' ? 'primary' : 'default'"
            @click="handleFilterByRole('reviewer')"
          >
            审核员
          </el-button>
          <el-button
            :type="queryParams.role === 'user' ? 'primary' : 'default'"
            @click="handleFilterByRole('user')"
          >
            普通用户
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-tabs v-model="activeTab" class="content-tabs">
        <!-- 用户列表 -->
        <el-tab-pane label="用户列表" name="users">
          <UserList
            :users="users"
            :loading="isLoading"
            :total="pagination.total"
            :page-size="pagination.pageSize"
            :current-page="pagination.page"
            :selected-users="selectedUsers"
            :query-params="queryParams"
            @page-change="handlePageChange"
            @size-change="handleSizeChange"
            @sort-change="handleSortChange"
            @selection-change="handleSelectionChange"
            @view-user="handleViewUser"
            @edit-user="handleEditUser"
            @update-status="handleUpdateStatus"
            @delete-user="handleDeleteUser"
            @search="handleSearch"
            @filter="handleFilter"
          />
        </el-tab-pane>

        <!-- 数据分析 -->
        <el-tab-pane label="数据分析" name="analytics">
          <UserAnalytics
            :statistics="userAnalytics"
            :loading="isLoading"
            @refresh="handleRefreshAnalytics"
          />
        </el-tab-pane>

        <!-- 行为追踪 -->
        <el-tab-pane label="行为追踪" name="behavior">
          <UserBehavior
            :behavior-data="behaviorData"
            :loading="isLoading"
            @refresh="handleRefreshBehavior"
          />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="userDetailVisible"
      :title="`用户详情 - ${userDetail?.username || ''}`"
      width="80%"
      destroy-on-close
    >
      <UserDetail
        v-if="userDetail"
        :user="userDetail"
        :show-full-data="hasFullDataPermission"
        @edit="handleEditUser"
        @update-status="handleUpdateStatus"
        @export-data="handleExportUserData"
        @close="userDetailVisible = false"
      />
    </el-dialog>

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="userEditVisible"
      :title="editingUser?.id ? '编辑用户' : '新建用户'"
      width="60%"
      destroy-on-close
    >
      <UserEdit
        v-if="userEditVisible"
        :user="editingUser"
        @save="handleSaveUser"
        @cancel="userEditVisible = false"
      />
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog
      v-model="batchOperationVisible"
      :title="`批量操作 - 已选择 ${selectedUsers.length} 个用户`"
      width="50%"
    >
      <div class="batch-operations">
        <div class="operation-group">
          <h4>状态操作</h4>
          <div class="operation-buttons">
            <el-button type="success" @click="handleBatchUpdateStatus('active')">
              批量启用
            </el-button>
            <el-button type="warning" @click="handleBatchUpdateStatus('disabled')">
              批量禁用
            </el-button>
            <el-button type="info" @click="handleBatchUpdateStatus('pending')">
              批量待审核
            </el-button>
          </div>
        </div>

        <div class="operation-group">
          <h4>角色操作</h4>
          <div class="operation-buttons">
            <el-button @click="handleBatchAssignRole('admin')">
              设为管理员
            </el-button>
            <el-button @click="handleBatchAssignRole('editor')">
              设为编辑
            </el-button>
            <el-button @click="handleBatchAssignRole('reviewer')">
              设为审核员
            </el-button>
            <el-button @click="handleBatchAssignRole('user')">
              设为普通用户
            </el-button>
          </div>
        </div>

        <div class="operation-group">
          <h4>数据操作</h4>
          <div class="operation-buttons">
            <el-button type="primary" @click="handleBatchExportUsers">
              批量导出
            </el-button>
            <el-button type="danger" @click="handleBatchDeleteUsers">
              批量删除
            </el-button>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="batchOperationVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Plus,
  CircleCheck,
  Download,
  Refresh
} from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'
import UserList from './components/UserList.vue'
import UserDetail from './components/UserDetail.vue'
import UserEdit from './components/UserEdit.vue'
import UserAnalytics from './components/UserAnalytics.vue'
import UserBehavior from './components/UserBehavior.vue'

// 使用组合式函数
const {
  // 状态
  isLoading,
  users,
  userDetail,
  userBehavior,
  userAnalytics,
  selectedUsers,
  pagination,
  queryParams,
  
  // 计算属性
  hasSelection,
  selectedUserIds,
  
  // 方法
  getUsers,
  getUserDetail,
  updateUser,
  updateUserStatus,
  deleteUser,
  getUserBehavior,
  getUserAnalytics,
  exportUsers,
  batchUpdateStatus,
  batchDelete,
  
  // 工具方法
  searchUsers,
  resetSearch,
  handlePageChange,
  handleSizeChange,
  handleSortChange,
  handleSelectionChange,
  handleSelectAll,
  clearError,
  resetData
} = useUserManagement()

// 响应式数据
const activeTab = ref('users')
const userDetailVisible = ref(false)
const userEditVisible = ref(false)
const batchOperationVisible = ref(false)
const editingUser = ref(null)

// 权限检查
const hasFullDataPermission = computed(() => {
  // 这里应该检查当前管理员是否有查看完整用户数据的权限
  return true
})

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 筛选操作
const handleFilterByStatus = (status) => {
  searchUsers({ status })
}

const handleFilterByVerified = (verified) => {
  searchUsers({ verified })
}

const handleFilterByRole = (role) => {
  searchUsers({ role })
}

// 用户操作
const handleViewUser = async (user) => {
  await getUserDetail(user.id)
  userDetailVisible.value = true
}

const handleEditUser = (user) => {
  editingUser.value = user ? { ...user } : null
  userEditVisible.value = true
}

const handleUpdateStatus = async (user, status, reason) => {
  try {
    await updateUserStatus(user.id, status)
  } catch (error) {
    console.error('更新用户状态失败:', error)
  }
}

const handleDeleteUser = async (user) => {
  try {
    await deleteUser(user.id)
  } catch (error) {
    console.error('删除用户失败:', error)
  }
}

const handleSaveUser = async (userData) => {
  try {
    if (userData.id) {
      await updateUser(userData.id, userData)
    } else {
      // Handle create new user if needed
      // await createUser(userData)
    }
    userEditVisible.value = false
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

const handleExportUserData = async (userId, format = 'json') => {
  try {
    // Use the exportUsers method with specific user filter
    await exportUsers({ userId, format })
  } catch (error) {
    console.error('导出用户数据失败:', error)
  }
}

const handleSearch = (keyword) => {
  searchUsers({ search: keyword })
}

const handleFilter = (filters) => {
  searchUsers(filters)
}

// 批量操作
const handleBatchUpdateStatus = async (status) => {
  try {
    await batchUpdateStatus(selectedUserIds.value, status)
    batchOperationVisible.value = false
  } catch (error) {
    console.error('批量更新状态失败:', error)
  }
}

const handleBatchAssignRole = async (role) => {
  try {
    // This would need to be implemented in the API
    // await batchUpdateRole(selectedUserIds.value, role)
    batchOperationVisible.value = false
  } catch (error) {
    console.error('批量分配角色失败:', error)
  }
}

const handleBatchExportUsers = async () => {
  try {
    await exportUsers({ userIds: selectedUserIds.value })
    batchOperationVisible.value = false
  } catch (error) {
    console.error('批量导出失败:', error)
  }
}

const handleBatchDeleteUsers = async () => {
  try {
    await batchDelete(selectedUserIds.value)
    batchOperationVisible.value = false
  } catch (error) {
    console.error('批量删除失败:', error)
  }
}

// 导出用户
const handleExportUsers = async () => {
  try {
    await exportUsers()
  } catch (error) {
    console.error('导出用户失败:', error)
  }
}

// 刷新数据
const handleRefreshUsers = async () => {
  await getUsers()
}

const handleRefreshAnalytics = async () => {
  await getUserAnalytics()
}

const handleRefreshBehavior = async () => {
  // This would need a specific user ID
  // await getUserBehavior(userId)
}

// 组件挂载
onMounted(async () => {
  await Promise.all([
    getUsers(),
    getUserAnalytics()
  ])
})
</script>

<style scoped>
.user-management {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-left p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.statistics-overview {
  margin-bottom: 24px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 8px;
  font-size: 20px;
  color: #fff;
}

.stat-icon.active {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-icon.new {
  background: linear-gradient(135deg, #e6a23c 0%, #f7ba2a 100%);
}

.stat-icon.verified {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
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
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  color: #909399;
}

.quick-filters {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.filter-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.main-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.content-tabs {
  padding: 0;
}

.batch-operations {
  padding: 20px 0;
}

.operation-group {
  margin-bottom: 24px;
}

.operation-group:last-child {
  margin-bottom: 0;
}

.operation-group h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.operation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management {
    padding: 12px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  
  .header-right {
    width: 100%;
    justify-content: flex-end;
  }
  
  .stat-cards {
    grid-template-columns: 1fr;
  }
  
  .quick-filters {
    grid-template-columns: 1fr;
  }
  
  .filter-buttons {
    justify-content: center;
  }
}

/* 动画效果 */
.stat-card,
.filter-section {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
