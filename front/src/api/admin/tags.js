/**
 * 标签管理模块API接口
 * 处理标签的增删改查、状态管理、批量操作等
 */

import api from './index'

// 模拟数据（作为fallback）
const mockTagsData = {
  tags: [
    {
      id: 1,
      name: '技术分享',
      description: '分享技术相关的内容和经验',
      category: 'content',
      color: '#409EFF',
      icon: 'Document',
      status: 'active',
      usageCount: 1234,
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-15T10:30:00Z',
      createdBy: 'admin'
    },
    {
      id: 2,
      name: '经验交流',
      description: '分享工作经验和心得',
      category: 'content',
      color: '#67C23A',
      icon: 'ChatDotRound',
      status: 'active',
      usageCount: 987,
      createdAt: '2024-01-02T00:00:00Z',
      updatedAt: '2024-01-10T15:20:00Z',
      createdBy: 'admin'
    },
    {
      id: 3,
      name: '问答求助',
      description: '提问和回答技术问题',
      category: 'content',
      color: '#E6A23C',
      icon: 'QuestionFilled',
      status: 'active',
      usageCount: 756,
      createdAt: '2024-01-03T00:00:00Z',
      updatedAt: '2024-01-08T09:15:00Z',
      createdBy: 'admin'
    },
    {
      id: 4,
      name: '资源分享',
      description: '分享有用的资源和工具',
      category: 'resource',
      color: '#F56C6C',
      icon: 'FolderOpened',
      status: 'active',
      usageCount: 543,
      createdAt: '2024-01-04T00:00:00Z',
      updatedAt: '2024-01-12T14:45:00Z',
      createdBy: 'admin'
    },
    {
      id: 5,
      name: '活动组织',
      description: '组织和管理各类活动',
      category: 'activity',
      color: '#909399',
      icon: 'Calendar',
      status: 'inactive',
      usageCount: 321,
      createdAt: '2024-01-05T00:00:00Z',
      updatedAt: '2024-01-07T11:30:00Z',
      createdBy: 'admin'
    }
  ],
  
  categories: [
    { value: 'content', label: '内容标签', description: '用于标记内容类型' },
    { value: 'resource', label: '资源标签', description: '用于标记资源类型' },
    { value: 'activity', label: '活动标签', description: '用于标记活动类型' },
    { value: 'user', label: '用户标签', description: '用于标记用户属性' },
    { value: 'system', label: '系统标签', description: '系统内部使用' }
  ]
}

