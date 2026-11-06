<template>
  <div class="portal-page portal-mine">
    <MineNav />
    <XgsInterviewList v-if="ready" />
  </div>
</template>

<script setup lang="ts" name="homeInterview">
  import MineNav from './MineNav.vue';
  import XgsInterviewList from '/@/views/xgsMyInterview/XgsInterviewList.vue';
  import { usePortalPerms } from './usePortalPerms';

  const ready = usePortalPerms();
</script>
