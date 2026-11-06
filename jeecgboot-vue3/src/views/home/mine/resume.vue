<template>
  <div class="portal-page portal-mine">
    <MineNav />
    <XgsMyresumeList v-if="ready" />
  </div>
</template>

<script setup lang="ts" name="homeResume">
  import MineNav from './MineNav.vue';
  import XgsMyresumeList from '/@/views/xgsMyresume/XgsMyresumeList.vue';
  import { usePortalPerms } from './usePortalPerms';

  const ready = usePortalPerms();
</script>
