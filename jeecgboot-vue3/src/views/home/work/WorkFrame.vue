<template>
  <div class="portal-page portal-mine">
    <MineNav />
    <component :is="current" v-if="ready && allowed" :key="String(route.name)" />
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useUserStore } from '@/store/modules/user';
  import MineNav from '../mine/MineNav.vue';
  import { WORK_PAGES } from '../mine/workPages';
  import { usePortalPerms } from '../mine/usePortalPerms';

  const route = useRoute();
  const router = useRouter();
  const userStore = useUserStore();
  const ready = usePortalPerms();
  const checked = ref(false);
  const current = computed(() => WORK_PAGES.find((item) => item.name === route.name)?.component);
  const allowed = computed(() => {
    const spec = WORK_PAGES.find((item) => item.name === route.name);
    const roles = (userStore.getRoleList || []) as string[];
    return !!spec && spec.roles.some((role) => roles.includes(role));
  });

  onMounted(async () => {
    try {
      await userStore.getUserInfoAction();
    } catch (error) {
      console.warn('读取角色失败', error);
    }
    checked.value = true;
    if (!allowed.value) {
      router.replace({ name: 'homeHome' });
    }
  });

  watch(
    () => route.name,
    () => {
      if (checked.value && !allowed.value) {
        router.replace({ name: 'homeHome' });
      }
    },
  );
</script>

<style scoped>
  .portal-mine :deep(.jeecg-basic-table-form-container) {
    margin-top: 4px;
    padding: 18px;
    border-radius: 16px;
    background:
      radial-gradient(circle at top left, rgba(0, 64, 152, 0.08), transparent 28%),
      #f3efe6;
    color: #1c1917;
  }

  .portal-mine :deep(.jeecg-basic-form) {
    margin-bottom: 12px;
    padding: 16px 16px 4px;
    background: #fffdf8;
    border: 1px solid #e7e0d4;
    border-radius: 16px;
  }

  .portal-mine :deep(.ant-form-item-label > label) {
    color: #44403c;
  }

  .portal-mine :deep(.ant-input),
  .portal-mine :deep(.ant-input-affix-wrapper),
  .portal-mine :deep(.ant-select-selector),
  .portal-mine :deep(.ant-picker) {
    border-color: #d6d0c4 !important;
    border-radius: 999px !important;
    background: #fff !important;
    box-shadow: none !important;
  }

  .portal-mine :deep(.ant-input:focus),
  .portal-mine :deep(.ant-input-affix-wrapper-focused),
  .portal-mine :deep(.ant-select-focused .ant-select-selector) {
    border-color: #004098 !important;
  }

  .portal-mine :deep(.ant-table-wrapper) {
    background: #fffdf8;
    border: 1px solid #e7e0d4;
    border-radius: 16px;
    overflow: hidden;
  }

  .portal-mine :deep(.ant-table) {
    background: transparent;
    color: #1c1917;
  }

  .portal-mine :deep(.ant-table-title) {
    padding: 14px 16px 8px;
    background: #fffdf8;
  }

  .portal-mine :deep(.ant-table-thead > tr > th) {
    background: #f4f0e6 !important;
    color: #44403c !important;
    font-weight: 600;
    border-bottom: 1px solid #e7e0d4 !important;
  }

  .portal-mine :deep(.ant-table-tbody > tr > td) {
    background: #fffdf8;
    border-bottom: 1px solid #f0ebe3 !important;
  }

  .portal-mine :deep(.ant-table-tbody > tr:hover > td) {
    background: #f7f4ec !important;
  }

  .portal-mine :deep(.ant-table-bordered .ant-table-container),
  .portal-mine :deep(.ant-table-bordered .ant-table-cell) {
    border-color: #f0ebe3 !important;
  }

  .portal-mine :deep(.ant-btn) {
    border-radius: 999px;
    box-shadow: none;
  }

  .portal-mine :deep(.ant-btn-primary) {
    background: #004098;
    border-color: #004098;
    color: #f6fffd;
  }

  .portal-mine :deep(.ant-btn-primary:hover) {
    background: #003067;
    border-color: #003067;
    color: #f6fffd;
  }

  .portal-mine :deep(.ant-btn-default) {
    background: #fff;
    border-color: #d6d0c4;
    color: #1c1917;
  }

  .portal-mine :deep(.ant-btn-link),
  .portal-mine :deep(.jeecg-basic-table-action a),
  .portal-mine :deep(.ant-table-tbody a) {
    color: #004098;
  }

  .portal-mine :deep(.ant-alert-info) {
    margin: 0 16px 8px;
    background: #f4f0e6;
    border: 1px solid #e7e0d4;
    border-radius: 12px;
    color: #57534e;
  }

  .portal-mine :deep(.ant-alert-info .anticon) {
    color: #004098;
  }

  .portal-mine :deep(.ant-pagination-item-active) {
    border-color: #004098;
    background: #004098;
  }

  .portal-mine :deep(.ant-pagination-item-active a) {
    color: #f6fffd;
  }
</style>
