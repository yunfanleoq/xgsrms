<template>
  <PortalPositionApply ref="applyRef" page-mode />
</template>

<script setup lang="ts">
  import { onMounted, ref, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import PortalPositionApply from './position/PortalPositionApply.vue';

  /** 三类人才项目共用现有投递表，只用项目身份区分栏目，不另做一套字段。 */
  const PROGRAMS: Record<string, string> = {
    领军: '高层次领军人才',
    海外优青: '海外优青',
    高层次青年: '高层次青年人才',
  };

  const route = useRoute();
  const applyRef = ref<{ addJob: (info: Record<string, string>) => void } | null>(null);

  const openForm = () => {
    const raw = route.query.program;
    const program = Array.isArray(raw) ? raw[0] : raw;
    const title = program ? PROGRAMS[program] : '';
    if (!title || !applyRef.value) return;
    applyRef.value.addJob({
      positionName: title,
      positionType: '副高级以上岗位',
      positionDept: '',
      status: '申请中',
    });
  };

  onMounted(openForm);
  watch(() => route.query.program, openForm);
</script>
