<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card stat-card-blue" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalWasteCount || 0 }}</div>
              <div class="stat-label">废弃物总数</div>
            </div>
            <el-icon class="stat-icon"><Box /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-card-orange" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.pickingCount || 0 }}</div>
              <div class="stat-label">清运中</div>
            </div>
            <el-icon class="stat-icon"><Truck /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-card-green" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.completedCount || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
            <el-icon class="stat-icon"><CircleCheck /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-card-purple" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">￥{{ stats.thisMonthFeeTotal || '0.00' }}</div>
              <div class="stat-label">本月收入</div>
            </div>
            <el-icon class="stat-icon"><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月委托清运</span>
              <el-tag type="primary">{{ stats.thisMonthOrderCount || 0 }} 单</el-tag>
            </div>
          </template>
          <el-empty v-if="!stats.thisMonthOrderCount" description="暂无数据" />
          <div v-else class="order-stat">
            <el-progress type="dashboard" :percentage="Math.min((stats.thisMonthOrderCount / 50) * 100, 100)" :color="colors">
              <template #default="{ percentage }">
                <span class="dashboard-text">{{ stats.thisMonthOrderCount }}</span>
              </template>
            </el-progress>
            <div class="order-stat-label">本月完成率目标 50 单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>废弃物分类统计</span>
            </div>
          </template>
          <div ref="chartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>各分类废弃物数量</span>
            </div>
          </template>
          <el-table :data="categoryList" stripe>
            <el-table-column prop="categoryName" label="分类名称" width="180" />
            <el-table-column prop="categoryCode" label="分类编码" width="150" />
            <el-table-column prop="count" label="数量">
              <template #default="{ row }">
                <el-tag :type="row.count > 0 ? 'success' : 'info'">{{ row.count || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="占比">
              <template #default="{ row }">
                <el-progress
                  :percentage="totalCount > 0 ? Math.round((row.count / totalCount) * 100) : 0"
                  :stroke-width="12"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { dashboardApi } from '@/api'

const stats = reactive({
  totalWasteCount: 0,
  pickingCount: 0,
  completedCount: 0,
  thisMonthOrderCount: 0,
  thisMonthFeeTotal: '0.00'
})

const categoryList = ref([])
const chartRef = ref(null)
let chartInstance = null

const colors = ['#409EFF', '#67C23A']

const totalCount = computed(() => {
  return categoryList.value.reduce((sum, item) => sum + (item.count || 0), 0)
})

const loadStats = async () => {
  const res = await dashboardApi.stats()
  if (res.data) {
    Object.assign(stats, res.data)
    if (res.data.categoryWasteCount) {
      categoryList.value = res.data.categoryWasteCount
    }
  }
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: '0%',
      left: 'center'
    },
    series: [
      {
        name: '废弃物数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categoryList.value
          .filter(item => item.count > 0)
          .map(item => ({
            value: item.count,
            name: item.categoryName
          }))
      }
    ]
  }
  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(async () => {
  await loadStats()
  await nextTick()
  initChart()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-cards {
    margin-bottom: 20px;
  }

  .stat-card {
    border-radius: 8px;
    border: none;

    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  .stat-card-blue {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }

  .stat-card-orange {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: #fff;
  }

  .stat-card-green {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: #fff;
  }

  .stat-card-purple {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
    color: #fff;
  }

  .stat-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.9;
    }
  }

  .stat-icon {
    font-size: 48px;
    opacity: 0.3;
  }

  .charts {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-weight: bold;
    }
  }

  .chart {
    height: 320px;
  }

  .order-stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0;

    .dashboard-text {
      font-size: 28px;
      font-weight: bold;
      color: #409EFF;
    }

    .order-stat-label {
      margin-top: 16px;
      color: #909399;
      font-size: 14px;
    }
  }
}
</style>
