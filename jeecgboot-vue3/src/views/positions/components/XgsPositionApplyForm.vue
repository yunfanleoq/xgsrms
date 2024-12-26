<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="XgsPositionApplyForm">
          <a-row>

						<a-col :span="12">
							<a-form-item label="申请人" v-bind="validateInfos.userName" id="XgsPositionApplyForm-userName" name="userName">
								<a-input v-model:value="formData.userName" placeholder="请输入申请人姓名"  allow-clear ></a-input>
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
						<a-col :span="12" v-if="false">
							<a-form-item label="申请状态" v-bind="validateInfos.status" id="XgsPositionApplyForm-status" name="status">
								<a-input v-model:value="formData.status" placeholder="请输入申请状态" style="width: 100%" disabled />
							</a-form-item>
						</a-col>

            <a-col :span="12">
              <a-form-item label="简历名称" v-bind="validateInfos.resumeName" id="XgsPositionApplyForm-resumeName" name="resumeName">
                <!--								<a-input v-model:value="formData.resumeName" placeholder="请输入简历名称"  allow-clear ></a-input>-->
                <a-select v-model:value="formData.resumeName" placeholder="请选择简历名称" allow-clear>
                  <a-select-option v-for="resume in resumeOptions" :key="resume.value" :value="resume.value">
                    {{ resume.label }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="备注" v-bind="validateInfos.mark" id="XgsPositionApplyForm-mark" name="mark" :label-col="{ span: 2 }" :wrapper-col="{ span: 22 }">
                <a-textarea v-model:value="formData.mark" placeholder="请输入申请备注" allow-clear :auto-size="{ minRows: 1, maxRows: 10 }" style="width: 93%;" />
              </a-form-item>
            </a-col>



          </a-row>
        </a-form>
<!--        //此处引入简历组件，展示简历填报页面-->
        <div>
          <xgsResumePTForm v-if="formData.positionType === '普通岗位'" :form-data="formData" :form-bpm="formBpm" />
          <xgsResumeBSHForm v-else-if="formData.positionType === '博士后岗位'" :form-data="formData" :form-bpm="formBpm" />
          <xgsResumeFGForm v-else-if="formData.positionType === '副高级岗位'" :form-data="formData" :form-bpm="formBpm" />
          <xgsResumeTJForm v-else-if="formData.positionType === '专家推荐岗位'" :form-data="formData" :form-bpm="formBpm" />
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
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  // import XgsResumeForm from '/@/views/xgsResume/components/xgsResumeBaseForm.vue';
  import xgsResumeBSHForm from '/@/views/xgsResumeBase/xgsResumeBSH/components/xgsResumeBSHForm.vue';
  import xgsResumePTForm from '/@/views/xgsResumeBase/xgsResumePT/components/xgsResumeBaseForm.vue';
  import xgsResumeFGForm from '/@/views/xgsResumeBase/xgsResumeFG/components/xgsResumeFGForm.vue';
  import xgsResumeTJForm from '/@/views/xgsResumeBase/xgsResumeTJ/components/xgsResumeTJForm.vue';



  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../XgsPositionApply.api';
  import { Form } from 'ant-design-vue';
  import { usePositionStoreWithOut} from "@/store/modules/positions";
  const positionStore = usePositionStoreWithOut();









  // 使用 defineComponent 注册组件（可选）
  import { defineComponent } from 'vue';

  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({})},
    formBpm: { type: Boolean, default: true }
  });


  const resumeOptions = computed(() => {
    // return props.formData.resumeOptions;
    return [
      { value: '1', label: '1' },
      { value: '2', label: '2' },
      { value: '3', label: '3' },
      { value: '4', label: '4' },
      { value: '5', label: '5' },
      { value: '6', label: '6' },

    ];
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
    status: '申请中',
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
    // 先根据 岗位id，userid 查询是否已存在，如果存在，则直接编辑，否则新增
    console.log('add111111111', record);

    positionStore.setCurrApplyPosition(record);
    // record = positionStore.currApplyPosition;
    console.log('add222222222', positionStore.currApplyPosition);
    edit(positionStore.currApplyPosition);
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
