/**
 * 用户管理模块API接口
 * 处理用户的增删改查、状态管理、数据分析等
 */

import api from './index'

// 模拟数据（作为fallback）
const mockUsersData = {
  users: [
    {
      id: 1,
      username: 'admin',
      email: 'admin@xyst.com',
      nickname: '管理员',
      avatar: '/avatars/admin.jpg',
      phone: '13800138000',
      gender: 'unknown',
      birthday: '1990-01-01',
      bio: '系统管理员',
      location: '北京',
      website: 'https://xyst.com',
      company: 'XYST科技',
      position: '系统管理员',
      
      // 状态信息
      status: 'active',
      emailVerified: true,
      phoneVerified: true,
      isVip: true,
      vipLevel: 'vip3',
      vipExpireAt: '2025-12-31T23:59:59Z',
      
      // 统计数据
      postsCount: 156,
      commentsCount: 892,
      likesCount: 2341,
      followersCount: 1234,
      followingCount: 567,
      
      // 活动数据
      lastLoginAt: '2024-01-15T10:30:00Z',
      lastActiveAt: '2024-01-15T14:25:00Z',
      loginCount: 1234,
      onlineDuration: 86400, // 秒
      
      // 安全信息
      loginAttempts: 0,
      lockedUntil: null,
      passwordUpdatedAt: '2024-01-01T00:00:00Z',
      
      // 权限信息
      roles: ['admin', 'moderator'],
      permissions: ['user:manage', 'content:moderate', 'system:config'],
      
      // 时间戳
      createdAt: '2023-01-01T00:00:00Z',
      updatedAt: '2024-01-15T10:30:00Z'
    },
    {
      id: 2,
      username: 'developer001',
      email: 'dev001@example.com',
      nickname: '开发者001',
      avatar: '/avatars/user2.jpg',
      phone: '13800138001',
      gender: 'male',
      birthday: '1992-05-15',
      bio: '前端开发工程师，热爱技术分享',
      location: '上海',
      website: 'https://github.com/developer001',
      company: '科技创新公司',
      position: '前端工程师',
      
      status: 'active',
      emailVerified: true,
      phoneVerified: false,
      isVip: true,
      vipLevel: 'vip1',
      vipExpireAt: '2024-06-30T23:59:59Z',
      
      postsCount: 89,
      commentsCount: 456,
      likesCount: 1234,
      followersCount: 567,
      followingCount: 234,
      
      lastLoginAt: '2024-01-15T09:15:00Z',
      lastActiveAt: '2024-01-15T16:45:00Z',
      loginCount: 567,
      onlineDuration: 43200,
      
      loginAttempts: 0,
      lockedUntil: null,
      passwordUpdatedAt: '2023-12-01T00:00:00Z',
      
      roles: ['user'],
      permissions: ['content:create', 'comment:create'],
      
      createdAt: '2023-02-15T00:00:00Z',
      updatedAt: '2024-01-10T15:20:00Z'
    },
    {
      id: 3,
      username: 'designer_pro',
      email: 'designer@example.com',
      nickname: '设计达人',
      avatar: '/avatars/user3.jpg',
      phone: '13800138002',
      gender: 'female',
      birthday: '1993-08-20',
      bio: 'UI/UX设计师，专注用户体验设计',
      location: '深圳',
      website: 'https://dribbble.com/designer_pro',
      company: '设计工作室',
      position: 'UI设计师',
      
      status: 'inactive',
      emailVerified: true,
      phoneVerified: true,
      isVip: false,
      vipLevel: null,
      vipExpireAt: null,
      
      postsCount: 45,
      commentsCount: 234,
      likesCount: 789,
      followersCount: 345,
      followingCount: 123,
      
      lastLoginAt: '2024-01-10T08:30:00Z',
      lastActiveAt: '2024-01-10T12:15:00Z',
      loginCount: 234,
      onlineDuration: 28800,
      
      loginAttempts: 0,
      lockedUntil: null,
      passwordUpdatedAt: '2023-11-15T00:00:00Z',
      
      roles: ['user'],
      permissions: ['content:create', 'comment:create'],
      
      createdAt: '2023-03-20T00:00:00Z',
      updatedAt: '2024-01-10T08:30:00Z'
    }
  ],
  
  // 用户行为数据
  userBehavior: {
    loginHistory: [
      { date: '2024-01-15', time: '10:30:00', ip: '192.168.1.100', location: '北京', device: 'Chrome/Windows' },
      { date: '2024-01-14', time: '14:20:00', ip: '192.168.1.100', location: '北京', device: 'Chrome/Windows' },
      { date: '2024-01-13', time: '09:15:00', ip: '192.168.1.101', location: '北京', device: 'Safari/Mac' }
    ],
    
    activityTimeline: [
      { date: '2024-01-15', action: '发布帖子', target: 'Vue3最佳实践分享', count: 1 },
      { date: '2024-01-15', action: '发表评论', target: 'React Hooks详解', count: 3 },
      { date: '2024-01-14', action: '点赞内容', target: 'TypeScript入门教程', count: 5 },
      { date: '2024-01-14', action: '关注用户', target: '前端大神', count: 2 }
    ],
    
    contentStats: {
      postsByCategory: {
        '技术分享': 45,
        '经验交流': 32,
        '问答求助': 23,
        '资源分享': 18
      },
      monthlyActivity: [
        { month: '2023-08', posts: 12, comments: 45, likes: 89 },
        { month: '2023-09', posts: 15, comments: 56, likes: 123 },
        { month: '2023-10', posts: 18, comments: 67, likes: 145 },
        { month: '2023-11', posts: 22, comments: 78, likes: 178 },
        { month: '2023-12', posts: 25, comments: 89, likes: 201 },
        { month: '2024-01', posts: 8, comments: 34, likes: 67 }
      ]
    }
  },
  
  // 用户分析数据
  userAnalytics: {
    overview: {
      totalUsers: 15420,
      activeUsers: 12450,
      newUsersToday: 45,
      newUsersThisMonth: 1234,
      vipUsers: 2340,
      verifiedUsers: 8900
    },
    
    userGrowth: [
      { date: '2024-01-10', newUsers: 32, activeUsers: 11800 },
      { date: '2024-01-11', newUsers: 38, activeUsers: 11950 },
      { date: '2024-01-12', newUsers: 42, activeUsers: 12100 },
      { date: '2024-01-13', newUsers: 41, activeUsers: 12200 },
      { date: '2024-01-14', newUsers: 43, activeUsers: 12350 },
      { date: '2024-01-15', newUsers: 45, activeUsers: 12450 }
    ],
    
    userDistribution: {
      byStatus: {
        active: 12450,
        inactive: 2340,
        suspended: 450,
        locked: 180
      },
      byVipLevel: {
        'free': 13080,
        'vip1': 1560,
        'vip2': 540,
        'vip3': 240
      },
      byVerification: {
        emailVerified: 8900,
        phoneVerified: 5600,
        bothVerified: 4500,
        noneVerified: 1540
      }
    }
  }
}

