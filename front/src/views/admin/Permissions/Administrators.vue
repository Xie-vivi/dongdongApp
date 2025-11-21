<template>
  <div class="administrators">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="handleAdd">
            添加管理员
          </el-button>
        </div>
      </template>
      
      <div class="administrators-content">
        <p>管理员列表内容</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
const handleAdd = () => {
  console.log('添加管理员')
}
</script>

<style scoped>
.administrators {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.administrators-content {
  padding: 20px;
  min-height: 400px;
}
</style>
