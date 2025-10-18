<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose title="岗位详情" :width="896" :showOkBtn="false" :showCancelBtn="false">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '@/views/positions/XgsPositionPublish.data';
  
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  
  //表单配置（只读模式）
  const [registerForm, { resetFields, setFieldsValue }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    labelWidth: 150,
    disabled: true, // 设置为只读
  });
  
  //表单赋值
  const [registerModal, { setModalProps }] = useModalInner(async (data) => {
    //重置表单
    await resetFields();
    setModalProps({ confirmLoading: false });
    
    if (data?.record) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });
    }
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

