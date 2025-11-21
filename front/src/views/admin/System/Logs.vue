<template>
  <div class="system-logs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="操作日志" name="operation">
          <div class="logs-content">
            <p>操作日志内容</p>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="系统日志" name="system">
          <div class="logs-content">
            <p>系统日志内容</p>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="错误日志" name="error">
          <div class="logs-content">
            <p>错误日志内容</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('operation')

const handleTabChange = (tab) => {
  console.log('切换到标签页:', tab)
}
</script>

<style scoped>
.system-logs {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logs-content {
  padding: 20px;
  min-height: 400px;
}
</style>
