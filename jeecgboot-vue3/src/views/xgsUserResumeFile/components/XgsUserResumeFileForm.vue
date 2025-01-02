<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsUserResumeFileForm">
          <a-row>
            <a-col :span="24">
              <a-form-item label="文件路径" v-bind="validateInfos.filePath" id="XgsUserResumeFileForm-filePath" name="filePath">
                <j-upload v-model:value="formData.filePath" :max-count="1" :multiple="false" />
                <a-button type="primary" v-if="formData.filePath" @click="analysisResume">简历分析</a-button>
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="文件大小" v-bind="validateInfos.fileSize" id="XgsUserResumeFileForm-fileSize" name="fileSize">
                <a-input-number v-model:value="formData.fileSize" placeholder="请输入文件大小" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="文件名称" v-bind="validateInfos.fileName" id="XgsUserResumeFileForm-fileName" name="fileName">
                <a-input v-model:value="formData.fileName" placeholder="请输入文件名称" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="扩展名" v-bind="validateInfos.fileExt" id="XgsUserResumeFileForm-fileExt" name="fileExt">
                <a-input v-model:value="formData.fileExt" placeholder="请输入扩展名" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="文件JSON" v-bind="validateInfos.fileJson" id="XgsUserResumeFileForm-fileJson" name="fileJson">
                <a-textarea v-model:value="formData.fileJson" :rows="4" placeholder="请输入文件JSON" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="简历字段信息" v-bind="validateInfos.resumeInfo" id="XgsUserResumeFileForm-resumeInfo" name="resumeInfo">
                <a-textarea v-model:value="formData.resumeInfo" :rows="4" placeholder="请输入简历字段信息" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="备注" v-bind="validateInfos.remarks" id="XgsUserResumeFileForm-remarks" name="remarks">
                <a-textarea v-model:value="formData.remarks" :rows="4" placeholder="请输入备注" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JUpload from '/@/components/Form/src/jeecg/components/JUpload/JUpload.vue';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../XgsUserResumeFile.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true },
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    fileSize: undefined,
    fileName: '',
    fileExt: '',
    fileJson: '',
    resumeInfo: '',
    filePath: '',
    remarks: '',
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
    if (props.formBpm === true) {
      if (props.formData.disabled === false) {
        return false;
      } else {
        return true;
      }
    }
    return props.formDisabled;
  });

  function analysisResume() {
    confirmLoading.value = true;
    defHttp
      .post({ url: '/resume/xgsUserResumeFile/analysisResume', timeout: 600000, data: formData })
      .then((data) => {
        if (data) {
          Object.assign(formData, data);
        } else {
          createMessage.warning('解析失败，请上传PDF格式的简历');
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if (record.hasOwnProperty(key)) {
          tmpData[key] = record[key];
        }
      });
      //赋值
      Object.assign(formData, tmpData);
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
    if (model.id) {
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
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
</style>
