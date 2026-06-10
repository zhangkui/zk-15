<template>
  <div>
    <el-card>
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :span="6">
          <el-input v-model="query.name" placeholder="按名称搜索" clearable @clear="loadData" @keyup.enter="loadData" />
        </el-col>
        <el-col :span="6">
          <el-select v-model="query.categoryId" placeholder="按分类筛选" clearable @change="loadData" style="width: 100%">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="query.status" placeholder="按状态筛选" clearable @change="loadData" style="width: 100%">
            <el-option label="待处理" value="待处理" />
            <el-option label="已委托" value="已委托" />
            <el-option label="清运中" value="清运中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openDialog(null)">新增</el-button>
        </el-col>
      </el-row>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="estimatedWeight" label="预估重量(kg)" width="120" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleDelegate(row)" :disabled="row.status !== '待处理'">委托</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑废弃物' : '新增废弃物'" width="600px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="预估重量(kg)">
          <el-input-number v-model="form.estimatedWeight" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewVisible" title="废弃物详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="名称">{{ viewData.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ viewData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ viewData.quantity }}</el-descriptions-item>
        <el-descriptions-item label="预估重量(kg)">{{ viewData.estimatedWeight }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ viewData.address }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ viewData.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(viewData.status)">{{ viewData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWasteItem, getWasteItem, addWasteItem, updateWasteItem, deleteWasteItem } from '../api/wasteItem'
import { listCategory } from '../api/category'

const router = useRouter()

const query = ref({ name: '', categoryId: '', status: '', pageNum: 1, pageSize: 10 })
const tableData = ref([])
const total = ref(0)
const categoryList = ref([])

const dialogVisible = ref(false)
const form = ref({})
const viewVisible = ref(false)
const viewData = ref({})

const statusTagType = (status) => {
  if (status === '待处理') return 'info'
  if (status === '已委托') return 'warning'
  if (status === '清运中') return ''
  if (status === '已完成') return 'success'
  return ''
}

const loadData = async () => {
  const params = { pageNum: query.value.pageNum, pageSize: query.value.pageSize, name: query.value.name, categoryId: query.value.categoryId, status: query.value.status }
  const res = await listWasteItem(params)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadCategory = async () => {
  const res = await listCategory()
  categoryList.value = res.data || []
}

const openDialog = (row) => {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { name: '', categoryId: '', quantity: 1, estimatedWeight: 0, address: '', contactName: '', contactPhone: '', remark: '', status: '待处理' }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await updateWasteItem(form.value)
  } else {
    await addWasteItem(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}

const handleView = async (row) => {
  const res = await getWasteItem(row.id)
  viewData.value = res.data
  viewVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该废弃物记录？', '提示', { type: 'warning' }).then(async () => {
    await deleteWasteItem(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleDelegate = (row) => {
  router.push({ path: '/cleaning-order/add', query: { wasteItemId: row.id } })
}

onMounted(() => {
  loadData()
  loadCategory()
})
</script>
