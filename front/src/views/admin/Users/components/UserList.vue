<template>
  <div class="user-list">
    <!-- 搜索和筛选区域 -->
    <div class="filter-section">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名、邮箱、真实姓名..."
          clearable
          @input="handleSearchInput"
          @clear="handleSearchClear"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="advanced-filters">
        <el-form :model="filterForm" inline>
          <el-form-item label="用户状态">
            <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
              <el-option
                v-for="(status, key) in USER_STATUS"
                :key="key"
                :label="status.label"
                :value="status.value"
              >
                <div class="status-option">
                  <el-icon :style="{ color: status.color }">
                    <component :is="status.icon" />
                  </el-icon>
                  {{ status.label }}
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="用户角色">
            <el-select v-model="filterForm.role" placeholder="选择角色" clearable>
              <el-option
                v-for="(role, key) in USER_ROLES"
                :key="key"
                :label="role.label"
                :value="role.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="认证状态">
            <el-select v-model="filterForm.verified" placeholder="选择认证状态" clearable>
              <el-option label="已认证" :value="true" />
              <el-option label="未认证" :value="false" />
            </el-select>
          </el-form-item>

          <el-form-item label="风险等级">
            <el-select v-model="filterForm.riskLevel" placeholder="选择风险等级" clearable>
              <el-option label="低风险" value="low" />
              <el-option label="中风险" value="medium" />
              <el-option label="高风险" value="high" />
            </el-select>
          </el-form-item>

          <el-form-item label="注册时间">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="applyFilters">
              <el-icon><Filter /></el-icon>
              筛选
            </el-button>
            <el-button @click="resetFilters">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 批量操作工具栏 -->
    <div class="toolbar" v-if="hasSelectedUsers">
      <div class="selection-info">
        <span>已选择 {{ selectedUsersCount }} 个用户</span>
        <el-button type="text" @click="clearSelection">清空选择</el-button>
      </div>
      
      <div class="batch-actions">
        <el-button-group>
          <el-button type="success" @click="batchUpdateStatus('active')">
            <el-icon><Check /></el-icon>
            批量启用
          </el-button>
          <el-button type="warning" @click="batchUpdateStatus('disabled')">
            <el-icon><Close /></el-icon>
            批量禁用
          </el-button>
          <el-button type="info" @click="batchUpdateStatus('pending')">
            <el-icon><Clock /></el-icon>
            批量待审核
          </el-button>
        </el-button-group>

        <el-button-group>
          <el-button @click="batchAssignRole('admin')">设为管理员</el-button>
          <el-button @click="batchAssignRole('editor')">设为编辑</el-button>
          <el-button @click="batchAssignRole('reviewer')">设为审核员</el-button>
          <el-button @click="batchAssignRole('user')">设为普通用户</el-button>
        </el-button-group>

        <el-button type="primary" @click="batchExportUsers">
          <el-icon><Download /></el-icon>
          批量导出
        </el-button>
        
        <el-button type="danger" @click="batchDeleteUsers">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 用户表格 -->
    <div class="table-container">
      <el-table
        :data="users"
        :loading="loading"
        stripe
        border
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="ID" width="80" sortable />
        
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="row.avatar" :size="40" />
              <div class="user-details">
                <div class="username">{{ row.username }}</div>
                <div class="real-name">{{ row.realName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="联系方式" min-width="180">
          <template #default="{ row }">
            <div class="contact-info">
              <div class="email">
                <el-icon><Message /></el-icon>
                {{ row.email }}
              </div>
              <div class="phone">
                <el-icon><Phone /></el-icon>
                {{ row.phone }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              <el-icon class="status-icon">
                <component :is="getUserStatusInfo(row.status).icon" />
              </el-icon>
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="认证" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.verified" color="#67c23a" size="16">
              <CircleCheck />
            </el-icon>
            <el-icon v-else color="#f56c6c" size="16">
              <CircleClose />
            </el-icon>
          </template>
        </el-table-column>

        <el-table-column label="风险等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)" size="small">
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="活跃度" width="120" align="center">
          <template #default="{ row }">
            <div class="activity-score">
              <el-progress
                :percentage="row.activityScore"
                :color="getActivityColor(row.activityScore)"
                :show-text="false"
                :stroke-width="6"
              />
              <span>{{ row.activityScore }}%</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="统计" width="120" align="center">
          <template #default="{ row }">
            <div class="statistics">
              <div>登录: {{ row.loginCount }}</div>
              <div>帖子: {{ row.postCount }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="registrationTime" label="注册时间" width="160" sortable>
          <template #default="{ row }">
            <div class="time-info">
              <div>{{ formatDate(row.registrationTime) }}</div>
              <div class="relative-time">{{ formatRelativeTime(row.registrationTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="lastLoginTime" label="最后登录" width="160" sortable>
          <template #default="{ row }">
            <div class="time-info">
              <div>{{ formatDate(row.lastLoginTime) }}</div>
              <div class="relative-time">{{ formatRelativeTime(row.lastLoginTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="viewUser(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button type="warning" size="small" @click="editUser(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-dropdown @command="(command) => handleUserAction(command, row)">
                <el-button size="small">
                  更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="updateStatus">
                      <el-icon><Tools /></el-icon>
                      更新状态
                    </el-dropdown-item>
                    <el-dropdown-item command="assignRole">
                      <el-icon><UserFilled /></el-icon>
                      分配角色
                    </el-dropdown-item>
                    <el-dropdown-item command="viewBehavior">
                      <el-icon><DataLine /></el-icon>
                      行为分析
                    </el-dropdown-item>
                    <el-dropdown-item command="exportData">
                      <el-icon><Download /></el-icon>
                      导出数据
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除用户
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <div class="pagination-info">
        <span>共 {{ total }} 条记录</span>
        <span>当前第 {{ currentPage }} 页，每页显示 {{ pageSize }} 条</span>
      </div>
      
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 状态更新对话框 -->
    <el-dialog v-model="statusDialogVisible" title="更新用户状态" width="400px">
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="选择状态">
            <el-option
              v-for="(status, key) in USER_STATUS"
              :key="key"
              :label="status.label"
              :value="status.value"
            >
              <div class="status-option">
                <el-icon :style="{ color: status.color }">
                  <component :is="status.icon" />
                </el-icon>
                {{ status.label }}
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="原因">
          <el-input
            v-model="statusForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入状态变更原因（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStatusUpdate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 角色分配对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配用户角色" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="新角色">
          <el-select v-model="roleForm.role" placeholder="选择角色">
            <el-option
              v-for="(role, key) in USER_ROLES"
              :key="key"
              :label="role.label"
              :value="role.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="原因">
          <el-input
            v-model="roleForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入角色变更原因（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRoleAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Filter,
  RefreshLeft,
  Check,
  Close,
  Clock,
  Download,
  Delete,
  Message,
  Phone,
  CircleCheck,
  CircleClose,
  View,
  Edit,
  ArrowDown,
  Tools,
  UserFilled,
  DataLine
} from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'

// Props
const props = defineProps({
  users: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  pageSize: {
    type: Number,
    default: 20
  },
  currentPage: {
    type: Number,
    default: 1
  },
  selectedUsers: {
    type: Array,
    default: () => []
  },
  queryParams: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits([
  'page-change',
  'size-change',
  'sort-change',
  'selection-change',
  'view-user',
  'edit-user',
  'update-status',
  'delete-user',
  'search',
  'filter'
])

// 使用组合式函数
const {
  USER_STATUS,
  USER_ROLES,
  formatDate,
  formatRelativeTime,
  getUserStatusInfo,
  getUserRoleInfo,
  getRiskLevelType,
  getRiskLevelText
} = useUserManagement()

// 响应式数据
const searchKeyword = ref('')
const filterForm = reactive({
  status: '',
  role: '',
  verified: undefined,
  riskLevel: '',
  dateRange: []
})

const statusDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const statusForm = reactive({
  status: '',
  reason: ''
})
const roleForm = reactive({
  role: '',
  reason: ''
})

const currentUser = ref(null)

// 计算属性
const hasSelectedUsers = computed(() => props.selectedUsers.length > 0)
const selectedUsersCount = computed(() => props.selectedUsers.length)

// 获取角色标签类型
const getRoleTagType = (role) => {
  const typeMap = {
    admin: 'danger',
    editor: 'warning',
    reviewer: 'info',
    user: 'success'
  }
  return typeMap[role] || 'info'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    active: 'success',
    disabled: 'danger',
    pending: 'warning',
    suspended: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取活跃度颜色
const getActivityColor = (score) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 搜索处理
const handleSearchInput = (value) => {
  emit('search', value)
}

const handleSearchClear = () => {
  searchKeyword.value = ''
  emit('search', '')
}

// 筛选处理
const applyFilters = () => {
  const filters = { ...filterForm }
  emit('filter', filters)
}

const resetFilters = () => {
  Object.assign(filterForm, {
    status: '',
    role: '',
    verified: undefined,
    riskLevel: '',
    dateRange: []
  })
  emit('filter', filterForm)
}

// 分页处理
const handlePageChange = (page) => {
  emit('page-change', page)
}

const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 排序处理
const handleSortChange = ({ prop, order }) => {
  const sortBy = prop
  const sortOrder = order === 'ascending' ? 'asc' : 'desc'
  emit('sort-change', { sortBy, sortOrder })
}

// 选择处理
const handleSelectionChange = (selection) => {
  emit('selection-change', selection)
}

// 用户操作
const viewUser = (user) => {
  emit('view-user', user)
}

const editUser = (user) => {
  emit('edit-user', user)
}

const handleUserAction = (command, user) => {
  currentUser.value = user
  
  switch (command) {
    case 'updateStatus':
      statusForm.status = user.status
      statusForm.reason = ''
      statusDialogVisible.value = true
      break
    case 'assignRole':
      roleForm.role = user.role
      roleForm.reason = ''
      roleDialogVisible.value = true
      break
    case 'viewBehavior':
      // 查看行为分析
      ElMessage.info('行为分析功能开发中...')
      break
    case 'exportData':
      // 导出用户数据
      ElMessage.info('导出数据功能开发中...')
      break
    case 'delete':
      deleteUser(user)
      break
  }
}

// 批量操作
const batchUpdateStatus = (status) => {
  ElMessageBox.confirm(
    `确定要将选中的 ${selectedUsersCount.value} 个用户状态更新为 ${getUserStatusInfo(status).label} 吗？`,
    '批量更新状态',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用批量更新状态的API
    ElMessage.success('批量更新状态成功')
  }).catch(() => {
    // 用户取消操作
  })
}

const batchAssignRole = (role) => {
  ElMessageBox.confirm(
    `确定要将选中的 ${selectedUsersCount.value} 个用户角色设置为 ${getUserRoleInfo(role).label} 吗？`,
    '批量分配角色',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用批量分配角色的API
    ElMessage.success('批量分配角色成功')
  }).catch(() => {
    // 用户取消操作
  })
}

const batchExportUsers = () => {
  ElMessageBox.confirm(
    `确定要导出选中的 ${selectedUsersCount.value} 个用户数据吗？`,
    '批量导出',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 这里应该调用批量导出的API
    ElMessage.success('批量导出成功')
  }).catch(() => {
    // 用户取消操作
  })
}

const batchDeleteUsers = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedUsersCount.value} 个用户吗？此操作不可恢复！`,
    '批量删除',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    // 这里应该调用批量删除的API
    ElMessage.success('批量删除成功')
  }).catch(() => {
    // 用户取消操作
  })
}

const deleteUser = (user) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${user.username} 吗？此操作不可恢复！`,
    '删除用户',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    emit('delete-user', user)
  }).catch(() => {
    // 用户取消操作
  })
}

// 确认状态更新
const confirmStatusUpdate = () => {
  if (!statusForm.status) {
    ElMessage.warning('请选择新状态')
    return
  }
  
  emit('update-status', currentUser.value, statusForm.status, statusForm.reason)
  statusDialogVisible.value = false
}

// 确认角色分配
const confirmRoleAssign = () => {
  if (!roleForm.role) {
    ElMessage.warning('请选择新角色')
    return
  }
  
  // 这里应该调用角色分配的API
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
}

// 清空选择
const clearSelection = () => {
  emit('selection-change', [])
}
</script>

<style scoped>
.user-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.filter-section {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.search-bar {
  margin-bottom: 16px;
}

.search-bar .el-input {
  max-width: 400px;
}

.advanced-filters .el-form {
  margin: 0;
}

.advanced-filters .el-form-item {
  margin-bottom: 0;
  margin-right: 16px;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f0f9ff;
  border-bottom: 1px solid #ebeef5;
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.batch-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.table-container {
  padding: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
}

.username {
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.real-name {
  font-size: 12px;
  color: #909399;
}

.contact-info {
  font-size: 12px;
  line-height: 1.4;
}

.contact-info div {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 2px;
}

.contact-info div:last-child {
  margin-bottom: 0;
}

.status-icon {
  margin-right: 4px;
}

.activity-score {
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-score .el-progress {
  flex: 1;
}

.activity-score span {
  font-size: 12px;
  color: #606266;
  min-width: 30px;
}

.statistics {
  font-size: 12px;
  line-height: 1.4;
  color: #606266;
}

.time-info {
  font-size: 12px;
  line-height: 1.4;
}

.relative-time {
  color: #909399;
  margin-top: 2px;
}

.action-buttons {
  display: flex;
  gap: 4px;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

.pagination-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-section {
    padding: 16px;
  }
  
  .advanced-filters .el-form-item {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 12px;
    padding: 16px;
  }
  
  .batch-actions {
    justify-content: center;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-table .el-table__row:hover) {
  background: #f0f9ff;
}

/* 按钮组样式 */
:deep(.el-button-group .el-button) {
  border-radius: 4px;
}

:deep(.el-button-group .el-button:first-child) {
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}

:deep(.el-button-group .el-button:last-child) {
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}
</style>
