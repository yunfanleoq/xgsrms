<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    @ok="handleOk"
    :okButtonProps="{ class: { 'jee-hidden': disableSubmit } }"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <XgsApplyForm 
      ref="registerForm" 
      @ok="submitCallback" 
      :formData="formData" 
      :formDisabled="disableSubmit" 
      :formBpm="formBpm" 
    />
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose } from 'vue';
  import XgsApplyForm from './index.vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';

  const title = ref<string>('');
  const width = ref<number>(1400);
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const registerForm = ref();
  const formBpm = ref<boolean>(false);

  const emit = defineEmits(['register', 'success']);
  
  const props = defineProps({
    disableSubmit: {
      type: Boolean,
      default: false,
    },
    formData: {
      type: Object,
      default: () => ({}),
    },
    formBpm: {
      type: Boolean,
      default: false,
    },
  });

  const formData = ref({});

  /**
   * 新增
   */
  function add(record) {
    title.value = '新增';
    visible.value = true;
    disableSubmit.value = false;
    formData.value = record || {};
    nextTick(() => {
      if (registerForm.value && registerForm.value.loadData) {
        registerForm.value.loadData(formData.value);
      }
    });
  }

  /**
   * 在线申请
   */
  function addJob(record) {
    title.value = '在线申请';
    visible.value = true;
    disableSubmit.value = false;
    formData.value = record || {};
    nextTick(() => {
      if (registerForm.value && registerForm.value.loadData) {
        registerForm.value.loadData(formData.value);
      }
    });
  }

  /**
   * 编辑
   * @param record
   */
  function edit(record) {
    title.value = disableSubmit.value ? '详情' : '编辑';
    visible.value = true;
    formData.value = record || {};
    nextTick(() => {
      if (registerForm.value && registerForm.value.loadData) {
        registerForm.value.loadData(formData.value);
      }
    });
  }

  /**
   * 详情（只读）
   * @param record
   */
  function detail(record) {
    title.value = '详情';
    visible.value = true;
    disableSubmit.value = true;
    formData.value = record || {};
    nextTick(() => {
      if (registerForm.value && registerForm.value.loadData) {
        registerForm.value.loadData(formData.value);
      }
    });
  }

  /**
   * 确定按钮点击事件
   */
  function handleOk() {
    if (registerForm.value && registerForm.value.submitForm) {
      registerForm.value.submitForm();
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
    formData.value = {};
  }

  defineExpose({
    add,
    addJob,
    edit,
    detail,
    disableSubmit,
  });
</script>

<style lang="less">
  /**隐藏样式-modal确定按钮 */
  .jee-hidden {
    display: none !important;
  }
</style>

