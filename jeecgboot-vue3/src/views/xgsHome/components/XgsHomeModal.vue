<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="800" @ok="handleSubmit">
      <BasicForm @register="registerForm" name="XgsHomeForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import {getFormSchemaByType} from '../XgsHome.data';
    import {saveOrUpdate} from '../XgsHome.api';
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const isDetail = ref(false);
    const currentNewsType = ref<string>('招聘公告');
    
    //表单配置
    const [registerForm, { setProps,resetFields, setFieldsValue, validate, scrollToField, updateSchema }] = useForm({
        labelWidth: 150,
        schemas: getFormSchemaByType('招聘公告'),
        showActionButtonGroup: false,
        baseColProps: {span: 24}
    });
    //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await resetFields();
        setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        isDetail.value = !!data?.showFooter;
        
        // 获取newsType并动态设置表单schemas
        currentNewsType.value = data?.newsType || '招聘公告';
        const schemas = getFormSchemaByType(currentNewsType.value);
        setProps({ schemas });
        
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
        } else {
            // 新增时设置newsType默认值
            await setFieldsValue({
                newsType: currentNewsType.value,
            });
        }
        
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //设置标题
    const title = computed(() => {
        const typeText = currentNewsType.value === '招聘公告' ? '招聘公告' : '新闻';
        return (!unref(isUpdate) ? `新增${typeText}` : !unref(isDetail) ? `${typeText}详情` : `编辑${typeText}`);
    });
    //表单提交事件
    async function handleSubmit(v) {
        try {
            let values = await validate();
            setModalProps({confirmLoading: true});
            //提交表单
            await saveOrUpdate(values, isUpdate.value);
            //关闭弹窗
            closeModal();
            //刷新列表
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