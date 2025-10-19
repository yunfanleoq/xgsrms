<template>
    <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896"
                @ok="handleSubmit"
                @cancel="handleCancel"
                cancelText="关闭">
      <BasicForm @register="registerForm" name="XgsUserPositionApplyForm" :positionType="positionType" :formData="formData" :formBpm="formBpm" />
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
    import {ref, computed, unref} from 'vue';
    import {BasicModal, useModalInner} from '/src/components/Modal';
    import {BasicForm, useForm} from '/src/components/Form';
    import {formSchema} from '../XgsUserPositionApply.data';
    import {saveOrUpdate} from '../XgsUserPositionApply.api';
    import axios from "axios";

    import xgsUserPositionApplyForm from './XgsUserPositionApplyForm.vue';

    import xgsResumeBSHForm from '/src/views/xgsApplyVerify/dep_verify/depYcl/xgsResumeBase/xgsResumeBSH/components/XgsResumeBSHForm.vue';
    import xgsResumePTForm from '/src/views/xgsResumeBase/xgsResumePT/components/XgsResumeBaseForm.vue';
    import xgsResumeFGForm from '/src/views/xgsResumeBase/xgsResumeFG/components/XgsResumeFGForm.vue';
    import xgsResumeTJForm from '/src/views/xgsResumeBase/xgsResumeTJ/components/XgsResumeTJForm.vue';
    import JModal from "@/components/Modal/src/JModal/JModal.vue";

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
        setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:false});
        isUpdate.value = !!data?.isUpdate;
        isDetail.value = !!data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
        }
        positionType.value = data.record.positionType;
        // const params = {id: data.record.id};
        // if (positionType.value === '普通岗位'){
        //   formData.value = await getResumePTById(params);
        // }else if (positionType.value === '博士后岗位'){
        //   formData.value = await getResumeBSHById(params);
        // }else if (positionType.value === '副高级以上岗位'){
        //   formData.value = await getResumeFGById(params);
        // }else if (positionType.value === '人才派遣岗位'){
        //   formData.value = await getResumeTJById(params);
        // }else {
        //     console.log('未知的 positionType：', positionType.value);
        // }
        // console.log("formData", formData.value);
        formData.value = {
          id: data.record.id,
          dataId: data.record.id,
          disabled: true
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
      isReady.value = true;
    });
    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(isDetail) ? '详情' : '查看'));
    //表单提交事件
    async function handleSubmit(v) {
        try {
          console.log("registerForm", useForm({
            schemas: formSchema,
            showActionButtonGroup: false,
            baseColProps: {span: 12}
          }));
          console.log("positionType",positionType.value);
            let values = await validate();
            // setModalProps({confirmLoading: true});
            // //提交表单
            await saveOrUpdate(values, isUpdate.value);
            // //关闭弹窗
            closeModal();
            // //刷新列表
            emit('success');
        } catch ({ errorFields }) {
           if (errorFields) {
             const firstField = errorFields[0];
             if (firstField) {
               scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
             }
           }
           return Promise.reject(errorFields);
        } finally {
            setModalProps({confirmLoading: false});
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
</style>