// 模拟API函数
const mockApi = {
  async getUsers(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    let filteredUsers = [...mockUsersData.users]
    
    // 搜索过滤
    if (params.search) {
      const searchLower = params.search.toLowerCase()
      filteredUsers = filteredUsers.filter(user => 
        user.username.toLowerCase().includes(searchLower) ||
        user.email.toLowerCase().includes(searchLower) ||
        user.nickname.toLowerCase().includes(searchLower)
      )
    }
    
    // 状态过滤
    if (params.status) {
      filteredUsers = filteredUsers.filter(user => user.status === params.status)
    }
    
    // VIP过滤
    if (params.isVip !== undefined) {
      filteredUsers = filteredUsers.filter(user => user.isVip === params.isVip)
    }
    
    // 验证状态过滤
    if (params.verified) {
      if (params.verified === 'email') {
        filteredUsers = filteredUsers.filter(user => user.emailVerified)
      } else if (params.verified === 'phone') {
        filteredUsers = filteredUsers.filter(user => user.phoneVerified)
      } else if (params.verified === 'both') {
        filteredUsers = filteredUsers.filter(user => user.emailVerified && user.phoneVerified)
      }
    }
    
    // 注册时间范围
    if (params.registeredAfter) {
      const afterDate = new Date(params.registeredAfter)
      filteredUsers = filteredUsers.filter(user => new Date(user.createdAt) >= afterDate)
    }
    
    if (params.registeredBefore) {
      const beforeDate = new Date(params.registeredBefore)
      filteredUsers = filteredUsers.filter(user => new Date(user.createdAt) <= beforeDate)
    }
    
    // 最后活跃时间范围
    if (params.lastActiveAfter) {
      const afterDate = new Date(params.lastActiveAfter)
      filteredUsers = filteredUsers.filter(user => new Date(user.lastActiveAt) >= afterDate)
    }
    
    // 排序
    if (params.sortBy) {
      filteredUsers.sort((a, b) => {
        const aValue = a[params.sortBy]
        const bValue = b[params.sortBy]
        
        if (params.sortOrder === 'desc') {
          return bValue > aValue ? 1 : -1
        } else {
          return aValue > bValue ? 1 : -1
        }
      })
    }
    
    // 分页
    const page = params.page || 1
    const pageSize = params.pageSize || 20
    const total = filteredUsers.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const items = filteredUsers.slice(start, end)
    
    return {
      success: true,
      data: {
        items,
        total,
        page,
        pageSize,
        totalPages: Math.ceil(total / pageSize)
      }
    }
  },
  
  async getUserById(id) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    const user = mockUsersData.users.find(user => user.id === parseInt(id))
    if (!user) {
      throw new Error('用户不存在')
    }
    
    return {
      success: true,
      data: user
    }
  },
  
  async updateUser(id, userData) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    const userIndex = mockUsersData.users.findIndex(user => user.id === parseInt(id))
    if (userIndex === -1) {
      throw new Error('用户不存在')
    }
    
    // 检查邮箱冲突
    if (userData.email) {
      const existingUser = mockUsersData.users.find(user => 
        user.email === userData.email && user.id !== parseInt(id)
      )
      if (existingUser) {
        throw new Error('邮箱已被使用')
      }
    }
    
    // 检查用户名冲突
    if (userData.username) {
      const existingUser = mockUsersData.users.find(user => 
        user.username === userData.username && user.id !== parseInt(id)
      )
      if (existingUser) {
        throw new Error('用户名已被使用')
      }
    }
    
    const updatedUser = {
      ...mockUsersData.users[userIndex],
      ...userData,
      updatedAt: new Date().toISOString()
    }
    
    mockUsersData.users[userIndex] = updatedUser
    
    return {
      success: true,
      data: updatedUser
    }
  },
  
  async updateUserStatus(id, status) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const userIndex = mockUsersData.users.findIndex(user => user.id === parseInt(id))
    if (userIndex === -1) {
      throw new Error('用户不存在')
    }
    
    mockUsersData.users[userIndex].status = status
    mockUsersData.users[userIndex].updatedAt = new Date().toISOString()
    
    return {
      success: true,
      data: mockUsersData.users[userIndex]
    }
  },
  
  async deleteUser(id) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    const userIndex = mockUsersData.users.findIndex(user => user.id === parseInt(id))
    if (userIndex === -1) {
      throw new Error('用户不存在')
    }
    
    mockUsersData.users.splice(userIndex, 1)
    
    return {
      success: true,
      message: '用户删除成功'
    }
  },
  
  async getUserBehavior(id, params = {}) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    const user = mockUsersData.users.find(user => user.id === parseInt(id))
    if (!user) {
      throw new Error('用户不存在')
    }
    
    let behaviorData = { ...mockUsersData.userBehavior }
    
    // 时间范围过滤
    if (params.startDate && params.endDate) {
      const startDate = new Date(params.startDate)
      const endDate = new Date(params.endDate)
      
      behaviorData.loginHistory = behaviorData.loginHistory.filter(item => {
        const itemDate = new Date(item.date)
        return itemDate >= startDate && itemDate <= endDate
      })
      
      behaviorData.activityTimeline = behaviorData.activityTimeline.filter(item => {
        const itemDate = new Date(item.date)
        return itemDate >= startDate && itemDate <= endDate
      })
    }
    
    return {
      success: true,
      data: behaviorData
    }
  },
  
  async getUserAnalytics(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 700))
    
    let analyticsData = { ...mockUsersData.userAnalytics }
    
    // 时间范围过滤
    if (params.startDate && params.endDate) {
      const startDate = new Date(params.startDate)
      const endDate = new Date(params.endDate)
      
      analyticsData.userGrowth = analyticsData.userGrowth.filter(item => {
        const itemDate = new Date(item.date)
        return itemDate >= startDate && itemDate <= endDate
      })
    }
    
    return {
      success: true,
      data: analyticsData
    }
  },
  
  async exportUsers(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const result = await mockApi.getUsers(params)
    
    return {
      success: true,
      data: {
        users: result.data.items,
        exportTime: new Date().toISOString(),
        total: result.data.total
      }
    }
  },
  
  async batchUpdateStatus(ids, status) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const updatedUsers = []
    for (const id of ids) {
      const userIndex = mockUsersData.users.findIndex(user => user.id === parseInt(id))
      if (userIndex !== -1) {
        mockUsersData.users[userIndex].status = status
        mockUsersData.users[userIndex].updatedAt = new Date().toISOString()
        updatedUsers.push(mockUsersData.users[userIndex])
      }
    }
    
    return {
      success: true,
      data: {
        updatedCount: updatedUsers.length,
        updatedUsers
      }
    }
  },
  
  async batchDelete(ids) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    let deletedCount = 0
    const failedDeletes = []
    
    for (const id of ids) {
      const userIndex = mockUsersData.users.findIndex(user => user.id === parseInt(id))
      if (userIndex !== -1) {
        const user = mockUsersData.users[userIndex]
        // 模拟检查：管理员不能删除
        if (user.roles.includes('admin')) {
          failedDeletes.push({
            id: user.id,
            username: user.username,
            reason: '管理员用户不能删除'
          })
        } else {
          mockUsersData.users.splice(userIndex, 1)
          deletedCount++
        }
      }
    }
    
    return {
      success: true,
      data: {
        deletedCount,
        failedDeletes
      }
    }
  }
}

