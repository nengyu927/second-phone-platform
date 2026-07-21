<script setup>defineProps({ title: { type: String, required: true } })</script>
<template><section class="empty-state card"><span class="empty-icon">◇</span><p class="eyebrow">COMING SOON</p><h1>{{ title }}</h1><p>功能開發中，本頁會在下一個實作批次完成。</p><RouterLink class="button button-primary" to="/">返回首頁</RouterLink></section></template>
