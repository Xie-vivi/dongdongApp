/**
 * 系统配置模块API接口
 * 处理网站配置、邮件设置、存储配置、安全设置等
 */

import api from './index'

// 模拟数据（作为fallback）
const mockSystemConfig = {
  // 网站基础配置
  site: {
    basic: {
      siteName: 'XYST社区',
      siteTitle: 'XYST - 技术分享与交流社区',
      siteDescription: '一个专注于技术分享、经验交流和资源互助的开发者社区',
      siteKeywords: '技术分享,经验交流,开发者社区,编程学习',
      siteLogo: '/logo.png',
      siteFavicon: '/favicon.ico',
      copyright: '© 2024 XYST Community. All rights reserved.',
      icpNumber: '京ICP备12345678号',
      domain: 'https://xyst.example.com'
    },
    
    theme: {
      primaryColor: '#409EFF',
      secondaryColor: '#67C23A',
      accentColor: '#E6A23C',
      backgroundColor: '#F5F7FA',
      textColor: '#303133',
      mode: 'light', // light, dark, auto
      customCSS: '',
      enableCustomTheme: false
    },
    
    features: {
      enableRegistration: true,
      enableEmailVerification: true,
      enableSmsVerification: false,
      enableSocialLogin: true,
      enableGuestComment: false,
      enableFileUpload: true,
      enableRealTimeNotification: true,
      maxFileSize: 10, // MB
      allowedFileTypes: ['jpg', 'jpeg', 'png', 'gif', 'pdf', 'doc', 'docx']
    }
  },
  
  // 邮件配置
  email: {
    smtp: {
      host: 'smtp.example.com',
      port: 587,
      secure: false,
      username: 'noreply@xyst.example.com',
      password: '********',
      fromName: 'XYST社区',
      fromEmail: 'noreply@xyst.example.com'
    },
    
    templates: {
      welcome: {
        enabled: true,
        subject: '欢迎加入XYST社区',
        template: 'welcome-email.html'
      },
      verification: {
        enabled: true,
        subject: '邮箱验证',
        template: 'email-verification.html'
      },
      passwordReset: {
        enabled: true,
        subject: '密码重置',
        template: 'password-reset.html'
      },
      notification: {
        enabled: true,
        subject: '系统通知',
        template: 'notification.html'
      }
    },
    
    settings: {
      rateLimit: 10, // 每小时最大发送数
      enableQueue: true,
      retryAttempts: 3,
      retryDelay: 300 // 秒
    }
  },
  
  // 存储配置
  storage: {
    local: {
      enabled: true,
      basePath: '/uploads',
      maxFileSize: 10,
      allowedTypes: ['jpg', 'jpeg', 'png', 'gif', 'pdf', 'doc', 'docx'],
      enableCompression: true,
      compressionQuality: 0.8
    },
    
    oss: {
      enabled: false,
      provider: 'aliyun', // aliyun, qcloud, aws
      region: 'oss-cn-beijing',
      bucket: 'xyst-storage',
      accessKeyId: '********',
      accessKeySecret: '********',
      endpoint: 'https://oss-cn-beijing.aliyuncs.com',
      domain: 'https://cdn.xyst.example.com'
    },
    
    cdn: {
      enabled: false,
      domain: 'https://cdn.xyst.example.com',
      enableHttps: true,
      enableCache: true,
      cacheExpire: 3600 // 秒
    }
  },
  
  // 安全配置
  security: {
    authentication: {
      sessionTimeout: 7200, // 秒
      maxLoginAttempts: 5,
      lockoutDuration: 900, // 秒
      enableTwoFactor: false,
      passwordMinLength: 8,
      passwordRequireUppercase: true,
      passwordRequireLowercase: true,
      passwordRequireNumbers: true,
      passwordRequireSymbols: false
    },
    
    protection: {
      enableCaptcha: true,
      captchaProvider: 'recaptcha', // recaptcha, hcaptcha, custom
      captchaSiteKey: '********',
      enableRateLimit: true,
      rateLimitWindow: 900, // 秒
      rateLimitMax: 100,
      enableIpWhitelist: false,
      ipWhitelist: [],
      enableIpBlacklist: true,
      ipBlacklist: []
    },
    
    audit: {
      enableLogging: true,
      logLevel: 'info', // debug, info, warn, error
      logRetention: 90, // 天
      enableAdminAudit: true,
      enableUserAudit: true,
      enableSystemAudit: true
    }
  }
}

