<template>
  <j-modal
    :title="title"
    :width="1400"
    :visible="visible"
    :maskClosable="false"
    :destroyOnClose="true"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="办理过程">
        <XgsFlowOpinionsList ref="flowListRef" :positionApply="positionApply"></XgsFlowOpinionsList>
      </a-tab-pane>
      <a-tab-pane key="2" tab="岗位申请单" force-render>
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
      </a-tab-pane>
    </a-tabs>
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose } from 'vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';
  import XgsFlowOpinionsList from '../XgsFlowOpinionsList.vue';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  // Emits声明
  const emit = defineEmits(['success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const activeKey = ref('1');
  const flowListRef = ref(null);
  const registerFormResume = ref();
  const positionApply = ref({});
  const resumeId = ref('');
  const visible = ref(false);
  const title = ref('办理过程');

  /**
   * 打开Modal
   */
  async function open(data) {
    visible.value = true;
    activeKey.value = '1'; // 重置到第一个 tab
    await nextTick();
    
    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !!data?.showFooter;
    positionApply.value = {};
    
    setTimeout(() => {
      positionApply.value = data.record;
    }, 100);
    
    // 设置简历ID并加载数据
    resumeId.value = data.record.resumeId || '';
    await nextTick();
    if (registerFormResume.value && data.record.resumeId) {
      await registerFormResume.value.loadFormData(data.record.resumeId);
    }
  }

  /**
   * 关闭Modal
   */
  function handleCancel() {
    visible.value = false;
    emit('success');
  }

  /**
   * 确定按钮点击事件
   */
  async function handleSubmit() {
    visible.value = false;
    emit('success');
  }
  
  // 向父组件暴露方法
  defineExpose({
    open
  });
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
