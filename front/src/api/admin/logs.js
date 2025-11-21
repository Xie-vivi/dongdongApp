import request from '@/utils/request'

// 获取操作日志列表
export const getOperationLogs = (params) => {
  return request({
    url: '/admin/logs/operations',
    method: 'get',
    params
  })
}

// 获取系统日志列表
export const getSystemLogs = (params) => {
  return request({
    url: '/admin/logs/system',
    method: 'get',
    params
  })
}

// 获取错误日志列表
export const getErrorLogs = (params) => {
  return request({
    url: '/admin/logs/errors',
    method: 'get',
    params
  })
}

// 清理日志
export const clearLogs = (type, beforeDate) => {
  return request({
    url: '/admin/logs/clear',
    method: 'post',
    data: { type, beforeDate }
  })
}

// 导出日志
export const exportLogs = (params) => {
  return request({
    url: '/admin/logs/export',
    method: 'post',
    data: params,
    responseType: 'blob'
  })
}
