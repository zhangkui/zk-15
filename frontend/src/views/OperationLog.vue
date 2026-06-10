<template>
  <div class="page-container">
    <el-card shadow="hover">
      <div class="page-header">
        <div class="header-title">操作日志</div>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="业务编号/操作类型/操作人/详情"
          clearable
          style="width: 280px"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 280px"
        />
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="businessType" label="业务类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeColor(row.businessType)">{{ row.businessType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="businessNo" label="业务编号" width="180" />
        <el-table-column prop="operationType" label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeColor(row.operationType)">
              {{ row.operationType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationDetail" label="操作详情" min-width="300" show-overflow-tooltip />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createdTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        v-model:current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="业务类型">
          <el-tag :type="getBusinessTypeColor(detailData.businessType)">
            {{ detailData.businessType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业务ID">{{ detailData.businessId }}</el-descriptions-item>
        <el-descriptions-item label="业务编号" :span="2">{{ detailData.businessNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeColor(detailData.operationType)">
            {{ detailData.operationType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="操作详情" :span="2">
          <div style="white-space: pre-wrap">{{ detailData.operationDetail || '-' }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { operationLogApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const dateRange = ref([])
const detailData = ref(null)

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getBusinessTypeColor = (type) => {
  const map = {
    '废弃物': '',
    '分类': 'success',
    '委托单': 'warning',
    '费用': 'danger',
    '凭证': 'info'
  }
  return map[type] || ''
}

const getOperationTypeColor = (type) => {
  const map = {
    '创建': 'success',
    '更新': 'warning',
    '删除': 'danger',
    '状态变更': ''
  }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await operationLogApi.list(params)
    if (res.data) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  dateRange.value = []
  pagination.pageNum = 1
  loadData()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadData()
}

const handleView = async (row) => {
  const res = await operationLogApi.getById(row.id)
  if (res.data) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
    }
  }

  .search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    flex-wrap: wrap;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
