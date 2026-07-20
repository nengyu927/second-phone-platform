<script setup>
defineProps({
  title: {
    type: String,
    required: true
  }
})
</script>

<template>
  <section class="card">
    <h2>{{ title }}</h2>
    <p>此功能尚未開始，將依照 docs/TODO.md 與 Codex 任務逐步完成。</p>
  </section>
</template>
