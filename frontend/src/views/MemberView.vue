<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createMember, deleteMember, getMembers, updateMember } from '../api/memberApi'

const members = ref([])
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const keyword = ref('')

const emptyForm = () => ({
  account: '', password: '', name: '', email: '', phone: '', role: 'USER', status: 'ACTIVE'
})
const form = reactive(emptyForm())
const roleLabels = { USER: '一般會員', ADMIN: '管理員' }
const statusLabels = { ACTIVE: '啟用', DISABLED: '停用' }

async function loadMembers() {
  loading.value = true
  errorMessage.value = ''
  try {
    members.value = await getMembers(keyword.value ? { keyword: keyword.value } : {})
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

function editMember(member) {
  editingId.value = member.id
  Object.assign(form, {
    account: member.account,
    password: member.password || '',
    name: member.name,
    email: member.email || '',
    phone: member.phone || '',
    role: member.role || 'USER',
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
  if (!window.confirm(`確定刪除會員「${member.name}」？`)) return
  errorMessage.value = ''
  try {
    await deleteMember(member.id)
    if (editingId.value === member.id) cancelEdit()
    await loadMembers()
  } catch (error) {
    errorMessage.value = error.message
  }
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

    <p v-if="errorMessage" class="alert error">{{ errorMessage }}</p>

    <form class="card" @submit.prevent="submitForm">
      <h3>{{ editingId ? `編輯會員 #${editingId}` : '新增會員' }}</h3>
      <div class="form-grid">
        <label>帳號 *<input v-model.trim="form.account" maxlength="50" required /></label>
        <label>密碼 *<input v-model="form.password" type="password" maxlength="255" required autocomplete="new-password" /></label>
        <label>姓名 *<input v-model.trim="form.name" maxlength="100" required /></label>
        <label>Email<input v-model.trim="form.email" type="email" maxlength="100" /></label>
        <label>手機<input v-model.trim="form.phone" maxlength="20" /></label>
        <label>角色 *<select v-model="form.role"><option value="USER">一般會員</option><option value="ADMIN">管理員</option></select></label>
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
              <td>{{ member.id }}</td><td>{{ member.account }}</td><td>{{ member.name }}</td>
              <td>{{ member.email || '-' }}</td><td>{{ member.phone || '-' }}</td>
              <td><span class="badge">{{ roleLabels[member.role] || member.role }}</span></td><td><span class="badge">{{ statusLabels[member.status] || member.status }}</span></td>
              <td>{{ formatDate(member.createdAt) }}</td>
              <td class="row-actions"><button class="secondary small" @click="editMember(member)">編輯</button><button class="danger small" @click="removeMember(member)">刪除</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>
