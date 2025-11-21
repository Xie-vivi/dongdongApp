/**
 * 认证授权模块API接口
 * 处理管理员登录、登出、token刷新等认证相关操作
 */

import api from './index'

// 模拟数据（作为fallback）
const mockAuthData = {
  login: {
    token: 'mock-jwt-token-' + Date.now(),
    refreshToken: 'mock-refresh-token-' + Date.now(),
    adminInfo: {
      id: 1,
      username: 'admin',
      nickname: '超级管理员',
      email: 'admin@example.com',
      avatar: 'https://via.placeholder.com/100',
      roles: ['super_admin'],
      permissions: ['*'],
      status: 'active',
      lastLoginTime: new Date().toISOString()
    },
    permissions: ['*']
  },
  
  profile: {
    adminInfo: {
      id: 1,
      username: 'admin',
      nickname: '超级管理员',
      email: 'admin@example.com',
      avatar: 'https://via.placeholder.com/100',
      roles: ['super_admin'],
      permissions: ['*'],
      status: 'active',
      lastLoginTime: new Date().toISOString(),
      createdAt: '2024-01-01T00:00:00Z'
    },
    permissions: ['*']
  }
}

// 模拟API函数
const mockApi = {
  async login(loginForm) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟登录验证
    if (loginForm.username === 'admin' && loginForm.password === 'admin123') {
      return {
        success: true,
        data: mockAuthData.login
      }
    } else {
      throw new Error('用户名或密码错误')
    }
  },
  
  async logout() {
    await new Promise(resolve => setTimeout(resolve, 500))
    return { success: true }
  },
  
  async refreshToken(refreshToken) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    if (refreshToken && refreshToken.startsWith('mock-refresh-token')) {
      return {
        success: true,
        data: {
          token: 'mock-new-jwt-token-' + Date.now(),
          refreshToken: 'mock-new-refresh-token-' + Date.now()
        }
      }
    } else {
      throw new Error('刷新token无效')
    }
  },
  
  async getProfile() {
    await new Promise(resolve => setTimeout(resolve, 500))
    return {
      success: true,
      data: mockAuthData.profile
    }
  }
}

// 真实API接口
export const authApi = {
  /**
   * 管理员登录
   * @param {Object} loginForm - 登录表单
   * @param {string} loginForm.username - 用户名
   * @param {string} loginForm.password - 密码
   * @param {boolean} loginForm.remember - 记住登录状态
   * @returns {Promise} 登录结果
   */
  async login(loginForm) {
    // 检查是否使用mock数据
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.login(loginForm)
    }
    
    try {
      const response = await api.post('/admin/auth/login', loginForm)
      return response
    } catch (error) {
      console.error('登录API调用失败:', error)
      // 如果真实API失败，fallback到mock数据（开发环境）
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.login(loginForm)
      }
      throw error
    }
  },

  /**
   * 管理员登出
   * @returns {Promise} 登出结果
   */
  async logout() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.logout()
    }
    
    try {
      const response = await api.post('/admin/auth/logout')
      return response
    } catch (error) {
      console.error('登出API调用失败:', error)
      // 登出失败不影响前端状态清理
      return { success: true }
    }
  },

  /**
   * 刷新访问令牌
   * @param {string} refreshToken - 刷新令牌
   * @returns {Promise} 刷新结果
   */
  async refreshToken(refreshToken) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.refreshToken(refreshToken)
    }
    
    try {
      const response = await api.post('/admin/auth/refresh', { refreshToken })
      return response
    } catch (error) {
      console.error('刷新token API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取当前管理员信息
   * @returns {Promise} 管理员信息
   */
  async getProfile() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getProfile()
    }
    
    try {
      const response = await api.get('/admin/auth/profile')
      return response
    } catch (error) {
      console.error('获取管理员信息API调用失败:', error)
      throw error
    }
  },

  /**
   * 修改密码
   * @param {Object} passwordData - 密码数据
   * @param {string} passwordData.oldPassword - 旧密码
   * @param {string} passwordData.newPassword - 新密码
   * @param {string} passwordData.confirmPassword - 确认密码
   * @returns {Promise} 修改结果
   */
  async changePassword(passwordData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      await new Promise(resolve => setTimeout(resolve, 1000))
      return { success: true }
    }
    
    try {
      const response = await api.post('/admin/auth/change-password', passwordData)
      return response
    } catch (error) {
      console.error('修改密码API调用失败:', error)
      throw error
    }
  },

  /**
   * 更新管理员个人信息
   * @param {Object} profileData - 个人信息数据
   * @returns {Promise} 更新结果
   */
  async updateProfile(profileData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      await new Promise(resolve => setTimeout(resolve, 1000))
      return { 
        success: true, 
        data: { ...mockAuthData.profile.adminInfo, ...profileData }
      }
    }
    
    try {
      const response = await api.put('/admin/auth/profile', profileData)
      return response
    } catch (error) {
      console.error('更新个人信息API调用失败:', error)
      throw error
    }
  }
}

// 为了向后兼容，导出单独的函数
export const adminLogin = authApi.login
export const adminLogout = authApi.logout
export const refreshAdminToken = authApi.refreshToken
export const getAdminProfile = authApi.getProfile
export const changeAdminPassword = authApi.changePassword
export const updateAdminProfile = authApi.updateProfile

export default authApi
