<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1400" :height="650" :style="{ top: '2vh' }" @ok="handleSubmit">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="办理过程">
        <XgsFlowOpinionsList ref="flowListRef" :positionApply="positionApply"></XgsFlowOpinionsList>
      </a-tab-pane>
      <a-tab-pane key="2" tab="岗位申请单" force-render>
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref, nextTick } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useUserStore } from '@/store/modules/user';
  import XgsFlowOpinionsList from '../XgsFlowOpinionsList.vue';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  import {saveOrUpdate} from "@/views/xgsResumeApproval/opinions/XgsFlowOpinions.api";
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const activeKey = ref('1');
  const flowListRef = ref(null);
  const registerFormResume = ref();
  const positionApply = ref({});
  const resumeId = ref('');
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
    
    // 设置简历ID并加载数据
    resumeId.value = data.record.resumeId || '';
    await nextTick();
    if (registerFormResume.value && data.record.resumeId) {
      await registerFormResume.value.loadFormData(data.record.resumeId);
    }
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
