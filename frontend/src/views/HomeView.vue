<script setup>
import { ref } from 'vue'
import http from '../api/http'

const apiMessage = ref('尚未測試')
const loading = ref(false)

async function testApi() {
  loading.value = true

  try {
    const response = await http.get('/test')
    apiMessage.value = response.data.message
  } catch (error) {
    apiMessage.value = '無法連線後端，請確認 Spring Boot 是否啟動'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section>
    <h2>期中專題簡化試作版</h2>
    <p>目前已完成 Vue、Axios、Router 與 Spring Boot API 基礎架構。</p>

    <div class="card">
      <h3>前後端連線測試</h3>
      <p>{{ apiMessage }}</p>
      <button @click="testApi" :disabled="loading">
        {{ loading ? '測試中...' : '測試 API' }}
      </button>
    </div>

    <div class="feature-grid">
      <article class="card"><h3>會員</h3><p>註冊、會員資料 CRUD</p></article>
      <article class="card"><h3>商品</h3><p>二手手機商品 CRUD</p></article>
      <article class="card"><h3>訂單</h3><p>訂單與訂單明細</p></article>
      <article class="card"><h3>維修</h3><p>維修單與進度管理</p></article>
      <article class="card"><h3>後台</h3><p>集中管理五大功能</p></article>
    </div>
  </section>
</template>
