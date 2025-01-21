<template>
  <BasicModal
    ref="modalRef"
    destroyOnClose
    wrapClassName="j-cgform-tab-modal"
    v-bind="$attrs"
    @register="registerModal"
    :width="896"
    @ok="handleSubmit"
  >
    <!-- 标题区域 -->
    <template #title>
      <div class="titleArea">
        <div class="title">{{ title }}</div>
        <div class="right">
          <a-radio-group v-model:value="activeKey">
            <template v-for="(item, index) in tabNav" :key="index">
              <a-radio-button :value="item.tableName">{{ item.tableTxt }}</a-radio-button>
            </template>
          </a-radio-group>
        </div>
      </div>
    </template>
    <!--表单区域 -->
    <div class="contentArea">
      <!--主表区域 -->
      <BasicForm @register="registerForm" ref="formRef" v-show="activeKey == refKeys[0]" name="XgsResumeFGForm" />
      <!--子表区域 -->
      <JVxeTable
        v-show="activeKey == 'xgsResumeWorks'"
        keep-source
        resizable
        ref="xgsResumeWorks"
        :loading="xgsResumeWorksTable.loading"
        :columns="xgsResumeWorksTable.columns"
        :dataSource="xgsResumeWorksTable.dataSource"
        :height="340"
        :disabled="formDisabled"
        :rowNumber="true"
        :rowSelection="true"
        :toolbar="true"
      />
      <JVxeTable
        v-show="activeKey == 'xgsResumeEdus'"
        keep-source
        resizable
        ref="xgsResumeEdus"
        :loading="xgsResumeEdusTable.loading"
        :columns="xgsResumeEdusTable.columns"
        :dataSource="xgsResumeEdusTable.dataSource"
        :height="340"
        :disabled="formDisabled"
        :rowNumber="true"
        :rowSelection="true"
        :toolbar="true"
      />
      <JVxeTable
        v-show="activeKey == 'xgsResumeHome'"
        keep-source
        resizable
        ref="xgsResumeHome"
        :loading="xgsResumeHomeTable.loading"
        :columns="xgsResumeHomeTable.columns"
        :dataSource="xgsResumeHomeTable.dataSource"
        :height="340"
        :disabled="formDisabled"
        :rowNumber="true"
        :rowSelection="true"
        :toolbar="true"
      />
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref, reactive } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts';
  import { formSchema, xgsResumeWorksColumns, xgsResumeEdusColumns, xgsResumeHomeColumns } from '../XgsResumeFG.data';
  import { saveOrUpdate, xgsResumeWorksList, xgsResumeEdusList, xgsResumeHomeList } from '../XgsResumeFG.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const formDisabled = ref(false);
  const modalRef = ref();
  const refKeys = ref(['xgsResumeFG', 'xgsResumeWorks', 'xgsResumeEdus', 'xgsResumeHome']);
  const tabNav = ref<any>([
    { tableName: 'xgsResumeFG', tableTxt: '基本信息-副高' },
    { tableName: 'xgsResumeWorks', tableTxt: '工作经历' },
    { tableName: 'xgsResumeEdus', tableTxt: '教育经历' },
    { tableName: 'xgsResumeHome', tableTxt: '家庭状况' },
  ]);
  const activeKey = ref('xgsResumeFG');
  const xgsResumeWorks = ref();
  const xgsResumeEdus = ref();
  const xgsResumeHome = ref();
  const tableRefs = { xgsResumeWorks, xgsResumeEdus, xgsResumeHome };
  const xgsResumeWorksTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeWorksColumns,
  });
  const xgsResumeEdusTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeEdusColumns,
  });
  const xgsResumeHomeTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeHomeColumns,
  });
  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });
  //表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    //重置表单
    await reset();
    setModalProps({ confirmLoading: false, showCancelBtn: data?.showFooter, showOkBtn: data?.showFooter });
    isUpdate.value = !!data?.isUpdate;
    formDisabled.value = !data?.showFooter;
    if (unref(isUpdate)) {
      //表单赋值
      await setFieldsValue({
        ...data.record,
      });
      requestSubTableData(xgsResumeWorksList, { id: data?.record?.id }, xgsResumeWorksTable);
      requestSubTableData(xgsResumeEdusList, { id: data?.record?.id }, xgsResumeEdusTable);
      requestSubTableData(xgsResumeHomeList, { id: data?.record?.id }, xgsResumeHomeTable);
    }
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });
  //方法配置
  const [handleChangeTabs, handleSubmit, requestSubTableData, formRef] = useJvxeMethod(
    requestAddOrEdit,
    classifyIntoFormData,
    tableRefs,
    activeKey,
    refKeys
  );
  // 弹窗tabs滚动区域的高度
  const tabsStyle = computed(() => {
    let height: Nullable<string> = null;
    let minHeight = '100px';
    let maxHeight: Nullable<string> = '500px';
    // 弹窗wrapper
    let modalWrapperRef = modalRef.value?.modalWrapperRef;
    if (modalWrapperRef) {
      if (modalWrapperRef.fullScreen) {
        height = 'calc(' + modalWrapperRef.spinStyle.height + ' - 50px)';
        maxHeight = null;
      }
    }
    let overflow = 'auto';
    return { height, minHeight, maxHeight, overflow };
  });
  //设置标题
  const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));
  //重置
  async function reset() {
    await resetFields();
    activeKey.value = 'xgsResumeFG';
    xgsResumeWorksTable.dataSource = [];
    xgsResumeEdusTable.dataSource = [];
    xgsResumeHomeTable.dataSource = [];
  }
  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, allValues.formValue);
    return {
      ...main, // 展开
      xgsResumeWorksList: allValues.tablesValue[0].tableData,
      xgsResumeEdusList: allValues.tablesValue[1].tableData,
      xgsResumeHomeList: allValues.tablesValue[2].tableData,
    };
  }
  //表单提交事件
  async function requestAddOrEdit(values) {
    try {
      setModalProps({ confirmLoading: true });
      //提交表单
      await saveOrUpdate(values, isUpdate.value);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
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

  .titleArea {
    display: flex;
    align-content: center;
    padding-right: 70px;
    .title {
      margin-right: 16px;
      line-height: 32px;
    }
    .right {
      overflow-x: auto;
      overflow-y: hidden;
      flex: 1;
      white-space: nowrap;
      .ant-radio-group {
        font-weight: normal;
      }
    }
  }

  html[data-theme='light'] {
    .right {
      .ant-radio-group {
        :deep(.ant-radio-button-wrapper:not(.ant-radio-button-wrapper-checked)) {
          color: #555;
        }
      }
    }
  }
</style>

<style lang="less">
  // Online表单Tab风格专属样式
  .j-cgform-tab-modal {
    .contentArea {
      padding: 20px 1.5% 0;
    }

    //.ant-modal-header {
    //  padding-top: 8px;
    //  padding-bottom: 8px;
    //  border-bottom: none !important;
    //}

    .ant-modal .ant-modal-body > .scrollbar,
    .ant-tabs-nav .ant-tabs-tab {
      padding-top: 0;
    }

    .ant-tabs-top-bar {
      width: calc(100% - 55px);
      position: relative;
      left: -14px;
    }

    .ant-tabs .ant-tabs-top-content > .ant-tabs-tabpane {
      overflow: hidden auto;
    }
  }
</style>
