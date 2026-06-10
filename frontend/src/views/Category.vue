<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in categoryList" :key="item.id" style="margin-bottom: 20px">
        <el-card shadow="hover">
          <div style="text-align: center; padding: 20px 0">
            <el-icon :size="48" style="color: #409eff"><el-icon-document /></el-icon>
            <h3 style="margin: 12px 0 8px">{{ item.name }}</h3>
            <p style="color: #909399; font-size: 14px; margin: 4px 0">编码：{{ item.code }}</p>
            <p style="color: #606266; font-size: 14px; margin: 4px 0">{{ item.description }}</p>
          </div>
          <div style="text-align: center">
            <el-button size="small" type="primary" @click="openDialog(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8" style="margin-bottom: 20px">
        <el-card shadow="hover" style="cursor: pointer; text-align: center; padding: 20px 0; min-height: 200px; display: flex; align-items: center; justify-content: center" @click="openDialog(null)">
          <div>
            <el-icon :size="48" style="color: #c0c4cc"><el-icon-plus /></el-icon>
            <p style="color: #909399; margin-top: 12px">新增分类</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCategory, addCategory, updateCategory, deleteCategory } from '../api/category'

const categoryList = ref([])
const dialogVisible = ref(false)
const form = ref({})

const loadData = async () => {
  const res = await listCategory()
  categoryList.value = res.data || []
}

const openDialog = (item) => {
  if (item) {
    form.value = { ...item }
  } else {
    form.value = { name: '', code: '', description: '' }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await updateCategory(form.value)
  } else {
    await addCategory(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (item) => {
  ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' }).then(async () => {
    await deleteCategory(item.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  loadData()
})
</script>
