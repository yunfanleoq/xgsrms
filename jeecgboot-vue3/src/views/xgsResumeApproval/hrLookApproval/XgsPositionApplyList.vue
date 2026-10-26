<template>
  <div v-auth="'positions:xgs_position_apply:list'">
    <RmsAgentWorkflow initial-phase="hr_screen" />
  </div>
</template>

<script lang="ts" name="resume-xgsPositionApply-hr-screen" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
