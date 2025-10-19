<template>
  <j-modal
    :title="title"
    :width="'80%'"
    :visible="visible"
    :maskClosable="false"
    @ok="handleSubmit"
    @cancel="handleCancel"
    okText="通过"
  >
    <template #footer>
      <a-button key="back" @click="handleCancel">驳回</a-button>
      <a-button key="submit" type="primary" @click="handleSubmit">通过</a-button>
    </template>
    <BasicForm
      @register="registerForm"
      name="XgsUserPositionApplyForm"
      :positionType="positionType"
      :status="status"
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
  const disableSubmit = ref<boolean>(false);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const positionType = ref('');
  const status = ref('');
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
    status.value = record.record.status;
    formData.value = {
      id: record.record.id,
      dataId: record.record.id,
      disabled: true,
    };
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !record?.showFooter });
    isReady.value = true;
  }

  //设置标题
  const titleComputed = computed(() => (!unref(isUpdate) ? '新增' : !unref(isDetail) ? '详情' : '查看'));

  //表单提交事件
  async function handleSubmit(v) {
    try {
      console.log('positionType', positionType.value);
      let values = await validate();
      values.status = '待人力处审核'; //修改状态
      // //提交表单
      await saveOrUpdate(values, isUpdate.value);
      // //关闭弹窗
      handleCancel();
      // //刷新列表
      emit('success');
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }
  }

  // 取消按钮点击事件
  async function handleCancel(v?) {
    if (v) {
      try {
        let values = await validate();
        values.status = '部门未通过'; //修改状态
        //提交表单
        await saveOrUpdate(values, isUpdate.value);
        //关闭弹窗
        visible.value = false;
        //刷新列表
        emit('success');
      } catch ({ errorFields }) {
        if (errorFields) {
          const firstField = errorFields[0];
          if (firstField) {
            scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
          }
        }
        return Promise.reject(errorFields);
      }
    } else {
      visible.value = false;
    }
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
