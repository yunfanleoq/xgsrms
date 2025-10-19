<template>
  <j-modal
    :title="title"
    :width="'80%'"
    :visible="visible"
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :okButtonProps="{ style: { display: 'none' } }"
  >
    <BasicForm
      @register="registerForm"
      name="XgsUserPositionApplyForm"
      :positionType="positionType"
      :formData="formData"
      :formBpm="formBpm"
    />

    <!-- 使用新的统一申请表单 -->
    <XgsApplyForm :formData="formData" :formDisabled="true" :formBpm="formBpm" />
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref, nextTick, defineExpose } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { formSchema } from '../XgsUserPositionApply.data';
  import { saveOrUpdate } from '../XgsUserPositionApply.api';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';

  const isReady = ref(false);

  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const positionType = ref('');
  const formData = ref({});
  const formBpm = ref(true);
  const visible = ref(false);
  const title = ref('详情');

  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  /**
   * 打开Modal
   */
  async function open(record) {
    visible.value = true;
    await nextTick();

    console.log('data', record);
    //重置表单
    await resetFields();
    
    isUpdate.value = !!record?.isUpdate;
    isDetail.value = !!record?.showFooter;
    
    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...record.record,
      });
    }
    
    positionType.value = record.record.positionType;
    formData.value = {
      id: record.record.id,
      dataId: record.record.id,
      disabled: true,
    };
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !record?.showFooter });
    isReady.value = true;
  }

  /**
   * 关闭Modal
   */
  function handleCancel() {
    visible.value = false;
  }

  /**
   * 确定按钮
   */
  function handleOk() {
    handleCancel();
  }

  // 暴露方法给父组件
  defineExpose({
    open,
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
