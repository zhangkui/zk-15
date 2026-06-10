<template>
  <div>
    <el-card>
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :span="6">
          <el-input v-model="query.orderNo" placeholder="按单号搜索" clearable @clear="loadData" @keyup.enter="loadData" />
        </el-col>
        <el-col :span="6">
          <el-select v-model="query.status" placeholder="按状态筛选" clearable @change="loadData" style="width: 100%">
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="清运中" value="清运中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button type="success" @click="openAddDialog">新增委托</el-button>
        </el-col>
      </el-row>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="orderNo" label="委托单号" width="160" />
        <el-table-column prop="wasteItemName" label="废弃物名称" />
        <el-table-column prop="serviceType" label="服务类型" width="120" />
        <el-table-column prop="appointmentTime" label="预约时间" width="160" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">查看详情</el-button>
            <el-button size="small" type="primary" @click="handleStatus(row, '已确认')" :disabled="row.status !== '待确认'">确认</el-button>
            <el-button size="small" type="success" @click="handleStatus(row, '已完成')" :disabled="row.status !== '清运中'">完成</el-button>
            <el-button size="small" type="danger" @click="handleStatus(row, '已取消')" :disabled="row.status === '已完成' || row.status === '已取消'">取消</el-button>
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

    <el-dialog v-model="addDialogVisible" title="新增清运委托" width="600px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="废弃物">
          <el-select v-model="form.wasteItemId" style="width: 100%" placeholder="选择废弃物">
            <el-option v-for="w in wasteItemList" :key="w.id" :label="w.name" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType" style="width: 100%">
            <el-option label="普通清运" value="普通清运" />
            <el-option label="加急清运" value="加急清运" />
            <el-option label="大件清运" value="大件清运" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="form.appointmentTime" type="datetime" placeholder="选择预约时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="清运地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="委托详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="委托单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="废弃物名称">{{ detailData.wasteItemName }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ detailData.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ detailData.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="清运地址" :span="2">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailData.status)">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listCleaningOrder, getCleaningOrder, addCleaningOrder, updateCleaningOrderStatus } from '../api/cleaningOrder'
import { listAllWasteItem } from '../api/wasteItem'

const route = useRoute()

const query = ref({ orderNo: '', status: '', pageNum: 1, pageSize: 10 })
const tableData = ref([])
const total = ref(0)

const addDialogVisible = ref(false)
const form = ref({})
const wasteItemList = ref([])

const detailVisible = ref(false)
const detailData = ref({})

const statusTagType = (status) => {
  if (status === '待确认') return 'info'
  if (status === '已确认') return 'warning'
  if (status === '清运中') return ''
  if (status === '已完成') return 'success'
  if (status === '已取消') return 'danger'
  return ''
}

const loadData = async () => {
  const params = { pageNum: query.value.pageNum, pageSize: query.value.pageSize, orderNo: query.value.orderNo, status: query.value.status }
  const res = await listCleaningOrder(params)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadWasteItems = async () => {
  const res = await listAllWasteItem()
  wasteItemList.value = res.data || []
}

const openAddDialog = () => {
  form.value = { wasteItemId: '', serviceType: '普通清运', appointmentTime: '', address: '', remark: '' }
  addDialogVisible.value = true
}

const handleAdd = async () => {
  await addCleaningOrder(form.value)
  ElMessage.success('新增成功')
  addDialogVisible.value = false
  loadData()
}

const handleDetail = async (row) => {
  const res = await getCleaningOrder(row.id)
  detailData.value = res.data
  detailVisible.value = true
}

const handleStatus = async (row, status) => {
  await updateCleaningOrderStatus(row.id, status)
  ElMessage.success('状态更新成功')
  loadData()
}

onMounted(() => {
  loadData()
  loadWasteItems()
  if (route.query.wasteItemId) {
    form.value.wasteItemId = Number(route.query.wasteItemId)
    addDialogVisible.value = true
  }
})
</script>
