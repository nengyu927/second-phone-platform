import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PlaceholderView from '../views/PlaceholderView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/members', component: PlaceholderView, props: { title: '會員管理' } },
  { path: '/products', component: PlaceholderView, props: { title: '商品管理' } },
  { path: '/orders', component: PlaceholderView, props: { title: '訂單管理' } },
  { path: '/repairs', component: PlaceholderView, props: { title: '維修管理' } },
  { path: '/admin', component: PlaceholderView, props: { title: '後台管理' } }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
