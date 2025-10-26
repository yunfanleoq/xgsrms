<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm">
          <a-row>
            <a-col :span="12">
              <a-form-item label="申请人" v-bind="validateInfos.userName" id="XgsPositionApplyForm-userName" name="userName">
                <a-input v-model:value="formData.userName" placeholder=""  allow-clear disabled></a-input>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="岗位名称" v-bind="validateInfos.positionName" id="XgsPositionApplyForm-positionName" name="positionName">
                <a-input v-model:value="formData.positionName" placeholder=""  allow-clear disabled></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位部门" v-bind="validateInfos.positionDept" id="XgsPositionApplyForm-positionDept" name="positionDept">
                <a-input v-model:value="formData.positionDept" placeholder=""  allow-clear disabled></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位类型" v-bind="validateInfos.positionType" id="XgsPositionApplyForm-positionType" name="positionType">
                <a-input v-model:value="formData.positionType" placeholder=""  allow-clear disabled></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="上传PDF简历" id="XgsPositionApplyForm-filePath" name="filePath">
                <div style="display: flex; align-items: flex-start; gap: 10px;">
                  <div style="flex: 1; min-width: 0;">
                    <j-upload v-model:value="formData.filePath" :max-count="1" :multiple="false" accept=".pdf" />
                  </div>
                  <div style="flex-shrink: 0;">
                    <a-button type="primary" :disabled="!formData.filePath" @click="analysisResume">自动填充</a-button>
                  </div>
                </div>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="false">
              <a-form-item label="申请状态" v-bind="validateInfos.status" id="XgsPositionApplyForm-status" name="status">
                <a-input v-model:value="formData.status" placeholder="" style="width: 100%" disabled />
              </a-form-item>
            </a-col>

            <a-col :span="12" v-show="false">
              <a-form-item label="简历名称" v-bind="validateInfos.resumeName" id="XgsPositionApplyForm-resumeName" name="resumeName">
                <a-input v-model:value="formData.resumeName" placeholder="请输入简历名称"  allow-clear ></a-input>
              </a-form-item>
            </a-col>

            <a-col :span="12" v-show="false">
              <a-form-item label="备注" v-bind="validateInfos.mark" id="XgsPositionApplyForm-mark" name="mark" :label-col="{ span: 2 }" :wrapper-col="{ span: 22 }">
                <a-textarea v-model:value="formData.mark" placeholder="" allow-clear :auto-size="{ minRows: 1, maxRows: 10 }" style="width: 93%;" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        
        <!-- 重构后的表单组件 -->
        <div>
          <!-- 使用我们重构的表单组件 -->
          <xgsApplyForm 
            ref="resumeFormRef"
            :formData="resumeFormData"
            :formDisabled="disabled"
            :formBpm="formBpm"
            :hideSubmitBtn="hideSubmitBtn"
          />
        </div>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  // 引入重构后的表单组件
  import xgsApplyForm from '/@/views/xgsApplyForm/xgsApplyForm.vue';
  
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { doPositionApply } from '/@/views/home/position/XgsPositionApply.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  import JUpload from '/@/components/Form/src/jeecg/components/JUpload/JUpload.vue';

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    hideSubmitBtn: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({
        id: '',
        userName: '',
        positionName: '',
        positionDept: '',
        positionType: '',
        status: '',
        resumeName: '',
        mark: '',
        resumeId: '',
        positionId: '',
        applyId: '',
        disabled: false,
        filePath: '',
      })},
    formBpm: { type: Boolean, default: true }
  });

  const resumeFormRef = ref();
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = ref({
    id: '',
    userName: '',
    positionName: '',
    positionDept: '',
    positionType: '',
    status: '',
    resumeName: '',
    mark: '',
    resumeId: '',
    positionId: '',
    applyId: '',
    disabled: false,
    filePath: '', // PDF简历文件路径
  })
  
  const resumeFormData = ref({
    userName: '',
    resumeName: '',
    resumeId: '',
    dataId: '',
    disabled: false,
    positionType: '',
    positionName: '',
    positionDept: '',
    positionId: '',
    applyDept: '',
    applyPosition: '',
  })

  const initFormData = async () => {
    resumeFormData.value = {
      userName: formData.value.userName,
      resumeName: formData.value.resumeName,
      resumeId: formData.value.resumeId,
      dataId: formData.value.resumeId,
      disabled: formData.value.disabled,
      positionType: formData.value.positionType,
      positionName: formData.value.positionName,
      positionDept: formData.value.positionDept,
      positionId: formData.value.positionId,
      applyDept: formData.value.positionDept,
      applyPosition: formData.value.positionName,
    }
  }
  
  // 分析简历
  function analysisResume() {
    confirmLoading.value = true;
    defHttp
      .post({ url: '/resume/xgsUserResumeFile/analysisResume', timeout: 600000, data: formData.value })
      .then((data) => {
        if (data && data.fileJson) {
          let resumeData = JSON.parse(data.fileJson);
          fillResumeInfo(resumeData);
          createMessage.success('简历解析成功');
        } else {
          createMessage.warning('解析失败，请上传PDF格式的简历');
        }
      })
      .catch((error) => {
        console.error('简历解析失败:', error);
        createMessage.error('简历解析失败，请重试');
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }
  
  // 填充简历信息
  function fillResumeInfo(resumeData) {
    // 组装PDF数据
    const pdfData = ref({
      ...resumeData,
    });
    
    // 调用重构后组件的 setDataByPDF 方法填充数据
    if (resumeFormRef.value) {
      resumeFormRef.value.setDataByPDF(pdfData);
    }
  }
  
  onMounted(() => {
    initFormData();
  })

  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  
  //表单验证
  const validatorRules = reactive({});
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

  // 表单禁用
  const disabled = computed(() => {
    if(props.formBpm === true) {
      if(props.formData.disabled === false) {
        return false;
      } else {
        return true;
      }
    }
    return props.formDisabled;
  });

  //页面完全加载完成并显示一秒后初始化数据
  nextTick(() => {
    setTimeout(() => {
      console.log('Props formData:', props.formData);
      formData.value.positionType = props.formData.category || '普通岗位';
      formData.value.resumeId = props.formData.resumeId || '';
      formData.value.positionId = props.formData.positionId || '';
      formData.value.applyId = props.formData.applyId || '';
      formData.value.userName = props.formData.userName || '';
      formData.value.positionName = props.formData.positionName || '';
      formData.value.positionDept = props.formData.positionDept || '';
      formData.value.status = props.formData.status || '';
      formData.value.resumeName = props.formData.resumeName || '';
      formData.value.mark = props.formData.mark || '';
      
      // 同步数据到简历表单
      initFormData();
    }, 1000);
  });
  
  /**
   * 新增
   */
  function add(record) {
    console.log('添加记录:', record);
    edit(record);
  }

  import { useUserStore } from '/@/store/modules/user';
  const userStore = useUserStore();
  /**
   * 编辑
   */
  function edit(record) {
    console.log('编辑记录:', record);
    nextTick(() => {
      resetFields();
      let tmpData = {};
      record.realname = userStore.getUserInfo.realname;
      record.username = userStore.getUserInfo.username;

      tmpData['filePath'] = '';
      tmpData['positionDept'] = record.positionDept;
      tmpData['positionName'] = record.positionName;
      tmpData['positionType'] = record.positionType;
      tmpData['resumeName'] = record.realname + record.username + '_' + record.positionName;
      tmpData['userName'] = record.realname;
      tmpData['resumeId'] = '';
      tmpData['positionId'] = record.id || record.positionId;

      // 赋值到表单数据
      Object.assign(formData.value, tmpData);
      
      // 同步数据到简历表单
      initFormData();
    });
  }

  /**
   * 提交数据 - 重构版：直接调用 doPositionApply 接口
   */
  async function submitForm() {
    try {
      // 1. 触发表单验证
      await validate();
      confirmLoading.value = true;
      
      // 2. 验证并获取简历表单数据（不保存）
      let resumeData: any = null;
      if (resumeFormRef.value) {
        try {
          // 只验证表单，不保存
          await resumeFormRef.value.validateForm();
          console.log('✅ 表单验证通过');
          
          // 获取表单数据（不保存）
          resumeData = resumeFormRef.value.getFormData();
          console.log('📋 获取到的简历数据:', resumeData);
        } catch (error: any) {
          confirmLoading.value = false;
          console.error('❌ 简历表单验证失败:', error);
          createMessage.error(error.message || '简历表单验证失败，请检查表单数据');
          return Promise.reject(error);
        }
      }
      
      // 3. 检查简历数据
      if (!resumeData) {
        confirmLoading.value = false;
        createMessage.error('获取简历数据失败，请重试');
        return Promise.reject(new Error('获取简历数据失败'));
      }
      
      // 4. 组装完整的申请数据（按照后端 XgsPositionApplyVO 格式）
      const applyData = {
        // 岗位ID
        positionId: formData.value.positionId,
        
        // 岗位申请信息
        xgsPositionApply: {
          positionId: formData.value.positionId,
          resumeName: formData.value.resumeName,
          userName: formData.value.userName,
          positionName: formData.value.positionName,
          positionDept: formData.value.positionDept,
          positionType: formData.value.positionType,
          status: '已提交',
          mark: formData.value.mark,
          filePath: formData.value.filePath,
        },
        
        // 简历信息（包含主表和子表）
        xgsResumeBasePage: {
          // 申请岗位信息
          applyUserName: formData.value.userName,
          applyPositionName: formData.value.positionName,
          applyPositionDept: formData.value.positionDept,
          applyPositionType: formData.value.positionType,
          
          // 简历主表数据
          ...resumeData,
          
          // 子表数据（已经在 resumeData 中包含了）
          // xgsResumeWorksList: 工作经历
          // xgsResumeEdusList: 教育经历
          // xgsResumeHomeList: 家庭状况
          // xgsResumeResearchResultList: 工作主要业绩
          // xgsResumePositionDescriptionList: 应聘岗位陈述
          // xgsResumeResearchDirectionList: 研究方向与专长
          // xgsResumeResearchPaperList: 论文专著专利
        }
      };
      
      console.log('组装的申请数据:', applyData);
      
      // 5. 调用岗位申请接口（一次性提交简历和申请）
      try {
        const res = await doPositionApply(applyData);
        console.log('申请提交结果:', res);
        
        if (res.success) {
          createMessage.success('申请提交成功');
          emit('ok');
        } else {
          createMessage.warning(res.message || '申请提交失败');
          return Promise.reject(new Error(res.message || '申请提交失败'));
        }
      } catch (error: any) {
        console.error('申请提交失败:', error);
        createMessage.error(error.message || '申请提交失败，请重试');
        return Promise.reject(error);
      } finally {
        confirmLoading.value = false;
      }
    } catch (error: any) {
      console.error('表单验证或提交失败:', error);
      confirmLoading.value = false;
      createMessage.error(error.message || '提交失败，请检查表单数据');
      return Promise.reject(error);
    }
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
