<template>
  <div class="salary-page">
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
    padding: 8px 0 24px;
  }

  .page-card {
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 8px 24px rgba(15, 40, 80, 0.08);
    padding: 28px 32px;
  }
</style>
