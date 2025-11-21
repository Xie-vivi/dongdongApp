import request from '@/utils/request'

// 获取系统概览数据
export const getSystemOverview = () => {
  return request({
    url: '/admin/analytics/overview',
    method: 'get'
  })
}

// 获取用户统计数据
export const getUserStats = (params) => {
  return request({
    url: '/admin/analytics/users',
    method: 'get',
    params
  })
}

// 获取内容统计数据
export const getContentStats = (params) => {
  return request({
    url: '/admin/analytics/content',
    method: 'get',
    params
  })
}

// 获取活动统计数据
export const getActivityStats = (params) => {
  return request({
    url: '/admin/analytics/activities',
    method: 'get',
    params
  })
}

// 导出统计数据
export const exportAnalyticsData = (params) => {
  return request({
    url: '/admin/analytics/export',
    method: 'post',
    data: params,
    responseType: 'blob'
  })
}
