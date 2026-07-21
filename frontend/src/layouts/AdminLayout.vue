<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore(), route = useRoute(), router = useRouter()
const collapsed = ref(false), mobileOpen = ref(false)
const titles = { dashboard: '營運總覽', members: '會員管理', products: '商品管理', brands: '品牌管理', categories: '分類管理', inventory: '庫存管理', orders: '訂單管理', repairs: '維修管理', 'trade-ins': '收購管理' }
const pageTitle = computed(() => titles[route.path.split('/')[2]] || '管理後台')
function logout() { auth.logout(); router.push('/') }
</script>
<template>
  <div :class="['portal-shell', 'admin-shell', { collapsed }]">
    <aside :class="['sidebar', { 'mobile-open': mobileOpen }]">
      <div class="sidebar-brand"><RouterLink class="brand brand-inverse" to="/admin"><span class="brand-mark">S</span><span v-if="!collapsed">Second Admin</span></RouterLink><button class="sidebar-close" @click="mobileOpen=false">×</button></div>
      <nav aria-label="後台導覽">
        <RouterLink to="/admin/dashboard"><span>◫</span><b>Dashboard</b></RouterLink><RouterLink to="/admin/members"><span>◎</span><b>會員管理</b></RouterLink>
        <RouterLink to="/admin/products"><span>▣</span><b>商品管理</b></RouterLink><RouterLink to="/admin/brands"><span>◇</span><b>品牌管理</b></RouterLink>
        <RouterLink to="/admin/categories"><span>▤</span><b>分類管理</b></RouterLink><RouterLink to="/admin/inventory"><span>▥</span><b>庫存管理</b></RouterLink>
        <RouterLink to="/admin/orders"><span>▧</span><b>訂單管理</b></RouterLink><RouterLink to="/admin/repairs"><span>⌁</span><b>維修管理</b></RouterLink>
        <RouterLink to="/admin/trade-ins"><span>↺</span><b>收購管理</b></RouterLink>
      </nav>
      <button class="sidebar-collapse" type="button" @click="collapsed=!collapsed"><span>{{ collapsed ? '→' : '←' }}</span><b>收合側邊欄</b></button>
    </aside>
    <div class="portal-content">
      <header class="toolbar"><button class="mobile-sidebar-toggle" @click="mobileOpen=true">☰</button><div><div class="breadcrumb">管理後台 ／ {{ pageTitle }}</div><strong>{{ pageTitle }}</strong></div><div class="toolbar-user"><span><b>{{ auth.currentUser?.name }}</b><small>{{ auth.currentUser?.role }}</small></span><button class="button button-ghost button-small" @click="logout">登出</button></div></header>
      <main><RouterView /></main>
    </div>
  </div>
</template>
