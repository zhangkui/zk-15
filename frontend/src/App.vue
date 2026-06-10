<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 16px; font-weight: bold">
        废弃物清运管理
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/waste-item">
          <el-icon><el-icon-document /></el-icon>
          <span>废弃物登记</span>
        </el-menu-item>
        <el-menu-item index="/cleaning-order">
          <el-icon><el-icon-list /></el-icon>
          <span>清运委托</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><el-icon-menu /></el-icon>
          <span>物品分类</span>
        </el-menu-item>
        <el-menu-item index="/fee">
          <el-icon><el-icon-coin /></el-icon>
          <span>清运费用</span>
        </el-menu-item>
        <el-menu-item index="/certificate">
          <el-icon><el-icon-upload /></el-icon>
          <span>处理凭证</span>
        </el-menu-item>
        <el-menu-item index="/archive">
          <el-icon><el-icon-folder /></el-icon>
          <span>留档管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/waste-item')) return '/waste-item'
  if (path.startsWith('/cleaning-order')) return '/cleaning-order'
  return path
})
</script>

<style>
body {
  margin: 0;
  padding: 0;
}
.el-aside {
  overflow: hidden;
}
.el-menu {
  border-right: none;
}
</style>
