<template>
  <div class="reset-container">
    <div class="reset-card">
      <div class="reset-header">
        <h2>重置密码</h2>
        <p>请输入您的用户名和验证码来重置密码</p>
      </div>
      
      <el-form
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        class="reset-form"
        @submit.prevent="handleResetPassword"
      >
        <el-form-item prop="username">
          <el-input
            v-model="resetForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="verificationCode">
          <div class="verification-code-container">
            <el-input
              v-model="resetForm.verificationCode"
              placeholder="请输入验证码"
              size="large"
              :prefix-icon="Key"
              clearable
            />
            <el-button
              type="primary"
              size="large"
              class="send-code-button"
              :disabled="codeSending || countdown > 0"
              @click="sendVerificationCode"
            >
              {{ codeSending ? '发送中...' : (countdown > 0 ? `${countdown}s` : '发送验证码') }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleResetPassword"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="reset-button"
            :loading="loading"
            @click="handleResetPassword"
          >
            重置密码
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="reset-footer">
        <router-link to="/login" class="login-link">
          返回登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Key, Lock } from '@element-plus/icons-vue'
import { sendVerificationCode as sendCode, resetPassword } from '@/api/auth'

const router = useRouter()

const resetFormRef = ref()
const loading = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
let countdownTimer = null

const resetForm = reactive({
  username: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const resetRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const sendVerificationCode = async () => {
  if (!resetForm.username) {
    ElMessage.warning('请先输入用户名')
    return
  }

  try {
    codeSending.value = true
    await sendCode({
      username: resetForm.username,
      type: 'email'
    })
    
    ElMessage.success('验证码已发送，请查看控制台日志')
    startCountdown()
    
  } catch (error) {
    console.error('发送验证码失败:', error)
  } finally {
    codeSending.value = false
  }
}

const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

const handleResetPassword = async () => {
  if (!resetFormRef.value) return
  
  try {
    await resetFormRef.value.validate()
    loading.value = true
    
    const { confirmPassword, ...resetData } = resetForm
    await resetPassword(resetData)
    
    ElMessage.success('密码重置成功，请登录')
    router.push('/login')
    
  } catch (error) {
    console.error('密码重置失败:', error)
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.reset-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.reset-header {
  text-align: center;
  margin-bottom: 30px;
}

.reset-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.reset-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.reset-form {
  margin-bottom: 20px;
}

.verification-code-container {
  display: flex;
  gap: 10px;
}

.verification-code-container .el-input {
  flex: 1;
}

.send-code-button {
  width: 120px;
  flex-shrink: 0;
}

.reset-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.reset-footer {
  text-align: center;
  font-size: 14px;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  transition: color 0.3s;
}

.login-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>
