<template>
  <j-modal 
  :title="title" 
  :width="width" 
  :visible="visible" 
  :showOkBtn="false"
  @cancel="handleCancel" 
  cancelText="关闭"
  :get-container="getContainer">
    <XgsPositionApplyFormNew ref="registerForm" 
    @ok="submitCallback" :formData="formData" 
    :formDisabled="disableSubmit" 
    :formBpm="false">
  </XgsPositionApplyFormNew>
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose } from 'vue';
  import XgsPositionApplyFormNew from './XgsPositionApplyFormNew.vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';

  const title = ref<string>('');
  const width = ref<number>(1200);
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const registerForm = ref();
  const emit = defineEmits(['register', 'success']);
  const props = defineProps({
    disableSubmit: {
      type: Boolean,
      default: false,
    },
    formData: {
      type: Object,
      default: () => {
        return {};
      },
    },
  });

  /**
   * 新增
   */
  function add(record) {
    title.value = '新增';
    visible.value = true;
    nextTick(() => {
      registerForm.value.add(record);
    });
  }

  function addJob(record) {
    title.value = '在线申请';
    visible.value = true;
    nextTick(() => {
      registerForm.value.add(record);
    });
  }

  /**
   * 编辑
   * @param record
   */
  function edit(record) {
    title.value = disableSubmit.value ? '详情' : '编辑';
    visible.value = true;
    nextTick(() => {
      registerForm.value.edit(record);
    });
  }

  /**
   * 确定按钮点击事件 - 现已隐藏确定按钮，保留此方法以维护兼容性
   * @deprecated 此方法已弃用，但保留以维护兼容性
   */
  // @ts-ignore - 保留此方法以维护兼容性
  function _handleOk() {
    registerForm.value.submitForm();
  }

  /**
   * form保存回调事件
   */
  function submitCallback() {
    handleCancel();
    emit('success');
  }

  /**
   * 取消按钮回调事件
   */
  function handleCancel() {
    visible.value = false;
  }

  /**
   * 获取弹窗容器（用于 getContainer）
   */
  function getContainer() {
    return document.body;
  }

  defineExpose({
    add,
    addJob,
    edit,
    disableSubmit,
  });
</script>

<style lang="less">
  /**隐藏样式-modal确定按钮 */
  .jee-hidden {
    display: none !important;
  }
</style>
