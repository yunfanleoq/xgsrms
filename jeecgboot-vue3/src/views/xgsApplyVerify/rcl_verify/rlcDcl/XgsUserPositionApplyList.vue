<template>
  <div v-auth="'positions:xgs_position_apply:list'">
    <RmsAgentWorkflow initial-phase="hr_decision" />
  </div>
</template>

<script lang="ts" name="xgsUserResume-xgsUserPositionApply-hr-todo" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
