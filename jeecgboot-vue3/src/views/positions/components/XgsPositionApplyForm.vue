<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm">
          <a-row>
						<a-col :span="12" v-if = "false">
							<a-form-item label="申请人ID" v-bind="validateInfos.userId" id="XgsPositionApplyForm-userId" name="userId">
								<a-input v-model:value="formData.userId" placeholder="请输入申请人ID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="申请人姓名" v-bind="validateInfos.userName" id="XgsPositionApplyForm-userName" name="userName">
								<a-input v-model:value="formData.userName" placeholder="请输入申请人姓名"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="简历ID" v-bind="validateInfos.resumeId" id="XgsPositionApplyForm-resumeId" name="resumeId">
								<a-input v-model:value="formData.resumeId" placeholder="请输入简历ID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="简历名称" v-bind="validateInfos.resumeName" id="XgsPositionApplyForm-resumeName" name="resumeName">
								<a-input v-model:value="formData.resumeName" placeholder="请输入简历名称"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="岗位ID" v-bind="validateInfos.positionId" id="XgsPositionApplyForm-positionId" name="positionId">
								<a-input v-model:value="formData.positionId" placeholder="请输入岗位ID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="岗位名称" v-bind="validateInfos.positionName" id="XgsPositionApplyForm-positionName" name="positionName">
								<a-input v-model:value="formData.positionName" placeholder="请输入岗位名称"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="岗位部门" v-bind="validateInfos.positionDept" id="XgsPositionApplyForm-positionDept" name="positionDept">
								<a-input v-model:value="formData.positionDept" placeholder="请输入岗位部门"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="岗位类型" v-bind="validateInfos.positionType" id="XgsPositionApplyForm-positionType" name="positionType">
								<a-input v-model:value="formData.positionType" placeholder="请输入岗位类型"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="申请状态" v-bind="validateInfos.status" id="XgsPositionApplyForm-status" name="status">
								<a-input-number v-model:value="formData.status" placeholder="请输入申请状态" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="申请备注" v-bind="validateInfos.mark" id="XgsPositionApplyForm-mark" name="mark">
								<a-input v-model:value="formData.mark" placeholder="请输入申请备注"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="申请标签" v-bind="validateInfos.tagIds" id="XgsPositionApplyForm-tagIds" name="tagIds">
								<a-input v-model:value="formData.tagIds" placeholder="请输入申请标签"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="创建者" v-bind="validateInfos.creator" id="XgsPositionApplyForm-creator" name="creator">
								<a-input v-model:value="formData.creator" placeholder="请输入创建者"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="更新者" v-bind="validateInfos.updater" id="XgsPositionApplyForm-updater" name="updater">
								<a-input v-model:value="formData.updater" placeholder="请输入更新者"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="是否删除" v-bind="validateInfos.deleted" id="XgsPositionApplyForm-deleted" name="deleted">
								<a-input v-model:value="formData.deleted" placeholder="请输入是否删除"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="租户编号" v-bind="validateInfos.tenantId" id="XgsPositionApplyForm-tenantId" name="tenantId">
								<a-input-number v-model:value="formData.tenantId" placeholder="请输入租户编号" style="width: 100%" />
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
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../XgsPositionApply.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({})},
    formBpm: { type: Boolean, default: true }
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    userId: '',
    userName: '',
    resumeId: '',
    resumeName: '',
    positionId: '',
    positionName: '',
    positionDept: '',
    positionType: '',
    status: undefined,
    mark: '',
    tagIds: '',
    creator: '',
    updater: '',
    deleted: '',
    tenantId: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

  // 表单禁用
  const disabled = computed(()=>{
    if(props.formBpm === true){
      if(props.formData.disabled === false){
        return false;
      }else{
        return true;
      }
    }
    return props.formDisabled;
  });


  /**
   * 新增
   */
  function add(record) {
    edit(record);
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if(record.hasOwnProperty(key)){
          tmpData[key] = record[key]
        }
      })
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
