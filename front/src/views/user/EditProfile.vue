<template>
  <div class="edit-profile-container">
    <div class="edit-profile-card">
      <div class="profile-header">
        <h2>编辑个人资料</h2>
        <p>完善您的个人信息</p>
      </div>
      
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        class="profile-form"
        label-width="100px"
        @submit.prevent="handleUpdateProfile"
      >
        <!-- 头像上传 -->
        <el-form-item label="头像">
          <div class="avatar-upload-container">
            <el-avatar
              :size="100"
              :src="profileForm.avatar || defaultAvatar"
              class="avatar-preview"
            />
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleUploadError"
            >
              <el-button type="primary" size="small">更换头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        
        <!-- 背景图上传 -->
        <el-form-item label="背景图">
          <div class="background-upload-container">
            <div
              class="background-preview"
              :style="{ backgroundImage: `url(${profileForm.background || defaultBackground})` }"
            />
            <el-upload
              class="background-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeBackgroundUpload"
              :on-success="handleBackgroundSuccess"
              :on-error="handleUploadError"
            >
              <el-button type="primary" size="small">更换背景图</el-button>
            </el-upload>
          </div>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="profileForm.nickname"
            placeholder="请输入昵称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="profileForm.birthday"
            type="date"
            placeholder="请选择生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="profileForm.bio"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="学校" prop="school">
          <el-input
            v-model="profileForm.school"
            placeholder="请输入学校名称"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="位置" prop="location">
          <el-input
            v-model="profileForm.location"
            placeholder="请输入位置信息"
            maxlength="50"
          />
        </el-form-item>
        
        <el-form-item label="MBTI" prop="mbti">
          <el-input
            v-model="profileForm.mbti"
            placeholder="请输入MBTI类型（如：INTJ）"
            maxlength="4"
            style="width: 120px"
          />
        </el-form-item>
        
        <el-form-item label="隐私设置">
          <div class="privacy-settings">
            <el-checkbox v-model="profileForm.showGender">显示性别</el-checkbox>
            <el-checkbox v-model="profileForm.showAge">显示年龄</el-checkbox>
            <el-checkbox v-model="profileForm.showMbti">显示MBTI</el-checkbox>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="save-button"
            :loading="loading"
            @click="handleUpdateProfile"
          >
            保存修改
          </el-button>
          <el-button
            size="large"
            @click="handleCancel"
          >
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile } from '@/api/user'

const router = useRouter()
const store = useStore()

const profileFormRef = ref()
const loading = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const defaultBackground = 'https://picsum.photos/seed/default-bg/800/200.jpg'

const profileForm = reactive({
  nickname: '',
  gender: '',
  birthday: '',
  bio: '',
  school: '',
  location: '',
  mbti: '',
  showGender: true,
  showAge: true,
  showMbti: true,
  avatar: '',
  background: ''
})

const uploadUrl = '/api/users/avatar'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${store.getters['user/token']}`
}))

const profileRules = {
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: '个人简介长度不能超过500个字符', trigger: 'blur' }
  ],
  school: [
    { max: 100, message: '学校名称长度不能超过100个字符', trigger: 'blur' }
  ],
  location: [
    { max: 50, message: '位置信息长度不能超过50个字符', trigger: 'blur' }
  ],
  mbti: [
    { pattern: /^[A-Z]{4}$/, message: 'MBTI类型必须是4位大写字母', trigger: 'blur' }
  ]
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const beforeBackgroundUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    profileForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleBackgroundSuccess = (response) => {
  if (response.code === 200) {
    profileForm.background = response.data
    ElMessage.success('背景图上传成功')
  } else {
    ElMessage.error(response.message || '背景图上传失败')
  }
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请重试')
}

const loadUserProfile = async () => {
  try {
    const response = await getUserProfile()
    const { data } = response
    
    Object.assign(profileForm, {
      nickname: data.nickname || '',
      gender: data.gender || '',
      birthday: data.birthday || '',
      bio: data.bio || '',
      school: data.school || '',
      location: data.location || '',
      mbti: data.mbti || '',
      showGender: data.showGender !== false,
      showAge: data.showAge !== false,
      showMbti: data.showMbti !== false,
      avatar: data.avatar || '',
      background: data.background || ''
    })
    
  } catch (error) {
    console.error('加载用户资料失败:', error)
  }
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    loading.value = true
    
    const updateData = { ...profileForm }
    // 移除不需要发送的字段
    delete updateData.avatar
    delete updateData.background
    
    await updateUserProfile(updateData)
    
    // 更新store中的用户信息
    await store.dispatch('user/getInfo')
    
    ElMessage.success('个人资料更新成功')
    router.push('/profile')
    
  } catch (error) {
    console.error('更新个人资料失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/profile')
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped>
.edit-profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.edit-profile-card {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.profile-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.profile-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.profile-form {
  margin-bottom: 20px;
}

.avatar-upload-container,
.background-upload-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview {
  flex-shrink: 0;
}

.background-preview {
  width: 200px;
  height: 100px;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  flex-shrink: 0;
}

.privacy-settings {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.save-button {
  margin-right: 12px;
}
</style>
