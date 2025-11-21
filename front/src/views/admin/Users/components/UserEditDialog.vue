<template>
  <FormDialog
    v-model="visible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    :fields="formFields"
    :form-data="formData"
    :loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
    @field-change="handleFieldChange"
  >
    <!-- 自定义头像上传字段 -->
    <template #field-avatar="{ field }">
      <el-upload
        :action="uploadAction"
        :headers="uploadHeaders"
        :show-file-list="false"
        :before-upload="beforeAvatarUpload"
        :on-success="handleAvatarSuccess"
        class="avatar-uploader"
      >
        <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <div class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</div>
    </template>
    
    <!-- 自定义标签选择字段 -->
    <template #field-tags="{ field }">
      <el-select
        v-model="formData.tags"
        multiple
        filterable
        allow-create
        placeholder="请选择或输入标签"
        style="width: 100%"
      >
        <el-option
          v-for="tag in availableTags"
          :key="tag.id"
          :label="tag.name"
          :value="tag.id"
        >
          <span :style="{ color: tag.color }">{{ tag.name }}</span>
        </el-option>
      </el-select>
    </template>
  </FormDialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import FormDialog from '@/components/admin/FormDialog.vue'
import { updateUser, createAdminUser } from '@/api/admin'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: null
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 是否为编辑模式
const isEdit = computed(() => !!props.user?.id)

// 加载状态
const loading = ref(false)

// 表单数据
const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  gender: 'unknown',
  birthday: '',
  bio: '',
  avatar: '',
  status: 'active',
  tags: []
})

// 可用标签列表（模拟数据，实际应该从API获取）
const availableTags = ref([
  { id: 1, name: '技术', color: '#409eff' },
  { id: 2, name: '运动', color: '#67c23a' },
  { id: 3, name: '音乐', color: '#e6a23c' },
  { id: 4, name: '旅行', color: '#f56c6c' },
  { id: 5, name: '美食', color: '#909399' }
])

// 上传配置
const uploadAction = '/api/upload/avatar'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('admin_token')}`
}

// 表单字段配置
const formFields = computed(() => [
  {
    prop: 'avatar',
    label: '头像',
    type: 'slot',
    required: false
  },
  {
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名',
    required: true,
    rules: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
      { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
    ]
  },
  {
    prop: 'nickname',
    label: '昵称',
    type: 'input',
    placeholder: '请输入昵称',
    required: true,
    rules: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
    ]
  },
  {
    prop: 'email',
    label: '邮箱',
    type: 'input',
    placeholder: '请输入邮箱',
    required: true,
    rules: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ]
  },
  {
    prop: 'phone',
    label: '手机号',
    type: 'input',
    placeholder: '请输入手机号',
    rules: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ]
  },
  {
    prop: 'password',
    label: '密码',
    type: 'password',
    placeholder: isEdit.value ? '留空则不修改密码' : '请输入密码',
    required: !isEdit.value,
    rules: [
      { required: !isEdit.value, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
  },
  {
    prop: 'confirmPassword',
    label: '确认密码',
    type: 'password',
    placeholder: '请再次输入密码',
    required: !isEdit.value,
    rules: [
      { required: !isEdit.value, message: '请确认密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value !== formData.password) {
            callback(new Error('两次输入的密码不一致'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  },
  {
    prop: 'gender',
    label: '性别',
    type: 'select',
    options: [
      { label: '未知', value: 'unknown' },
      { label: '男', value: 'male' },
      { label: '女', value: 'female' }
    ]
  },
  {
    prop: 'birthday',
    label: '生日',
    type: 'date',
    valueFormat: 'YYYY-MM-DD'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 'active' },
      { label: '禁用', value: 'inactive' }
    ]
  },
  {
    prop: 'bio',
    label: '个人简介',
    type: 'textarea',
    placeholder: '请输入个人简介',
    rows: 4,
    maxlength: 500,
    showWordLimit: true
  },
  {
    prop: 'tags',
    label: '兴趣标签',
    type: 'slot'
  }
])

// 监听用户数据变化
watch(
  () => props.user,
  (newUser) => {
    if (newUser) {
      // 编辑模式，填充表单数据
      Object.assign(formData, {
        username: newUser.username || '',
        nickname: newUser.nickname || '',
        email: newUser.email || '',
        phone: newUser.phone || '',
        password: '',
        confirmPassword: '',
        gender: newUser.gender || 'unknown',
        birthday: newUser.birthday || '',
        bio: newUser.bio || '',
        avatar: newUser.avatar || '',
        status: newUser.status || 'active',
        tags: newUser.tags?.map(tag => tag.id) || []
      })
    } else {
      // 新增模式，重置表单数据
      resetFormData()
    }
  },
  { immediate: true }
)

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    username: '',
    nickname: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    gender: 'unknown',
    birthday: '',
    bio: '',
    avatar: '',
    status: 'active',
    tags: []
  })
}

// 处理确认
const handleConfirm = async (data) => {
  try {
    loading.value = true
    
    // 构建提交数据
    const submitData = { ...data }
    
    // 移除确认密码字段
    delete submitData.confirmPassword
    
    // 如果是编辑模式且密码为空，则不提交密码
    if (isEdit.value && !submitData.password) {
      delete submitData.password
    }
    
    let response
    if (isEdit.value) {
      // 编辑用户
      response = await updateUser(props.user.id, submitData)
      ElMessage.success('更新用户成功')
    } else {
      // 新增用户
      response = await createAdminUser(submitData)
      ElMessage.success('创建用户成功')
    }
    
    emit('success', response.data)
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error(error.message || '保存用户失败')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  visible.value = false
}

// 处理字段变化
const handleFieldChange = (prop, value) => {
  // 可以在这里处理字段变化的逻辑
  console.log('字段变化:', prop, value)
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    formData.avatar = response.data.url
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}
</script>

<style scoped>
.avatar-uploader {
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .avatar-uploader {
    width: 80px;
    height: 80px;
  }
  
  .avatar-uploader-icon {
    width: 80px;
    height: 80px;
    line-height: 80px;
    font-size: 24px;
  }
  
  .avatar {
    width: 80px;
    height: 80px;
  }
}
</style>
