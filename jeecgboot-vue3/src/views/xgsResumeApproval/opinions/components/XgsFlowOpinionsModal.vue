<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="1400" :height="650" :style="{ top: '2vh' }" @ok="handleSubmit">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="审核意见">
        <BasicForm @register="registerForm" name="XgsFlowOpinionsForm" />
      </a-tab-pane>
      <a-tab-pane key="2" tab="岗位申请单" force-render>
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
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
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const activeKey = ref('1');
  const positionApply = ref({});
  const registerFormResume = ref();
  const resumeId = ref('');
  const userStore = useUserStore();

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
    //重置表单
    await resetFields();
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = !!data?.isUpdate;
    isDetail.value = !!data?.showFooter;
    positionApply.value = data.record;
    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });
    } else {
      // 新增审批意见时，根据当前记录的审批环节设置表单
      let approvalNode = data.record.approvalNode;
      let opinions = '同意';
      
      // 如果当前是"待查看"状态，确保审批环节设置为"待查看"
      if (data.record.approvalNode === '待查看') {
        approvalNode = '待查看';
        opinions = '同意';
      }
      
      await setFieldsValue({
        approvalUser: userStore.userInfo.realname,
        approvalNode: approvalNode,
        approvalStatus: '同意',
        opinions: opinions,
        parentId: data.record.id,
      });
    }
    // 设置简历ID并加载数据
    resumeId.value = data.record.resumeId || '';
    await nextTick();
    if (registerFormResume.value && data.record.resumeId) {
      await registerFormResume.value.loadFormData(data.record.resumeId);
    }
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
