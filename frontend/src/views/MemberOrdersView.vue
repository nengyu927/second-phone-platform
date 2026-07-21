<script setup>
import { onMounted, ref } from 'vue'
import { getMyOrders } from '../api/orderApi'
const orders=ref([]),loading=ref(true),error=ref('')
const money=v=>new Intl.NumberFormat('zh-TW',{style:'currency',currency:'TWD',maximumFractionDigits:0}).format(v||0)
const labels={PENDING:'待確認',CONFIRMED:'已確認',PROCESSING:'處理中',COMPLETED:'已完成',CANCELLED:'已取消'}
onMounted(async()=>{try{orders.value=await getMyOrders()}catch(e){error.value=e.message}finally{loading.value=false}})
</script>
<template><section class="member-section"><div class="page-heading"><div><span class="eyebrow">MY ORDERS</span><h1>我的訂單</h1><p>查看訂單明細、付款與配送進度。</p></div></div><p v-if="error" class="alert alert-error">{{ error }}</p><div v-if="loading" class="loading-state">正在載入訂單…</div><div v-else-if="!orders.length" class="empty-state"><h2>目前沒有訂單</h2><p>完成選購後，訂單會顯示在這裡。</p><RouterLink class="button button-primary" to="/products">前往選購</RouterLink></div><div v-else class="order-list"><article v-for="order in orders" :key="order.id" class="order-card card"><div><span class="muted">{{ new Date(order.createdAt).toLocaleString('zh-TW') }}</span><h2>{{ order.orderNumber }}</h2><p>{{ order.items?.length || 0 }} 項商品</p></div><div class="order-card-meta"><span class="badge" :class="`status-${order.orderStatus?.toLowerCase()}`">{{ labels[order.orderStatus] || order.orderStatus }}</span><strong>{{ money(order.totalAmount) }}</strong><RouterLink class="button button-secondary" :to="`/member/orders/${order.id}`">查看明細</RouterLink></div></article></div></section></template>
