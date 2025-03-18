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
  import { ref } from 'vue';
  import IndexChart from './homePage/IndexChart.vue';
  import IndexBdc from './homePage/IndexBdc.vue';
  import { defHttp } from '/@/utils/http/axios';

  const indexStyle = ref(1);
  const loading = ref(true);
  const showIndexStyle = ref(false);
  defHttp
    .get({ url: '/sys/api/queryLoginUserRoles' }, { errorMessageMode: 'none' })
    .then((res) => {
      if (res.data.includes('admin')) {
        indexStyle.value = 0;
        showIndexStyle.value = true;
      } else {
        indexStyle.value = 1;
      }
    })
    .finally(() => {
      loading.value = false;
    });

  const formData = ref({
    gwsl: 5,
    zpsl: 100,
    shsl: 20,
    jlsl: 60,
  });
</script>
