<template>
    <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896" wrapClassName="j-cgform-tab-modal xgsUserPositionApplyModal">

      <!--        流程ui-->
      <PageWrapper title="" contentBackground>
        <div class="pt-4 m-4 desc-wrap">
          <a-card title="流程进度" :bordered="true">
            <a-steps :current="statusIndex" progress-dot size="small">
              <a-step v-for="(item, index) in statusList" :title="item.statusName">
                <template #description>
                  <div>{{item.statusType}}</div>
                </template>
              </a-step>
<!--                <template #description>-->
<!--                  <div>Jeecg</div>-->
<!--                  <p>2016-12-12 12:32</p>-->
<!--                </template>-->
<!--              </a-step>-->
<!--              <a-step title="部门初审">-->
<!--                <template #description>-->
<!--                  <p>Chad</p>-->
<!--                </template>-->
<!--              </a-step>-->
<!--              <a-step title="财务复核" />-->
<!--              <a-step title="完成" />-->
            </a-steps>
          </a-card>
        </div>
      </PageWrapper>
<!--      end-->
      <div class="contentArea">
        <BasicForm @register="registerForm" name="XgsUserPositionApplyForm" :positionType="positionType" :formData="formData" :formBpm="formBpm" >
          <template #uploadResume="{ model, field }">
            <a-form-item label="文件路径" id="XgsUserResumeFileForm-filePath" name="filePath" v-if="false">
              <j-upload v-model:value="fileData.filePath" :max-count="1" :multiple="false" />
              <a-button type="primary" :disabled="!fileData.filePath" @click="analysisResume">简历分析</a-button>
              <div v-text="resumeText" style="width: 800px"></div>
            </a-form-item>
          </template>
        </BasicForm>
      </div>
