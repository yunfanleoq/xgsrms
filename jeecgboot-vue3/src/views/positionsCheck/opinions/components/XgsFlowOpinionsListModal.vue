<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1000" @ok="handleSubmit">
    <XgsFlowOpinionsList ref="flowListRef" :positionApply="positionApply"></XgsFlowOpinionsList>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useUserStore } from '@/store/modules/user';
  import XgsFlowOpinionsList from '../XgsFlowOpinionsList.vue';
  import {saveOrUpdate} from "@/views/xgsResumeApproval/opinions/XgsFlowOpinions.api";
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const flowListRef = ref(null);
  const positionApply = ref({});
  const userStore = useUserStore();

  //表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    //重置表单
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !!data?.showFooter;
    positionApply.value = null;
    // flowListRef.value.reload();
    setTimeout(() => {
      positionApply.value = data.record;
    }, 100);
  });
  //设置标题
  const title = ref('办理过程');
  //表单提交事件
  async function handleSubmit(v) {
    return Promise.resolve(v);
  }
</script>

<style lang="less" scoped>
  /** 时间和数字输入框样式 */
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>
