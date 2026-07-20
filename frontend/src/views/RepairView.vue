<script setup>
import { onMounted,reactive,ref } from 'vue'
import { getMembers } from '../api/memberApi'
import { createRepair,deleteRepair,getRepairs,updateRepair } from '../api/repairApi'
const repairs=ref([]),members=ref([]),loading=ref(false),saving=ref(false),errorMessage=ref(''),editingId=ref(null)
const filters=reactive({memberId:'',repairStatus:'',deviceBrand:''})
const empty=()=>({memberId:'',deviceBrand:'',deviceModel:'',imei:'',problemDescription:'',repairStatus:'RECEIVED',estimatedCost:null,finalCost:null,technicianNote:''})
const form=reactive(empty())
const params=()=>Object.fromEntries(Object.entries(filters).filter(([,v])=>v!==''))
async function loadRepairs(){loading.value=true;errorMessage.value='';try{repairs.value=await getRepairs(params())}catch(e){errorMessage.value=e.message}finally{loading.value=false}}
function editRepair(r){editingId.value=r.id;Object.assign(form,{memberId:r.memberId,deviceBrand:r.deviceBrand,deviceModel:r.deviceModel,imei:r.imei||'',problemDescription:r.problemDescription,repairStatus:r.repairStatus,estimatedCost:r.estimatedCost,finalCost:r.finalCost,technicianNote:r.technicianNote||''});scrollTo({top:0,behavior:'smooth'})}
function cancelEdit(){editingId.value=null;Object.assign(form,empty());errorMessage.value=''}
async function submit(){saving.value=true;errorMessage.value='';try{const payload={...form,memberId:Number(form.memberId),estimatedCost:form.estimatedCost===''||form.estimatedCost===null?null:Number(form.estimatedCost),finalCost:form.finalCost===''||form.finalCost===null?null:Number(form.finalCost)};if(editingId.value)await updateRepair(editingId.value,payload);else await createRepair(payload);cancelEdit();await loadRepairs()}catch(e){errorMessage.value=e.message}finally{saving.value=false}}
async function remove(r){if(!confirm(`確定刪除維修單 #${r.id}？`))return;try{await deleteRepair(r.id);await loadRepairs()}catch(e){errorMessage.value=e.message}}
function clearFilters(){Object.assign(filters,{memberId:'',repairStatus:'',deviceBrand:''});loadRepairs()}
const money=v=>v==null?'-':new Intl.NumberFormat('zh-TW',{style:'currency',currency:'TWD'}).format(v)
const date=v=>v?new Date(v).toLocaleString('zh-TW'):'-'
onMounted(async()=>{try{members.value=await getMembers();await loadRepairs()}catch(e){errorMessage.value=e.message}})
</script>

<template><section class="management-page">
  <div class="page-heading"><div><h2>維修管理</h2><p>管理手機送修資料與維修進度。</p></div></div><p v-if="errorMessage" class="alert error">{{errorMessage}}</p>
  <form class="card" @submit.prevent="submit"><h3>{{editingId?`編輯維修單 #${editingId}`:'新增維修單'}}</h3><div class="form-grid">
    <label>會員<select v-model="form.memberId" required><option value="" disabled>請選擇</option><option v-for="m in members" :key="m.id" :value="m.id">{{m.id}}－{{m.account}}－{{m.name}}</option></select></label>
    <label>手機品牌<input v-model.trim="form.deviceBrand" maxlength="50" required /></label><label>手機型號<input v-model.trim="form.deviceModel" maxlength="100" required /></label><label>IMEI<input v-model.trim="form.imei" maxlength="30" /></label>
    <label>維修狀態<select v-model="form.repairStatus"><option>RECEIVED</option><option>INSPECTING</option><option>WAITING_PARTS</option><option>REPAIRING</option><option>COMPLETED</option><option>RETURNED</option><option>CANCELLED</option></select></label>
    <label>預估費用<input v-model.number="form.estimatedCost" type="number" min="0" step="0.01" /></label><label>最終費用<input v-model.number="form.finalCost" type="number" min="0" step="0.01" /></label>
    <label class="full-width">故障說明<textarea v-model.trim="form.problemDescription" rows="3" required></textarea></label><label class="full-width">維修人員備註<textarea v-model.trim="form.technicianNote" rows="3"></textarea></label>
  </div><div class="form-actions"><button class="primary" :disabled="saving">{{saving?'儲存中...':editingId?'儲存修改':'新增維修單'}}</button><button v-if="editingId" class="secondary" type="button" @click="cancelEdit">取消編輯</button></div></form>
  <form class="card search-bar" @submit.prevent="loadRepairs"><label>會員<select v-model="filters.memberId"><option value="">全部</option><option v-for="m in members" :key="m.id" :value="m.id">{{m.id}}－{{m.name}}</option></select></label><label>狀態<select v-model="filters.repairStatus"><option value="">全部</option><option>RECEIVED</option><option>INSPECTING</option><option>WAITING_PARTS</option><option>REPAIRING</option><option>COMPLETED</option><option>RETURNED</option><option>CANCELLED</option></select></label><label>品牌<input v-model.trim="filters.deviceBrand" /></label><button class="primary">搜尋</button><button class="secondary" type="button" @click="clearFilters">清除</button></form>
  <div class="card"><h3>維修單列表</h3><p v-if="loading">載入中...</p><p v-else-if="!repairs.length">目前沒有維修單。</p><div v-else class="table-scroll"><table><thead><tr><th>ID</th><th>會員</th><th>品牌</th><th>型號</th><th>故障說明</th><th>狀態</th><th>預估費</th><th>最終費</th><th>收件時間</th><th>完成時間</th><th>操作</th></tr></thead><tbody><tr v-for="r in repairs" :key="r.id"><td>{{r.id}}</td><td>{{r.memberId}}</td><td>{{r.deviceBrand}}</td><td>{{r.deviceModel}}</td><td class="wrap-cell">{{r.problemDescription}}</td><td><span class="badge">{{r.repairStatus}}</span></td><td>{{money(r.estimatedCost)}}</td><td>{{money(r.finalCost)}}</td><td>{{date(r.receivedAt)}}</td><td>{{date(r.completedAt)}}</td><td class="row-actions"><button class="secondary small" @click="editRepair(r)">編輯</button><button class="danger small" @click="remove(r)">刪除</button></td></tr></tbody></table></div></div>
</section></template>
