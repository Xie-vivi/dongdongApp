<template>
  <div class="user-edit">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="right"
    >
      <el-tabs v-model="activeTab" class="edit-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="form-section">
            <div class="avatar-upload">
              <el-avatar :src="formData.avatar" :size="100" />
              <div class="upload-actions">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-button size="small">更换头像</el-button>
                </el-upload>
                <el-button size="small" type="danger" @click="removeAvatar">
                  删除头像
                </el-button>
              </div>
            </div>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input
                    v-model="formData.username"
                    placeholder="请输入用户名"
                    :disabled="!!user?.id"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input
                    v-model="formData.realName"
                    placeholder="请输入真实姓名"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="formData.email"
                    placeholder="请输入邮箱地址"
                    type="email"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="formData.phone"
                    placeholder="请输入手机号码"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
                <el-radio label="other">其他</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker
                    v-model="formData.birthDate"
                    type="date"
                    placeholder="选择出生日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input
                    v-model="formData.idCard"
                    placeholder="请输入身份证号"
                    maxlength="18"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="地址" prop="address">
              <el-input
                v-model="formData.address"
                placeholder="请输入详细地址"
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 账户设置 -->
        <el-tab-pane label="账户设置" name="account">
          <div class="form-section">
            <el-form-item label="用户角色" prop="role">
              <el-select v-model="formData.role" placeholder="选择用户角色">
                <el-option
                  v-for="(role, key) in USER_ROLES"
                  :key="key"
                  :label="role.label"
                  :value="role.value"
                >
                  <div class="role-option">
                    <el-tag :type="getRoleTagType(role.value)" size="small">
                      {{ role.label }}
                    </el-tag>
                    <span class="role-desc">权限等级: {{ role.level }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="账户状态" prop="status">
              <el-select v-model="formData.status" placeholder="选择账户状态">
                <el-option
                  v-for="(status, key) in USER_STATUS"
                  :key="key"
                  :label="status.label"
                  :value="status.value"
                >
                  <div class="status-option">
                    <el-icon :style="{ color: status.color }">
                      <component :is="status.icon" />
                    </el-icon>
                    {{ status.label }}
                  </div>
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="认证状态" prop="verified">
              <el-switch
                v-model="formData.verified"
                active-text="已认证"
                inactive-text="未认证"
              />
            </el-form-item>

            <el-form-item label="账户封禁" prop="banned">
              <el-switch
                v-model="formData.banned"
                active-text="已封禁"
                inactive-text="正常"
                @change="handleBanChange"
              />
            </el-form-item>

            <el-form-item
              v-if="formData.banned"
              label="封禁原因"
              prop="banReason"
            >
              <el-input
                v-model="formData.banReason"
                type="textarea"
                :rows="3"
                placeholder="请输入封禁原因"
              />
            </el-form-item>

            <el-form-item label="密码设置" v-if="!user?.id">
              <el-button type="primary" @click="generatePassword">
                <el-icon><Key /></el-icon>
                生成随机密码
              </el-button>
              <el-button @click="showPasswordDialog = true">
                <el-icon><Edit /></el-icon>
                自定义密码
              </el-button>
            </el-form-item>

            <el-form-item label="重置密码" v-if="user?.id">
              <el-button type="warning" @click="resetPassword">
                <el-icon><RefreshRight /></el-icon>
                重置用户密码
              </el-button>
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="personal">
          <div class="form-section">
            <el-form-item label="职业" prop="occupation">
              <el-input
                v-model="formData.occupation"
                placeholder="请输入职业"
              />
            </el-form-item>

            <el-form-item label="教育程度" prop="education">
              <el-select v-model="formData.education" placeholder="选择教育程度">
                <el-option label="小学" value="primary" />
                <el-option label="初中" value="middle" />
                <el-option label="高中" value="high" />
                <el-option label="大专" value="college" />
                <el-option label="本科" value="bachelor" />
                <el-option label="硕士" value="master" />
                <el-option label="博士" value="doctor" />
              </el-select>
            </el-form-item>

            <el-form-item label="兴趣爱好" prop="interests">
              <el-select
                v-model="formData.interests"
                multiple
                filterable
                allow-create
                placeholder="选择或输入兴趣爱好"
              >
                <el-option
                  v-for="interest in commonInterests"
                  :key="interest"
                  :label="interest"
                  :value="interest"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="个人签名" prop="signature">
              <el-input
                v-model="formData.signature"
                type="textarea"
                :rows="4"
                placeholder="请输入个人签名"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="formData.bio"
                type="textarea"
                :rows="6"
                placeholder="请输入个人简介"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 隐私设置 -->
        <el-tab-pane label="隐私设置" name="privacy">
          <div class="form-section">
            <h4>信息公开设置</h4>
            <div class="privacy-options">
              <el-form-item label="邮箱公开">
                <el-switch
                  v-model="formData.privacy.emailPublic"
                  active-text="公开"
                  inactive-text="私密"
                />
              </el-form-item>

              <el-form-item label="手机公开">
                <el-switch
                  v-model="formData.privacy.phonePublic"
                  active-text="公开"
                  inactive-text="私密"
                />
              </el-form-item>

              <el-form-item label="地址公开">
                <el-switch
                  v-model="formData.privacy.addressPublic"
                  active-text="公开"
                  inactive-text="私密"
                />
              </el-form-item>

              <el-form-item label="生日公开">
                <el-switch
                  v-model="formData.privacy.birthDatePublic"
                  active-text="公开"
                  inactive-text="私密"
                />
              </el-form-item>

              <el-form-item label="真实姓名公开">
                <el-switch
                  v-model="formData.privacy.realNamePublic"
                  active-text="公开"
                  inactive-text="私密"
                />
              </el-form-item>
            </div>

            <h4>消息设置</h4>
            <div class="message-settings">
              <el-form-item label="接收系统消息">
                <el-switch
                  v-model="formData.settings.receiveSystemMessage"
                  active-text="开启"
                  inactive-text="关闭"
                />
              </el-form-item>

              <el-form-item label="接收营销消息">
                <el-switch
                  v-model="formData.settings.receiveMarketingMessage"
                  active-text="开启"
                  inactive-text="关闭"
                />
              </el-form-item>

              <el-form-item label="接收邮件通知">
                <el-switch
                  v-model="formData.settings.receiveEmailNotification"
                  active-text="开启"
                  inactive-text="关闭"
                />
              </el-form-item>

              <el-form-item label="接收短信通知">
                <el-switch
                  v-model="formData.settings.receiveSmsNotification"
                  active-text="开启"
                  inactive-text="关闭"
                />
              </el-form-item>
            </div>
          </div>
        </el-tab-pane>

        <!-- 管理员备注 -->
        <el-tab-pane label="管理员备注" name="admin">
          <div class="form-section">
            <el-form-item label="备注信息" prop="notes">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="6"
                placeholder="请输入管理员备注信息"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="formData.riskLevel" placeholder="选择风险等级">
                <el-option label="低风险" value="low" />
                <el-option label="中风险" value="medium" />
                <el-option label="高风险" value="high" />
              </el-select>
            </el-form-item>

            <el-form-item label="特殊标记">
              <el-checkbox-group v-model="formData.tags">
                <el-checkbox label="vip">VIP用户</el-checkbox>
                <el-checkbox label="important">重要用户</el-checkbox>
                <el-checkbox label="monitor">重点关注</el-checkbox>
                <el-checkbox label="restricted">权限受限</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 表单操作按钮 -->
      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
        <el-button type="success" @click="handleSaveAndContinue" :loading="saving">
          保存并继续编辑
        </el-button>
      </div>
    </el-form>

    <!-- 密码设置对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="设置密码"
      width="400px"
    >
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="passwordForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Key,
  Edit,
  RefreshRight
} from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'

