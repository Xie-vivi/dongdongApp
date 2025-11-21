/**
 * 管理后台API服务层入口
 * 统一管理所有API模块，提供环境切换和拦截器配置
 */

import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

// 创建axios实例
const api = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 添加认证token
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    
    // 添加请求时间戳
    config.metadata = { startTime: new Date() }
    
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 计算请求耗时
    const endTime = new Date()
    const duration = endTime - response.config.metadata.startTime
    
    // 记录API调用日志（开发环境）
    if (import.meta.env.DEV) {
      console.log(`API调用: ${response.config.method?.toUpperCase()} ${response.config.url} - ${duration}ms`)
    }
    
    return response.data
  },
  async (error) => {
    const authStore = useAuthStore()
    
    // 处理401未授权错误
    if (error.response?.status === 401) {
      try {
        // 尝试刷新token
        const refreshResult = await authStore.refreshToken()
        if (refreshResult) {
          // 重新发送原请求
          return api.request(error.config)
        }
      } catch (refreshError) {
        // 刷新失败，跳转到登录页
        authStore.logout()
        ElMessage.error('登录已过期，请重新登录')
        window.location.href = '/admin/login'
      }
    }
    
    // 处理403权限错误
    if (error.response?.status === 403) {
      ElMessage.error('权限不足，无法执行此操作')
    }
    
    // 处理404错误
    if (error.response?.status === 404) {
      ElMessage.error('请求的资源不存在')
    }
    
    // 处理500服务器错误
    if (error.response?.status === 500) {
      ElMessage.error('服务器内部错误，请稍后重试')
    }
    
    // 处理网络错误
    if (error.code === 'NETWORK_ERROR') {
      ElMessage.error('网络连接失败，请检查网络设置')
    }
    
    // 处理超时错误
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    }
    
    return Promise.reject(error)
  }
)

// API模块导出
export * from './auth'
export * from './users'
export * from './content'
export * from './system'
export * from './analytics'
export * from './permissions'
export * from './files'
export * from './logs'
export * from './tags'

// 默认导出axios实例
export default api
