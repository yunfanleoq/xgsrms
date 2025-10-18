<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose title="状态变更" :width="600" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { FormSchema } from '/@/components/Form';
  import { saveOrUpdate } from '../XgsPositions.api';
  
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const recordId = ref('');
  
  // 状态选项
  const statusOptions = [
    { label: '草稿', value: '草稿' },
    { label: '待审核', value: '待审核' },
    { label: '审核通过', value: '审核通过' },
    { label: '审核未通过', value: '审核未通过' },
    { label: '招聘中', value: '招聘中' },
    { label: '已结束', value: '已结束' },
  ];
  
  // 表单配置
  const formSchema: FormSchema[] = [
    {
      field: 'status',
      label: '岗位状态',
      component: 'Select',
      componentProps: {
        options: statusOptions,
      },
      required: true,
    },
  ];
  
  // 表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
  });
  
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    // 重置表单
    await resetFields();
    setModalProps({ confirmLoading: false });
    
    if (data?.record) {
      recordId.value = data.record.id;
      // 表单赋值
      await setFieldsValue({
        status: data.record.status,
      });
    }
  });
  
  // 表单提交事件
  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      
      // 提交表单，只更新状态
      await saveOrUpdate(
        {
          id: recordId.value,
          status: values.status,
        },
        true
      );
      
      // 关闭弹窗
      closeModal();
      // 刷新列表
      emit('success');
    } catch (error) {
      console.error('状态变更失败:', error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-select) {
    width: 100%;
  }
</style>

