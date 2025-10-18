<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm">
          <a-row>
            <a-col :span="12">
              <a-form-item label="申请人" v-bind="validateInfos.userName" id="XgsPositionApplyForm-userName" name="userName">
                <a-input v-model:value="formData.userName" placeholder="" allow-clear disabled />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="岗位名称" v-bind="validateInfos.positionName" id="XgsPositionApplyForm-positionName" name="positionName">
                <a-input v-model:value="formData.positionName" placeholder="" allow-clear disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位部门" v-bind="validateInfos.positionDept" id="XgsPositionApplyForm-positionDept" name="positionDept">
                <a-input v-model:value="formData.positionDept" placeholder="" allow-clear disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="岗位类型" v-bind="validateInfos.positionType" id="XgsPositionApplyForm-positionType" name="positionType">
                <a-input v-model:value="formData.positionType" placeholder="" allow-clear disabled />
              </a-form-item>
            </a-col>
            <a-col :span="12" v-show="false">
              <a-form-item label="申请状态" v-bind="validateInfos.status" id="XgsPositionApplyForm-status" name="status">
                <a-input v-model:value="formData.status" placeholder="" style="width: 100%" disabled />
              </a-form-item>
            </a-col>

            <a-col :span="12" v-show="false">
              <a-form-item label="简历名称" v-bind="validateInfos.resumeName" id="XgsPositionApplyForm-resumeName" name="resumeName">
                <a-input v-model:value="formData.resumeName" placeholder="请输入简历名称" allow-clear />
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
                <a-textarea v-model:value="formData.mark" placeholder="" allow-clear :auto-size="{ minRows: 1, maxRows: 10 }" style="width: 93%" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <!--        //此处引入简历组件，展示简历填报页面-->
        <div>
          <xgsResumePTForm v-if="formData.positionType === '普通岗位'" :form-data="resumeFormData" :form-bpm="formBpm" />
          <xgsResumeBSHForm v-else-if="formData.positionType === '博士后岗位'" :form-data="resumeFormData" :form-bpm="formBpm" />
          <xgsResumeFGForm v-else-if="formData.positionType === '副高级以上岗位'" :form-data="resumeFormData" :form-bpm="formBpm" />
          <xgsResumeTJForm v-else-if="formData.positionType === '专家推荐岗位'" :form-data="resumeFormData" :form-bpm="formBpm" />
          <div v-else>
            <!-- 可选：当 positionType 不匹配任何已知类型时显示的内容 -->
            载入中...
          </div>
        </div>
        
        <!-- 附件下载区域 -->
        <div v-if="otherFilesData" class="other-files-section">
          <a-divider orientation="left">
            <span style="font-size: 16px; font-weight: 600;">附件材料</span>
          </a-divider>
          <FileDownloadList :value="otherFilesData" />
        </div>
        <!--        <component :is="currentComponent" :form-data="formData" :form-bpm="formBpm" />-->
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  // import XgsResumeForm from '/@/views/xgsResume/components/xgsResumeBaseForm.vue';
  import xgsResumeBSHForm from '/@/views/xgsResumeBase/xgsResumeBSH/components/xgsResumeBSHForm.vue';
  import xgsResumePTForm from '/@/views/xgsResumeBase/xgsResumePT/components/xgsResumeBaseForm.vue';
  import xgsResumeFGForm from '/@/views/xgsResumeBase/xgsResumeFG/components/xgsResumeFGForm.vue';
  import xgsResumeTJForm from '/@/views/xgsResumeBase/xgsResumeTJ/components/xgsResumeTJForm.vue';
  import FileDownloadList from '/@/components/FileList/FileDownloadList.vue';

  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate, getPositionApplyInfo } from '../XgsPositionApply.api';
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
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = ref({
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
  });
  const resumeFormData = ref({
    userName: '',
    resumeName: '',
    resumeId: '',
    dataId: '',
    disabled: false,
  });
  
  // 附件数据
  const otherFilesData = ref<string>('');

  const initFormData = async () => {
  };
  onMounted(() => {
    // initFormData();
  });

  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({});
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

  // 表单禁用
  const disabled = computed(() => {
    return false;
    // if (props.formBpm === true) {
    //   if (props.formData.disabled === false) {
    //     return false;
    //   } else {
    //     return true;
    //   }
    // }
    // return props.formDisabled;
  });

  //页面完全加载完成并 显示一秒后 打印 formData，
  // setTimeout(() => {
  //   nextTick(() => {
  //     console.log('...............................formData', formData);
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
    // positionApplyStore.currPositionApply = record;
    // record = positionStore.currApplyPosition;
    console.log('add222222222', positionApplyStore.currPositionApply);
    edit(record);
  }

  import { useUserStore } from '/@/store/modules/user';
  const userStore = useUserStore();
  /**
   * 编辑
   */
  function edit(record) {
    console.log('edit>>>>>>>>>', record);
    nextTick(() => {
      resetFields();
      let tmpData = {};
      record.realname = userStore.getUserInfo.realname;
      record.username = userStore.getUserInfo.username;

      console.log('record', record);

      tmpData['positionDept'] = record.positionDept;
      tmpData['positionName'] = record.positionName;
      tmpData['positionType'] = record.positionType;
      tmpData['resumeName'] = record.realname + record.username + '_' + record.positionName;
      tmpData['userName'] = record.realname;
      tmpData['resumeId'] = '';

      console.log('edit>>>>>positionDept>>>>', record.positionDept, '<<<<<<<', tmpData['positionName']);
      console.log('tmpData', tmpData);
      // Object.keys(formData).forEach((key) => {
      //   if(record.hasOwnProperty(key)){
      //     tmpData[key] = record[key]
      //   }
      // })
      //赋值
      Object.assign(formData.value, tmpData);
      //
      // formData.value = tmpData;
      console.log('formData', formData);
    });
    console.log('edit>>>>end>>>>>', formData);
  }

  /**
   * 编辑
   */
  function detail(record) {
    nextTick(() => {
      resetFields();
      // Object.assign(formData.value, record);
      getPositionApplyInfo({ xgsPositionApply: record }).then((data) => {
        Object.assign(formData.value, data.xgsPositionApply);
        resumeFormData.value.dataId = data.xgsPositionApply.resumeId;
        formData.value.disabled = true;
        resumeFormData.value.disabled = true;
        
        // 加载附件数据
        if (data.xgsResumeBasePage && data.xgsResumeBasePage.otherFiles) {
          otherFilesData.value = data.xgsResumeBasePage.otherFiles;
        }
        // cons
        // Object.assign(resumeFormData.value, data.xgsResumeBasePage);
      });
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    try {
      // 触发表单验证
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
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.value.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    await saveOrUpdate(model, isUpdate.value)
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
    detail,
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
