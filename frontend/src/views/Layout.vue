<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon size="28" color="#409EFF"><Trash /></el-icon>
        <span>废弃物清运管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#b0b7bf"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>首页统计</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><List /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/waste">
          <el-icon><Box /></el-icon>
          <span>废弃物信息</span>
        </el-menu-item>
        <el-menu-item index="/pickup">
          <el-icon><Truck /></el-icon>
          <span>清运委托</span>
        </el-menu-item>
        <el-menu-item index="/fee">
          <el-icon><Money /></el-icon>
          <span>费用记录</span>
        </el-menu-item>
        <el-menu-item index="/voucher">
          <el-icon><Document /></el-icon>
          <span>处理凭证</span>
        </el-menu-item>
        <el-menu-item index="/log">
          <el-icon><Notebook /></el-icon>
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="breadcrumb">
          <el-icon><Location /></el-icon>
          <span>{{ currentTitle }}</span>
        </div>
        <div class="user-info">
          <el-avatar :size="32" style="background-color: #409EFF">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">管理员</span>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '首页')
</script>

<style scoped lang="scss">
.layout-container {
  height: 100%;
}

.aside {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
  gap: 8px;
}

.menu {
  flex: 1;
  border-right: none;

  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;

    &:hover {
      background-color: #1f2d3d !important;
    }
  }

  :deep(.el-menu-item.is-active) {
    background-color: #409EFF !important;
  }
}

.header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  color: #606266;
  font-size: 14px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
