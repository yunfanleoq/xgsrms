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
  .salary-page {
    color: #1c2430;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
    font-size: 16px;
    line-height: 1.75;
  }

  .salary-page.portal-page .page-head h1 {
    margin-bottom: 20px;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 36px;
    font-weight: 600;
    line-height: 1.3;
    letter-spacing: 0.02em;
    color: #1a1a1a;
  }

  .salary-page.portal-page .page-card {
    padding: 36px 40px 42px;
  }

  @media (max-width: 720px) {
    .salary-page.portal-page .page-head h1 {
      font-size: 28px;
      letter-spacing: 0.04em;
    }

    .salary-page.portal-page .page-card {
      padding: 22px 16px 28px;
    }
  }
</style>
