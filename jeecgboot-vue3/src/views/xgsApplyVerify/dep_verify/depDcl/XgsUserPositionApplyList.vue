<template>
  <div v-auth="'positions:xgs_position_apply:list'">
    <RmsAgentWorkflow initial-phase="dept_review" my-dept />
  </div>
</template>

<script lang="ts" name="xgsUserResume-xgsUserPositionApply-dept-todo" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