// 模拟API函数
const mockApi = {
  async getConfig(section = 'all') {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    if (section === 'all') {
      return {
        success: true,
        data: mockSystemConfig
      }
    } else {
      return {
        success: true,
        data: mockSystemConfig[section] || {}
      }
    }
  },
  
  async updateConfig(section, config) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 验证配置格式
    if (!mockSystemConfig[section]) {
      throw new Error('配置节不存在')
    }
    
    // 模拟配置更新
    Object.assign(mockSystemConfig[section], config)
    
    return {
      success: true,
      data: mockSystemConfig[section],
      message: '配置更新成功'
    }
  },
  
  async testEmailConnection(emailConfig) {
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟邮件连接测试
    if (emailConfig.smtp.host === 'invalid.smtp.com') {
      throw new Error('SMTP服务器连接失败')
    }
    
    return {
      success: true,
      message: '邮件配置测试成功'
    }
  },
  
  async testStorageConnection(storageConfig) {
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 模拟存储连接测试
    if (storageConfig.oss.enabled && !storageConfig.oss.accessKeyId) {
      throw new Error('OSS配置不完整')
    }
    
    return {
      success: true,
      message: '存储配置测试成功'
    }
  },
  
  async resetConfig(section) {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟配置重置
    const defaultConfig = {
      site: mockSystemConfig.site,
      email: mockSystemConfig.email,
      storage: mockSystemConfig.storage,
      security: mockSystemConfig.security
    }
    
    if (defaultConfig[section]) {
      mockSystemConfig[section] = defaultConfig[section]
      return {
        success: true,
        data: defaultConfig[section],
        message: '配置已重置为默认值'
      }
    } else {
      throw new Error('配置节不存在')
    }
  },
  
  async exportConfig() {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    return {
      success: true,
      data: {
        config: mockSystemConfig,
        exportTime: new Date().toISOString(),
        version: '1.0.0'
      },
      message: '配置导出成功'
    }
  },
  
  async importConfig(configData) {
    await new Promise(resolve => setTimeout(resolve, 1200))
    
    // 模拟配置导入验证
    if (!configData.config) {
      throw new Error('配置文件格式错误')
    }
    
    // 模拟配置导入
    Object.assign(mockSystemConfig, configData.config)
    
    return {
      success: true,
      message: '配置导入成功'
    }
  }
}

// 数据转换函数
export const transformSystemConfig = {
  /**
   * 转换系统配置数据
   */
  systemConfig(data, section) {
    if (section === 'all') {
      return data
    } else {
      return data[section] || {}
    }
  },
  
  /**
   * 转换配置更新数据
   */
  configUpdate(data) {
    return {
      ...data,
      updatedAt: new Date().toISOString()
    }
  }
}

// 真实API接口
export const systemApi = {
  /**
   * 获取系统配置
   * @param {string} section - 配置节 (all, site, email, storage, security)
   * @returns {Promise} 系统配置
   */
  async getConfig(section = 'all') {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getConfig(section)
    }
    
    try {
      const response = await api.get('/admin/system/config', {
        params: { section }
      })
      
      if (response.success) {
        return {
          success: true,
          data: transformSystemConfig.systemConfig(response.data, section)
        }
      } else {
        throw new Error(response.message || '获取系统配置失败')
      }
    } catch (error) {
      console.error('获取系统配置API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getConfig(section)
      }
      throw error
    }
  },

  /**
   * 更新系统配置
   * @param {string} section - 配置节
   * @param {Object} config - 配置数据
   * @returns {Promise} 更新结果
   */
  async updateConfig(section, config) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.updateConfig(section, config)
    }
    
    try {
      const response = await api.put(`/admin/system/config/${section}`, config)
      
      if (response.success) {
        return {
          success: true,
          data: transformSystemConfig.configUpdate(response.data),
          message: response.message || '配置更新成功'
        }
      } else {
        throw new Error(response.message || '更新配置失败')
      }
    } catch (error) {
      console.error('更新系统配置API调用失败:', error)
      throw error
    }
  },

  /**
   * 测试邮件配置
   * @param {Object} emailConfig - 邮件配置
   * @returns {Promise} 测试结果
   */
  async testEmailConnection(emailConfig) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.testEmailConnection(emailConfig)
    }
    
    try {
      const response = await api.post('/admin/system/test/email', emailConfig)
      return response
    } catch (error) {
      console.error('测试邮件配置API调用失败:', error)
      throw error
    }
  },

  /**
   * 测试存储配置
   * @param {Object} storageConfig - 存储配置
   * @returns {Promise} 测试结果
   */
  async testStorageConnection(storageConfig) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.testStorageConnection(storageConfig)
    }
    
    try {
      const response = await api.post('/admin/system/test/storage', storageConfig)
      return response
    } catch (error) {
      console.error('测试存储配置API调用失败:', error)
      throw error
    }
  },

  /**
   * 重置配置
   * @param {string} section - 配置节
   * @returns {Promise} 重置结果
   */
  async resetConfig(section) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.resetConfig(section)
    }
    
    try {
      const response = await api.post(`/admin/system/config/${section}/reset`)
      return response
    } catch (error) {
      console.error('重置配置API调用失败:', error)
      throw error
    }
  },

  /**
   * 导出配置
   * @returns {Promise} 导出结果
   */
  async exportConfig() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.exportConfig()
    }
    
    try {
      const response = await api.get('/admin/system/config/export')
      return response
    } catch (error) {
      console.error('导出配置API调用失败:', error)
      throw error
    }
  },

  /**
   * 导入配置
   * @param {Object} configData - 配置数据
   * @returns {Promise} 导入结果
   */
  async importConfig(configData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.importConfig(configData)
    }
    
    try {
      const response = await api.post('/admin/system/config/import', configData)
      return response
    } catch (error) {
      console.error('导入配置API调用失败:', error)
      throw error
    }
  }
}

// 为了向后兼容，导出单独的函数
export const getSystemConfig = systemApi.getConfig
export const updateSystemConfig = systemApi.updateConfig
export const createSystemConfig = systemApi.updateConfig // 创建配置实际上就是更新配置
export const testEmailConfig = systemApi.testEmailConnection
export const testStorageConfig = systemApi.testStorageConnection
export const resetSystemConfig = systemApi.resetConfig
export const exportSystemConfig = systemApi.exportConfig
export const importSystemConfig = systemApi.importConfig

export default systemApi
