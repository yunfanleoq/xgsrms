<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-row :gutter="20">
          <a-col :span="12">
            <!-- 优化后的只读信息展示区域 -->
            <div class="position-info-card">
              <div class="info-header">
                <span class="info-header-title">申请岗位信息</span>
              </div>
              <div class="info-content">
                <div class="info-item">
                  <span class="info-label">申请人：</span>
                  <span class="info-value">{{ formData.userName || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">岗位名称：</span>
                  <span class="info-value highlight">{{ formData.positionName || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">岗位部门：</span>
                  <span class="info-value">{{ formData.positionDept || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">岗位类型：</span>
                  <span class="info-value">{{ formData.positionType || '-' }}</span>
                </div>
              </div>
            </div>

          </a-col>
          <a-col :span="12">

            <!-- 文件上传区域 -->
            <div class="upload-section-card">
              <div class="upload-header">
                <span class="upload-header-title">上传简历文件</span>
                <span class="upload-hint">支持 PDF 格式，上传后可自动填充表单信息</span>
              </div>
              <div class="upload-content">
                <div class="upload-area">
                  <j-upload v-model:value="formData.filePath" :max-count="1" :multiple="false" accept=".pdf" />
                </div>
                <div class="upload-action">
                  <a-button 
                    type="primary" 
                    size="large"
                    :disabled="!formData.filePath" 
                    @click="analysisResume"
                    class="auto-fill-btn"
                  >
                    <template #icon>
                      <span class="btn-icon">✨</span>
                    </template>
                    自动填充表单
                  </a-button>
                </div>
              </div>
            </div>
            
          </a-col>
        </a-row>

        <!-- 隐藏的表单字段 -->
        <a-form ref="formRef" class="antd-modal-form hidden-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm" style="display: none;">
          <a-row>
            <a-col :span="24">
              <a-form-item label="上传PDF简历" id="XgsPositionApplyForm-filePath" name="filePath">
                <a-input v-model:value="formData.filePath" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        
        <!-- 重构后的表单组件 -->
        <div>
          <!-- 使用我们重构的表单组件 -->
          <xgsApplyForm 
            ref="resumeFormRef"
            :formDisabled="disabled"
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

  import { useUserStore } from '/@/store/modules/user';
  const userStore = useUserStore();

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    hideSubmitBtn: { type: Boolean, default: false },
    formBpm: { type: Boolean, default: true }
  });

  const resumeFormRef = ref();
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);

  // 岗位信息
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

  // 个人简历填报信息
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


  // 初始化数据
  const initFormData = async () => {
    resumeFormData.value = {
      userName: formData.value.userName,
      resumeName: formData.value.resumeName,
      resumeId: formData.value.resumeId,
      dataId: formData.value.resumeId,
      disabled: formData.value.disabled,
      positionType: formData.value.positionType || '普通岗位', // 传递岗位类型，默认为普通岗位
      positionName: formData.value.positionName,
      positionDept: formData.value.positionDept,
      positionId: formData.value.positionId,
      applyDept: formData.value.positionDept,
      applyPosition: formData.value.positionName,
    }
  }

  /**
   * 新增
   */
  function add(positionInfo) {
    console.log('添加记录:', positionInfo);
    positionInfo.realname = userStore.getUserInfo.realname;
    positionInfo.username = userStore.getUserInfo.username;
    let positionApplyInfo = {};
    positionApplyInfo['filePath'] = '';
    positionApplyInfo['positionDept'] = positionInfo.positionDept;
    positionApplyInfo['positionName'] = positionInfo.positionName;
    positionApplyInfo['positionType'] = positionInfo.positionType;
    positionApplyInfo['resumeName'] = positionInfo.realname + positionInfo.username + '_' + positionInfo.positionName;
    positionApplyInfo['userName'] = positionInfo.realname;
    positionApplyInfo['resumeId'] = '';
    positionApplyInfo['positionId'] = positionInfo.id || positionInfo.positionId;
    nextTick(() => {
      resetFields();
      // 赋值到表单数据
      Object.assign(formData.value, positionApplyInfo);
      // 同步数据到简历表单
      initFormData();
      resumeFormRef.value.createResumeBaseByPositionInfo(resumeFormData.value);
    });
  }

  /**
   * 编辑
   */
  function edit(positionId: string) {
    console.log('编辑记录:', positionId);
    nextTick(() => {
      resetFields();
      // 赋值到表单数据
      resumeFormRef.value.loadResumeBaseById(positionId);
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
  /* 岗位信息卡片样式 */
  .position-info-card {
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    border-radius: 8px;
    padding: 0;
    margin-bottom: 20px;
    border: 1px solid #e0e6ed;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    overflow: hidden;
  }

  .info-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 10px 16px;
    border-bottom: none;
  }

  .info-header-title {
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    display: flex;
    align-items: center;
    
    &::before {
      content: '';
      display: inline-block;
      width: 3px;
      height: 14px;
      background: #fff;
      margin-right: 8px;
      border-radius: 2px;
    }
  }

  .info-content {
    padding: 16px;
    background: #fff;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .info-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    background: #fafafa;
    border-radius: 6px;
    border-left: 3px solid #667eea;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f0f5ff;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
    }
  }

  .info-label {
    font-size: 13px;
    color: #8c8c8c;
    font-weight: 500;
    white-space: nowrap;
    flex-shrink: 0;
  }

  .info-value {
    font-size: 14px;
    color: #262626;
    font-weight: 500;
    word-break: break-all;
    flex: 1;
    
    &.highlight {
      font-size: 15px;
      font-weight: 600;
      color: #667eea;
    }
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .info-content {
      grid-template-columns: 1fr;
      gap: 10px;
    }
    
    .position-info-card {
      margin-bottom: 16px;
    }
    
    .info-header {
      padding: 8px 12px;
    }
    
    .info-content {
      padding: 12px;
    }
    
    .info-item {
      padding: 6px 10px;
    }
  }

  /* 上传区域样式 */
  .upload-section-card {
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    border-radius: 8px;
    padding: 0;
    margin-bottom: 20px;
    border: 1px solid #e0e6ed;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    overflow: hidden;
  }

  .upload-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 10px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 8px;
  }

  .upload-header-title {
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    display: flex;
    align-items: center;
    
    &::before {
      content: '📎';
      display: inline-block;
      margin-right: 6px;
      font-size: 16px;
    }
  }

  .upload-hint {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.85);
    font-weight: 400;
  }

  .upload-content {
    padding: 20px;
    background: #fff;
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .upload-area {
    flex: 1;
    min-width: 0;
  }

  .upload-action {
    flex-shrink: 0;
  }

  .auto-fill-btn {
    height: 40px;
    padding: 0 24px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
    
    &:not(:disabled):hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
    
    .btn-icon {
      font-size: 16px;
      margin-right: 4px;
    }
  }

  /* 上传区域响应式 */
  @media (max-width: 768px) {
    .upload-header {
      padding: 8px 12px;
      flex-direction: column;
      align-items: flex-start;
    }
    
    .upload-hint {
      font-size: 11px;
    }
    
    .upload-content {
      flex-direction: column;
      padding: 16px;
      gap: 12px;
    }
    
    .upload-action {
      width: 100%;
      
      .auto-fill-btn {
        width: 100%;
      }
    }
    
    .upload-section-card {
      margin-bottom: 16px;
    }
  }

  .antd-modal-form {
    padding: 14px;
  }
  
  .hidden-form {
    display: none !important;
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
