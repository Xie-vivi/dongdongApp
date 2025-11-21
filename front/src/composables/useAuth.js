/**
 * 认证授权组合式函数
 * 统一管理认证状态和操作，与store集成
 */

import { ref, computed, watch } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { authApi } from '@/api/admin/auth'

// 全局认证状态
const globalAuthState = ref({
  isLoading: false,
  loginError: '',
  profileError: ''
})

export function useAuth() {
  const store = useStore()
  const router = useRouter()
  
  // 响应式状态
  const isLoading = computed(() => globalAuthState.value.isLoading)
  const loginError = computed(() => globalAuthState.value.loginError)
  const profileError = computed(() => globalAuthState.value.profileError)
  
  // 从store获取认证状态
  const isLoggedIn = computed(() => store.getters['admin/isLoggedIn'])
  const adminInfo = computed(() => store.getters['admin/adminInfo'])
  const adminName = computed(() => store.getters['admin/adminName'])
  const adminAvatar = computed(() => store.getters['admin/adminAvatar'])
  const permissions = computed(() => store.getters['admin/permissions'])
  const token = computed(() => store.getters['admin/token'])
  const refreshToken = computed(() => store.getters['admin/refreshToken'])
  
  /**
   * 管理员登录
   * @param {Object} loginForm - 登录表单
   * @param {string} loginForm.username - 用户名
   * @param {string} loginForm.password - 密码
   * @param {boolean} loginForm.remember - 记住登录状态
   * @param {string} redirectUrl - 登录成功后重定向的URL
   * @returns {Promise} 登录结果
   */
  const login = async (loginForm, redirectUrl = '/admin/dashboard') => {
    try {
      globalAuthState.value.isLoading = true
      globalAuthState.value.loginError = ''
      
      // 验证表单
      if (!loginForm.username || !loginForm.password) {
        throw new Error('请输入用户名和密码')
      }
      
      // 调用登录API
      const result = await authApi.login(loginForm)
      
      if (result.success) {
        const { token, refreshToken, adminInfo, permissions } = result.data
        
        // 更新store状态
        await store.dispatch('admin/login', {
          token,
          refreshToken,
          adminInfo,
          permissions
        })
        
        ElMessage.success('登录成功')
        
        // 跳转到目标页面
        if (redirectUrl) {
          await router.push(redirectUrl)
        }
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '登录失败')
      }
    } catch (error) {
      globalAuthState.value.loginError = error.message
      ElMessage.error(error.message || '登录失败')
      return { success: false, message: error.message }
    } finally {
      globalAuthState.value.isLoading = false
    }
  }
  
  /**
   * 管理员登出
   * @param {boolean} showMessage - 是否显示提示消息
   * @returns {Promise} 登出结果
   */
  const logout = async (showMessage = true) => {
    try {
      globalAuthState.value.isLoading = true
      
      // 调用登出API
      await authApi.logout()
      
      // 清除store状态
      await store.dispatch('admin/logout')
      
      if (showMessage) {
        ElMessage.success('已安全退出')
      }
      
      // 跳转到登录页
      await router.push('/admin/login')
      
      return { success: true }
    } catch (error) {
      console.error('登出失败:', error)
      // 即使API调用失败，也要清除本地状态
      await store.dispatch('admin/logout')
      await router.push('/admin/login')
      return { success: true }
    } finally {
      globalAuthState.value.isLoading = false
    }
  }
  
  /**
   * 刷新访问令牌
   * @returns {Promise} 刷新结果
   */
  const refreshAccessToken = async () => {
    try {
      const currentRefreshToken = refreshToken.value
      if (!currentRefreshToken) {
        throw new Error('没有有效的刷新令牌')
      }
      
      const result = await authApi.refreshToken(currentRefreshToken)
      
      if (result.success) {
        const { token: newToken, refreshToken: newRefreshToken } = result.data
        
        // 更新store中的token
        store.commit('admin/SET_TOKEN', newToken)
        store.commit('admin/SET_REFRESH_TOKEN', newRefreshToken)
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '令牌刷新失败')
      }
    } catch (error) {
      console.error('刷新令牌失败:', error)
      // 刷新失败，执行登出
      await logout(false)
      return { success: false, message: error.message }
    }
  }
  
  /**
   * 获取当前管理员信息
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 管理员信息
   */
  const getProfile = async (forceRefresh = false) => {
    try {
      globalAuthState.value.profileError = ''
      
      // 如果已有信息且不强制刷新，直接返回
      if (!forceRefresh && adminInfo.value && adminInfo.value.id) {
        return { success: true, data: { adminInfo: adminInfo.value, permissions: permissions.value } }
      }
      
      globalAuthState.value.isLoading = true
      
      const result = await authApi.getProfile()
      
      if (result.success) {
        const { adminInfo, permissions } = result.data
        
        // 更新store状态
        store.commit('admin/SET_ADMIN_INFO', adminInfo)
        store.commit('admin/SET_PERMISSIONS', permissions)
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '获取管理员信息失败')
      }
    } catch (error) {
      globalAuthState.value.profileError = error.message
      console.error('获取管理员信息失败:', error)
      return { success: false, message: error.message }
    } finally {
      globalAuthState.value.isLoading = false
    }
  }
  
  /**
   * 检查权限
   * @param {string|Array} permission - 权限标识
   * @returns {boolean} 是否有权限
   */
  const hasPermission = (permission) => {
    if (Array.isArray(permission)) {
      return permission.some(p => store.getters['admin/hasPermission'](p))
    }
    return store.getters['admin/hasPermission'](permission)
  }
  
  /**
   * 检查角色
   * @param {string|Array} role - 角色标识
   * @returns {boolean} 是否有角色
   */
  const hasRole = (role) => {
    if (Array.isArray(role)) {
      return role.some(r => store.getters['admin/hasRole'](r))
    }
    return store.getters['admin/hasRole'](role)
  }
  
  /**
   * 修改密码
   * @param {Object} passwordData - 密码数据
   * @returns {Promise} 修改结果
   */
  const changePassword = async (passwordData) => {
    try {
      globalAuthState.value.isLoading = true
      
      // 验证密码数据
      if (!passwordData.oldPassword || !passwordData.newPassword) {
        throw new Error('请输入完整的密码信息')
      }
      
      if (passwordData.newPassword !== passwordData.confirmPassword) {
        throw new Error('新密码和确认密码不一致')
      }
      
      if (passwordData.newPassword.length < 6) {
        throw new Error('新密码长度不能少于6位')
      }
      
      const result = await authApi.changePassword(passwordData)
      
      if (result.success) {
        ElMessage.success('密码修改成功')
        return { success: true }
      } else {
        throw new Error(result.message || '密码修改失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '密码修改失败')
      return { success: false, message: error.message }
    } finally {
      globalAuthState.value.isLoading = false
    }
  }
  
  /**
   * 更新个人信息
   * @param {Object} profileData - 个人信息数据
   * @returns {Promise} 更新结果
   */
  const updateProfile = async (profileData) => {
    try {
      globalAuthState.value.isLoading = true
      
      const result = await authApi.updateProfile(profileData)
      
      if (result.success) {
        // 更新store中的管理员信息
        store.commit('admin/SET_ADMIN_INFO', result.data)
        ElMessage.success('个人信息更新成功')
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '个人信息更新失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '个人信息更新失败')
      return { success: false, message: error.message }
    } finally {
      globalAuthState.value.isLoading = false
    }
  }
  
  /**
   * 检查登录状态并自动跳转
   * @param {string} redirectUrl - 未登录时重定向的URL
   * @returns {Promise} 检查结果
   */
  const checkAuthStatus = async (redirectUrl = '/admin/login') => {
    if (!isLoggedIn.value) {
      await router.push(redirectUrl)
      return { success: false, message: '未登录' }
    }
    
    // 验证token有效性
    try {
      const result = await getProfile()
      if (!result.success) {
        await logout(false)
        await router.push(redirectUrl)
        return { success: false, message: '登录已过期' }
      }
      return { success: true }
    } catch (error) {
      await logout(false)
      await router.push(redirectUrl)
      return { success: false, message: '登录验证失败' }
    }
  }
  
  /**
   * 清除错误状态
   */
  const clearErrors = () => {
    globalAuthState.value.loginError = ''
    globalAuthState.value.profileError = ''
  }
  
  // 监听token变化，自动刷新用户信息
  watch(token, (newToken) => {
    if (newToken && !adminInfo.value?.id) {
      getProfile()
    }
  }, { immediate: true })
  
  return {
    // 状态
    isLoading,
    loginError,
    profileError,
    isLoggedIn,
    adminInfo,
    adminName,
    adminAvatar,
    permissions,
    token,
    refreshToken,
    
    // 方法
    login,
    logout,
    refreshAccessToken,
    getProfile,
    hasPermission,
    hasRole,
    changePassword,
    updateProfile,
    checkAuthStatus,
    clearErrors
  }
}

// 导出全局状态（用于跨组件共享）
export { globalAuthState }

export default useAuth
