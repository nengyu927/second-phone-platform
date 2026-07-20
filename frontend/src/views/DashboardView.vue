<script setup>
import { computed,onMounted,ref } from 'vue'
import { getDashboard } from '../api/dashboardApi'

const emptySummary=()=>({memberCount:0,productCount:0,orderCount:0,repairCount:0,pendingOrderCount:0,activeRepairCount:0,completedRepairCount:0,lowStockProductCount:0,totalOrderAmount:0,completedOrderAmount:0})
const emptyRecent=()=>({recentMembers:[],recentProducts:[],recentOrders:[],recentRepairs:[]})
const summary=ref(emptySummary()),recentData=ref(emptyRecent()),loading=ref(false),errorMessage=ref(''),lastUpdated=ref(null)

const mainCards=computed(()=>[
  {title:'會員總數',value:summary.value.memberCount,icon:'👥',to:'/members'},
  {title:'商品總數',value:summary.value.productCount,icon:'📱',to:'/products'},
  {title:'訂單總數',value:summary.value.orderCount,icon:'📦',to:'/orders'},
  {title:'維修單總數',value:summary.value.repairCount,icon:'🔧',to:'/repairs'}
])
const statusCards=computed(()=>[
  {title:'待確認訂單',value:summary.value.pendingOrderCount,label:'待處理'},
  {title:'進行中維修',value:summary.value.activeRepairCount,label:'處理中'},
  {title:'已完成維修',value:summary.value.completedRepairCount,label:'已完成'},
  {title:'低庫存商品',value:summary.value.lowStockProductCount,label:'需補貨',warning:summary.value.lowStockProductCount>0}
])
async function loadDashboard(){loading.value=true;errorMessage.value='';try{const data=await getDashboard();summary.value={...emptySummary(),...(data?.summary||{})};recentData.value={...emptyRecent(),...(data?.recentData||{})};for(const key of Object.keys(emptyRecent()))if(!Array.isArray(recentData.value[key]))recentData.value[key]=[];lastUpdated.value=new Date()}catch(e){errorMessage.value=e.message;summary.value=emptySummary();recentData.value=emptyRecent()}finally{loading.value=false}}
const formatCurrency=value=>new Intl.NumberFormat('zh-TW',{style:'currency',currency:'TWD',maximumFractionDigits:0}).format(Number(value)||0)
const formatDate=value=>value?new Date(value).toLocaleString('zh-TW'):'-'
const orderLabels={PENDING:'待確認',CONFIRMED:'已確認',SHIPPED:'已出貨',COMPLETED:'已完成',CANCELLED:'已取消'}
const repairLabels={RECEIVED:'已收件',INSPECTING:'檢測中',WAITING_PARTS:'等待零件',REPAIRING:'維修中',COMPLETED:'已完成',RETURNED:'已交還',CANCELLED:'已取消'}
onMounted(loadDashboard)
</script>

<template><section class="dashboard-page">
  <div class="page-heading dashboard-heading"><div><h2>後台儀表板</h2><p>二手手機販售與維修整合平台</p><small>最後更新：{{lastUpdated?formatDate(lastUpdated):'尚未更新'}}</small></div><button class="primary" :disabled="loading" @click="loadDashboard">{{loading?'更新中...':'重新整理'}}</button></div>
  <div v-if="loading && !lastUpdated" class="card status-text">載入儀表板資料中...</div>
  <div v-if="errorMessage" class="alert error"><p>{{errorMessage}}</p><button class="danger" @click="loadDashboard">重新載入</button></div>

  <div class="dashboard-grid main-stats"><RouterLink v-for="card in mainCards" :key="card.title" :to="card.to" class="stat-card"><span class="stat-icon">{{card.icon}}</span><div><span>{{card.title}}</span><strong>{{card.value}}</strong></div></RouterLink></div>
  <section><h3>營運狀態</h3><div class="dashboard-grid status-grid"><article v-for="card in statusCards" :key="card.title" class="card status-card" :class="{warning:card.warning}"><span>{{card.title}}</span><strong>{{card.value}}</strong><em>{{card.label}}</em></article></div></section>
  <section class="amount-grid"><article class="card amount-card"><span>訂單總金額</span><strong>{{formatCurrency(summary.totalOrderAmount)}}</strong><small>不包含已取消訂單</small></article><article class="card amount-card"><span>已完成訂單金額</span><strong>{{formatCurrency(summary.completedOrderAmount)}}</strong><small>僅計算 COMPLETED</small></article></section>
  <section><h3>快速操作</h3><div class="quick-actions"><RouterLink class="primary" to="/members">新增會員</RouterLink><RouterLink class="primary" to="/products">新增商品</RouterLink><RouterLink class="primary" to="/orders">新增訂單</RouterLink><RouterLink class="primary" to="/repairs">新增維修單</RouterLink></div></section>

  <section><h3>最近資料</h3><div class="recent-grid">
    <article class="card recent-card"><header><h4>最近會員</h4><RouterLink to="/members">查看全部</RouterLink></header><p v-if="!recentData.recentMembers.length">尚無會員資料</p><ul v-else><li v-for="m in recentData.recentMembers" :key="m.id"><div><strong>#{{m.id}} {{m.account||m.name}}</strong><span>{{m.email||m.phone||'-'}}</span></div><time>{{formatDate(m.createdAt)}}</time></li></ul></article>
    <article class="card recent-card"><header><h4>最近商品</h4><RouterLink to="/products">查看全部</RouterLink></header><p v-if="!recentData.recentProducts.length">尚無商品資料</p><ul v-else><li v-for="p in recentData.recentProducts" :key="p.id"><div><strong>#{{p.id}} {{p.productName}}</strong><span>{{formatCurrency(p.price)}}｜庫存 {{p.stock??0}}</span></div><time>{{formatDate(p.createdAt)}}</time></li></ul></article>
    <article class="card recent-card"><header><h4>最近訂單</h4><RouterLink to="/orders">查看全部</RouterLink></header><p v-if="!recentData.recentOrders.length">尚無訂單資料</p><ul v-else><li v-for="o in recentData.recentOrders" :key="o.id"><div><strong>#{{o.id}}｜會員 {{o.memberId}}</strong><span>{{formatCurrency(o.totalAmount)}}｜{{orderLabels[o.orderStatus]||o.orderStatus}}</span></div><time>{{formatDate(o.createdAt)}}</time></li></ul></article>
    <article class="card recent-card"><header><h4>最近維修</h4><RouterLink to="/repairs">查看全部</RouterLink></header><p v-if="!recentData.recentRepairs.length">尚無維修資料</p><ul v-else><li v-for="r in recentData.recentRepairs" :key="r.id"><div><strong>#{{r.id}}｜會員 {{r.memberId}}</strong><span>{{r.deviceBrand}} {{r.deviceModel}}｜{{repairLabels[r.repairStatus]||r.repairStatus}}</span></div><time>{{formatDate(r.receivedAt||r.createdAt)}}</time></li></ul></article>
  </div></section>
</section></template>
