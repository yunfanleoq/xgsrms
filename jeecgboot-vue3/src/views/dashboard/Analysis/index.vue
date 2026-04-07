<template>
  <a-spin :spinning="loading">
    <div v-if="showIndexStyle" style="text-align: center; margin-top: 20px">
      <a-radio-group v-model:value="indexStyle">
        <a-radio :value="0">管理员首页</a-radio>
        <a-radio :value="1">应聘人员首页</a-radio>
      </a-radio-group>
      <span><b>（部署正式环境不显示此选项）</b></span>
    </div>
    <IndexChart v-if="indexStyle === 0" :formData="formData" />
    <IndexBdc v-if="indexStyle == 1" />
  </a-spin>
</template>
<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import IndexChart from './homePage/IndexChart.vue';
  import IndexBdc from './homePage/IndexBdc.vue';
  import { useUserStore } from '/@/store/modules/user';

  const indexStyle = ref(1);
  const loading = ref(true);
  const showIndexStyle = ref(false);
  const userStore = useUserStore();

  /** 登录后角色已在 getUserInfoAction 中写入 Pinia，勿请求不存在的 /sys/api/queryLoginUserRoles（否则会 404 静态资源） */
  function applyHomeStyleByRole() {
    const roles = (userStore.getRoleList || []) as unknown as string[];
    if (roles.includes('admin')) {
      indexStyle.value = 0;
      showIndexStyle.value = true;
    } else {
      indexStyle.value = 1;
    }
  }

  onMounted(() => {
    applyHomeStyleByRole();
    loading.value = false;
  });

  const formData = ref({
    gwsl: 5,
    zpsl: 100,
    shsl: 20,
    jlsl: 60,
  });
</script>
