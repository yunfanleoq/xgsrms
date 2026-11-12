<template>
  <div class="portal-page portal-mine">
    <MineNav />
    <MyXgsFavoriteJobList v-if="ready" />
  </div>
</template>

<script setup lang="ts" name="homeFavorite">
  import MineNav from './MineNav.vue';
  import MyXgsFavoriteJobList from '/@/views/positions/MyXgsFavoriteJobList.vue';
  import { usePortalPerms } from './usePortalPerms';

  const ready = usePortalPerms();
</script>
