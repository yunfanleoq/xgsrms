<template>
  <j-modal
    :title="title"
    :width="'80%'"
    :visible="visible"
    :maskClosable="false"
    :destroyOnClose="true"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :switchFullscreen="true"
    class="xgs-flow-opinions-modal"
  >
    <template v-if="!showFooter" #footer>
      <a-button @click="handleCancel">关闭</a-button>
    </template>
    <!-- a-tabs 仅用于显示标题和切换状态，不包含内容 -->
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="审核意见" />
      <a-tab-pane key="2" tab="岗位申请单" />
    </a-tabs>

    <!-- 表单内容区域，使用 v-show 控制显示，避免组件销毁导致表单数据丢失 -->
    <div class="tab-content-container">
      <!-- 第一个 Tab: 审核意见表单 -->
      <div v-show="activeKey === '1'" class="tab-content-pane">
        <BasicForm @register="registerForm" name="XgsFlowOpinionsForm" />
      </div>

      <!-- 第二个 Tab: 岗位申请表单 -->
      <div v-show="activeKey === '2'" class="tab-content-pane">
        <XgsApplyForm ref="registerFormResume" :formDisabled="true" :formBpm="false" :dataId="resumeId" />
      </div>
    </div>
  </j-modal>
</template>

<script lang="ts" setup>
import {ref, unref, nextTick, defineExpose} from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../XgsFlowOpinions.data';
  import { saveOrUpdate } from '../XgsFlowOpinions.api';
  import { useUserStore } from '@/store/modules/user';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';
  
  // Emits声明
  const emit = defineEmits(['success', 'register']);
  const visible = ref(false);
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
   * 打开弹窗
   */
  async function open(data: any) {
    visible.value = true;
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
      
      // 延迟2秒后重新计算导航位置，确保DOM完全渲染
      setTimeout(() => {
        if (registerFormResume.value && typeof registerFormResume.value.computeAffixOffset === 'function') {
          registerFormResume.value.computeAffixOffset();
        }
      }, 2000);
    }
    
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  }

  /**
   * 关闭Modal
   */
  function handleCancel() {
    visible.value = false;
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
      visible.value = false;
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
  
  // 暴露方法给父组件
  defineExpose({
    open,
  });
</script>

<style lang="less" scoped>
  /** 隐藏确定按钮的样式 */
  .jee-hidden {
    display: none !important;
  }

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
  }
</style>
