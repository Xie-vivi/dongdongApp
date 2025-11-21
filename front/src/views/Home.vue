<template>
  <div class="home-container">
    <div class="home-header">
      <h1>XYST社交平台</h1>
      <p>欢迎来到校园社交平台</p>
    </div>
    
    <div class="user-welcome">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <el-avatar
            :size="80"
            :src="userInfo.avatar || defaultAvatar"
          />
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo.nickname || userInfo.username }}！</h2>
            <p>开始探索校园生活，认识新朋友吧</p>
          </div>
        </div>
        
        <div class="quick-actions">
          <el-button type="primary" size="large" @click="goToProfile">
            <el-icon><User /></el-icon>
            个人资料
          </el-button>
          <el-button type="success" size="large" @click="goToSearch">
            <el-icon><Search /></el-icon>
            搜索用户
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div class="feature-grid">
      <el-card class="feature-card">
        <div class="feature-icon">
          <el-icon size="48" color="#409eff"><Document /></el-icon>
        </div>
        <h3>发布帖子</h3>
        <p>分享你的想法和经历</p>
        <el-button type="primary" plain>功能开发中...</el-button>
      </el-card>
      
      <el-card class="feature-card">
        <div class="feature-icon">
          <el-icon size="48" color="#67c23a"><Calendar /></el-icon>
        </div>
        <h3>组织活动</h3>
        <p>创建和参与校园活动</p>
        <el-button type="success" plain>功能开发中...</el-button>
      </el-card>
      
      <el-card class="feature-card">
        <div class="feature-icon">
          <el-icon size="48" color="#e6a23c"><ChatDotRound /></el-icon>
        </div>
        <h3>社交互动</h3>
        <p>关注、点赞、评论互动</p>
        <el-button type="warning" plain>功能开发中...</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { User, Search, Document, Calendar, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userInfo = computed(() => store.getters['user/userInfo'] || {})

const goToProfile = () => {
  router.push('/profile')
}

const goToSearch = () => {
  router.push('/search')
}

onMounted(() => {
  // 如果没有用户信息，尝试获取
  if (!userInfo.value.id) {
    store.dispatch('user/getInfo').catch(error => {
      console.error('获取用户信息失败:', error)
    })
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.home-header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.home-header h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
}

.home-header p {
  font-size: 20px;
  opacity: 0.9;
  margin: 0;
}

.user-welcome {
  max-width: 800px;
  margin: 0 auto 40px;
}

.welcome-card {
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.welcome-text h2 {
  color: #333;
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-text p {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.quick-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.feature-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.feature-card {
  text-align: center;
  padding: 40px 24px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
}

.feature-icon {
  margin-bottom: 20px;
}

.feature-card h3 {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.feature-card p {
  color: #666;
  font-size: 14px;
  margin: 0 0 20px 0;
  line-height: 1.5;
}
</style>
