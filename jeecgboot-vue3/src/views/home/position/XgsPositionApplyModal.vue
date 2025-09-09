<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    @ok="handleOk"
    :okButtonProps="{ class: { 'jee-hidden': disableSubmitButton } }"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <XgsPositionApplyForm ref="registerForm" @ok="submitCallback" :formData="formData" :formDisabled="disableSubmit" :formBpm="false" />
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose, onMounted } from 'vue';
  import XgsPositionApplyForm from './XgsPositionApplyForm.vue';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';
  import { defHttp } from '@/utils/http/axios';
  import { useMessage } from '@/hooks/web/useMessage';

  const title = ref<string>('');
  const width = ref<number>(1200);
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const disableSubmitButton = ref<boolean>(false);
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
   * 初始化表单, 表单初始化,启动时候，触发 add，传参数 record
   */
  //   async function initFormdata(record) {
  //
  //     await nextTick();
  //     console.log('initFormdata', props.formData);
  //     registerForm.value.add(record);
  //   }
  // onMounted(() => {
  //   console.log('nextTick props.formData', props.formData);
  //   initFormdata(props.formData);
  // });
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

  const { createMessage } = useMessage();
  // 检查 是否已经申请
  function checkHasApplied(jobId) {
    return defHttp.post(
      { url: '/positions/xgsPositionApply/checkApplyByPosId', timeout: 600000, data: { positionId: jobId } },
      { isTransformResponse: false }
    );
  }

  function addJob(record) {
    checkHasApplied(record.id).then((data) => {
      if (data && data.success) {
        title.value = '在线申请';
        visible.value = true;
        nextTick(() => {
          let wrap = Array.from(document.getElementsByClassName('ant-modal-body'));
          wrap[0].scrollTop = 0;
          registerForm.value.add(record);
        });
      } else {
        createMessage.warning('岗位已申请，请勿重复申请！');
        handleCancel();
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
    nextTick(() => {
      registerForm.value.edit(record);
    });
  }

  /**
   * 确定按钮点击事件
   */
  function handleOk() {
    registerForm.value.submitForm();
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
<style lang="less" scoped></style>
