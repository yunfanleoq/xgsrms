<template>
  <div v-auth="'positions:xgs_position_apply:list'">
    <RmsAgentWorkflow initial-phase="hr_decision" />
  </div>
</template>

<script lang="ts" name="xgsUserResume-xgsUserPositionApply-hr-done" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
