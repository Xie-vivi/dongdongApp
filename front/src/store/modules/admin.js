import { adminLogin, adminLogout, getAdminProfile, refreshAdminToken } from '@/api/admin/auth'

const state = {
  token: localStorage.getItem('admin_token') || '',
  refreshToken: localStorage.getItem('admin_refresh_token') || '',
  adminInfo: JSON.parse(localStorage.getItem('admin_info') || '{}'),
  permissions: JSON.parse(localStorage.getItem('admin_permissions') || '[]'),
  isLoggedIn: !!localStorage.getItem('admin_token')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    localStorage.setItem('admin_token', token)
    state.isLoggedIn = !!token
  },
  
  SET_REFRESH_TOKEN(state, refreshToken) {
    state.refreshToken = refreshToken
    localStorage.setItem('admin_refresh_token', refreshToken)
  },
  
  SET_ADMIN_INFO(state, adminInfo) {
    state.adminInfo = adminInfo
    localStorage.setItem('admin_info', JSON.stringify(adminInfo))
  },
  
  SET_PERMISSIONS(state, permissions) {
    state.permissions = permissions
    localStorage.setItem('admin_permissions', JSON.stringify(permissions))
  },
  
  CLEAR_ADMIN_DATA(state) {
    state.token = ''
    state.refreshToken = ''
    state.adminInfo = {}
    state.permissions = []
    state.isLoggedIn = false
    
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_refresh_token')
    localStorage.removeItem('admin_info')
    localStorage.removeItem('admin_permissions')
  }
}

const actions = {
  // 管理员登录
  async login({ commit }, loginForm) {
    try {
      const response = await adminLogin(loginForm)
      const { token, refreshToken, adminInfo, permissions } = response.data
      
      commit('SET_TOKEN', token)
      commit('SET_REFRESH_TOKEN', refreshToken)
      commit('SET_ADMIN_INFO', adminInfo)
      commit('SET_PERMISSIONS', permissions)
      
      return { success: true, data: response.data }
    } catch (error) {
      return { success: false, message: error.message }
    }
  },
  
  // 管理员登出
  async logout({ commit }) {
    try {
      await adminLogout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      commit('CLEAR_ADMIN_DATA')
    }
  },
  
  // 获取管理员信息
  async getAdminInfo({ commit }) {
    try {
      const response = await getAdminProfile()
      const { adminInfo, permissions } = response.data
      
      commit('SET_ADMIN_INFO', adminInfo)
      commit('SET_PERMISSIONS', permissions)
      
      return { success: true, data: response.data }
    } catch (error) {
      commit('CLEAR_ADMIN_DATA')
      return { success: false, message: error.message }
    }
  },
  
  // 刷新访问令牌
  async refreshToken({ commit, state }) {
    try {
      const response = await refreshAdminToken(state.refreshToken)
      const { token, refreshToken } = response.data
      
      commit('SET_TOKEN', token)
      commit('SET_REFRESH_TOKEN', refreshToken)
      
      return { success: true, data: response.data }
    } catch (error) {
      commit('CLEAR_ADMIN_DATA')
      return { success: false, message: error.message }
    }
  },
  
  // 检查权限
  hasPermission({ state }, permission) {
    return state.permissions.includes(permission) || state.permissions.includes('*')
  },
  
  // 检查角色
  hasRole({ state }, role) {
    return state.adminInfo.roles?.includes(role) || false
  }
}

const getters = {
  isLoggedIn: state => state.isLoggedIn,
  adminInfo: state => state.adminInfo,
  adminName: state => state.adminInfo.username || state.adminInfo.nickname,
  adminAvatar: state => state.adminInfo.avatar,
  permissions: state => state.permissions,
  token: state => state.token,
  refreshToken: state => state.refreshToken
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
