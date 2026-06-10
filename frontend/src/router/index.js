import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页统计', icon: 'DataLine' }
      },
      {
        path: 'category',
        name: 'WasteCategory',
        component: () => import('@/views/WasteCategory.vue'),
        meta: { title: '分类管理', icon: 'List' }
      },
      {
        path: 'waste',
        name: 'WasteInfo',
        component: () => import('@/views/WasteInfo.vue'),
        meta: { title: '废弃物信息', icon: 'Box' }
      },
      {
        path: 'pickup',
        name: 'PickupOrder',
        component: () => import('@/views/PickupOrder.vue'),
        meta: { title: '清运委托', icon: 'Truck' }
      },
      {
        path: 'fee',
        name: 'FeeRecord',
        component: () => import('@/views/FeeRecord.vue'),
        meta: { title: '费用记录', icon: 'Money' }
      },
      {
        path: 'voucher',
        name: 'DisposalVoucher',
        component: () => import('@/views/DisposalVoucher.vue'),
        meta: { title: '处理凭证', icon: 'Document' }
      },
      {
        path: 'log',
        name: 'OperationLog',
        component: () => import('@/views/OperationLog.vue'),
        meta: { title: '操作日志', icon: 'Notebook' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
