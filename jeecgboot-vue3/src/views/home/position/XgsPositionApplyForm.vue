<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm">
          <a-row>
            <a-col :span="12">
              <a-form-item label="申请人" v-bind="validateInfos.userName" id="XgsPositionApplyForm-userName" name="userName">
                <a-input v-model:value="formData.userName" placeholder="请输入申请人姓名" disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位名称" v-bind="validateInfos.positionName" id="XgsPositionApplyForm-positionName" name="positionName">
                <a-input v-model:value="formData.positionName" placeholder="请输入岗位名称" disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位部门" v-bind="validateInfos.positionDept" id="XgsPositionApplyForm-positionDept" name="positionDept">
                <a-input v-model:value="formData.positionDept" placeholder="请输入岗位部门" disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位类型" v-bind="validateInfos.positionType" id="XgsPositionApplyForm-positionType" name="positionType">
                <a-input v-model:value="formData.positionType" placeholder="请输入岗位类型" disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="false">
              <a-form-item label="申请状态" v-bind="validateInfos.status" id="XgsPositionApplyForm-status" name="status">
                <a-input v-model:value="formData.status" placeholder="请输入申请状态" style="width: 100%" disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="false">
              <a-form-item label="简历名称" v-bind="validateInfos.resumeFile" id="XgsPositionApplyForm-resumeName" name="resumeFile">
                <a-input v-model:value="formData.resumeFile" placeholder="请输入简历名称" allow-clear />
                <!--                <a-select v-model:value="formData.resumeName" placeholder="请选择简历名称" allow-clear>-->
                <!--                  <a-select-option v-for="resume in resumeOptions" :key="resume.value" :value="resume.value">-->
                <!--                    {{ resume.label }}-->
                <!--                  </a-select-option>-->
                <!--                </a-select>-->
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="false">
              <a-form-item
                label="备注"
                v-bind="validateInfos.mark"
                id="XgsPositionApplyForm-mark"
                name="mark"
                :label-col="{ span: 2 }"
                :wrapper-col="{ span: 22 }"
              >
                <a-textarea
                  v-model:value="formData.mark"
                  placeholder="请输入申请备注"
                  allow-clear
                  :auto-size="{ minRows: 1, maxRows: 10 }"
                  style="width: 93%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="上传PDF简历" id="XgsUserResumeFileForm-filePath" name="filePath">
                <j-upload v-model:value="formData.filePath" :max-count="1" :multiple="false" accept=".pdf" />
                <a-button type="primary" :disabled="!formData.filePath" @click="analysisResume">自动填充简历信息</a-button>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <!--        //此处引入简历组件，展示简历填报页面-->
        <div>
          <xgsResumePTForm ref="formPT" v-if="formData.positionType === '普通岗位'" :form-data="formData" :form-bpm="true" />
          <xgsResumeBSHForm ref="formBSH" v-else-if="formData.positionType === '博士后岗位'" :form-data="formData" :form-bpm="true" />
          <xgsResumeFGForm ref="formFG" v-else-if="formData.positionType === '副高级岗位'" :form-data="formData" :form-bpm="true" />
          <xgsResumeTJForm ref="formTJ" v-else-if="formData.positionType === '专家推荐岗位'" :form-data="formData" :form-bpm="true" />
          <div v-else>
            <!-- 可选：当 positionType 不匹配任何已知类型时显示的内容 -->
            未知的岗位类型
          </div>
        </div>
        <!--        <component :is="currentComponent" :form-data="formData" :form-bpm="formBpm" />-->
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted, toRaw } from 'vue';
  // import XgsResumeForm from '/@/views/xgsResume/components/xgsResumeBaseForm.vue';
  import xgsResumeBSHForm from '/@/views/xgsResumeBase/xgsResumeBSH/components/xgsResumeBSHForm.vue';
  import xgsResumePTForm from '/@/views/xgsResumeBase/xgsResumePT/components/xgsResumeBaseForm.vue';
  import xgsResumeFGForm from '/@/views/xgsResumeBase/xgsResumeFG/components/xgsResumeFGForm.vue';
  import xgsResumeTJForm from '/@/views/xgsResumeBase/xgsResumeTJ/components/xgsResumeTJForm.vue';

  import { useUserStore } from '/@/store/modules/user';
  import JUpload from '../../../components/Form/src/jeecg/components/JUpload/JUpload.vue';
  import {render} from "@/utils/common/renderUtils";
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import { doPositionApply } from './XgsPositionApply.api';
  import { Form } from 'ant-design-vue';
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  const positionApplyStore = usePositionApplyStoreWithOut();

  // 使用 defineComponent 注册组件（可选）
  import { defineComponent } from 'vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: {
      type: Object,
      default: () => ({
        userName: '',
        positionName: '',
        positionDept: '',
        positionType: '',
        status: '',
        resumeName: '',
        resumeFile: '',
        mark: '',
        resumeId: '',
        positionId: '',
        applyId: '',
        disabled: false,
      }),
    },

    formBpm: { type: Boolean, default: true },
  });

  const formRef = ref();
  const formPT = ref();
  const formBSH = ref();
  const formFG = ref();
  const formTJ = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = ref({
    userName: '',
    positionName: '',
    positionDept: '',
    positionType: '',
    status: '',
    resumeName: '',
    resumeFile: '',
    mark: '',
    resumeId: '',
    positionId: '',
    applyId: '',
    disabled: false,
  });
  const resumeFormData = ref({
    userName: '',
    resumeName: '',
    resumeFile: '',
    resumeId: '',
    dataId: '',
    disabled: false,
  });

  // 分析简历
  const resumeText = ref('');
  function analysisResume() {
    confirmLoading.value = true;
    resumeText.value = '请稍等，正在分析简历...';
    defHttp
      .post({ url: '/resume/xgsUserResumeFile/analysisResume', timeout: 600000, data: formData.value })
      .then((data) => {
        if (data && data.fileJson) {
          let resumeData = JSON.parse(data.fileJson);
          fillResumeInfo(resumeData);
          resumeText.value = '';
        } else {
          createMessage.warning('解析失败，请上传PDF格式的简历');
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }
  function fillResumeInfo(resumeData) {
    // console.log(resumeData);
    // 在这里根据解析结果填充表单数据
    const pdfData = ref({
      ...resumeData,
    });
    if (formData.value.positionType === '普通岗位') {
      formPT.value.setDataByPDF(pdfData);
    }
    if (formData.value.positionType === '博士后岗位') {
      formBSH.value.setDataByPDF(pdfData);
    }
    if (formData.value.positionType === '副高级岗位') {
      formFG.value.setDataByPDF(pdfData);
    }
    if (formData.value.positionType === '专家推荐岗位') {
      formTJ.value.setDataByPDF(pdfData);
    }
  }
  const initFormData = async () => {
    resumeFormData.value = {
      userName: formData.value.userName,
      resumeName: formData.value.resumeName,
      resumeFile: formData.value.resumeFile,
      resumeId: formData.value.resumeId,
      dataId: formData.value.resumeId,
      disabled: formData.value.disabled,
    };
  };
  onMounted(() => {
    initFormData();
  });

  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
    name: [{ required: true, message: 'Please input name' }],
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, {
    immediate: false,
  });

  // 表单禁用
  const disabled = computed(() => {
    if (props.formBpm === true) {
      if (props.formData.disabled === false) {
        return false;
      } else {
        return true;
      }
    }
    return props.formDisabled;
  });

  /**
   * 新增
   */
  function add(record) {
    // 先根据 岗位id，userid 查询是否已存在，如果存在，则直接编辑，否则新增
    // positionApplyStore.currPositionApply = record;
    // record = positionStore.currApplyPosition;
    formData.value.positionType = '';
    edit(record);
  }
  const userStore = useUserStore();
  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      let tmpData = {};
      record.realname = userStore.getUserInfo.realname;
      record.username = userStore.getUserInfo.username;

      tmpData['filePath'] = '';
      tmpData['positionDept'] = record.dept_dictText;
      tmpData['positionId'] = record.id;
      tmpData['positionName'] = record.positionName;
      tmpData['positionType'] = record.category;
      tmpData['resumeName'] = record.realname + record.username + '_' + record.positionName;
      tmpData['userName'] = record.realname;
      tmpData['resumeId'] = '';

      //赋值
      Object.assign(formData.value, tmpData);
      console.log(3333, formData.value);
    });
    console.log('edit>>>>end>>>>>', formData);
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    // 触发表单验证
    try {
      await validate();
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }
    let resumeFormData = {};
    try {
      if (formData.value.positionType === '普通岗位') {
        resumeFormData = await formPT.value.handleSubmit();
      }
      if (formData.value.positionType === '博士后岗位') {
        resumeFormData = await formBSH.value.handleSubmit();
      }
      if (formData.value.positionType === '副高级岗位') {
        resumeFormData = await formFG.value.handleSubmit();
      }
      if (formData.value.positionType === '专家推荐岗位') {
        resumeFormData = await formTJ.value.handleSubmit();
      }
    } catch (e) {
      createMessage.warning('表单验证失败');
      return Promise.reject(e);
    }
    // 提交表单
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.value.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in formData) {
      //如果该数据是数组并且是字符串类型
      if (formData[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = formData[data].join(',');
        }
      }
    }
    // 附件路径
    model.value.resumeFile = formData.value.filePath;
    let params = {
      xgsPositionApply: model.value,
      xgsResumeBasePage: resumeFormData,
    };
    doPositionApply(params)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
  .form-item-full-width .ant-form-item {
    display: flex;
    align-items: flex-start;
    width: 100%;
  }

  .form-item-full-width .ant-form-item-label {
    width: auto;
  }

  .form-item-full-width .ant-form-item-control-wrapper {
    flex: 1;
  }
</style>
