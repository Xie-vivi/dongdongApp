import request from '@/utils/request'

// 获取文件列表
export const getFiles = (params) => {
  return request({
    url: '/admin/files',
    method: 'get',
    params
  })
}

// 上传文件
export const uploadFile = (formData) => {
  return request({
    url: '/admin/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除文件
export const deleteFile = (id) => {
  return request({
    url: `/admin/files/${id}`,
    method: 'delete'
  })
}

// 批量删除文件
export const batchDeleteFiles = (ids) => {
  return request({
    url: '/admin/files/batch-delete',
    method: 'post',
    data: { ids }
  })
}

// 获取文件统计
export const getFileStats = () => {
  return request({
    url: '/admin/files/stats',
    method: 'get'
  })
}
