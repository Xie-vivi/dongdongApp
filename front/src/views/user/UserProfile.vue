<template>
  <div class="user-profile-container">
    <div class="user-profile-card" v-loading="loading">
      <!-- 用户头部信息 -->
      <div class="profile-header">
        <div class="cover-image" :style="{ backgroundImage: `url(${userInfo.background || defaultBackground})` }">
          <div class="cover-overlay">
            <el-avatar
              :size="120"
              :src="userInfo.avatar || defaultAvatar"
              class="profile-avatar"
            />
            <div class="profile-info">
              <h2 class="nickname">{{ userInfo.nickname || userInfo.username }}</h2>
              <p class="username">@{{ userInfo.username }}</p>
              <div class="user-badges">
                <el-tag v-if="userInfo.isCertified" type="success" size="small">
                  <el-icon><Check /></el-icon>
                  已认证
                </el-tag>
                <el-tag v-if="userInfo.schoolCertified" type="primary" size="small">
                  <el-icon><School /></el-icon>
                  校园认证
                </el-tag>
              </div>
            </div>
            <div class="profile-actions">
              <el-button type="primary" @click="followUser" :loading="followLoading">
                <el-icon><Plus /></el-icon>
                关注
              </el-button>
              <el-button @click="sendMessage">
                <el-icon><ChatDotRound /></el-icon>
                私信
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户详细信息 -->
      <div class="profile-content">
        <el-row :gutter="24">
          <el-col :span="16">
            <!-- 基本信息 -->
            <div class="info-section">
              <h3>基本信息</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
                <el-descriptions-item label="昵称">{{ userInfo.nickname || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="UID">{{ userInfo.uid || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="性别">
                  {{ formatGender(userInfo.gender) }}
                </el-descriptions-item>
                <el-descriptions-item label="生日">{{ userInfo.birthday || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="年龄">
                  {{ userInfo.showAge ? (userInfo.age || '未设置') : '隐藏' }}
                </el-descriptions-item>
                <el-descriptions-item label="学校">{{ userInfo.school || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="位置">{{ userInfo.location || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="MBTI">
                  {{ userInfo.showMbti ? (userInfo.mbti || '未设置') : '隐藏' }}
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">{{ formatDate(userInfo.createdAt) }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 个人简介 -->
            <div class="info-section">
              <h3>个人简介</h3>
              <div class="bio-content">
                {{ userInfo.bio || '这个人很懒，什么都没有留下~' }}
              </div>
            </div>

            <!-- 选项卡 -->
            <el-tabs v-model="activeTab" class="user-tabs">
              <el-tab-pane label="帖子" name="posts">
                <div class="tab-content">
                  <el-empty description="暂无帖子" />
                </div>
              </el-tab-pane>
              <el-tab-pane label="活动" name="activities">
                <div class="tab-content">
                  <el-empty description="暂无活动" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-col>

          <el-col :span="8">
            <!-- 统计信息 -->
            <div class="stats-section">
              <h3>统计信息</h3>
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-number">{{ userInfo.followersCount || 0 }}</div>
                  <div class="stat-label">关注者</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ userInfo.followingCount || 0 }}</div>
                  <div class="stat-label">关注中</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ userInfo.fieldCount || 0 }}</div>
                  <div class="stat-label">圈子</div>
                </div>
              </div>
            </div>

            <!-- 相似用户 -->
            <div class="similar-users-section">
              <h3>相似用户</h3>
              <div class="similar-users">
                <el-empty description="暂无推荐" />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, School, Plus, ChatDotRound } from '@element-plus/icons-vue'
import { getUserById } from '@/api/user'

const route = useRoute()
const router = useRouter()

const userInfo = ref({})
const loading = ref(false)
const followLoading = ref(false)
const activeTab = ref('posts')

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const defaultBackground = 'https://picsum.photos/seed/default-bg/800/200.jpg'

const formatGender = (gender) => {
  const genderMap = {
    male: '男',
    female: '女',
    other: '其他'
  }
  return genderMap[gender] || '未设置'
}

const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const loadUserProfile = async () => {
  try {
    loading.value = true
    const userId = route.params.id
    const response = await getUserById(userId)
    userInfo.value = response.data
  } catch (error) {
    console.error('加载用户资料失败:', error)
    ElMessage.error('用户不存在或加载失败')
    router.push('/home')
  } finally {
    loading.value = false
  }
}

const followUser = () => {
  // TODO: 实现关注功能
  ElMessage.info('关注功能开发中...')
}

const sendMessage = () => {
  // TODO: 实现私信功能
  ElMessage.info('私信功能开发中...')
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped>
.user-profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.user-profile-card {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-header {
  position: relative;
}

.cover-image {
  height: 300px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  padding: 40px;
  display: flex;
  align-items: flex-end;
  gap: 24px;
}

.profile-avatar {
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.profile-info {
  flex: 1;
  color: white;
}

.nickname {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.username {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 12px 0;
}

.user-badges {
  display: flex;
  gap: 8px;
}

.profile-actions {
  display: flex;
  gap: 12px;
}

.profile-content {
  padding: 40px;
}

.info-section {
  margin-bottom: 32px;
}

.info-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.bio-content {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  line-height: 1.6;
  color: #666;
  min-height: 80px;
}

.user-tabs {
  margin-bottom: 32px;
}

.tab-content {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.similar-users-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.similar-users {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
