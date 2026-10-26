<template>
  <div v-auth="'positions:xgs_position_apply:list'">
    <RmsAgentWorkflow initial-phase="dept_review" my-dept />
  </div>
</template>

<script lang="ts" name="resume-xgsPositionApply-dept" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
