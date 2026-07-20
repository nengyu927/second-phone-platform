<script setup>
</script>

<template>
  <header class="site-header">
    <h1>二手手機販賣與維修平台</h1>
    <nav>
      <RouterLink to="/">首頁</RouterLink>
      <RouterLink to="/members">會員</RouterLink>
      <RouterLink to="/products">商品</RouterLink>
      <RouterLink to="/orders">訂單</RouterLink>
      <RouterLink to="/repairs">維修</RouterLink>
      <RouterLink to="/admin">後台</RouterLink>
    </nav>
  </header>

  <main class="container">
    <RouterView />
  </main>
</template>
