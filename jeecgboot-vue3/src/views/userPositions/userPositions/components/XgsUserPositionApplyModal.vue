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
    :okButtonProps="{ style: { display: 'none' } }"
  >
    <template v-if="visible">
      <a-card title="流程进度" :bordered="true">
        <a-steps :current="statusIndex" progress-dot size="small">
          <a-step v-for="(item, index) in statusList" :title="item.statusName">
            <template #description>
              <div>{{ item.statusType }}</div>
            </template>
          </a-step>
        </a-steps>
        <a-descriptions :title="currentNode" bordered>
          <a-descriptions-item :label="approvalStatus">{{ flowOpinion }}</a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!--      end-->
      <div class="contentArea">
        <BasicForm @register="registerForm" 
        name="XgsUserPositionApplyForm" 
        :formBpm="formBpm">
          <template #uploadResume="{ model, field }">
            <a-form-item label="文件路径" id="XgsUserResumeFileForm-filePath" name="filePath" v-if=" title == '编辑'">
              <j-upload v-model:value="fileData.filePath" :max-count="1" :multiple=" title == '编辑'" />
              <a-button type="primary" :disabled="!fileData.filePath" @click="analysisResume">简历分析</a-button>
              <div v-text="resumeText" style="width: 800px"></div>
            </a-form-item>
          </template>
        </BasicForm>
      </div>

      <!-- 使用新的统一申请表单 -->
      <XgsApplyForm ref="applyFormRef" 
      :formDisabled="!isDetail" 
      :formBpm="formBpm" />
      
      <!-- 附件下载区域 -->
      <div v-if="otherFilesData" class="other-files-section">
        <a-divider orientation="left">
          <span style="font-size: 16px; font-weight: 600;">附件材料</span>
        </a-divider>
        <FileDownloadList :value="otherFilesData" />
      </div>
    </template>
  </j-modal>
</template>

