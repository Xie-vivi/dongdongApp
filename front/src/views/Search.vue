<template>
  <div class="search-container">
    <div class="search-header">
      <h2>搜索用户</h2>
      <p>发现志同道合的朋友</p>
    </div>
    
    <div class="search-form">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入用户名、昵称或UID进行搜索"
        size="large"
        class="search-input"
        :prefix-icon="Search"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSearch"
          >
            搜索
          </el-button>
        </template>
      </el-input>
    </div>

    <div class="search-results" v-loading="loading">
      <div v-if="searchResults.length > 0" class="user-list">
        <div
          v-for="user in searchResults"
          :key="user.id"
          class="user-item"
          @click="goToUserProfile(user.id)"
        >
          <el-avatar
            :size="60"
            :src="user.avatar || defaultAvatar"
            class="user-avatar"
          />
          <div class="user-info">
            <div class="user-name">
              <span class="nickname">{{ user.nickname || user.username }}</span>
              <div class="user-badges">
                <el-tag v-if="user.isCertified" type="success" size="small">
                  <el-icon><Check /></el-icon>
                  已认证
                </el-tag>
                <el-tag v-if="user.schoolCertified" type="primary" size="small">
                  <el-icon><School /></el-icon>
                  校园认证
                </el-tag>
              </div>
            </div>
            <p class="username">@{{ user.username }}</p>
            <p class="user-bio">{{ user.bio || '这个人很懒，什么都没有留下~' }}</p>
            <div class="user-meta">
              <span v-if="user.school" class="meta-item">
                <el-icon><School /></el-icon>
                {{ user.school }}
              </span>
              <span v-if="user.location" class="meta-item">
                <el-icon><Location /></el-icon>
                {{ user.location }}
              </span>
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ user.followersCount || 0 }} 关注者
              </span>
            </div>
          </div>
          <div class="user-actions">
            <el-button type="primary" size="small" @click.stop="followUser(user)">
              <el-icon><Plus /></el-icon>
              关注
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-else-if="hasSearched" class="no-results">
        <el-empty description="没有找到相关用户">
          <el-button type="primary" @click="clearSearch">清空搜索</el-button>
        </el-empty>
      </div>
      
      <div v-else class="search-placeholder">
        <el-empty description="输入关键词开始搜索用户">
          <template #image>
            <el-icon size="80" color="#c0c4cc"><Search /></el-icon>
          </template>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Check, School, Location, User, Plus } from '@element-plus/icons-vue'
import { searchUsers } from '@/api/user'

const router = useRouter()

const searchKeyword = ref('')
const loading = ref(false)
const hasSearched = ref(false)
const searchResults = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  try {
    loading.value = true
    const response = await searchUsers({
      keyword: searchKeyword.value.trim(),
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'created_at',
      sortOrder: 'desc'
    })

    searchResults.value = response.data.records || []
    total.value = response.data.total || 0
    hasSearched.value = true

    if (searchResults.value.length === 0) {
      ElMessage.info('没有找到相关用户')
    }

  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('搜索失败，请重试')
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
  searchResults.value = []
  total.value = 0
  currentPage.value = 1
  hasSearched.value = false
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  if (hasSearched.value) {
    handleSearch()
  }
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  if (hasSearched.value) {
    handleSearch()
  }
}

const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
}

const followUser = (user) => {
  // TODO: 实现关注功能
  ElMessage.info(`关注 ${user.nickname || user.username} 功能开发中...`)
}
</script>

<style scoped>
.search-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.search-header {
  text-align: center;
  margin-bottom: 30px;
}

.search-header h2 {
  color: #333;
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 8px;
}

.search-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.search-form {
  max-width: 600px;
  margin: 0 auto 40px;
}

.search-input {
  width: 100%;
}

.search-results {
  max-width: 800px;
  margin: 0 auto;
  min-height: 400px;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.nickname {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.user-badges {
  display: flex;
  gap: 4px;
}

.username {
  color: #666;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.user-bio {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.user-actions {
  flex-shrink: 0;
}

.no-results,
.search-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.pagination-container {
  max-width: 800px;
  margin: 40px auto 0;
  display: flex;
  justify-content: center;
}
</style>
