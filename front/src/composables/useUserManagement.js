/**
 * 用户管理组合式函数
 * 统一管理用户数据的获取、更新、删除等功能
 */

import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 全局状态管理
const globalUserState = ref({
  isLoading: false,
  error: null,
  lastUpdated: null,
  isUsingFallbackData: false
})

// 缓存数据
const cachedData = reactive({
  users: [],
  userDetail: null,
  userBehavior: null,
  userAnalytics: null
})

/**
 * 用户管理组合式函数
 */
export function useUserManagement() {
  // 响应式状态
  const isLoading = computed(() => globalUserState.value.isLoading)
  const error = computed(() => globalUserState.value.error)
  const lastUpdated = computed(() => globalUserState.value.lastUpdated)
  const isUsingFallbackData = computed(() => globalUserState.value.isUsingFallbackData)
  
  // 数据状态
  const users = ref([])
  const userDetail = ref(null)
  const userBehavior = ref(null)
  const userAnalytics = ref(null)
  const selectedUsers = ref([])
  
  // 分页和查询参数
  const pagination = reactive({
    page: 1,
    pageSize: 20,
    total: 0
  })
  
  const queryParams = reactive({
    keyword: '',
    status: '',
    role: '',
    verified: null,
    sortBy: 'createdAt',
    sortOrder: 'desc',
    page: 1,
    pageSize: 20
  })
  
  // 计算属性
  const hasSelection = computed(() => selectedUsers.value.length > 0)
  const selectedUserIds = computed(() => selectedUsers.value.map(user => user.id))
  
  /**
   * 获取用户列表
   */
  const getUsers = async (params = {}) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 合并查询参数
      const finalParams = { ...queryParams, ...params }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 模拟数据
      const mockUsers = [
        {
          id: 1,
          username: 'admin',
          email: 'admin@example.com',
          nickname: '管理员',
          avatar: '',
          status: 'active',
          role: 'admin',
          verified: true,
          createdAt: '2024-01-01 10:00:00',
          lastLoginAt: '2024-01-15 09:30:00'
        },
        {
          id: 2,
          username: 'user001',
          email: 'user001@example.com',
          nickname: '普通用户',
          avatar: '',
          status: 'active',
          role: 'user',
          verified: false,
          createdAt: '2024-01-02 14:20:00',
          lastLoginAt: '2024-01-14 16:45:00'
        }
      ]
      
      users.value = mockUsers
      cachedData.users = mockUsers
      pagination.total = 50
      globalUserState.value.lastUpdated = new Date().toISOString()
      
      return { success: true, data: mockUsers, fromCache: false }
    } catch (error) {
      globalUserState.value.error = error.message
      
      // 使用缓存数据
      if (cachedData.users.length > 0) {
        users.value = cachedData.users
        globalUserState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存的用户数据')
        return { success: true, data: cachedData.users, fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取用户列表失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 获取用户详情
   */
  const getUserDetail = async (userId) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      
      const mockDetail = {
        id: userId,
        username: 'user001',
        email: 'user001@example.com',
        nickname: '用户详情',
        avatar: '',
        status: 'active',
        role: 'user',
        verified: false,
        profile: {
          bio: '这是用户简介',
          location: '北京',
          website: 'https://example.com'
        },
        stats: {
          postsCount: 10,
          followersCount: 50,
          followingCount: 30
        },
        createdAt: '2024-01-01 10:00:00',
        lastLoginAt: '2024-01-15 09:30:00'
      }
      
      userDetail.value = mockDetail
      cachedData.userDetail = mockDetail
      
      return { success: true, data: mockDetail }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '获取用户详情失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 更新用户信息
   */
  const updateUser = async (userId, userData) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('用户信息更新成功')
      
      // 更新本地数据
      const userIndex = users.value.findIndex(user => user.id === userId)
      if (userIndex !== -1) {
        users.value[userIndex] = { ...users.value[userIndex], ...userData }
      }
      
      return { success: true, data: userData }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '更新用户信息失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 更新用户状态
   */
  const updateUserStatus = async (userId, status) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      
      ElMessage.success('用户状态更新成功')
      
      // 更新本地数据
      const userIndex = users.value.findIndex(user => user.id === userId)
      if (userIndex !== -1) {
        users.value[userIndex].status = status
      }
      
      return { success: true }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '更新用户状态失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 删除用户
   */
  const deleteUser = async (userId) => {
    try {
      await ElMessageBox.confirm(
        '确定要删除该用户吗？此操作不可恢复。',
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('用户删除成功')
      
      // 更新本地数据
      users.value = users.value.filter(user => user.id !== userId)
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        globalUserState.value.error = error.message
        ElMessage.error(error.message || '删除用户失败')
      }
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 获取用户行为数据
   */
  const getUserBehavior = async (userId) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      
      const mockBehavior = {
        loginHistory: [
          { time: '2024-01-15 09:30:00', ip: '192.168.1.1', device: 'Chrome/Windows' },
          { time: '2024-01-14 16:45:00', ip: '192.168.1.2', device: 'Safari/macOS' }
        ],
        activityLog: [
          { action: '发布文章', time: '2024-01-15 10:00:00', details: '标题：技术分享' },
          { action: '点赞评论', time: '2024-01-15 09:45:00', details: '评论ID：123' }
        ]
      }
      
      userBehavior.value = mockBehavior
      cachedData.userBehavior = mockBehavior
      
      return { success: true, data: mockBehavior }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '获取用户行为数据失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 获取用户分析数据
   */
  const getUserAnalytics = async (userId) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      
      const mockAnalytics = {
        engagement: {
          postsCount: 10,
          likesReceived: 150,
          commentsReceived: 45,
          sharesReceived: 20
        },
        growth: {
          followersGrowth: [10, 15, 20, 25, 30, 35, 40, 45, 50],
          postsGrowth: [1, 2, 3, 5, 6, 7, 8, 9, 10]
        }
      }
      
      userAnalytics.value = mockAnalytics
      cachedData.userAnalytics = mockAnalytics
      
      return { success: true, data: mockAnalytics }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '获取用户分析数据失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 导出用户数据
   */
  const exportUsers = async (params = {}) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟导出
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      ElMessage.success('用户数据导出成功')
      return { success: true }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '导出用户数据失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 批量更新用户状态
   */
  const batchUpdateStatus = async (userIds, status) => {
    try {
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success(`批量更新 ${userIds.length} 个用户状态成功`)
      
      // 更新本地数据
      users.value.forEach(user => {
        if (userIds.includes(user.id)) {
          user.status = status
        }
      })
      
      return { success: true }
    } catch (error) {
      globalUserState.value.error = error.message
      ElMessage.error(error.message || '批量更新用户状态失败')
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 批量删除用户
   */
  const batchDelete = async (userIds) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${userIds.length} 个用户吗？此操作不可恢复。`,
        '确认批量删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      globalUserState.value.isLoading = true
      globalUserState.value.error = null
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success(`批量删除 ${userIds.length} 个用户成功`)
      
      // 更新本地数据
      users.value = users.value.filter(user => !userIds.includes(user.id))
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        globalUserState.value.error = error.message
        ElMessage.error(error.message || '批量删除用户失败')
      }
      return { success: false, message: error.message }
    } finally {
      globalUserState.value.isLoading = false
    }
  }
  
  /**
   * 搜索用户
   */
  const searchUsers = async (params = {}) => {
    Object.assign(queryParams, params)
    queryParams.page = 1
    return await getUsers()
  }
  
  /**
   * 重置搜索
   */
  const resetSearch = () => {
    Object.assign(queryParams, {
      keyword: '',
      status: '',
      role: '',
      verified: null,
      sortBy: 'createdAt',
      sortOrder: 'desc',
      page: 1,
      pageSize: 20
    })
    getUsers()
  }
  
  /**
   * 分页处理
   */
  const handlePageChange = (page) => {
    queryParams.page = page
    pagination.page = page
    getUsers()
  }
  
  const handleSizeChange = (size) => {
    queryParams.pageSize = size
    queryParams.page = 1
    pagination.pageSize = size
    pagination.page = 1
    getUsers()
  }
  
  /**
   * 排序处理
   */
  const handleSortChange = ({ prop, order }) => {
    queryParams.sortBy = prop
    queryParams.sortOrder = order === 'ascending' ? 'asc' : 'desc'
    getUsers()
  }
  
  /**
   * 选择处理
   */
  const handleSelectionChange = (selection) => {
    selectedUsers.value = selection
  }
  
  const handleSelectAll = (selection) => {
    selectedUsers.value = selection
  }
  
  /**
   * 清除错误
   */
  const clearError = () => {
    globalUserState.value.error = null
  }
  
  /**
   * 重置数据
   */
  const resetData = () => {
    users.value = []
    userDetail.value = null
    userBehavior.value = null
    userAnalytics.value = null
    selectedUsers.value = []
    Object.assign(pagination, { page: 1, pageSize: 20, total: 0 })
    Object.assign(queryParams, {
      keyword: '',
      status: '',
      role: '',
      verified: null,
      sortBy: 'createdAt',
      sortOrder: 'desc',
      page: 1,
      pageSize: 20
    })
  }
  
  return {
    // 状态
    isLoading,
    error,
    lastUpdated,
    isUsingFallbackData,
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
  }
}
