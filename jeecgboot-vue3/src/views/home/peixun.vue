<template>
  <div class="portal-page training-page">
    <header class="page-head">
      <h1>培训发展</h1>
    </header>
    <div class="page-card">
      <PeixunFazhan />
    </div>
  </div>
</template>

<script setup lang="ts" name="homeTraining">
  import PeixunFazhan from '/@/views/home/category/peixunfazhan.vue';
</script>

<style>
  .training-page.portal-page .page-head h1 {
    margin-bottom: 20px;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-weight: 600;
    font-size: 36px;
    line-height: 1.3;
    letter-spacing: 0.02em;
    color: #1a1a1a;
  }

  .training-page.portal-page .page-card {
    padding: 36px 40px 48px;
    border-color: #e2e6ec;
  }

  @media (max-width: 960px) {
    .training-page.portal-page .page-card {
      padding: 24px 18px 32px;
    }
  }

  @media (max-width: 720px) {
    .training-page.portal-page .page-head h1 {
      font-size: 28px;
      letter-spacing: 0.04em;
    }
  }
</style>
