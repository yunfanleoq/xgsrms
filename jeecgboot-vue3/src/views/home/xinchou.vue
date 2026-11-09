<template>
  <div class="portal-page salary-page">
    <header class="page-head">
      <h1>薪酬福利</h1>
    </header>
    <div class="page-card">
      <XinchouFuli />
    </div>
  </div>
</template>

<script setup lang="ts" name="homeSalary">
  import XinchouFuli from '/@/views/home/category/xinchoufuli.vue';
</script>

<style scoped>
  .salary-page.portal-page .page-head h1 {
    font-family: 'Songti SC', 'STSong', 'SimSun', 'PingFang SC', serif;
    font-weight: 600;
    letter-spacing: 0.06em;
    color: #141414;
  }

  .salary-page.portal-page .page-card {
    padding: 36px 40px 42px;
  }

  @media (max-width: 720px) {
    .salary-page.portal-page .page-card {
      padding: 22px 16px 28px;
    }
  }
</style>