<!--      <xgsUserPositionApplyForm ref="registerForm"  :positionType="positionType" :formData="formData" :formBpm="formBpm" />-->

        <div>
          <xgsResumePTForm v-if="positionType === '普通岗位'" :formData="formData" :formBpm="formBpm"></xgsResumePTForm>
          <xgsResumeBSHForm v-else-if="positionType === '博士后岗位'" :formData="formData" :formBpm="formBpm"></xgsResumeBSHForm>
          <xgsResumeFGForm v-else-if="positionType === '副高级以上岗位'" :formData="formData" :formBpm="formBpm"></xgsResumeFGForm>
          <xgsResumeTJForm v-else-if="positionType === '人才派遣岗位'" :formData="formData" :formBpm="formBpm"></xgsResumeTJForm>
          <div v-else>
            未知的 positionType
          </div>
        </div>
    </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, reactive, unref} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import {formSchema} from '../XgsUserPositionApply.data';
    import {saveOrUpdate} from '../XgsUserPositionApply.api';
    import axios from "axios";

    import xgsUserPositionApplyForm from './XgsUserPositionApplyForm.vue';

    import xgsResumeBSHForm from '/@/views/userPositions/xgsResumeBase/xgsResumeBSH/components/xgsResumeBSHForm.vue';
    import xgsResumePTForm from '/@/views/userPositions/xgsResumeBase/xgsResumePT/components/xgsResumeBaseForm.vue';
    import xgsResumeFGForm from '/@/views/userPositions/xgsResumeBase/xgsResumeFG/components/xgsResumeFGForm.vue';
    import xgsResumeTJForm from '/@/views/userPositions/xgsResumeBase/xgsResumeTJ/components/xgsResumeTJForm.vue';
    import JUpload from "@/components/Form/src/jeecg/components/JUpload/JUpload.vue";
    import {defHttp} from "@/utils/http/axios";
    import {
      xgsResumeEdusColumns,
      xgsResumeHomeColumns, xgsResumeWorksColumns
    } from "@/views/home/position/components/XgsResumeBase.data";
    import {PageWrapper} from "@/components/Page";
    import {getDictItems} from "@/views/userPositions/userPositions/XgsUserPositionApply.api";

    const isReady = ref(false);

    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const isDetail = ref(false);
    const positionType = ref("");
    const formData = ref({});
    const formBpm = ref(true);
    //表单配置
    const [registerForm, { setProps,resetFields, setFieldsValue, validate, scrollToField }] = useForm({
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 12}
    });
    //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
      console.log('data', data)
        //重置表单
        await resetFields();
        setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        isDetail.value = !!data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
        }
        positionType.value = data.record.positionType;
        formData.value = {
          id: data.record.id,
          dataId: data.record.resumeId,
          disabled: true
        }

      let statusTypeList = await getDictItems({dictCode: '审核节点'})
      statusUI(data.record.approvalNode, data.record.status, statusTypeList.result)
      console.log("statusList===",statusList)
        // 隐藏底部时禁用整个表单
      setProps({ disabled: !data?.showFooter })
      isReady.value = true;
    });
    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(isDetail) ? '详情' : '查看'));
    //表单提交事件
    // async function handleSubmit(v) {
    //     try {
    //       console.log("registerForm", useForm({
    //         schemas: formSchema,
    //         showActionButtonGroup: false,
    //         baseColProps: {span: 12}
    //       }));
    //       console.log("positionType",positionType.value);
    //         let values = await validate();
    //         // setModalProps({confirmLoading: true});
    //         // //提交表单
    //         await saveOrUpdate(values, isUpdate.value);
    //         // //关闭弹窗
    //         closeModal();
    //         // //刷新列表
    //         emit('success');
    //     } catch ({ errorFields }) {
    //        if (errorFields) {
    //          const firstField = errorFields[0];
    //          if (firstField) {
    //            scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
    //          }
    //        }
    //        return Promise.reject(errorFields);
    //     } finally {
    //         setModalProps({confirmLoading: false});
    //     }
    // }

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
      console.log('fileData', fileData.value)
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
      {statusName:"状态1", statusType:""},
      {statusName:"状态2", statusType:"2"},
      {statusName:"状态3", statusType:""},
      {statusName:"状态4", statusType:""},
      ])

    // statusType:审核节点
    // statusResume:最后一次(当前)审核状态
    // statusTypeList:获取到的审核节点集合
    function statusUI(statusType,statusResume,statusTypeList){
      console.log("statusType==========================",statusType)
      console.log("statusResume==========================",statusResume)
      console.log("statusTypeList==========================",statusTypeList)
      const statusText = ref("已通过")
      let thisStatusList: { statusName: string; statusType: string }[] = [];

      statusIndex.value = 0;
      for(const item of statusTypeList){
        if(item.value == statusType && statusResume == "未通过"){
          thisStatusList.push({statusName:item.value, statusType: "未通过"})
          statusText.value = ""
        }else if(item.value == statusType && statusResume == "审核中"){
          thisStatusList.push({statusName:item.value, statusType: "已通过"})
          statusText.value = ""
        }else {
          thisStatusList.push({statusName:item.value, statusType: statusText.value})
        }

        if(statusText.value != ""){
          statusIndex.value = statusIndex.value + 1;
        }
      }

      thisStatusList[0].statusType = "";
      thisStatusList[thisStatusList.length - 1].statusType = "";

      statusList.value = thisStatusList;

    }

    function statusUI2(statusType){
      if (statusType == '待部门审核'){
        statusIndex.value = 1;
        statusList.value = [
          {statusName:"申请人", statusType:""},
          {statusName:"部门审核", statusType:"待审核"},
          {statusName:"人力处审核", statusType:""},
          {statusName:"初审完成", statusType:""},
        ];
      }else if(statusType == '部门未通过'){
        statusIndex.value = 1;
        statusList.value = [
          {statusName:"申请人", statusType:""},
          {statusName:"部门审核", statusType:"未通过"},
          {statusName:"人力处审核", statusType:""},
          {statusName:"初审完成", statusType:""},
        ];
      }else if(statusType == '待人力处审核'){
        statusIndex.value = 2;
        statusList.value = [
          {statusName:"申请人", statusType:""},
          {statusName:"部门审核", statusType:"已通过"},
          {statusName:"人力处审核", statusType:"待审核"},
          {statusName:"初审完成", statusType:""},
        ];
      }else if(statusType == '人力处未通过'){
        statusIndex.value = 2;
        statusList.value = [
          {statusName:"申请人", statusType:""},
          {statusName:"部门审核", statusType:"已通过"},
          {statusName:"人力处审核", statusType:"已通过"},
          {statusName:"初审完成", statusType:""},
        ];
      }else if(statusType == '初审通过'){
        statusIndex.value = 3;
        statusList.value = [
          {statusName:"申请人", statusType:""},
          {statusName:"部门审核", statusType:""},
          {statusName:"人力处审核", statusType:""},
          {statusName:"初审完成", statusType:""},
        ];
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
