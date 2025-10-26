<template>
  <j-modal 
  :title="title" 
  :width="width" 
  :visible="visible" 
  :confirmLoading="confirmLoading"
  @ok="handleOk"
  @cancel="handleCancel" 
  okText="提交申请"
  cancelText="关闭"
  :get-container="getContainer">
    <XgsPositionApplyFormNew ref="registerForm" 
    @ok="submitCallback" :formData="formData" 
    :formDisabled="disableSubmit" 
    :formBpm="false"
    :hideSubmitBtn="true">
  </XgsPositionApplyFormNew>
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose } from 'vue';
  import XgsPositionApplyFormNew from './XgsPositionApplyFormNew.vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';

  const title = ref<string>('');
  const width = ref<string>('90%');
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const confirmLoading = ref<boolean>(false);
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
   * 确定按钮点击事件
   */
  async function handleOk() {
    try {
      confirmLoading.value = true;
      await registerForm.value.submitForm();
      // submitForm 成功后会触发 emit('ok')，进而调用 submitCallback
    } catch (error) {
      console.error('表单提交失败:', error);
    } finally {
      confirmLoading.value = false;
    }
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
