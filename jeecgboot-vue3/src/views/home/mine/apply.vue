<template>
  <div class="portal-page portal-mine">
    <MineNav />
    <XgsUserPositionApplyList v-if="ready" />
  </div>
</template>

<script setup lang="ts" name="homeApply">
  import MineNav from './MineNav.vue';
  import XgsUserPositionApplyList from '/@/views/userPositions/userPositions/XgsUserPositionApplyList.vue';
  import { usePortalPerms } from './usePortalPerms';

  const ready = usePortalPerms();
</script>