// Props
const props = defineProps({
  user: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['save', 'cancel'])

// 使用组合式函数
const {
  USER_STATUS,
  USER_ROLES
} = useUserManagement()

// 响应式数据
const formRef = ref()
const activeTab = ref('basic')
const saving = ref(false)
const showPasswordDialog = ref(false)

// 表单数据
const formData = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: '',
  gender: 'male',
  birthDate: '',
  idCard: '',
  address: '',
  role: 'user',
  status: 'active',
  verified: false,
  banned: false,
  banReason: '',
  occupation: '',
  education: '',
  interests: [],
  signature: '',
  bio: '',
  notes: '',
  riskLevel: 'low',
  tags: [],
  privacy: {
    emailPublic: false,
    phonePublic: false,
    addressPublic: false,
    birthDatePublic: false,
    realNamePublic: false
  },
  settings: {
    receiveSystemMessage: true,
    receiveMarketingMessage: false,
    receiveEmailNotification: true,
    receiveSmsNotification: false
  }
})

const passwordForm = reactive({
  password: '',
  confirmPassword: ''
})

// 常见兴趣爱好
const commonInterests = [
  '编程', '阅读', '旅行', '音乐', '电影', '运动', '美食', '摄影',
  '绘画', '写作', '游戏', '健身', '瑜伽', '烹饪', '园艺', '手工',
  '科技', '历史', '艺术', '文学', '哲学', '心理学', '经济学', '投资'
]

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '真实姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择账户状态', trigger: 'change' }
  ],
  banReason: [
    { required: true, message: '请输入封禁原因', trigger: 'blur' }
  ]
}

