<template>
  <div>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs" tab-position="left">
      <!--主表区域 -->
      <a-tab-pane tab="基本信息-副高" :key="refKeys[0]" :forceRender="true" :style="tabsStyle">
        <BasicForm @register="registerForm" ref="formRef" />
      </a-tab-pane>
      <!--子表单区域 -->
      <a-tab-pane tab="工作经历" key="xgsResumeWorks" :forceRender="true" :style="tabsStyle">
        <JVxeTable
          keep-source
          resizable
          ref="xgsResumeWorks"
          v-if="xgsResumeWorksTable.show"
          :loading="xgsResumeWorksTable.loading"
          :columns="xgsResumeWorksTable.columns"
          :dataSource="xgsResumeWorksTable.dataSource"
          :height="340"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
        />
      </a-tab-pane>
      <a-tab-pane tab="教育经历" key="xgsResumeEdus" :forceRender="true" :style="tabsStyle">
        <JVxeTable
          keep-source
          resizable
          ref="xgsResumeEdus"
          v-if="xgsResumeEdusTable.show"
          :loading="xgsResumeEdusTable.loading"
          :columns="xgsResumeEdusTable.columns"
          :dataSource="xgsResumeEdusTable.dataSource"
          :height="340"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
        />
      </a-tab-pane>
      <a-tab-pane tab="家庭状况" key="xgsResumeHome" :forceRender="true" :style="tabsStyle">
        <JVxeTable
          keep-source
          resizable
          ref="xgsResumeHome"
          v-if="xgsResumeHomeTable.show"
          :loading="xgsResumeHomeTable.loading"
          :columns="xgsResumeHomeTable.columns"
          :dataSource="xgsResumeHomeTable.dataSource"
          :height="340"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
        />
      </a-tab-pane>
    </a-tabs>

    <div style="width: 100%; text-align: center; margin-top: 10px" v-if="false">
      <a-button preIcon="ant-design:check-outlined" style="width: 126px" type="primary" @click="handleSubmit">提 交</a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { defHttp } from '/@/utils/http/axios';
  import {ref, computed, unref, reactive, onMounted, defineProps, defineExpose} from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts';
  import { formSchema, xgsResumeWorksColumns, xgsResumeEdusColumns, xgsResumeHomeColumns } from '../XgsResumeFG.data';
  import { saveOrUpdate, xgsResumeWorksList, xgsResumeEdusList, xgsResumeHomeList } from '../XgsResumeFG.api';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  const refKeys = ref(['xgsResumeFG', 'xgsResumeWorks', 'xgsResumeEdus', 'xgsResumeHome']);
  const activeKey = ref('xgsResumeFG');
  const xgsResumeWorks = ref();
  const xgsResumeEdus = ref();
  const xgsResumeHome = ref();
  const tableRefs = { xgsResumeWorks, xgsResumeEdus, xgsResumeHome };
  const xgsResumeWorksTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeWorksColumns,
    show: true,
  });
  const xgsResumeEdusTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeEdusColumns,
    show: true,
  });
  const xgsResumeHomeTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeHomeColumns,
    show: true,
  });

  const props = defineProps({
    formData: { type: Object, default: () => {} },
    formBpm: { type: Boolean, default: true },
  });
  const formDisabled = computed(() => {
    if (props.formBpm === true) {
      if (props.formData.disabled === false) {
        return false;
      }
    }
    return true;
  });
  // 是否显示提交按钮
  const showFlowSubmitButton = computed(() => {
    if (props.formBpm === true) {
      if (props.formData.disabled === false) {
        return true;
      }
    }
    return false;
  });

  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 150,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  onMounted(() => {
    initFormData();
  });
  //渲染流程表单数据
  const queryByIdUrl = '/xgsResume/xgsResumeFG/queryById';
  async function initFormData() {
    if (props.formBpm === true && props.formData.dataId) {
      await reset();
      let params = { id: props.formData.dataId };
      const data = await defHttp.get({ url: queryByIdUrl, params });
      //表单赋值
      await setFieldsValue({
        ...data,
      });
      requestSubTableData(xgsResumeWorksList, { id: data.id }, xgsResumeWorksTable, () => {
        xgsResumeWorksTable.show = true;
      });
      requestSubTableData(xgsResumeEdusList, { id: data.id }, xgsResumeEdusTable, () => {
        xgsResumeEdusTable.show = true;
      });
      requestSubTableData(xgsResumeHomeList, { id: data.id }, xgsResumeHomeTable, () => {
        xgsResumeHomeTable.show = true;
      });
      // 隐藏底部时禁用整个表单
      setProps({ disabled: formDisabled.value });
    }
  }

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
    // 弹窗wrapper
    let overflow = 'auto';
    return { height, minHeight, overflow };
  });

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
    //提交表单
    // await saveOrUpdate(values, true);
    return new Promise((resolve, reject) => {
      resolve(values);
    });
  }
  function setDataByPDF(data) {
    setFieldsValue({
      ...data.value,
    });
    xgsResumeWorksTable.dataSource = data.value.xgsResumeWorks;
    xgsResumeEdusTable.dataSource = data.value.xgsResumeEdus;
    xgsResumeHomeTable.dataSource = data.value.xgsResumeHome;
  }
  defineExpose({
    handleSubmit,
    setDataByPDF,
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

<style lang="less">
  // Online表单Tab风格专属样式
  .j-cgform-tab-modal {
    .ant-modal-header {
      padding-top: 8px;
      padding-bottom: 8px;
      border-bottom: none !important;
    }

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
