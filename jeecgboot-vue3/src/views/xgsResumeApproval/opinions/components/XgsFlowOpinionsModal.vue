<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="title"
    width="80%"
    :showFooter="showFooter"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
    class="xgs-flow-opinions-modal"
  >
    <!-- a-tabs 仅用于显示标题和切换状态，不包含内容 -->
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="审核意见" />
      <a-tab-pane key="2" tab="岗位申请单" />
    </a-tabs>

    <!-- 表单内容区域，使用 v-if 控制显示，不放在 a-tab-pane 里 -->
    <div class="tab-content-container">
      <!-- 第一个 Tab: 审核意见表单 -->
      <div v-if="activeKey === '1'" class="tab-content-pane">
        <BasicForm @register="registerForm" name="XgsFlowOpinionsForm" />
      </div>

      <!-- 第二个 Tab: 岗位申请表单 -->
      <div v-if="activeKey === '2'" class="tab-content-pane scrollable-pane">
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
import {ref, unref, nextTick} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../XgsFlowOpinions.data';
  import { saveOrUpdate } from '../XgsFlowOpinions.api';
  import { useUserStore } from '@/store/modules/user';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  
  // Emits声明
  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  const activeKey = ref('1');
  const positionApply = ref({});
  const registerFormResume = ref();
  const resumeId = ref('');
  const userStore = useUserStore();
  const title = ref('审批');
  const showFooter = ref(true);


  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate, scrollToField }] = useForm({
    labelWidth: 150,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
  });

  /**
   * Modal注册
   */
  const [registerModal, { closeModal }] = useModalInner(async (data) => {
    activeKey.value = '1'; // 重置到第一个 tab
    await nextTick();
    
    //重置表单
    await resetFields();
    
    isUpdate.value = !!data?.isUpdate;
    showFooter.value = !!data?.showFooter;
    positionApply.value = data.record;
    
    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });
      title.value = '详情';
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
        approvalUser: userStore.userInfo?.realname || '',
        approvalNode: approvalNode,
        approvalStatus: '同意',
        opinions: opinions,
        parentId: data.record.id,
      });
      title.value = '审批';
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

  /**
   * 关闭Modal
   */
  function handleCancel() {
    closeModal();
    emit('success');
  }

  /**
   * 确定按钮 - 提交表单
   */
  async function handleOk() {
    // 如果不显示底部按钮，直接关闭
    if (!showFooter.value) {
      handleCancel();
      return;
    }
    
    try {
      let values = await validate();
      
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } catch (error: any) {
      console.error('表单验证失败或保存失败:', error);
      if (error?.errorFields) {
        const firstField = error.errorFields[0];
        if (firstField) {
          scrollToField(firstField.name, { behavior: 'smooth' });
        }
      }
      return Promise.reject(error);
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

  // 隐藏 a-tab-pane 的内容区域（因为我们用 v-if 在外面显示内容）
  :deep(.ant-tabs-content) {
    display: none;
  }

  // Tab 内容容器样式
  .tab-content-container {
    margin-top: 16px;
    
    .tab-content-pane {
      // 岗位申请表单的容器，需要滚动
      &.scrollable-pane {
        max-height: 600px;
        overflow-y: auto;
        overflow-x: hidden;
        
        // 确保在 Firefox 中也能正常滚动
        scrollbar-width: thin;
      }
    }
  }
</style>