// 数据转换函数
export const transformUsersData = {
  /**
   * 转换用户列表数据
   */
  userList(data) {
    return {
      items: data.items || [],
      pagination: {
        total: data.total || 0,
        page: data.page || 1,
        pageSize: data.pageSize || 20,
        totalPages: data.totalPages || 0
      }
    }
  },
  
  /**
   * 转换单个用户数据
   */
  userDetail(data) {
    return {
      id: data.id,
      username: data.username,
      email: data.email,
      nickname: data.nickname,
      avatar: data.avatar,
      phone: data.phone,
      gender: data.gender,
      birthday: data.birthday,
      bio: data.bio,
      location: data.location,
      website: data.website,
      company: data.company,
      position: data.position,
      
      status: data.status,
      emailVerified: data.emailVerified,
      phoneVerified: data.phoneVerified,
      isVip: data.isVip,
      vipLevel: data.vipLevel,
      vipExpireAt: data.vipExpireAt,
      
      postsCount: data.postsCount || 0,
      commentsCount: data.commentsCount || 0,
      likesCount: data.likesCount || 0,
      followersCount: data.followersCount || 0,
      followingCount: data.followingCount || 0,
      
      lastLoginAt: data.lastLoginAt,
      lastActiveAt: data.lastActiveAt,
      loginCount: data.loginCount || 0,
      onlineDuration: data.onlineDuration || 0,
      
      loginAttempts: data.loginAttempts || 0,
      lockedUntil: data.lockedUntil,
      passwordUpdatedAt: data.passwordUpdatedAt,
      
      roles: data.roles || [],
      permissions: data.permissions || [],
      
      createdAt: data.createdAt,
      updatedAt: data.updatedAt
    }
  },
  
  /**
   * 转换用户行为数据
   */
  userBehavior(data) {
    return {
      loginHistory: data.loginHistory || [],
      activityTimeline: data.activityTimeline || [],
      contentStats: data.contentStats || {}
    }
  },
  
  /**
   * 转换用户分析数据
   */
  userAnalytics(data) {
    return {
      overview: data.overview || {},
      userGrowth: data.userGrowth || [],
      userDistribution: data.userDistribution || {}
    }
  }
}

