import request from '@/utils/request'

// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}

// 管理员登出
export function adminLogout() {
  return request({
    url: '/admin/auth/logout',
    method: 'post'
  })
}

// 刷新管理员Token
export function refreshAdminToken(refreshToken) {
  return request({
    url: '/admin/auth/refresh',
    method: 'post',
    params: { refreshToken }
  })
}

// 获取当前管理员信息
export function getAdminProfile() {
  return request({
    url: '/admin/auth/profile',
    method: 'get'
  })
}

// 获取系统概览统计
export function getSystemOverview() {
  return request({
    url: '/admin/statistics/overview',
    method: 'get'
  })
}

// 获取用户趋势数据
export function getUserTrends() {
  return request({
    url: '/admin/statistics/user-trends',
    method: 'get'
  })
}

// 获取内容统计数据
export function getContentStats() {
  return request({
    url: '/admin/statistics/content-stats',
    method: 'get'
  })
}

// 获取系统健康状态
export function getSystemHealth() {
  return request({
    url: '/admin/statistics/system-health',
    method: 'get'
  })
}

// 获取用户列表
export function getUsersList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'post',
    data: { status }
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

// 获取待审核帖子
export function getPendingPosts(params) {
  return request({
    url: '/admin/content/posts/pending',
    method: 'get',
    params
  })
}

// 获取待审核活动
export function getPendingActivities(params) {
  return request({
    url: '/admin/content/activities/pending',
    method: 'get',
    params
  })
}

// 审核帖子
export function approvePost(id) {
  return request({
    url: `/admin/content/posts/${id}/approve`,
    method: 'post'
  })
}

// 拒绝帖子
export function rejectPost(id, reason) {
  return request({
    url: `/admin/content/posts/${id}/reject`,
    method: 'post',
    data: { reason }
  })
}

// 获取系统配置
export function getSystemConfigs() {
  return request({
    url: '/admin/system/configs',
    method: 'get'
  })
}

// 更新系统配置
export function updateSystemConfig(key, value) {
  return request({
    url: `/admin/system/configs/${key}`,
    method: 'put',
    data: { value }
  })
}

// 获取操作日志
export function getOperationLogs(params) {
  return request({
    url: '/admin/operation-logs',
    method: 'get',
    params
  })
}

// 获取管理员列表
export function getAdministrators(params) {
  return request({
    url: '/admin/administrators',
    method: 'get',
    params
  })
}

// 创建管理员
export function createAdministrator(data) {
  return request({
    url: '/admin/administrators',
    method: 'post',
    data
  })
}

// 更新管理员
export function updateAdministrator(id, data) {
  return request({
    url: `/admin/administrators/${id}`,
    method: 'put',
    data
  })
}

// 获取角色列表
export function getRoles() {
  return request({
    url: '/admin/roles',
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/admin/roles',
    method: 'post',
    data
  })
}

// 获取标签列表
export function getTags() {
  return request({
    url: '/tags',
    method: 'get'
  })
}

// 创建标签
export function createTag(data) {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

// 更新标签
export function updateTag(id, data) {
  return request({
    url: `/tags/${id}`,
    method: 'put',
    data
  })
}

// 删除标签
export function deleteTag(id) {
  return request({
    url: `/tags/${id}`,
    method: 'delete'
  })
}

// 根据标签ID查询活动
export function getActivitiesByTagId(tagId, params) {
  return request({
    url: `/activities/field/by-tag/${tagId}`,
    method: 'get',
    params
  })
}

// 根据场地ID查询活动
export function getActivitiesByFieldId(fieldId, params) {
  return request({
    url: `/activities/field/by-field/${fieldId}`,
    method: 'get',
    params
  })
}