<script lang="ts" setup>
  import { ref, computed, reactive, unref, nextTick, defineExpose } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../XgsUserPositionApply.data';
  import JModal from '/@/components/Modal/src/JModal/JModal.vue';
  import XgsApplyForm from '@/components/XgsApplyForm/index.vue';
  import JUpload from '@/components/Form/src/jeecg/components/JUpload/JUpload.vue';
  import FileDownloadList from '/@/components/FileList/FileDownloadList.vue';
  import { defHttp } from '@/utils/http/axios';
  import { xgsResumeEdusColumns, xgsResumeHomeColumns, xgsResumeWorksColumns } from '@/views/home/position/components/XgsResumeBase.data';
  import { getDictItems } from '@/views/userPositions/userPositions/XgsUserPositionApply.api';

  const isReady = ref(false);

  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const isUpdate = ref(true);
  const isDetail = ref(false);
  const positionType = ref('');
  const formData = ref<any>({});
  const formBpm = ref(true);
  const visible = ref(false);
  const title = ref('');
  const applyFormRef = ref<any>(null);

  const currentNode = ref('');
  const appplyStatus = ref('');
  const approvalStatus = ref('');
  const flowOpinion = ref('');
  
  // 附件数据
  const otherFilesData = ref<string>('');

  function getFlowOpinion(applyId) {
    defHttp.get({ url: '/resume/xgsFlowOpinions/getDepartRejectByApplyId', params: { applyId: applyId } }).then((data) => {
      if (data) {
        approvalStatus.value = data.approvalStatus;
        flowOpinion.value = data.opinions;
      }
    }).catch((error) => {
      // 如果没有找到审批意见数据，不做处理（新提交的申请可能还没有审批意见）
      console.log('暂无审批意见数据:', error);
      approvalStatus.value = '';
      flowOpinion.value = '';
    });
  }
  
  // 加载附件数据
  function loadOtherFiles(resumeId: string) {
    if (!resumeId) return;
    
    defHttp.get({ url: '/xgsResume/xgsResumeBase/queryById', params: { id: resumeId } }).then((data) => {
      if (data && data.otherFiles) {
        otherFilesData.value = data.otherFiles;
      }
    }).catch((error) => {
      console.error('加载附件数据失败:', error);
    });
  }

  //表单配置
  const [registerForm, { setProps, resetFields, setFieldsValue }] = useForm({
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
    //重置表单
    await resetFields();
    
    isUpdate.value = !!record?.isUpdate;
    isDetail.value = !!record?.showFooter;
    
    // 设置标题
    title.value = !unref(isUpdate) ? '新增' : !unref(isDetail) ? '查看' : '编辑';
    
    formData.value = record.record;
    formData.value.disabled = title.value !== '编辑';
    //设置表单的值
    await setFieldsValue({...formData.value});
    //默认是禁用
    await setProps({ disabled: true });

    positionType.value = record.record.positionType;
    // 当前状态
    currentNode.value = record.record.approvalNode;
    appplyStatus.value = record.record.status;
    getFlowOpinion(record.record.id);
    
    // 加载附件数据
    if (record.record.resumeId) {
      loadOtherFiles(record.record.resumeId);
    }

    let statusTypeList = await getDictItems({ dictCode: '审核节点' });
    statusUI(record.record.approvalNode, record.record.status, statusTypeList.result);
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !record?.showFooter });
    
    // 加载表单数据
    if (applyFormRef.value && record.record) {
      await applyFormRef.value.loadFormData(record.record);
    }
    
    isReady.value = true;
  }

  /**
   * 关闭Modal
   */
  function handleCancel() {
    visible.value = false;
    emit('success');
  }

  /**
   * 确定按钮
   */
  function handleOk() {
    handleCancel();
  }
  //简历文件处理
  const fileData = reactive<Record<string, any>>({
    id: '',
    fileSize: undefined,
    fileName: '',
    fileExt: '',
    fileJson: '',
    resumeInfo: '',
    filePath: '',
    remarks: '',
  });
  const resumeText = ref('');
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
  const xgsResumeWorksTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeWorksColumns,
  });
  function analysisResume() {
    setModalProps({ confirmLoading: true });
    resumeText.value = '请稍等，正在分析简历...';
    console.log('fileData', fileData.value);
    defHttp
      .post({ url: '/resume/xgsUserResumeFile/analysisResume', timeout: 600000, data: fileData })
      .then((data) => {
        if (data) {
          // TODO 在这里做分析后的对应关系
          console.log(data);
          resumeText.value = '示例：在这里做分析后的对应关系，分析结果如下： ' + JSON.stringify(data);
        } else {
          createMessage.warning('解析失败，请上传PDF格式的简历');
        }
      })
      .finally(() => {
        setModalProps({ confirmLoading: false });
      });
  }

  //     流程ui样式 .approvalStatus
  const statusIndex = ref(0);
  const statusList = ref([
    { statusName: '状态1', statusType: '' },
    { statusName: '状态2', statusType: '2' },
    { statusName: '状态3', statusType: '' },
    { statusName: '状态4', statusType: '' },
  ]);

  // statusType:审核节点
  // statusResume:最后一次(当前)审核状态
  // statusTypeList:获取到的审核节点集合
  function statusUI(statusType, statusResume, statusTypeList) {
    const statusText = ref('审核中');
    let thisStatusList: { statusName: string; statusType: string }[] = [];

    statusIndex.value = 0;
    let num = 0;
    let fieldChange = false;
    for (const item of statusTypeList) {
      if(fieldChange){
        thisStatusList.push({ statusName: item.value, statusType: '未通过' });
        fieldChange = false;
        statusIndex.value++;
      }else {
        if (item.value == statusType && statusResume.includes('未通过')) {
          thisStatusList.push({ statusName: item.value, statusType: '已通过' });
          statusText.value = '';
          fieldChange = true;
        } else if (item.value == statusType && statusResume.includes('审核中')) {
          thisStatusList.push({ statusName: item.value, statusType: statusResume });
          statusText.value = '';
        } else {
          thisStatusList.push({ statusName: item.value, statusType: statusText.value });
        }
      }


      if (item.value === statusType) {
        statusIndex.value = num;
      }
      num++;
    }

    thisStatusList[0].statusType = '';
    thisStatusList[thisStatusList.length - 1].statusType = '';

    statusList.value = thisStatusList;
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
  .xgsUserPositionApplyModal .ant-upload-list {
    width: 600px;
  }
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

  //  流程ui样式
</style>
