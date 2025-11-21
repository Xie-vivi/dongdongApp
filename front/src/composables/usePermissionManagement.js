import { ref, reactive, computed, onMounted } from 'vue'
// 权限管理组合式函数
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdministrators,
  getAdministratorDetail,
  createAdministrator,
  updateAdministrator,
  deleteAdministrator,
  updateAdministratorStatus,
  getRoles,
  getRoleDetail,
  createRole,
  updateRole,
  deleteRole,
  getPermissions,
  assignRoles,
  assignPermissions,
  getAdministratorRoles,
  getRolePermissions
} from '@/api/admin'

export function usePermissionManagement() {
  // 响应式数据
  const loading = ref(false)
  const administrators = ref([])
  const roles = ref([])
  const permissions = ref([])
  const total = ref(0)
  const currentAdministrator = ref(null)
  const currentRole = ref(null)
  
  // 搜索参数
  const searchParams = reactive({
    page: 1,
    size: 20,
    username: '',
    email: '',
    status: '',
    roleId: ''
  })
  
  // 角色搜索参数
  const roleSearchParams = reactive({
    page: 1,
    size: 20,
    name: '',
    description: '',
    status: ''
  })
  
  // 表格选择
  const selectedAdministrators = ref([])
  const selectedRoles = ref([])
  
  // 管理员搜索字段配置
  const adminSearchFields = [
    {
      prop: 'username',
      label: '用户名',
      type: 'input',
      placeholder: '请输入用户名'
    },
    {
      prop: 'email',
      label: '邮箱',
      type: 'input',
      placeholder: '请输入邮箱'
    },
    {
      prop: 'status',
      label: '状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '启用', value: 'active' },
        { label: '禁用', value: 'inactive' }
      ]
    },
    {
      prop: 'roleId',
      label: '角色',
      type: 'select',
      options: computed(() => [
        { label: '全部', value: '' },
        ...roles.value.map(role => ({ label: role.name, value: role.id }))
      ])
    }
  ]
  
  // 角色搜索字段配置
  const roleSearchFields = [
    {
      prop: 'name',
      label: '角色名称',
      type: 'input',
      placeholder: '请输入角色名称'
    },
    {
      prop: 'description',
      label: '角色描述',
      type: 'input',
      placeholder: '请输入角色描述'
    },
    {
      prop: 'status',
      label: '状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '启用', value: 'active' },
        { label: '禁用', value: 'inactive' }
      ]
    }
  ]
  
  // 管理员表格列配置
  const adminTableColumns = [
    {
      prop: 'id',
      label: 'ID',
      width: 80,
      sortable: true
    },
    {
      prop: 'username',
      label: '用户名',
      minWidth: 120,
      type: 'link'
    },
    {
      prop: 'nickname',
      label: '昵称',
      minWidth: 120
    },
    {
      prop: 'email',
      label: '邮箱',
      minWidth: 180
    },
    {
      prop: 'roles',
      label: '角色',
      width: 200,
      formatter: (row) => {
        if (!row.roles || row.roles.length === 0) return '-'
        return row.roles.map(role => role.name).join(', ')
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      tag: {
        type: 'system',
        textMap: {
          active: '启用',
          inactive: '禁用'
        },
        typeMap: {
          active: 'success',
          inactive: 'danger'
        }
      }
    },
    {
      prop: 'lastLoginAt',
      label: '最后登录',
      width: 160,
      sortable: true,
      formatter: (row) => {
        return row.lastLoginAt ? new Date(row.lastLoginAt).toLocaleString() : '-'
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
  
  // 角色表格列配置
  const roleTableColumns = [
    {
      prop: 'id',
      label: 'ID',
      width: 80,
      sortable: true
    },
    {
      prop: 'name',
      label: '角色名称',
      minWidth: 150,
      type: 'link'
    },
    {
      prop: 'description',
      label: '角色描述',
      minWidth: 200,
      showOverflowTooltip: true
    },
    {
      prop: 'permissionsCount',
      label: '权限数量',
      width: 100,
      sortable: true,
      formatter: (row) => {
        return row.permissions ? row.permissions.length : 0
      }
    },
    {
      prop: 'administratorsCount',
      label: '管理员数量',
      width: 120,
      sortable: true,
      formatter: (row) => {
        return row.administratorsCount || 0
      }
    },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      tag: {
        type: 'system',
        textMap: {
          active: '启用',
          inactive: '禁用'
        },
        typeMap: {
          active: 'success',
          inactive: 'danger'
        }
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
  
  // 获取管理员列表
  const fetchAdministrators = async (params = {}) => {
    try {
      loading.value = true
      const queryParams = {
        ...searchParams,
        ...params
      }
      
      const response = await getAdministrators(queryParams)
      administrators.value = response.data.records || []
      total.value = response.data.total || 0
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取管理员列表失败:', error)
      ElMessage.error('获取管理员列表失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 获取角色列表
  const fetchRoles = async () => {
    try {
      const response = await getRoles()
      roles.value = response.data || []
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取角色列表失败:', error)
      return { success: false, message: error.message }
    }
  }
  
  // 获取权限列表
  const fetchPermissions = async () => {
    try {
      const response = await getPermissions()
      permissions.value = response.data || []
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取权限列表失败:', error)
      return { success: false, message: error.message }
    }
  }
  
  // 获取管理员详情
  const fetchAdministratorDetail = async (adminId) => {
    try {
      loading.value = true
      const response = await getAdministratorDetail(adminId)
      currentAdministrator.value = response.data
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取管理员详情失败:', error)
      ElMessage.error('获取管理员详情失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 获取角色详情
  const fetchRoleDetail = async (roleId) => {
    try {
      loading.value = true
      const response = await getRoleDetail(roleId)
      currentRole.value = response.data
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('获取角色详情失败:', error)
      ElMessage.error('获取角色详情失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 创建管理员
  const createAdministratorHandler = async (adminData) => {
    try {
      loading.value = true
      const response = await createAdministrator(adminData)
      
      ElMessage.success('创建管理员成功')
      
      // 刷新列表
      await fetchAdministrators()
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('创建管理员失败:', error)
      ElMessage.error('创建管理员失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 更新管理员
  const updateAdministratorHandler = async (adminId, adminData) => {
    try {
      loading.value = true
      const response = await updateAdministrator(adminId, adminData)
      
      ElMessage.success('更新管理员成功')
      
      // 更新列表中的管理员
      const index = administrators.value.findIndex(admin => admin.id === adminId)
      if (index !== -1) {
        Object.assign(administrators.value[index], response.data)
      }
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('更新管理员失败:', error)
      ElMessage.error('更新管理员失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 删除管理员
  const deleteAdministrator = async (adminId) => {
    try {
      await ElMessageBox.confirm(
        '确定要删除该管理员吗？删除后无法恢复！',
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      loading.value = true
      await deleteAdministrator(adminId)
      
      ElMessage.success('删除管理员成功')
      
      // 从列表中移除
      const index = administrators.value.findIndex(admin => admin.id === adminId)
      if (index !== -1) {
        administrators.value.splice(index, 1)
        total.value -= 1
      }
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除管理员失败:', error)
        ElMessage.error('删除管理员失败')
      }
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 更新管理员状态
  const updateAdministratorStatus = async (adminId, status) => {
    try {
      loading.value = true
      await updateAdministrator(adminId, { status })
      
      ElMessage.success('更新管理员状态成功')
      
      // 更新列表中的状态
      const index = administrators.value.findIndex(admin => admin.id === adminId)
      if (index !== -1) {
        administrators.value[index].status = status
      }
      
      return { success: true }
    } catch (error) {
      console.error('更新管理员状态失败:', error)
      ElMessage.error('更新管理员状态失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 创建角色
  const createRole = async (roleData) => {
    try {
      loading.value = true
      const response = await createRole(roleData)
      
      ElMessage.success('创建角色成功')
      
      // 刷新角色列表
      await fetchRoles()
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('创建角色失败:', error)
      ElMessage.error('创建角色失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 更新角色
  const updateRoleHandler = async (roleId, roleData) => {
    try {
      loading.value = true
      const response = await updateRole(roleId, roleData)
      
      ElMessage.success('更新角色成功')
      
      // 更新列表中的角色
      const index = roles.value.findIndex(role => role.id === roleId)
      if (index !== -1) {
        Object.assign(roles.value[index], response.data)
      }
      
      return { success: true, data: response.data }
    } catch (error) {
      console.error('更新角色失败:', error)
      ElMessage.error('更新角色失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 删除角色
  const deleteRoleHandler = async (roleId) => {
    try {
      await ElMessageBox.confirm(
        '确定要删除该角色吗？删除后无法恢复！',
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      loading.value = true
      await deleteRole(roleId)
      
      ElMessage.success('删除角色成功')
      
      // 从列表中移除
      const index = roles.value.findIndex(role => role.id === roleId)
      if (index !== -1) {
        roles.value.splice(index, 1)
      }
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除角色失败:', error)
        ElMessage.error('删除角色失败')
      }
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 分配角色
  const assignRolesToAdministrator = async (adminId, roleIds) => {
    try {
      loading.value = true
      await assignRoles(adminId, roleIds)
      
      ElMessage.success('分配角色成功')
      
      // 刷新管理员列表
      await fetchAdministrators()
      
      return { success: true }
    } catch (error) {
      console.error('分配角色失败:', error)
      ElMessage.error('分配角色失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 分配权限
  const assignPermissionsToRole = async (roleId, permissionIds) => {
    try {
      loading.value = true
      await assignPermissions(roleId, permissionIds)
      
      ElMessage.success('分配权限成功')
      
      // 刷新角色详情
      await fetchRoleDetail(roleId)
      
      return { success: true }
    } catch (error) {
      console.error('分配权限失败:', error)
      ElMessage.error('分配权限失败')
      return { success: false, message: error.message }
    } finally {
      loading.value = false
    }
  }
  
  // 搜索管理员
  const searchAdministrators = (params) => {
    Object.assign(searchParams, params)
    searchParams.page = 1
    return fetchAdministrators()
  }
  
  // 重置搜索
  const resetSearch = () => {
    Object.assign(searchParams, {
      page: 1,
      size: 20,
      username: '',
      email: '',
      status: '',
      roleId: ''
    })
    return fetchAdministrators()
  }
  
  // 分页处理
  const handlePageChange = (page) => {
    searchParams.page = page
    return fetchAdministrators()
  }
  
  const handleSizeChange = (size) => {
    searchParams.size = size
    searchParams.page = 1
    return fetchAdministrators()
  }
  
  // 排序处理
  const handleSortChange = ({ prop, order }) => {
    searchParams.sortBy = prop
    searchParams.sortOrder = order === 'ascending' ? 'asc' : 'desc'
    return fetchAdministrators()
  }
  
  // 选择处理
  const handleSelectionChange = (selection) => {
    selectedAdministrators.value = selection
  }
  
  // 计算属性
  const hasSelection = computed(() => selectedAdministrators.value.length > 0)
  const selectedAdministratorIds = computed(() => selectedAdministrators.value.map(admin => admin.id))
  
  // 组件挂载时获取数据
  onMounted(async () => {
    await fetchRoles()
    await fetchPermissions()
    await fetchAdministrators()
  })
  
  return {
    // 数据
    loading,
    administrators,
    roles,
    permissions,
    total,
    currentAdministrator,
    currentRole,
    searchParams,
    selectedAdministrators,
    adminSearchFields,
    adminTableColumns,
    roleSearchFields,
    roleTableColumns,
    
    // 计算属性
    hasSelection,
    selectedAdministratorIds,
    
    // 方法
    fetchAdministrators,
    fetchRoles,
    fetchPermissions,
    fetchAdministratorDetail,
    fetchRoleDetail,
    createAdministrator,
    updateAdministrator,
    deleteAdministrator,
    updateAdministratorStatus,
    createRole,
    updateRole: updateRoleHandler,
    deleteRole: deleteRoleHandler,
    assignRolesToAdministrator,
    assignPermissionsToRole,
    searchAdministrators,
    resetSearch,
    handlePageChange,
    handleSizeChange,
    handleSortChange,
    handleSelectionChange
  }
}
