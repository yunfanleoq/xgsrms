<template>
  <div class="train-page">
    <div class="page-card">
      <h2>培训发展</h2>
      <PeixunFazhan />
    </div>
  </div>
</template>

<script setup lang="ts" name="homeTraining">
  import PeixunFazhan from '/@/views/home/category/peixunfazhan.vue';
</script>

<style scoped>
  .train-page {
    padding: 8px 0 24px;
  }

  .page-card {
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 8px 24px rgba(15, 40, 80, 0.08);
    padding: 28px 32px;
  }

  h2 {
    margin: 0 0 16px;
    color: #0b2f6b;
    font-size: 24px;
    font-weight: 700;
  }
</style>
