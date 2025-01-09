<!--<template>-->
<!--  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896" @ok="handleSubmit">-->
<!--      <BasicForm @register="registerForm" name="XgsFavoriteJobForm" />-->
<!--  </BasicModal>-->
<!--</template>-->

<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896" :height="750">
    <TextComponent :id="favoriteJobId"/>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref, onMounted} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import {formSchema} from '../XgsFavoriteJob.data';
    import TextComponent from './PositionFavoriteDetail.vue';

    const isReady = ref(false);
    import {saveOrUpdate} from '../XgsFavoriteJob.api';
    const favoriteJobId = ref('')
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const isDetail = ref(false);
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
      setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
      // isUpdate.value = !!data?.isUpdate;
      // isDetail.value = !!data?.showFooter;
      isUpdate.value = true;
      isDetail.value = true;
      // if (unref(isUpdate)) {
      //   console.log('==:::::::::::::::::::::::::::::::===========')
      //     //表单赋值
      //     await setFieldsValue({
      //         ...data.record,
      //     });
      //   console.log('==::::::::::::::://///:::::::::::===========')
      // }
      // 隐藏底部时禁用整个表单
      setProps({ disabled: !data?.showFooter })
      isReady.value = true;
      favoriteJobId.value = data.record
      console.log("1111", favoriteJobId.value)
    });
    //设置标题
    // const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(isDetail) ? '详情' : '编辑'));
    const title = ref('岗位详情')

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
