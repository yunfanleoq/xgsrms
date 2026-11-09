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
    font-family: 'Songti SC', 'STSong', 'Songti TC', 'PingFang SC', serif;
    font-weight: 600;
    font-size: 36px;
    line-height: 1.25;
    letter-spacing: 0.08em;
    color: #10243f;
  }

  .training-page.portal-page .page-head h1::after {
    content: '';
    display: block;
    width: 48px;
    height: 2px;
    margin-top: 16px;
    background: #004098;
  }

  .training-page.portal-page .page-card {
    background: transparent;
    border: none;
    padding: 6px 0 8px;
  }

  @media (max-width: 720px) {
    .training-page.portal-page .page-head h1 {
      font-size: 28px;
      letter-spacing: 0.06em;
    }
  }
</style>
