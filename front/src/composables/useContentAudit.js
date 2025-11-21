import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api, CONTENT_TYPES, AUDIT_STATUS } from '@/api/admin/content'

export function useContentAudit(contentType = 'posts') {
  // 响应式数据
  const loading = ref(false)
  const contentList = ref([])
  const total = ref(0)
  const currentContent = ref(null)
  
  // 搜索参数
  const searchParams = reactive({
    page: 1,
    size: 20,
    status: 'pending', // 默认显示待审核
    keyword: '',
    startDate: '',
    endDate: '',
    userId: ''
  })
  
  // 表格选择
  const selectedContent = ref([])
  
  // 内容类型配置
  const contentTypeConfig = {
    posts: {
      name: '帖子',
      contentType: CONTENT_TYPES.POST,
      api: {
        getList: (params) => api.getContentList(CONTENT_TYPES.POST, params),
        approve: (id, data) => api.approveContent(CONTENT_TYPES.POST, id, data),
        reject: (id, data) => api.rejectContent(CONTENT_TYPES.POST, id, data),
        batchApprove: (ids, data) => api.batchApprove(CONTENT_TYPES.POST, ids, data),
        batchReject: (ids, data) => api.batchReject(CONTENT_TYPES.POST, ids, data)
      },
      fields: [
        {
          prop: 'keyword',
          label: '关键词',
          type: 'input',
          placeholder: '请输入标题或内容关键词'
        },
        {
          prop: 'status',
          label: '状态',
          type: 'select',
          options: [
            { label: '全部', value: '' },
            { label: '待审核', value: 'pending' },
            { label: '已通过', value: 'approved' },
            { label: '已拒绝', value: 'rejected' }
          ]
        },
        {
          prop: 'userId',
          label: '用户ID',
          type: 'input',
          placeholder: '请输入用户ID'
        },
        {
          prop: 'dateRange',
          label: '发布时间',
          type: 'daterange',
          valueFormat: 'YYYY-MM-DD'
        }
      ],
      columns: [
        {
          prop: 'id',
          label: 'ID',
          width: 80,
          sortable: true
        },
        {
          prop: 'title',
          label: '标题',
          minWidth: 200,
          type: 'link'
        },
        {
          prop: 'content',
          label: '内容',
          minWidth: 300,
          showOverflowTooltip: true,
          formatter: (row) => {
            return row.content ? row.content.substring(0, 100) + '...' : '-'
          }
        },
        {
          prop: 'author',
          label: '作者',
          width: 120,
          formatter: (row) => row.user?.username || '-'
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          tag: {
            type: 'content',
            textMap: {
              pending: '待审核',
              approved: '已通过',
              rejected: '已拒绝'
            },
            typeMap: {
              pending: 'warning',
              approved: 'success',
              rejected: 'danger'
            }
          }
        },
        {
          prop: 'likesCount',
          label: '点赞数',
          width: 100,
          sortable: true
        },
        {
          prop: 'commentsCount',
          label: '评论数',
          width: 100,
          sortable: true
        },
        {
          prop: 'createdAt',
          label: '发布时间',
          width: 160,
          sortable: true,
          formatter: (row) => {
            return new Date(row.createdAt).toLocaleString()
          }
        }
      ]
    },
    activities: {
      name: '活动',
      contentType: CONTENT_TYPES.ACTIVITY,
      api: {
        getList: (params) => api.getContentList(CONTENT_TYPES.ACTIVITY, params),
        approve: (id, data) => api.approveContent(CONTENT_TYPES.ACTIVITY, id, data),
        reject: (id, data) => api.rejectContent(CONTENT_TYPES.ACTIVITY, id, data),
        batchApprove: (ids, data) => api.batchApprove(CONTENT_TYPES.ACTIVITY, ids, data),
        batchReject: (ids, data) => api.batchReject(CONTENT_TYPES.ACTIVITY, ids, data)
      },
      fields: [
        {
          prop: 'keyword',
          label: '关键词',
          type: 'input',
          placeholder: '请输入活动标题或描述关键词'
        },
        {
          prop: 'status',
          label: '状态',
          type: 'select',
          options: [
            { label: '全部', value: '' },
            { label: '草稿', value: 'draft' },
            { label: '待审核', value: 'pending' },
            { label: '已发布', value: 'published' },
            { label: '进行中', value: 'ongoing' },
            { label: '已完成', value: 'completed' },
            { label: '已取消', value: 'cancelled' }
          ]
        },
        {
          prop: 'userId',
          label: '组织者ID',
          type: 'input',
          placeholder: '请输入组织者ID'
        },
        {
          prop: 'dateRange',
          label: '活动时间',
          type: 'daterange',
          valueFormat: 'YYYY-MM-DD'
        }
      ],
      columns: [
        {
          prop: 'id',
          label: 'ID',
          width: 80,
          sortable: true
        },
        {
          prop: 'title',
          label: '活动标题',
          minWidth: 200,
          type: 'link'
        },
        {
          prop: 'description',
          label: '活动描述',
          minWidth: 300,
          showOverflowTooltip: true,
          formatter: (row) => {
            return row.description ? row.description.substring(0, 100) + '...' : '-'
          }
        },
        {
          prop: 'organizer',
          label: '组织者',
          width: 120,
          formatter: (row) => row.user?.username || '-'
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          tag: {
            type: 'activity',
            textMap: {
              draft: '草稿',
              pending: '待审核',
              published: '已发布',
              ongoing: '进行中',
              completed: '已完成',
              cancelled: '已取消'
            },
            typeMap: {
              draft: 'info',
              pending: 'warning',
              published: 'success',
              ongoing: 'primary',
              completed: 'success',
              cancelled: 'danger'
            }
          }
        },
        {
          prop: 'participantsCount',
          label: '参与人数',
          width: 100,
          sortable: true
        },
        {
          prop: 'activityDate',
          label: '活动时间',
          width: 160,
          sortable: true,
          formatter: (row) => {
            return row.activityDate ? new Date(row.activityDate).toLocaleString() : '-'
          }
        },
        {
          prop: 'createdAt',
          label: '创建时间',
          width: 160,
          sortable: true,
          formatter: (row) => {
            return new Date(row.createdAt).toLocaleString()
          }
        }
      ]
    },
    comments: {
      name: '评论',
      contentType: CONTENT_TYPES.COMMENT,
      api: {
        getList: (params) => api.getContentList(CONTENT_TYPES.COMMENT, params),
        approve: (id, data) => api.approveContent(CONTENT_TYPES.COMMENT, id, data),
        reject: (id, data) => api.rejectContent(CONTENT_TYPES.COMMENT, id, data),
        batchApprove: (ids, data) => api.batchApprove(CONTENT_TYPES.COMMENT, ids, data),
        batchReject: (ids, data) => api.batchReject(CONTENT_TYPES.COMMENT, ids, data)
      },
      fields: [
        {
          prop: 'keyword',
          label: '关键词',
          type: 'input',
          placeholder: '请输入评论内容关键词'
        },
        {
          prop: 'status',
          label: '状态',
          type: 'select',
          options: [
            { label: '全部', value: '' },
            { label: '待审核', value: 'pending' },
            { label: '已通过', value: 'approved' },
            { label: '已拒绝', value: 'rejected' }
          ]
        },
        {
          prop: 'userId',
          label: '用户ID',
          type: 'input',
          placeholder: '请输入用户ID'
        },
        {
          prop: 'dateRange',
          label: '评论时间',
          type: 'daterange',
          valueFormat: 'YYYY-MM-DD'
        }
      ],
      columns: [
        {
          prop: 'id',
          label: 'ID',
          width: 80,
          sortable: true
        },
        {
          prop: 'content',
          label: '评论内容',
          minWidth: 300,
          showOverflowTooltip: true,
          formatter: (row) => {
            return row.content ? row.content.substring(0, 100) + '...' : '-'
          }
        },
        {
          prop: 'author',
          label: '评论者',
          width: 120,
          formatter: (row) => row.user?.username || '-'
        },
        {
          prop: 'contentType',
          label: '内容类型',
          width: 100,
          formatter: (row) => {
            const typeMap = {
              post: '帖子',
              activity: '活动'
            }
            return typeMap[row.contentType] || '-'
          }
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          tag: {
            type: 'content',
            textMap: {
              pending: '待审核',
              approved: '已通过',
              rejected: '已拒绝'
            },
            typeMap: {
              pending: 'warning',
              approved: 'success',
              rejected: 'danger'
            }
          }
        },
        {
          prop: 'likesCount',
          label: '点赞数',
          width: 100,
          sortable: true
        },
        {
          prop: 'createdAt',
          label: '评论时间',
          width: 160,
          sortable: true,
          formatter: (row) => {
            return new Date(row.createdAt).toLocaleString()
          }
        }
      ]
    }
  }
  
  // 获取当前内容类型配置
  const currentConfig = computed(() => contentTypeConfig[contentType] || contentTypeConfig.posts)
  
  // 获取内容列表
  const fetchContentList = async (params = {}) => {
    try {
      loading.value = true
      const queryParams = {
        ...searchParams,
        ...params
      }
      
      // 处理日期范围
      if (queryParams.dateRange && queryParams.dateRange.length === 2) {
        queryParams.startDate = queryParams.dateRange[0]
        queryParams.endDate = queryParams.dateRange[1]
        delete queryParams.dateRange
      }
      
      const response = await currentConfig.value.api.getList(queryParams)
      
      // 新API返回格式: { success: true, data: { items, pagination } }
      if (response.success) {
        contentList.value = response.data.items || []
        total.value = response.data.pagination?.total || 0
      } else {
        // 兼容旧API格式
        contentList.value = response.data?.records || []
        total.value = response.data?.total || 0
      }
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error(`获取${currentConfig.value.name}列表失败:`, error)
      ElMessage.error(`获取${currentConfig.value.name}列表失败`)
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 审核通过
  const approveContent = async (contentId, reason = '') => {
    try {
      loading.value = true
      const response = await currentConfig.value.api.approve(contentId, { reason })
      
      if (response.success) {
        ElMessage.success(`${currentConfig.value.name}审核通过`)
        
        // 更新列表中的状态
        const index = contentList.value.findIndex(item => item.id === contentId)
        if (index !== -1) {
          contentList.value[index].status = AUDIT_STATUS.APPROVED
          // 如果API返回了更新后的数据，合并到列表中
          if (response.data) {
            Object.assign(contentList.value[index], response.data)
          }
        }
      } else {
        throw new Error(response.message || '审核失败')
      }
      
      return { success: true }
    } catch (error) {
      console.error(`审核${currentConfig.value.name}失败:`, error)
      ElMessage.error(`审核${currentConfig.value.name}失败`)
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 审核拒绝
  const rejectContent = async (contentId, reason = '') => {
    if (!reason.trim()) {
      ElMessage.warning('请输入拒绝理由')
      return { success: false }
    }
    
    try {
      loading.value = true
      const response = await currentConfig.value.api.reject(contentId, { reason })
      
      if (response.success) {
        ElMessage.success(`${currentConfig.value.name}审核拒绝`)
        
        // 更新列表中的状态
        const index = contentList.value.findIndex(item => item.id === contentId)
        if (index !== -1) {
          contentList.value[index].status = AUDIT_STATUS.REJECTED
          // 如果API返回了更新后的数据，合并到列表中
          if (response.data) {
            Object.assign(contentList.value[index], response.data)
          }
        }
      } else {
        throw new Error(response.message || '拒绝失败')
      }
      
      return { success: true }
    } catch (error) {
      console.error(`拒绝${currentConfig.value.name}失败:`, error)
      ElMessage.error(`拒绝${currentConfig.value.name}失败`)
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 批量审核通过
  const batchApproveContent = async (contentIds, reason = '') => {
    if (!contentIds || contentIds.length === 0) {
      ElMessage.warning('请选择要审核的内容')
      return { success: false }
    }
    
    try {
      await ElMessageBox.confirm(
        `确定要通过选中的 ${contentIds.length} 个${currentConfig.value.name}吗？`,
        '批量审核确认',
        {
          confirmButtonText: '确定通过',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      loading.value = true
      const response = await currentConfig.value.api.batchApprove(contentIds, { reason })
      
      if (response.success) {
        ElMessage.success(`成功通过 ${response.data?.approvedCount || contentIds.length} 个${currentConfig.value.name}`)
        
        // 刷新列表
        await fetchContentList()
      } else {
        throw new Error(response.message || '批量审核失败')
      }
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        console.error(`批量审核${currentConfig.value.name}失败:`, error)
        ElMessage.error(`批量审核${currentConfig.value.name}失败`)
      }
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 批量审核拒绝
  const batchRejectContent = async (contentIds, reason = '') => {
    if (!contentIds || contentIds.length === 0) {
      ElMessage.warning('请选择要拒绝的内容')
      return { success: false }
    }
    
    if (!reason.trim()) {
      ElMessage.warning('请输入拒绝理由')
      return { success: false }
    }
    
    try {
      await ElMessageBox.confirm(
        `确定要拒绝选中的 ${contentIds.length} 个${currentConfig.value.name}吗？`,
        '批量拒绝确认',
        {
          confirmButtonText: '确定拒绝',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      loading.value = true
      const response = await currentConfig.value.api.batchReject(contentIds, { reason })
      
      if (response.success) {
        ElMessage.success(`成功拒绝 ${response.data?.rejectedCount || contentIds.length} 个${currentConfig.value.name}`)
        
        // 刷新列表
        await fetchContentList()
      } else {
        throw new Error(response.message || '批量拒绝失败')
      }
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        console.error(`批量拒绝${currentConfig.value.name}失败:`, error)
        ElMessage.error(`批量拒绝${currentConfig.value.name}失败`)
      }
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 搜索内容
  const searchContent = (params) => {
    Object.assign(searchParams, params)
    searchParams.page = 1
    return fetchContentList()
  }
  
  // 重置搜索
  const resetSearch = () => {
    Object.assign(searchParams, {
      page: 1,
      size: 20,
      status: 'pending',
      keyword: '',
      startDate: '',
      endDate: '',
      userId: ''
    })
    return fetchContentList()
  }
  
  // 分页处理
  const handlePageChange = (page) => {
    searchParams.page = page
    return fetchContentList()
  }
  
  const handleSizeChange = (size) => {
    searchParams.size = size
    searchParams.page = 1
    return fetchContentList()
  }
  
  // 排序处理
  const handleSortChange = ({ prop, order }) => {
    searchParams.sortBy = prop
    searchParams.sortOrder = order === 'ascending' ? 'asc' : 'desc'
    return fetchContentList()
  }
  
  // 选择处理
  const handleSelectionChange = (selection) => {
    selectedContent.value = selection
  }
  
  // 计算属性
  const hasSelection = computed(() => selectedContent.value.length > 0)
  const selectedContentIds = computed(() => selectedContent.value.map(item => item.id))
  const pendingCount = computed(() => {
    return contentList.value.filter(item => item.status === 'pending').length
  })
  
  // 组件挂载时获取数据
  onMounted(() => {
    fetchContentList()
  })
  
  return {
    // 数据
    loading,
    contentList,
    total,
    currentContent,
    searchParams,
    selectedContent,
    
    // 配置
    currentConfig,
    
    // 计算属性
    hasSelection,
    selectedContentIds,
    pendingCount,
    
    // 方法
    fetchContentList,
    approveContent,
    rejectContent,
    batchApproveContent,
    batchRejectContent,
    searchContent,
    resetSearch,
    handlePageChange,
    handleSizeChange,
    handleSortChange,
    handleSelectionChange
  }
}
