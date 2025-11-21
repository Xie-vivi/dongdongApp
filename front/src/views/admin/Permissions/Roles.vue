<template>
  <div class="roles">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            添加角色
          </el-button>
        </div>
      </template>
      
      <div class="roles-content">
        <p>角色列表内容</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
const handleAdd = () => {
  console.log('添加角色')
}
</script>

<style scoped>
.roles {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.roles-content {
  padding: 20px;
  min-height: 400px;
}
</style>
