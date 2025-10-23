<template>
  <j-modal
    :title="title"
    :width="'80%'"
    :visible="visible"
    :maskClosable="false"
    :destroyOnClose="true"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭"
    :switchFullscreen="true"
    class="xgs-flow-opinions-list-modal"
  >
    <!-- a-tabs 仅用于显示标题和切换状态，不包含内容 -->
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="办理过程" />
      <a-tab-pane key="2" tab="岗位申请单" />
    </a-tabs>

    <!-- 表单内容区域，使用 v-if 控制显示，不放在 a-tab-pane 里 -->
    <div class="tab-content-container">
      <!-- 第一个 Tab: 办理过程 -->
      <div v-if="activeKey === '1'" class="tab-content-pane">
        <XgsFlowOpinionsList ref="flowListRef" :positionApply="positionApply"></XgsFlowOpinionsList>
      </div>

      <!-- 第二个 Tab: 岗位申请表单 -->
      <div v-if="activeKey === '2'" class="tab-content-pane">
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
      </div>
    </div>
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
      
      // 延迟2秒后重新计算导航位置，确保DOM完全渲染
      setTimeout(() => {
        if (registerFormResume.value && typeof registerFormResume.value.computeAffixOffset === 'function') {
          registerFormResume.value.computeAffixOffset();
        }
      }, 2000);
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

  // 隐藏 a-tab-pane 的内容区域（因为我们用 v-if 在外面显示内容）
  :deep(.ant-tabs-content) {
    display: none;
  }

  // Tab 内容容器样式
  .tab-content-container {
    margin-top: 16px;
  }
</style>
