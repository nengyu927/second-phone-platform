<script setup>
import { computed,onMounted,reactive,ref } from 'vue'
import { getMembers } from '../api/memberApi'
import { getProducts } from '../api/productApi'
import { createOrder,deleteOrder,getOrders,updateOrder } from '../api/orderApi'

const orders=ref([]),members=ref([]),products=ref([]),loading=ref(false),saving=ref(false),errorMessage=ref(''),editingId=ref(null)
const filters=reactive({memberId:'',productId:'',orderStatus:''})
const empty=()=>({memberId:'',productId:'',quantity:1,unitPrice:0,recipientName:'',recipientPhone:'',shippingAddress:'',paymentMethod:'CASH_ON_DELIVERY',orderStatus:'PENDING',note:''})
const form=reactive(empty())
const estimatedTotal=computed(()=>Number(form.unitPrice||0)*Number(form.quantity||0))
const params=()=>Object.fromEntries(Object.entries(filters).filter(([,v])=>v!==''))
async function loadReferenceData(){[members.value,products.value]=await Promise.all([getMembers(),getProducts()])}
async function loadOrders(){loading.value=true;errorMessage.value='';try{orders.value=await getOrders(params())}catch(e){errorMessage.value=e.message}finally{loading.value=false}}
function productChanged(){const product=products.value.find(p=>p.id===Number(form.productId));if(product)form.unitPrice=Number(product.price)}
function editOrder(o){editingId.value=o.id;Object.assign(form,{memberId:o.memberId,productId:o.productId,quantity:o.quantity,unitPrice:o.unitPrice,recipientName:o.recipientName,recipientPhone:o.recipientPhone,shippingAddress:o.shippingAddress,paymentMethod:o.paymentMethod,orderStatus:o.orderStatus,note:o.note||''});window.scrollTo({top:0,behavior:'smooth'})}
function cancelEdit(){editingId.value=null;Object.assign(form,empty());errorMessage.value=''}
async function submit(){saving.value=true;errorMessage.value='';try{const payload={...form,memberId:Number(form.memberId),productId:Number(form.productId),quantity:Number(form.quantity),unitPrice:Number(form.unitPrice)};if(editingId.value)await updateOrder(editingId.value,payload);else await createOrder(payload);cancelEdit();await loadOrders()}catch(e){errorMessage.value=e.message}finally{saving.value=false}}
async function remove(o){if(!confirm(`確定刪除訂單 #${o.id}？`))return;try{await deleteOrder(o.id);await loadOrders()}catch(e){errorMessage.value=e.message}}
function clearFilters(){Object.assign(filters,{memberId:'',productId:'',orderStatus:''});loadOrders()}
const money=v=>new Intl.NumberFormat('zh-TW',{style:'currency',currency:'TWD'}).format(v||0)
const date=v=>v?new Date(v).toLocaleString('zh-TW'):'-'
onMounted(async()=>{try{await loadReferenceData();await loadOrders()}catch(e){errorMessage.value=e.message}})
</script>

<template><section class="management-page">
  <div class="page-heading"><div><h2>訂單管理</h2><p>管理單一商品訂單；總金額由後端重新計算。</p></div></div>
  <p v-if="errorMessage" class="alert error">{{ errorMessage }}</p>
  <form class="card" @submit.prevent="submit"><h3>{{ editingId?`編輯訂單 #${editingId}`:'新增訂單' }}</h3>
    <div class="form-grid">
      <label>會員<select v-model="form.memberId" required><option value="" disabled>請選擇</option><option v-for="m in members" :key="m.id" :value="m.id">{{ m.id }}－{{ m.account }}－{{ m.name }}</option></select></label>
      <label>商品<select v-model="form.productId" required @change="productChanged"><option value="" disabled>請選擇</option><option v-for="p in products" :key="p.id" :value="p.id">{{ p.id }}－{{ p.productName }}－{{ money(p.price) }}</option></select></label>
      <label>數量<input v-model.number="form.quantity" type="number" min="1" required /></label><label>商品單價<input v-model.number="form.unitPrice" type="number" min="0" step="0.01" required /></label>
      <label>收件人<input v-model.trim="form.recipientName" maxlength="100" required /></label><label>電話<input v-model.trim="form.recipientPhone" maxlength="20" required /></label>
      <label class="full-width">收件地址<input v-model.trim="form.shippingAddress" maxlength="300" required /></label>
      <label>付款方式<select v-model="form.paymentMethod"><option>CASH_ON_DELIVERY</option><option>BANK_TRANSFER</option><option>CREDIT_CARD</option></select></label>
      <label>訂單狀態<select v-model="form.orderStatus"><option>PENDING</option><option>CONFIRMED</option><option>SHIPPED</option><option>COMPLETED</option><option>CANCELLED</option></select></label>
      <label class="full-width">備註<textarea v-model.trim="form.note" rows="3"></textarea></label>
    </div><p class="summary-box">預估總金額：{{ money(estimatedTotal) }}（實際金額由後端計算）</p>
    <div class="form-actions"><button class="primary" :disabled="saving">{{ saving?'儲存中...':editingId?'儲存修改':'新增訂單' }}</button><button v-if="editingId" class="secondary" type="button" @click="cancelEdit">取消編輯</button></div>
  </form>
  <form class="card search-bar" @submit.prevent="loadOrders"><label>會員<select v-model="filters.memberId"><option value="">全部</option><option v-for="m in members" :key="m.id" :value="m.id">{{ m.id }}－{{ m.name }}</option></select></label><label>商品<select v-model="filters.productId"><option value="">全部</option><option v-for="p in products" :key="p.id" :value="p.id">{{ p.id }}－{{ p.productName }}</option></select></label><label>狀態<select v-model="filters.orderStatus"><option value="">全部</option><option>PENDING</option><option>CONFIRMED</option><option>SHIPPED</option><option>COMPLETED</option><option>CANCELLED</option></select></label><button class="primary">搜尋</button><button class="secondary" type="button" @click="clearFilters">清除</button></form>
  <div class="card"><h3>訂單列表</h3><p v-if="loading">載入中...</p><p v-else-if="!orders.length">目前沒有訂單。</p><div v-else class="table-scroll"><table><thead><tr><th>ID</th><th>會員</th><th>商品</th><th>數量</th><th>單價</th><th>總金額</th><th>收件人</th><th>電話</th><th>狀態</th><th>建立時間</th><th>操作</th></tr></thead><tbody><tr v-for="o in orders" :key="o.id"><td>{{o.id}}</td><td>{{o.memberId}}</td><td>{{o.productId}}</td><td>{{o.quantity}}</td><td>{{money(o.unitPrice)}}</td><td>{{money(o.totalAmount)}}</td><td>{{o.recipientName}}</td><td>{{o.recipientPhone}}</td><td><span class="badge">{{o.orderStatus}}</span></td><td>{{date(o.createdAt)}}</td><td class="row-actions"><button class="secondary small" @click="editOrder(o)">編輯</button><button class="danger small" @click="remove(o)">刪除</button></td></tr></tbody></table></div></div>
</section></template>
