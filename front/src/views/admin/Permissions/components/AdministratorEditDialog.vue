<template>
  <FormDialog
    v-model="visible"
    :title="isEdit ? '编辑管理员' : '新增管理员'"
    :fields="formFields"
    :form-data="formData"
    :loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
  >
    <!-- 自定义密码字段 -->
    <template #field-password="{ field }">
      <div class="password-field">
        <el-input
          v-model="formData.password"
          type="password"
          :placeholder="isEdit ? '留空表示不修改密码' : '请输入密码'"
          show-password
          clearable
        />
        <div v-if="!isEdit" class="password-strength">
          <div class="strength-label">密码强度：</div>
          <div class="strength-indicator">
            <div
              v-for="level in 4"
              :key="level"
              class="strength-level"
              :class="{ active: passwordStrength >= level }"
            />
          </div>
          <span class="strength-text">{{ getStrengthText(passwordStrength) }}</span>
        </div>
      </div>
    </template>
    
    <!-- 自定义角色选择字段 -->
    <template #field-roleIds="{ field }">
      <div class="role-selection">
        <el-select
          v-model="formData.roleIds"
          multiple
          placeholder="请选择角色"
          style="width: 100%"
        >
          <el-option
            v-for="role in roles"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          >
            <div class="role-option">
              <div class="role-name">{{ role.name }}</div>
              <div class="role-desc">{{ role.description || '暂无描述' }}</div>
            </div>
          </el-option>
        </el-select>
        <div class="role-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>选择多个角色时，权限将合并生效</span>
        </div>
      </div>
    </template>
  </FormDialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import FormDialog from '@/components/admin/FormDialog.vue'
import { createAdministrator, updateAdministrator } from '@/api/admin'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  administrator: {
    type: Object,
    default: null
  },
  roles: {
    type: Array,
    default: () => []
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
const isEdit = computed(() => !!props.administrator?.id)

// 加载状态
const loading = ref(false)

// 表单数据
const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  roleIds: [],
  status: 'active'
})

// 密码强度
const passwordStrength = computed(() => {
  if (!formData.password) return 0
  
  let strength = 0
  const password = formData.password
  
  // 长度检查
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++
  
  // 复杂度检查
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  return Math.min(strength, 4)
})

// 表单字段配置
const formFields = computed(() => [
  {
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名',
    required: true,
    disabled: isEdit.value,
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
    rules: [
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
    type: 'slot',
    required: !isEdit.value,
    rules: isEdit.value ? [] : [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
    ]
  },
  {
    prop: 'roleIds',
    label: '角色',
    type: 'slot',
    required: true,
    rules: [
      { required: true, message: '请选择至少一个角色', trigger: 'change' }
    ]
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 'active' },
      { label: '禁用', value: 'inactive' }
    ]
  }
])

// 监听管理员数据变化
watch(
  () => props.administrator,
  (newAdministrator) => {
    if (newAdministrator) {
      // 编辑模式，填充表单数据
      Object.assign(formData, {
        username: newAdministrator.username || '',
        nickname: newAdministrator.nickname || '',
        email: newAdministrator.email || '',
        phone: newAdministrator.phone || '',
        password: '',
        roleIds: newAdministrator.roles ? newAdministrator.roles.map(role => role.id) : [],
        status: newAdministrator.status || 'active'
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
    roleIds: [],
    status: 'active'
  })
}

// 获取密码强度文本
const getStrengthText = (strength) => {
  const strengthMap = {
    0: '无',
    1: '弱',
    2: '一般',
    3: '强',
    4: '很强'
  }
  return strengthMap[strength] || '无'
}

// 处理确认
const handleConfirm = async (data) => {
  try {
    loading.value = true
    
    // 构建提交数据
    const submitData = { ...data }
    
    // 编辑模式下，如果密码为空则不提交密码字段
    if (isEdit.value && !submitData.password) {
      delete submitData.password
    }
    
    let response
    if (isEdit.value) {
      // 编辑管理员
      response = await updateAdministrator(props.administrator.id, submitData)
      ElMessage.success('更新管理员成功')
    } else {
      // 新增管理员
      response = await createAdministrator(submitData)
      ElMessage.success('创建管理员成功')
    }
    
    emit('success', response.data)
  } catch (error) {
    console.error('保存管理员失败:', error)
    ElMessage.error(error.message || '保存管理员失败')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.password-field {
  width: 100%;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 4px;
}

.strength-label {
  font-size: 12px;
  color: #606266;
  min-width: 60px;
}

.strength-indicator {
  display: flex;
  gap: 2px;
  flex: 1;
}

.strength-level {
  flex: 1;
  height: 4px;
  background: #e4e7ed;
  border-radius: 2px;
  transition: background-color 0.3s ease;
}

.strength-level.active {
  background: #409eff;
}

.strength-level.active:nth-child(1) { background: #f56c6c; }
.strength-level.active:nth-child(2) { background: #e6a23c; }
.strength-level.active:nth-child(3) { background: #67c23a; }
.strength-level.active:nth-child(4) { background: #409eff; }

.strength-text {
  font-size: 12px;
  color: #606266;
  min-width: 40px;
  text-align: right;
}

.role-selection {
  width: 100%;
}

.role-option {
  padding: 8px 0;
}

.role-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.role-desc {
  font-size: 12px;
  color: #909399;
}

.role-tips {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
  color: #409eff;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .password-strength {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .strength-label {
    min-width: auto;
  }
  
  .strength-text {
    min-width: auto;
    text-align: left;
  }
}

/* 动画效果 */
.strength-level {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scaleX(0);
  }
  to {
    opacity: 1;
    transform: scaleX(1);
  }
}
</style>