// 计算属性
const isEdit = computed(() => !!props.user?.id)

// 监听用户数据变化
watch(() => props.user, (newUser) => {
  if (newUser) {
    // 编辑模式，填充表单数据
    Object.assign(formData, {
      ...newUser,
      privacy: newUser.privacy || formData.privacy,
      settings: newUser.settings || formData.settings,
      interests: newUser.interests || []
    })
  } else {
    // 新建模式，重置表单
    resetForm()
  }
}, { immediate: true })

// 方法
const resetForm = () => {
  Object.assign(formData, {
    username: '',
    realName: '',
    email: '',
    phone: '',
    avatar: '',
    gender: 'male',
    birthDate: '',
    idCard: '',
    address: '',
    role: 'user',
    status: 'active',
    verified: false,
    banned: false,
    banReason: '',
    occupation: '',
    education: '',
    interests: [],
    signature: '',
    bio: '',
    notes: '',
    riskLevel: 'low',
    tags: [],
    privacy: {
      emailPublic: false,
      phonePublic: false,
      addressPublic: false,
      birthDatePublic: false,
      realNamePublic: false
    },
    settings: {
      receiveSystemMessage: true,
      receiveMarketingMessage: false,
      receiveEmailNotification: true,
      receiveSmsNotification: false
    }
  })
}

const getRoleTagType = (role) => {
  const typeMap = {
    admin: 'danger',
    editor: 'warning',
    reviewer: 'info',
    user: 'success'
  }
  return typeMap[role] || 'info'
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }

  // 这里应该上传图片到服务器
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.avatar = e.target.result
  }
  reader.readAsDataURL(file)
  
  return false // 阻止默认上传行为
}

const removeAvatar = () => {
  formData.avatar = ''
  ElMessage.success('头像已删除')
}

const generatePassword = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*'
  let password = ''
  for (let i = 0; i < 12; i++) {
    password += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  passwordForm.password = password
  passwordForm.confirmPassword = password
  showPasswordDialog.value = true
  ElMessage.success('随机密码已生成')
}

const confirmPassword = () => {
  if (!passwordForm.password) {
    ElMessage.error('请输入密码')
    return
  }
  if (passwordForm.password !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  if (passwordForm.password.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }
  
  formData.password = passwordForm.password
  showPasswordDialog.value = false
  ElMessage.success('密码设置成功')
}

const handleBanChange = (value) => {
  if (value && !formData.banReason) {
    formData.banReason = '违反社区规定'
  }
}

const resetPassword = () => {
  ElMessage.success('密码重置成功，新密码已发送到用户邮箱')
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    emit('save', { ...formData })
    ElMessage.success(isEdit.value ? '用户更新成功' : '用户创建成功')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    saving.value = false
  }
}

const handleSaveAndContinue = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    emit('save', { ...formData })
    ElMessage.success(isEdit.value ? '用户更新成功' : '用户创建成功')
    // 不关闭对话框，继续编辑
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    saving.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.user-edit {
  padding: 20px;
}

.edit-tabs {
  margin-bottom: 20px;
}

.form-section {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-uploader {
  text-align: center;
}

.role-option,
.status-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-desc {
  font-size: 12px;
  color: #909399;
}

.privacy-options,
.message-settings {
  margin-bottom: 24px;
}

.privacy-options h4,
.message-settings h4 {
  margin: 0 0 16px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-edit {
    padding: 16px;
  }
  
  .form-section {
    padding: 16px;
  }
  
  .avatar-upload {
    flex-direction: column;
    text-align: center;
  }
  
  .form-actions {
    flex-direction: column;
  }
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__inner) {
  border-radius: 4px;
}

:deep(.el-select .el-input__inner) {
  border-radius: 4px;
}

:deep(.el-textarea__inner) {
  border-radius: 4px;
}

/* 标签页样式 */
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

:deep(.el-tabs__item) {
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

/* 头像上传样式 */
:deep(.avatar-uploader .el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

:deep(.avatar-uploader .el-upload:hover) {
  border-color: #409eff;
}

/* 动画效果 */
.form-section {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
