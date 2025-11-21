/**
 * 内容审核模块API接口
 * 统一管理帖子、活动、评论的审核相关接口
 */

import request from '@/utils/request'

// 内容类型常量
export const CONTENT_TYPES = {
  POST: 'post',
  ACTIVITY: 'activity', 
  COMMENT: 'comment'
}

// 审核状态常量
export const AUDIT_STATUS = {
  PENDING: 'pending',    // 待审核
  APPROVED: 'approved',  // 已通过
  REJECTED: 'rejected',  // 已拒绝
  DELETED: 'deleted'     // 已删除
}

/**
 * 内容审核API对象
 */
export const contentApi = {
  /**
   * 获取内容列表
   * @param {string} contentType - 内容类型 (post/activity/comment)
   * @param {Object} params - 查询参数
   * @returns {Promise} 获取结果
   */
  getContentList(contentType, params = {}) {
    let url
    switch (contentType) {
      case CONTENT_TYPES.POST:
        url = '/posts/admin/list'
        break
      case CONTENT_TYPES.ACTIVITY:
        url = '/activities/admin/list'
        break
      case CONTENT_TYPES.COMMENT:
        url = '/comments/admin/list'
        break
      default:
        url = '/posts/admin/list'
    }
    
    return request({
      url,
      method: 'get',
      params
    })
  },

  /**
   * 获取内容详情
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @returns {Promise} 获取结果
   */
  getContentDetail(contentType, id) {
    return request({
      url: `/admin/content/${contentType}/${id}`,
      method: 'get'
    })
  },

  /**
   * 审核通过内容
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @param {Object} data - 审核数据
   * @returns {Promise} 审核结果
   */
  approveContent(contentType, id, data = {}) {
    let url
    switch (contentType) {
      case CONTENT_TYPES.POST:
        url = `/posts/admin/${id}/review`
        break
      case CONTENT_TYPES.ACTIVITY:
        url = `/activities/admin/${id}/review`
        break
      case CONTENT_TYPES.COMMENT:
        url = `/comments/admin/${id}/review`
        break
      default:
        url = `/posts/admin/${id}/review`
    }
    
    return request({
      url,
      method: 'post',
      data: {
        action: 'approve',
        ...data
      }
    })
  },

  /**
   * 审核拒绝内容
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @param {Object} data - 拒绝数据（包含拒绝原因）
   * @returns {Promise} 审核结果
   */
  rejectContent(contentType, id, data = {}) {
    let url
    switch (contentType) {
      case CONTENT_TYPES.POST:
        url = `/posts/admin/${id}/review`
        break
      case CONTENT_TYPES.ACTIVITY:
        url = `/activities/admin/${id}/review`
        break
      case CONTENT_TYPES.COMMENT:
        url = `/comments/admin/${id}/review`
        break
      default:
        url = `/posts/admin/${id}/review`
    }
    
    return request({
      url,
      method: 'post',
      data: {
        action: 'reject',
        ...data
      }
    })
  },

  /**
   * 删除内容
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @param {Object} data - 删除数据
   * @returns {Promise} 删除结果
   */
  deleteContent(contentType, id, data = {}) {
    let url
    switch (contentType) {
      case CONTENT_TYPES.POST:
        url = `/posts/admin/${id}`
        break
      case CONTENT_TYPES.ACTIVITY:
        url = `/activities/admin/${id}`
        break
      case CONTENT_TYPES.COMMENT:
        url = `/comments/admin/${id}`
        break
      default:
        url = `/posts/admin/${id}`
    }
    
    return request({
      url,
      method: 'delete',
      data
    })
  },

  /**
   * 恢复内容
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @returns {Promise} 恢复结果
   */
  restoreContent(contentType, id) {
    return request({
      url: `/admin/content/${contentType}/${id}/restore`,
      method: 'post'
    })
  },

  /**
   * 批量审核通过
   * @param {string} contentType - 内容类型
   * @param {Array} ids - 内容ID数组
   * @param {Object} data - 批量操作数据
   * @returns {Promise} 批量审核结果
   */
  batchApprove(contentType, ids, data = {}) {
    // 后台暂不支持批量操作，使用Mock适配层
    if (process.env.NODE_ENV === 'development') {
      return mockContentApi.batchApprove(contentType, ids, data)
    }
    
    // 生产环境尝试调用API，失败时降级到单个操作
    return this._batchOperationFallback(contentType, ids, 'approve', data)
  },

  /**
   * 批量审核拒绝
   * @param {string} contentType - 内容类型
   * @param {Array} ids - 内容ID数组
   * @param {Object} data - 批量拒绝数据
   * @returns {Promise} 批量审核结果
   */
  batchReject(contentType, ids, data = {}) {
    // 后台暂不支持批量操作，使用Mock适配层
    if (process.env.NODE_ENV === 'development') {
      return mockContentApi.batchReject(contentType, ids, data)
    }
    
    // 生产环境尝试调用API，失败时降级到单个操作
    return this._batchOperationFallback(contentType, ids, 'reject', data)
  },

  /**
   * 批量删除
   * @param {string} contentType - 内容类型
   * @param {Array} ids - 内容ID数组
   * @param {Object} data - 批量删除数据
   * @returns {Promise} 批量删除结果
   */
  batchDelete(contentType, ids, data = {}) {
    // 后台暂不支持批量操作，使用Mock适配层
    if (process.env.NODE_ENV === 'development') {
      return mockContentApi.batchDelete(contentType, ids, data)
    }
    
    // 生产环境尝试调用API，失败时降级到单个操作
    return this._batchOperationFallback(contentType, ids, 'delete', data)
  },

  /**
   * 批量操作fallback方法 - 将批量操作拆分为单个操作
   * @param {string} contentType - 内容类型
   * @param {Array} ids - 内容ID数组
   * @param {string} action - 操作类型 (approve/reject/delete)
   * @param {Object} data - 操作数据
   * @returns {Promise} 批量操作结果
   */
  async _batchOperationFallback(contentType, ids, action, data = {}) {
    const results = []
    let successCount = 0
    let failCount = 0
    
    for (const id of ids) {
      try {
        let result
        switch (action) {
          case 'approve':
            result = await this.approveContent(contentType, id, data)
            break
          case 'reject':
            result = await this.rejectContent(contentType, id, data)
            break
          case 'delete':
            result = await this.deleteContent(contentType, id, data)
            break
          default:
            throw new Error(`不支持的操作类型: ${action}`)
        }
        
        if (result.success) {
          successCount++
          results.push({ id, success: true })
        } else {
          failCount++
          results.push({ id, success: false, error: result.message })
        }
      } catch (error) {
        failCount++
        results.push({ id, success: false, error: error.message })
      }
    }
    
    return {
      success: successCount > 0,
      data: {
        totalCount: ids.length,
        successCount,
        failCount,
        results
      }
    }
  },

  /**
   * 获取审核统计
   * @param {string} contentType - 内容类型（可选）
   * @param {Object} params - 查询参数
   * @returns {Promise} 统计结果
   */
  getAuditStatistics(contentType = null, params = {}) {
    const url = contentType 
      ? `/admin/content/${contentType}/statistics`
      : '/admin/content/statistics'
    
    return request({
      url,
      method: 'get',
      params
    })
  },

  /**
   * 获取审核历史
   * @param {string} contentType - 内容类型
   * @param {number} id - 内容ID
   * @param {Object} params - 查询参数
   * @returns {Promise} 审核历史结果
   */
  getAuditHistory(contentType, id, params = {}) {
    return request({
      url: `/admin/content/${contentType}/${id}/history`,
      method: 'get',
      params
    })
  },

  /**
   * 导出审核数据
   * @param {string} contentType - 内容类型
   * @param {Object} params - 导出参数
   * @returns {Promise} 导出结果
   */
  exportAuditData(contentType, params = {}) {
    return request({
      url: `/admin/content/${contentType}/export`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  /**
   * 获取审核配置
   * @param {string} contentType - 内容类型（可选）
   * @returns {Promise} 配置结果
   */
  getAuditConfig(contentType = null) {
    const url = contentType 
      ? `/admin/content/${contentType}/config`
      : '/admin/content/config'
    
    return request({
      url,
      method: 'get'
    })
  },

  /**
   * 更新审核配置
   * @param {string} contentType - 内容类型
   * @param {Object} config - 配置数据
   * @returns {Promise} 更新结果
   */
  updateAuditConfig(contentType, config) {
    return request({
      url: `/admin/content/${contentType}/config`,
      method: 'put',
      data: config
    })
  }
}

// Mock数据生成函数
export const generateMockContentData = (contentType, count = 20) => {
  const mockData = []
  
  for (let i = 1; i <= count; i++) {
    let item = {
      id: i,
      title: `${contentType === 'post' ? '帖子' : contentType === 'activity' ? '活动' : '评论'}标题 ${i}`,
      content: `这是${contentType === 'post' ? '帖子' : contentType === 'activity' ? '活动' : '评论'}的内容描述 ${i}`,
      author: {
        id: Math.floor(Math.random() * 1000) + 1,
        username: `用户${Math.floor(Math.random() * 1000) + 1}`,
        avatar: `https://picsum.photos/seed/user${i}/40/40.jpg`
      },
      status: Math.random() > 0.7 ? AUDIT_STATUS.PENDING : 
              Math.random() > 0.5 ? AUDIT_STATUS.APPROVED : 
              Math.random() > 0.3 ? AUDIT_STATUS.REJECTED : AUDIT_STATUS.DELETED,
      createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      updatedAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
      viewCount: Math.floor(Math.random() * 10000),
      likeCount: Math.floor(Math.random() * 1000),
      commentCount: Math.floor(Math.random() * 100),
      reportCount: Math.floor(Math.random() * 10),
      tags: [`${contentType}标签${Math.floor(Math.random() * 5) + 1}`, `${contentType}标签${Math.floor(Math.random() * 5) + 6}`]
    }

    // 根据内容类型添加特定字段
    if (contentType === 'post') {
      item.category = `分类${Math.floor(Math.random() * 10) + 1}`
      item.isTop = Math.random() > 0.9
      item.isEssence = Math.random() > 0.8
    } else if (contentType === 'activity') {
      item.startTime = new Date(Date.now() + Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString()
      item.endTime = new Date(Date.now() + Math.random() * 60 * 24 * 60 * 60 * 1000).toISOString()
      item.location = `地点${Math.floor(Math.random() * 10) + 1}`
      item.maxParticipants = Math.floor(Math.random() * 100) + 10
      item.currentParticipants = Math.floor(Math.random() * 100)
    } else if (contentType === 'comment') {
      item.postId = Math.floor(Math.random() * 100) + 1
      item.postTitle = `帖子标题${Math.floor(Math.random() * 100) + 1}`
      item.parentId = Math.random() > 0.7 ? Math.floor(Math.random() * 50) + 1 : null
    }

    mockData.push(item)
  }

  return mockData
}

// Mock API函数（用于开发环境）
export const mockContentApi = {
  /**
   * Mock获取内容列表
   */
  async getContentList(contentType, params = {}) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const mockData = generateMockContentData(contentType, 50)
    
    // 过滤和分页处理
    let filteredData = mockData
    
    if (params.status) {
      filteredData = filteredData.filter(item => item.status === params.status)
    }
    
    if (params.search) {
      filteredData = filteredData.filter(item => 
        item.title.includes(params.search) || item.content.includes(params.search)
      )
    }
    
    const page = params.page || 1
    const pageSize = params.pageSize || 20
    const total = filteredData.length
    const totalPages = Math.ceil(total / pageSize)
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const items = filteredData.slice(start, end)
    
    return {
      success: true,
      data: {
        items,
        pagination: {
          total,
          page,
          pageSize,
          totalPages
        }
      }
    }
  },

  /**
   * Mock获取内容详情
   */
  async getContentDetail(contentType, id) {
    await new Promise(resolve => setTimeout(resolve, 200))
    
    const mockData = generateMockContentData(contentType, 1)
    const item = mockData[0]
    item.id = id
    
    return {
      success: true,
      data: item
    }
  },

  /**
   * Mock审核拒绝
   */
  async rejectContent(contentType, id, data = {}) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    return {
      success: true,
      data: {
        id,
        status: AUDIT_STATUS.REJECTED,
        auditTime: new Date().toISOString(),
        auditor: '当前管理员',
        reason: data.reason || '违规内容',
        ...data
      }
    }
  },

  /**
   * Mock删除内容
   */
  async deleteContent(contentType, id, data = {}) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    return {
      success: true,
      data: {
        id,
        status: AUDIT_STATUS.DELETED,
        deletedTime: new Date().toISOString(),
        deletedBy: '当前管理员',
        ...data
      }
    }
  },

  /**
   * Mock批量审核通过
   */
  async batchApprove(contentType, ids, data = {}) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const approvedCount = ids.length
    const failedCount = 0
    
    return {
      success: true,
      data: {
        totalCount: ids.length,
        approvedCount,
        failedCount,
        auditTime: new Date().toISOString(),
        auditor: '当前管理员',
        ...data
      }
    }
  },

  /**
   * Mock批量审核拒绝
   */
  async batchReject(contentType, ids, data = {}) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const rejectedCount = ids.length
    const failedCount = 0
    
    return {
      success: true,
      data: {
        totalCount: ids.length,
        rejectedCount,
        failedCount,
        auditTime: new Date().toISOString(),
        auditor: '当前管理员',
        reason: data.reason || '批量违规内容',
        ...data
      }
    }
  },

  /**
   * Mock批量删除
   */
  async batchDelete(contentType, ids, data = {}) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    const deletedCount = ids.length
    const failedCount = 0
    
    return {
      success: true,
      data: {
        totalCount: ids.length,
        deletedCount,
        failedCount,
        deletedTime: new Date().toISOString(),
        deletedBy: '当前管理员',
        ...data
      }
    }
  },

  /**
   * Mock获取审核统计
   */
  async getAuditStatistics(contentType = null, params = {}) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const stats = {
      totalCount: 1250,
      pendingCount: 23,
      approvedCount: 1180,
      rejectedCount: 35,
      deletedCount: 12,
      todayAuditCount: 45,
      weeklyAuditCount: 280,
      monthlyAuditCount: 1200
    }
    
    if (contentType) {
      // 根据内容类型返回不同的统计数据
      return {
        success: true,
        data: {
          contentType,
          ...stats,
          totalCount: Math.floor(stats.totalCount / 3),
          pendingCount: Math.floor(stats.pendingCount / 3),
          approvedCount: Math.floor(stats.approvedCount / 3),
          rejectedCount: Math.floor(stats.rejectedCount / 3)
        }
      }
    }
    
    return {
      success: true,
      data: stats
    }
  },

  /**
   * Mock获取审核历史
   */
  async getAuditHistory(contentType, id, params = {}) {
    await new Promise(resolve => setTimeout(resolve, 200))
    
    const history = [
      {
        id: 1,
        action: 'submit',
        operator: '用户' + id,
        time: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
        reason: '提交审核'
      },
      {
        id: 2,
        action: 'approve',
        operator: '管理员A',
        time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
        reason: '内容符合规范'
      }
    ]
    
    return {
      success: true,
      data: {
        items: history,
        pagination: {
          total: history.length,
          page: params.page || 1,
          pageSize: params.pageSize || 20,
          totalPages: 1
        }
      }
    }
  }
}

// 根据环境选择API实现
const isDevelopment = process.env.NODE_ENV === 'development' && process.env.VUE_APP_USE_MOCK_API === 'true'

export const api = isDevelopment ? mockContentApi : contentApi

export default api
