<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1200" @ok="handleSubmit">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="审核意见">
        <BasicForm @register="registerForm" name="XgsFlowOpinionsForm" />
      </a-tab-pane>
      <a-tab-pane key="2" tab="岗位信息" force-render>
        <XgsPositionsForm v-if="formBool" ref="registerFormPosition" @ok="submitCallback" :formDisabled="true" :formData="positionApply" :formBpm="false" />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
import {ref, computed, unref, nextTick} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../XgsFlowOpinions.data';
  import { saveOrUpdate, getResumeData } from '../XgsFlowOpinions.api';
  import { useUserStore } from '@/store/modules/user';
  import XgsPositionsForm from '@/views/positionsCheck/components/XgsPositionsForm.vue';
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const activeKey = ref('1');
  const positionApply = ref({});
  const registerFormPosition = ref();
  const userStore = useUserStore();

  const formBool = ref(false);

  /**
   * form保存回调事件
   */
  function submitCallback() {
    handleCancel();
    emit('success');
  }
  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    labelWidth: 150,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
  });
  //表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('XgsFlowOptionsModal.useModalInner>>>>>', data);
    //重置表单
    await resetFields();
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !!data?.showFooter;
    positionApply.value = data.record;
    if (unref(isUpdate)) {
      await setFieldsValue({
        approvalUser: userStore.userInfo.realname,
        approvalNode: data.record.approvalNode,
        approvalStatus: '同意',
        parentId: data.record.id,
      });
      formBool.value = true
    }
    // registerFormPosition.value = data.record;
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //设置标题
  const title = computed(() => (!unref(isUpdate) ? '审批' : !unref(isDetail) ? '详情' : '审批'));
  //表单提交事件
  async function handleSubmit(v) {
    try {
      let values = await validate();
      setModalProps({ confirmLoading: true });
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      //关闭弹窗
      closeModal();
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
    } finally {
      setModalProps({ confirmLoading: false });
    }
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
