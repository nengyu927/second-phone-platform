import { createRouter, createWebHistory } from 'vue-router'
import { pinia } from '../stores'
import { useAuthStore } from '../stores/auth'
import PublicLayout from '../layouts/PublicLayout.vue'
import MemberLayout from '../layouts/MemberLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import HomeView from '../views/HomeView.vue'
import DashboardView from '../views/DashboardView.vue'
import MemberView from '../views/MemberView.vue'
import ProductView from '../views/ProductView.vue'
import OrderView from '../views/OrderView.vue'
import RepairView from '../views/RepairView.vue'
import PlaceholderView from '../views/PlaceholderView.vue'

const placeholder = (title) => ({ component: PlaceholderView, props: { title } })
const routes = [
  { path: '/', component: PublicLayout, children: [
    { path: '', component: HomeView },
    { path: 'products', component: () => import('../views/ProductCatalogView.vue') },
    { path: 'products/:id', component: () => import('../views/ProductDetailView.vue') },
    { path: 'repairs', component: () => import('../views/RepairApplicationView.vue') },
    { path: 'trade-in', component: () => import('../views/TradeInApplicationView.vue') },
    { path: 'about', ...placeholder('關於平台') },
    { path: 'cart', component: () => import('../views/CartView.vue'), meta: { requiresAuth: true } },
    { path: 'checkout', component: () => import('../views/CheckoutView.vue'), meta: { requiresAuth: true } },
    { path: 'checkout/success', component: () => import('../views/CheckoutSuccessView.vue'), meta: { requiresAuth: true } },
    { path: 'login', component: () => import('../views/LoginView.vue'), meta: { guestOnly: true } },
    { path: 'register', component: () => import('../views/RegisterView.vue'), meta: { guestOnly: true } },
    { path: 'forbidden', component: () => import('../views/ForbiddenView.vue') }
  ] },
  { path: '/member', component: MemberLayout, meta: { requiresAuth: true }, children: [
    { path: '', redirect: '/member/profile' },
    { path: 'profile', component: () => import('../views/ProfileView.vue') },
    { path: 'orders', component: () => import('../views/MemberOrdersView.vue') },
    { path: 'orders/:id', component: () => import('../views/MemberOrderDetailView.vue') },
    { path: 'repairs', component: () => import('../views/MemberRepairsView.vue') },
    { path: 'repairs/:id', component: () => import('../views/MemberRepairDetailView.vue') },
    { path: 'trade-ins', component: () => import('../views/MemberTradeInsView.vue') },
    { path: 'trade-ins/:id', component: () => import('../views/MemberTradeInDetailView.vue') }
  ] },
  { path: '/admin', component: AdminLayout, meta: { requiresAuth: true, requiresStaff: true }, children: [
    { path: '', redirect: '/admin/dashboard' },
    { path: 'dashboard', component: DashboardView },
    { path: 'members', component: MemberView },
    { path: 'products', component: ProductView },
    { path: 'brands', component: () => import('../views/BrandView.vue') },
    { path: 'categories', component: () => import('../views/CategoryView.vue') },
    { path: 'orders', component: OrderView },
    { path: 'orders/:id', component: () => import('../views/AdminOrderDetailView.vue') },
    { path: 'repairs', component: RepairView },
    { path: 'trade-ins', component: () => import('../views/TradeInView.vue') },
    { path: 'trade-ins/:id', component: () => import('../views/AdminTradeInDetailView.vue') },
    { path: 'inventory', component: () => import('../views/InventoryView.vue') }
  ] },
  { path: '/dashboard', redirect: '/admin/dashboard' },
  { path: '/members', redirect: '/admin/members' },
  { path: '/orders', redirect: '/admin/orders' },
  { path: '/:pathMatch(.*)*', component: () => import('../views/NotFoundView.vue') }
]

const router = createRouter({ history: createWebHistory(), routes, scrollBehavior: () => ({ top: 0 }) })
let restored = false
router.beforeEach(async (to) => {
  const auth = useAuthStore(pinia)
  if (!restored) { restored = true; await auth.restoreSession() }
  if (to.meta.requiresAuth && !auth.isAuthenticated) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.meta.requiresStaff && !auth.isStaff) return '/forbidden'
  if (to.meta.guestOnly && auth.isAuthenticated) return auth.isStaff ? '/admin' : '/member'
})
window.addEventListener('auth:forbidden', () => { if (router.currentRoute.value.path.startsWith('/admin')) router.push('/forbidden') })
export default router
