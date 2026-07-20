import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import MemberView from '../views/MemberView.vue'
import ProductView from '../views/ProductView.vue'
import OrderView from '../views/OrderView.vue'
import RepairView from '../views/RepairView.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: DashboardView },
  { path: '/members', component: MemberView },
  { path: '/products', component: ProductView },
  { path: '/orders', component: OrderView },
  { path: '/repairs', component: RepairView },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
