import request from '@/utils/request'

// 获取权限列表
export const getPermissions = (params) => {
  return request({
    url: '/admin/permissions',
    method: 'get',
    params
  })
}

// 获取角色列表
export const getRoles = (params) => {
  return request({
    url: '/admin/roles',
    method: 'get',
    params
  })
}

// 创建角色
export const createRole = (data) => {
  return request({
    url: '/admin/roles',
    method: 'post',
    data
  })
}

// 更新角色
export const updateRole = (id, data) => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export const deleteRole = (id) => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'delete'
  })
}

// 分配权限给角色
export const assignPermissions = (roleId, permissions) => {
  return request({
    url: `/admin/roles/${roleId}/permissions`,
    method: 'post',
    data: { permissions }
  })
}

// 获取管理员详情
export const getAdministratorDetail = (id) => {
  return request({
    url: `/admin/administrators/${id}`,
    method: 'get'
  })
}

// 获取角色详情
export const getRoleDetail = (id) => {
  return request({
    url: `/admin/roles/${id}`,
    method: 'get'
  })
}

// 分配角色给管理员
export const assignRoles = (adminId, roles) => {
  return request({
    url: `/admin/administrators/${adminId}/roles`,
    method: 'post',
    data: { roles }
  })
}

// 获取管理员的角色
export const getAdministratorRoles = (adminId) => {
  return request({
    url: `/admin/administrators/${adminId}/roles`,
    method: 'get'
  })
}

// 获取角色的权限
export const getRolePermissions = (roleId) => {
  return request({
    url: `/admin/roles/${roleId}/permissions`,
    method: 'get'
  })
}
