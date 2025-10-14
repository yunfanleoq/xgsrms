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
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../XgsPositionApply.api';
  import { Form } from 'ant-design-vue';
  import { usePositionApplyStoreWithOut} from "@/store/modules/positionApply";
  const positionApplyStore = usePositionApplyStoreWithOut();
  
  import { defineComponent } from 'vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({
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
      })},
    formBpm: { type: Boolean, default: true }
  });

  const resumeFormRef = ref();
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
  setTimeout(() => {
    nextTick(() => {
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
    });
  }, 1000);
  
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

      console.log("记录数据:", record);

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
   * 提交数据
   */
  async function submitForm() {
    try {
      // 触发表单验证
      await validate();
      
      // 验证简历表单
      if (resumeFormRef.value) {
        await resumeFormRef.value.handleSubmit();
      }
    } catch (error) {
      console.error('表单验证失败:', error);
      return Promise.reject(error);
    }
    
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    
    //时间格式化
    let model = formData.value;
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