// 模拟API函数
const mockApi = {
  async getTags(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    let filteredTags = [...mockTagsData.tags]
    
    // 搜索过滤
    if (params.search) {
      const searchLower = params.search.toLowerCase()
      filteredTags = filteredTags.filter(tag => 
        tag.name.toLowerCase().includes(searchLower) ||
        tag.description.toLowerCase().includes(searchLower)
      )
    }
    
    // 分类过滤
    if (params.category) {
      filteredTags = filteredTags.filter(tag => tag.category === params.category)
    }
    
    // 状态过滤
    if (params.status) {
      filteredTags = filteredTags.filter(tag => tag.status === params.status)
    }
    
    // 排序
    if (params.sortBy) {
      filteredTags.sort((a, b) => {
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
    const pageSize = params.pageSize || 10
    const total = filteredTags.length
    const start = (page - 1) * pageSize
    const end = start + pageSize
    const items = filteredTags.slice(start, end)
    
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
  
  async createTag(tagData) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 检查标签名是否已存在
    const existingTag = mockTagsData.tags.find(tag => tag.name === tagData.name)
    if (existingTag) {
      throw new Error('标签名称已存在')
    }
    
    const newTag = {
      id: Math.max(...mockTagsData.tags.map(t => t.id)) + 1,
      ...tagData,
      status: tagData.status || 'active',
      usageCount: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      createdBy: 'admin'
    }
    
    mockTagsData.tags.unshift(newTag)
    
    return {
      success: true,
      data: newTag
    }
  },
  
  async updateTag(id, tagData) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    const tagIndex = mockTagsData.tags.findIndex(tag => tag.id === parseInt(id))
    if (tagIndex === -1) {
      throw new Error('标签不存在')
    }
    
    // 检查标签名是否与其他标签冲突
    if (tagData.name) {
      const existingTag = mockTagsData.tags.find(tag => 
        tag.name === tagData.name && tag.id !== parseInt(id)
      )
      if (existingTag) {
        throw new Error('标签名称已存在')
      }
    }
    
    const updatedTag = {
      ...mockTagsData.tags[tagIndex],
      ...tagData,
      updatedAt: new Date().toISOString()
    }
    
    mockTagsData.tags[tagIndex] = updatedTag
    
    return {
      success: true,
      data: updatedTag
    }
  },
  
  async deleteTag(id) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    const tagIndex = mockTagsData.tags.findIndex(tag => tag.id === parseInt(id))
    if (tagIndex === -1) {
      throw new Error('标签不存在')
    }
    
    const tag = mockTagsData.tags[tagIndex]
    
    // 检查是否正在使用
    if (tag.usageCount > 0) {
      throw new Error(`该标签正在被 ${tag.usageCount} 个内容使用，无法删除`)
    }
    
    mockTagsData.tags.splice(tagIndex, 1)
    
    return {
      success: true,
      message: '标签删除成功'
    }
  },
  
  async getTagById(id) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const tag = mockTagsData.tags.find(tag => tag.id === parseInt(id))
    if (!tag) {
      throw new Error('标签不存在')
    }
    
    return {
      success: true,
      data: tag
    }
  },
  
  async getTagCategories() {
    await new Promise(resolve => setTimeout(resolve, 200))
    
    return {
      success: true,
      data: mockTagsData.categories
    }
  },
  
  async batchUpdateStatus(ids, status) {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const updatedTags = []
    for (const id of ids) {
      const tagIndex = mockTagsData.tags.findIndex(tag => tag.id === parseInt(id))
      if (tagIndex !== -1) {
        mockTagsData.tags[tagIndex].status = status
        mockTagsData.tags[tagIndex].updatedAt = new Date().toISOString()
        updatedTags.push(mockTagsData.tags[tagIndex])
      }
    }
    
    return {
      success: true,
      data: {
        updatedCount: updatedTags.length,
        updatedTags
      }
    }
  },
  
  async batchDelete(ids) {
    await new Promise(resolve => setTimeout(resolve, 600))
    
    let deletedCount = 0
    const failedDeletes = []
    
    for (const id of ids) {
      const tagIndex = mockTagsData.tags.findIndex(tag => tag.id === parseInt(id))
      if (tagIndex !== -1) {
        const tag = mockTagsData.tags[tagIndex]
        if (tag.usageCount > 0) {
          failedDeletes.push({
            id: tag.id,
            name: tag.name,
            reason: `正在被 ${tag.usageCount} 个内容使用`
          })
        } else {
          mockTagsData.tags.splice(tagIndex, 1)
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
  },
  
  async getTagUsageStats(id) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    const tag = mockTagsData.tags.find(tag => tag.id === parseInt(id))
    if (!tag) {
      throw new Error('标签不存在')
    }
    
    return {
      success: true,
      data: {
        tagId: tag.id,
        tagName: tag.name,
        totalUsage: tag.usageCount,
        usageByType: {
          posts: Math.floor(tag.usageCount * 0.6),
          activities: Math.floor(tag.usageCount * 0.3),
          comments: Math.floor(tag.usageCount * 0.1)
        },
        recentUsage: [
          { date: '2024-01-15', count: 12 },
          { date: '2024-01-14', count: 8 },
          { date: '2024-01-13', count: 15 },
          { date: '2024-01-12', count: 6 },
          { date: '2024-01-11', count: 9 }
        ]
      }
    }
  }
}

// 数据转换函数
export const transformTagsData = {
  /**
   * 转换标签列表数据
   */
  tagList(data) {
    return {
      items: data.items || [],
      pagination: {
        total: data.total || 0,
        page: data.page || 1,
        pageSize: data.pageSize || 10,
        totalPages: data.totalPages || 0
      }
    }
  },
  
  /**
   * 转换单个标签数据
   */
  tagDetail(data) {
    return {
      id: data.id,
      name: data.name,
      description: data.description,
      category: data.category,
      color: data.color,
      icon: data.icon,
      status: data.status,
      usageCount: data.usageCount || 0,
      createdAt: data.createdAt,
      updatedAt: data.updatedAt,
      createdBy: data.createdBy
    }
  },
  
  /**
   * 转换标签分类数据
   */
  categories(data) {
    return data.map(category => ({
      value: category.value,
      label: category.label,
      description: category.description
    }))
  }
}

// 真实API接口
export const tagsApi = {
  /**
   * 获取标签列表
   * @param {Object} params - 查询参数
   * @returns {Promise} 标签列表
   */
  async getTags(params = {}) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getTags(params)
    }
    
    try {
      const response = await api.get('/api/tags', { params })
      
      if (response.success) {
        return {
          success: true,
          data: transformTagsData.tagList(response.data)
        }
      } else {
        throw new Error(response.message || '获取标签列表失败')
      }
    } catch (error) {
      console.error('获取标签列表API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getTags(params)
      }
      throw error
    }
  },

  /**
   * 创建标签
   * @param {Object} tagData - 标签数据
   * @returns {Promise} 创建结果
   */
  async createTag(tagData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.createTag(tagData)
    }
    
    try {
      const response = await api.post('/api/tags', tagData)
      
      if (response.success) {
        return {
          success: true,
          data: transformTagsData.tagDetail(response.data)
        }
      } else {
        throw new Error(response.message || '创建标签失败')
      }
    } catch (error) {
      console.error('创建标签API调用失败:', error)
      throw error
    }
  },

  /**
   * 更新标签
   * @param {number} id - 标签ID
   * @param {Object} tagData - 标签数据
   * @returns {Promise} 更新结果
   */
  async updateTag(id, tagData) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.updateTag(id, tagData)
    }
    
    try {
      const response = await api.put(`/api/tags/${id}`, tagData)
      
      if (response.success) {
        return {
          success: true,
          data: transformTagsData.tagDetail(response.data)
        }
      } else {
        throw new Error(response.message || '更新标签失败')
      }
    } catch (error) {
      console.error('更新标签API调用失败:', error)
      throw error
    }
  },

  /**
   * 删除标签
   * @param {number} id - 标签ID
   * @returns {Promise} 删除结果
   */
  async deleteTag(id) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.deleteTag(id)
    }
    
    try {
      const response = await api.delete(`/api/tags/${id}`)
      return response
    } catch (error) {
      console.error('删除标签API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取标签详情
   * @param {number} id - 标签ID
   * @returns {Promise} 标签详情
   */
  async getTagById(id) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getTagById(id)
    }
    
    try {
      const response = await api.get(`/api/tags/${id}`)
      
      if (response.success) {
        return {
          success: true,
          data: transformTagsData.tagDetail(response.data)
        }
      } else {
        throw new Error(response.message || '获取标签详情失败')
      }
    } catch (error) {
      console.error('获取标签详情API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取标签分类
   * @returns {Promise} 标签分类列表
   */
  async getTagCategories() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getTagCategories()
    }
    
    try {
      const response = await api.get('/api/tags/categories')
      
      if (response.success) {
        return {
          success: true,
          data: transformTagsData.categories(response.data)
        }
      } else {
        throw new Error(response.message || '获取标签分类失败')
      }
    } catch (error) {
      console.error('获取标签分类API调用失败:', error)
      throw error
    }
  },

  /**
   * 批量更新标签状态
   * @param {Array} ids - 标签ID数组
   * @param {string} status - 状态
   * @returns {Promise} 批量更新结果
   */
  async batchUpdateStatus(ids, status) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.batchUpdateStatus(ids, status)
    }
    
    try {
      const response = await api.post('/api/tags/batch-status', { ids, status })
      return response
    } catch (error) {
      console.error('批量更新标签状态API调用失败:', error)
      throw error
    }
  },

  /**
   * 批量删除标签
   * @param {Array} ids - 标签ID数组
   * @returns {Promise} 批量删除结果
   */
  async batchDelete(ids) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.batchDelete(ids)
    }
    
    try {
      const response = await api.post('/api/tags/batch-delete', { ids })
      return response
    } catch (error) {
      console.error('批量删除标签API调用失败:', error)
      throw error
    }
  },

  /**
   * 获取标签使用统计
   * @param {number} id - 标签ID
   * @returns {Promise} 使用统计数据
   */
  async getTagUsageStats(id) {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getTagUsageStats(id)
    }
    
    try {
      const response = await api.get(`/api/tags/${id}/usage-stats`)
      return response
    } catch (error) {
      console.error('获取标签使用统计API调用失败:', error)
      throw error
    }
  }
}

// 为了向后兼容，导出单独的函数
export const getTagsList = tagsApi.getTags
export const createNewTag = tagsApi.createTag
export const updateTagInfo = tagsApi.updateTag
export const deleteTagById = tagsApi.deleteTag
export const getTagDetail = tagsApi.getTagById
export const getTagCategoriesList = tagsApi.getTagCategories
export const batchUpdateTagsStatus = tagsApi.batchUpdateStatus
export const batchDeleteTags = tagsApi.batchDelete
export const getTagUsageStatistics = tagsApi.getTagUsageStats

export default tagsApi
