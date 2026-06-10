import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/waste-item' },
  { path: '/waste-item', component: () => import('../views/WasteItem.vue') },
  { path: '/waste-item/add', component: () => import('../views/WasteItem.vue') },
  { path: '/waste-item/edit/:id', component: () => import('../views/WasteItem.vue') },
  { path: '/cleaning-order', component: () => import('../views/CleaningOrder.vue') },
  { path: '/cleaning-order/add', component: () => import('../views/CleaningOrder.vue') },
  { path: '/cleaning-order/detail/:id', component: () => import('../views/CleaningOrder.vue') },
  { path: '/category', component: () => import('../views/Category.vue') },
  { path: '/fee', component: () => import('../views/Fee.vue') },
  { path: '/certificate', component: () => import('../views/Certificate.vue') },
  { path: '/archive', component: () => import('../views/Archive.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
