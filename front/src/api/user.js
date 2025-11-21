import request from '@/utils/request'

// 获取当前用户资料
export function getUserProfile() {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

// 更新当前用户资料
export function updateUserProfile(data) {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

// 获取指定用户信息
export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 根据UID获取用户信息
export function getUserByUid(uid) {
  return request({
    url: `/users/uid/${uid}`,
    method: 'get'
  })
}

// 搜索用户
export function searchUsers(data) {
  return request({
    url: '/users/search',
    method: 'post',
    data
  })
}

// 上传头像
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传背景图
export function uploadBackground(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/users/background',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 校园认证申请
export function applySchoolCertification(data) {
  const formData = new FormData()
  formData.append('school', data.school)
  if (data.certificate) {
    formData.append('certificate', data.certificate)
  }
  
  return request({
    url: '/users/school-certify',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取用户统计信息
export function getUserStats(id) {
  return request({
    url: `/users/stats/${id}`,
    method: 'get'
  })
}

// 批量获取用户信息
export function getUsersByIds(ids) {
  return request({
    url: '/users/batch',
    method: 'post',
    data: ids
  })
}
