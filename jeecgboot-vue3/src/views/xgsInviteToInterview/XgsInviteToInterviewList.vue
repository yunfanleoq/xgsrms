<template>
  <div v-auth="'xgsInviteToInterview:xgs_invite_to_interview:list'">
    <RmsAgentWorkflow initial-phase="invite" />
  </div>
</template>

<script lang="ts" name="xgs-invite-to-interview" setup>
  import RmsAgentWorkflow from '@/views/rmsAgent/RmsAgentWorkflow.vue';
</script>
