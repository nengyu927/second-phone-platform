<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createMember, deleteMember, getAdminMembers, updateMember, updateMemberRole, updateMemberStatus } from '../api/memberApi'
import { useAuthStore } from '../stores/auth'
import ConfirmModal from '../components/ConfirmModal.vue'

const auth = useAuthStore()
const members = ref([])
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const keyword = ref('')
const pendingDelete = ref(null)
const actionMessage = ref('')

const emptyForm = () => ({
  account: '', password: '', name: '', email: '', phone: '', role: 'CUSTOMER', status: 'ACTIVE'
})
const form = reactive(emptyForm())
const roleLabels = { CUSTOMER: '一般會員', STAFF: '員工', ADMIN: '管理員' }
const statusLabels = { ACTIVE: '啟用', DISABLED: '停用' }

async function loadMembers() {
  loading.value = true
  errorMessage.value = ''
  try {
    members.value = await getAdminMembers(keyword.value ? { keyword: keyword.value } : {})
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

function editMember(member) {
  editingId.value = member.id
  Object.assign(form, {
    account: member.username,
    password: member.password || '',
    name: member.name,
    email: member.email || '',
    phone: member.phone || '',
    role: member.role || 'CUSTOMER',
    status: member.status || 'ACTIVE'
  })
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelEdit() {
  editingId.value = null
  Object.assign(form, emptyForm())
  errorMessage.value = ''
}

async function submitForm() {
  saving.value = true
  errorMessage.value = ''
  try {
    if (editingId.value) {
      await updateMember(editingId.value, { ...form })
    } else {
      await createMember({ ...form })
    }
    cancelEdit()
    await loadMembers()
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    saving.value = false
  }
}

async function removeMember(member) {
  errorMessage.value = ''
  try {
    await deleteMember(member.id)
    if (editingId.value === member.id) cancelEdit()
    await loadMembers()
  } catch (error) {
    errorMessage.value = error.message
  }
}

async function toggleStatus(member) {
  errorMessage.value = ''; actionMessage.value = ''
  try { await updateMemberStatus(member.id, member.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'); actionMessage.value = '會員狀態已更新。'; await loadMembers() }
  catch (error) { errorMessage.value = error.message }
}

async function changeRole(member, role) {
  if (role === member.role) return
  errorMessage.value = ''; actionMessage.value = ''
  try { await updateMemberRole(member.id, role); actionMessage.value = '會員角色已更新。'; await loadMembers() }
  catch (error) { errorMessage.value = error.message; await loadMembers() }
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString('zh-TW') : '-'
}

function clearSearch() {
  keyword.value = ''
  loadMembers()
}

onMounted(loadMembers)
</script>

<template>
  <section class="management-page">
    <div class="page-heading">
      <div><h2>會員管理</h2><p>新增、查詢、修改與刪除會員資料。</p></div>
      <button class="secondary" type="button" @click="loadMembers" :disabled="loading">重新整理</button>
    </div>

    <p v-if="errorMessage" class="alert alert-error">{{ errorMessage }}</p>
    <p v-if="actionMessage" class="alert alert-success">{{ actionMessage }}</p>

    <form v-if="auth.isAdmin" class="card service-form" @submit.prevent="submitForm">
      <h3>{{ editingId ? `編輯會員 #${editingId}` : '新增會員' }}</h3>
      <div class="form-grid">
        <label>帳號 *<input v-model.trim="form.account" maxlength="50" required /></label>
        <label>密碼{{ editingId ? '（留空則不變）' : ' *' }}<input v-model="form.password" type="password" minlength="8" maxlength="72" :required="!editingId" autocomplete="new-password" /></label>
        <label>姓名 *<input v-model.trim="form.name" maxlength="100" required /></label>
        <label>Email<input v-model.trim="form.email" type="email" maxlength="100" /></label>
        <label>手機<input v-model.trim="form.phone" maxlength="20" /></label>
        <label>角色 *<select v-model="form.role"><option value="CUSTOMER">一般會員</option><option value="STAFF">員工</option><option value="ADMIN">管理員</option></select></label>
        <label>狀態 *<select v-model="form.status"><option value="ACTIVE">啟用</option><option value="DISABLED">停用</option></select></label>
      </div>
      <div class="form-actions">
        <button class="primary" :disabled="saving">{{ saving ? '儲存中...' : editingId ? '儲存修改' : '新增會員' }}</button>
        <button v-if="editingId" class="secondary" type="button" @click="cancelEdit">取消編輯</button>
      </div>
    </form>

    <form class="card search-bar" @submit.prevent="loadMembers">
      <label>關鍵字<input v-model.trim="keyword" placeholder="搜尋帳號或姓名" /></label>
      <button class="primary">搜尋</button>
      <button class="secondary" type="button" @click="clearSearch">清除條件</button>
    </form>

    <div class="card">
      <h3>會員列表</h3>
      <p v-if="loading" class="status-text">資料載入中...</p>
      <p v-else-if="members.length === 0" class="status-text">目前沒有會員資料。</p>
      <div v-else class="table-scroll">
        <table>
          <thead><tr><th>ID</th><th>帳號</th><th>姓名</th><th>Email</th><th>手機</th><th>角色</th><th>狀態</th><th>建立時間</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="member in members" :key="member.id">
              <td>{{ member.id }}</td><td>{{ member.username }}</td><td>{{ member.name }}</td>
              <td>{{ member.email || '-' }}</td><td>{{ member.phone || '-' }}</td>
              <td><select v-if="auth.isAdmin" :value="member.role" @change="changeRole(member,$event.target.value)"><option value="CUSTOMER">一般會員</option><option value="STAFF">員工</option><option value="ADMIN">管理員</option></select><span v-else class="badge">{{ roleLabels[member.role] || member.role }}</span></td><td><span class="badge">{{ statusLabels[member.status] || member.status }}</span></td>
              <td>{{ formatDate(member.createdAt) }}</td>
              <td class="row-actions"><button class="secondary small" @click="toggleStatus(member)">{{member.status==='ACTIVE'?'停用':'啟用'}}</button><button v-if="auth.isAdmin" class="secondary small" @click="editMember(member)">編輯</button><button v-if="auth.isAdmin" class="danger small" @click="pendingDelete=member">刪除</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <ConfirmModal :open="Boolean(pendingDelete)" title="刪除會員" :message="`確定刪除會員「${pendingDelete?.name||''}」？此操作無法復原。`" :busy="saving" @cancel="pendingDelete=null" @confirm="removeMember(pendingDelete);pendingDelete=null" />
  </section>
</template>
