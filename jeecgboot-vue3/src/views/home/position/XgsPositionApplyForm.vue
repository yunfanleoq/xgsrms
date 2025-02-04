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
            未知的 positionType
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
    console.log(resumeData);
    // {
    //   "教育背景": {
    //   "学校": "上海大学",
    //     "时间": "2012.09-2016.07",
    //     "学位": "本科",
    //     "主修课程": [
    //     "基础会计学",
    //   ]
    // },
    //   "工作经验": [
    //   {
    //     "时间": "2016.03-至今",
    //     "公司": "贵泽实业有限公司",
    //     "职责": [
    //       "负责本部的行政人事管理和日常事务，协助总监搞好各部门之间的综合协调，落实公司规章制度，沟通内外联系，保证上情下达和下情上报，负责对会议文件决定的事项进行催办、查办和落实，负责全公司组织系统及工作职责研讨和修订。",
    //       "编制公司人事管理制度，规避各项人事风险。",
    //       "负责招聘工作，制定公司的人力资源发展计划，确保人才梯队发展和人才储备及培养。",
    //       "督导公司各项行政、人事制度的执行，以及各项行政人事工作的进展情况，并采取必要的措施。"
    //     ],
    //     "职位": "行政主管"
    //   },
    //   {
    //     "时间": "2015.03-2016.01",
    //     "公司": "一路网络科技有限公司",
    //     "职责": [
    //       "负责中心的接待工作；",
    //       "负责中心的行政事务及前台管理；",
    //       "协助处理客户服务及简单客诉；",
    //       "负责中心简单财务管理，资产管控；"
    //     ],
    //     "职位": "行政助理"
    //   }
    // ]
    // }
    // {title: '本人照片', align: 'center', dataIndex: 'photograph', customRender: render.renderImage,},
    // {title: '性别', align: 'center', dataIndex: 'sex_dictText',},
    // {title: '籍贯', align: 'center', dataIndex: 'nativePlace',},
    // {title: '出生年月', align: 'center', dataIndex: 'birthday', customRender: ({ text }) => {text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;return text;},},
    // {title: '民族', align: 'center', dataIndex: 'nation',},
    // {title: '身份证号', align: 'center', dataIndex: 'idNumber',},
    // {title: '政治面貌', align: 'center', dataIndex: 'politicBackground',},
    // {title: '户口所在地', align: 'center', dataIndex: 'hukou',},
    // {title: '是否应届毕业生', align: 'center', dataIndex: 'yjbys_dictText',},
    // {title: '毕业院校', align: 'center', dataIndex: 'graduateCollege',},
    // {title: '学位', align: 'center', dataIndex: 'degree',},
    // {title: '专业', align: 'center', dataIndex: 'profession',},
    // {title: '毕业时间', align: 'center', dataIndex: 'graduateDate', customRender: ({ text }) => {text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;return text;},},
    // {title: '参加工作时间', align: 'center', dataIndex: 'workDate', customRender: ({ text }) => {text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;return text;},},
    // {title: '目前工作单位', align: 'center', dataIndex: 'workUnit',},
    // {title: '档案所在单位', align: 'center', dataIndex: 'personFilesUnit',},
    // {title: '现行政职务', align: 'center', dataIndex: 'adminPosition',},
    // {title: '任职时间', align: 'center', dataIndex: 'adminPositionDate', customRender: ({ text }) => {text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;return text;},},
    // {title: '现岗位', align: 'center', dataIndex: 'professionLevel',},
    // {title: '聘任时间', align: 'center', dataIndex: 'professionLevelDate', customRender: ({ text }) => {text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;return text;},},
    // 在这里根据解析结果填充表单数据
    const pdfData = ref({
      name: resumeData['基本信息']['姓名'],
      email: resumeData['基本信息']['邮箱'],
      mobile: resumeData['基本信息']['手机'],
      education: resumeData['基本信息']['学历'],
      // formData.value.userName = resumeData['基本信息']['姓名'];
      // formData.value.userName = resumeData['基本信息']['邮箱'];
      // formData.value.userName = resumeData['基本信息']['手机'];
      // formData.value.userName = resumeData['基本信息']['微信'];
      // formData.value.userName = resumeData['基本信息']['地址'];
      // formData.value.userName = resumeData['基本信息']['学历'];
      // formData.value.userName = resumeData['基本信息']['年龄'];
      // formData.value.userName = resumeData['基本信息']['求职意向'];
      // formData.value.positionName = resumeData.position;
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

  // //页面完全加载完成并 显示一秒后 打印 formData，
  // setTimeout(() => {
  //   nextTick(() => {
  //     console.log('...............................props', props);
  //     console.log('》》》》》》》》》》》》》》》props.formData', props.formData);
  //     // Object.assign(formData , props.formData);
  //     formData.value.positionType = props.formData.category || '普通岗位';
  //     console.log('===============================');
  //     formData.value.resumeId = props.formData.resumeId || '';
  //     console.log('-------------------------------');
  //     formData.value.positionId = props.formData.positionId || '';
  //     formData.value.applyId = props.formData.applyId || '';
  //     formData.value.userName = props.formData.userName || '';
  //     formData.value.positionName = props.formData.positionName || '';
  //     formData.value.positionDept = props.formData.positionDept || '';
  //     formData.value.status = props.formData.status || '';
  //     formData.value.resumeName = props.formData.resumeName || '';
  //     formData.value.mark = props.formData.mark || '';
  //
  //     console.log('》》》》》》》》》》》》》》》formData', formData);
  //     // initFormdata();
  //   });
  // }, 1000);
  /**
   * 新增
   */
  function add(record) {
    // 先根据 岗位id，userid 查询是否已存在，如果存在，则直接编辑，否则新增
    console.log('add111111111', record);

    // positionApplyStore.currPositionApply = record;
    // record = positionStore.currApplyPosition;
    console.log('add222222222', positionApplyStore.currPositionApply);
    edit(record);
  }

  import { useUserStore } from '/@/store/modules/user';
  import JUpload from '../../../components/Form/src/jeecg/components/JUpload/JUpload.vue';
  import {render} from "@/utils/common/renderUtils";
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

      tmpData['positionDept'] = record.dept_dictText;
      tmpData['positionName'] = record.positionName;
      tmpData['positionType'] = record.category;
      tmpData['resumeName'] = record.realname + record.username + '_' + record.positionName;
      tmpData['userName'] = record.realname;
      tmpData['resumeId'] = '';

      //赋值
      Object.assign(formData.value, tmpData);
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
    let params = {
      xgsPositionApply: model.value,
      resumeFormData,
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