// 真实API接口
export const usersApi = {
  /**
   * 获取用户列表
   * @param {Object} params - 查询参数
   * @returns {Promise} 用户列表
   */
  async getUsers(params = {}) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getUsers(params)
    }
    
    try {
      const response = await api.get('/users', { params })
      
      if (response.success) {
        return {
          success: true,
          data: transformUsersData.userList(response.data)
        }
      } else {
        throw new Error(response.message || '获取用户列表失败')
      }
    } catch (error) {
      console.error('获取用户列表API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getUsers(params)
      }
      throw error
    }
  },

  /**
   * 获取用户详情
   * @param {number} id - 用户ID
   * @returns {Promise} 用户详情
   */
  async getUserById(id) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getUserById(id)
    }
    
    try {
      const response = await api.get(`/users/${id}`)
      
      if (response.success) {
        return {
          success: true,
          data: transformUsersData.userDetail(response.data)
        }
      } else {
        throw new Error(response.message || '获取用户详情失败')
      }
    } catch (error) {
      console.error('获取用户详情API调用失败:', error)
      throw error
    }
  },

  /**
   * 更新用户信息
   * @param {number} id - 用户ID
   * @param {Object} userData - 用户数据
   * @returns {Promise} 更新结果
   */
  async updateUser(id, userData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.updateUser(id, userData)
    }
    
    try {
      const response = await api.put(`/users/${id}`, userData)
      
      if (response.success) {
        return {
          success: true,
          data: transformUsersData.userDetail(response.data)
        }
      } else {
        throw new Error(response.message || '更新用户信息失败')
      }
    } catch (error) {
      console.error('更新用户信息API调用失败:', error)
      throw error
    }
  },

  /**
   * 更新用户状态
   * @param {number} id - 用户ID
   * @param {string} status - 状态
   * @returns {Promise} 更新结果
   */
  async updateUserStatus(id, status) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.updateUserStatus(id, status)
    }
    
    try {
      const response = await api.post(`/users/${id}/status`, { status })
      return response
    } catch (error) {
      console.error('更新用户状态API调用失败:', error)
      throw error
    }
  },

  /**
   * 删除用户
   * @param {number} id - 用户ID
   * @returns {Promise} 删除结果
   */
  async deleteUser(id) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.deleteUser(id)
    }
    
    try {
      const response = await api.delete(`/users/${id}`)
      return response
    } catch (error) {
      console.error('删除用户API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取用户行为数据
   * @param {number} id - 用户ID
   * @param {Object} params - 查询参数
   * @returns {Promise} 用户行为数据
   */
  async getUserBehavior(id, params = {}) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getUserBehavior(id, params)
    }
    
    try {
      const response = await api.get(`/users/${id}/behavior`, { params })
      
      if (response.success) {
        return {
          success: true,
          data: transformUsersData.userBehavior(response.data)
        }
      } else {
        throw new Error(response.message || '获取用户行为数据失败')
      }
    } catch (error) {
      console.error('获取用户行为数据API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取用户分析数据
   * @param {Object} params - 查询参数
   * @returns {Promise} 用户分析数据
   */
  async getUserAnalytics(params = {}) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getUserAnalytics(params)
    }
    
    try {
      const response = await api.get('/users/analytics', { params })
      
      if (response.success) {
        return {
          success: true,
          data: transformUsersData.userAnalytics(response.data)
        }
      } else {
        throw new Error(response.message || '获取用户分析数据失败')
      }
    } catch (error) {
      console.error('获取用户分析数据API调用失败:', error)
      throw error
    }
  },

  /**
   * 导出用户数据
   * @param {Object} params - 查询参数
   * @returns {Promise} 导出结果
   */
  async exportUsers(params = {}) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.exportUsers(params)
    }
    
    try {
      const response = await api.get('/users/export', { params })
      return response
    } catch (error) {
      console.error('导出用户数据API调用失败:', error)
      throw error
    }
  },

  /**
   * 批量更新用户状态
   * @param {Array} ids - 用户ID数组
   * @param {string} status - 状态
   * @returns {Promise} 批量更新结果
   */
  async batchUpdateStatus(ids, status) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.batchUpdateStatus(ids, status)
    }
    
    try {
      const response = await api.post('/users/batch-status', { ids, status })
      return response
    } catch (error) {
      console.error('批量更新用户状态API调用失败:', error)
      throw error
    }
  },

  /**
   * 批量删除用户
   * @param {Array} ids - 用户ID数组
   * @returns {Promise} 批量删除结果
   */
  async batchDelete(ids) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.batchDelete(ids)
    }
    
    try {
      const response = await api.post('/users/batch-delete', { ids })
      return response
    } catch (error) {
      console.error('批量删除用户API调用失败:', error)
      throw error
    }
  }
}

// 为了向后兼容，导出单独的函数
export const getUsersList = usersApi.getUsers
export const getUserDetail = usersApi.getUserById
export const updateUserInfo = usersApi.updateUser
export const updateUserStatus = usersApi.updateUserStatus
export const deleteUserById = usersApi.deleteUser
export const getUserBehaviorData = usersApi.getUserBehavior
export const getUserAnalyticsData = usersApi.getUserAnalytics
export const exportUsersData = usersApi.exportUsers
export const batchUpdateUsersStatus = usersApi.batchUpdateStatus
export const batchDeleteUsers = usersApi.batchDelete

export default usersApi
