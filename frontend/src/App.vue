<template>
  <header class="site-header">
    <h1>二手手機販賣與維修平台</h1>
    <nav>
      <RouterLink to="/">首頁</RouterLink>
      <RouterLink to="/members">會員管理</RouterLink>
      <RouterLink to="/products">商品管理</RouterLink>
    </nav>
  </header>

  <main class="container">
    <RouterView />
  </main>
</template>
