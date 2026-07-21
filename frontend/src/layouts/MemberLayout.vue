<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore(), router = useRouter()
function logout() { auth.logout(); router.push('/') }
</script>
<template><div class="portal-shell member-shell"><aside class="sidebar member-sidebar"><RouterLink class="brand" to="/"><span class="brand-mark">S</span><span>Second Phone</span></RouterLink><div><p class="eyebrow">MY ACCOUNT</p><h2>會員中心</h2><p>{{ auth.currentUser?.name }}</p></div><nav><RouterLink to="/member/profile">個人資料</RouterLink><RouterLink to="/member/orders">我的訂單</RouterLink><RouterLink to="/member/repairs">我的維修</RouterLink><RouterLink to="/member/trade-ins">我的收購</RouterLink></nav><button class="button button-ghost" @click="logout">登出帳號</button></aside><div class="portal-content"><header class="toolbar"><div><span class="breadcrumb">首頁 ／ 會員中心</span><strong>您好，{{ auth.currentUser?.name }}</strong></div><RouterLink to="/" class="button button-ghost button-small">返回商城</RouterLink></header><main><RouterView /></main></div></div></template>
