<template>
<div>
    <!--引用表格-->
   <BasicTable @register="registerTable">
       <!--操作栏-->
      <template #action="{ record }">
          <TableAction :actions="getTableAction(record)"/>
      </template>
      </BasicTable>
      <!-- 表单区域 -->
      <XgsFavoriteJobModal @register="registerModal" @success="handleSuccess"></XgsFavoriteJobModal>
      <!-- 在线申请表单 -->
      <XgsPositionApplyModalNew ref="registerModalRef" :form-data="applyRecord" :formBpm="true" @success="handleApplySuccess" />
  </div>
</template>

<script lang="ts" name="positions-myXgsFavoriteJob" setup>
  import {reactive, ref} from 'vue';
  import {BasicTable, TableAction} from '/@/components/Table';
  import {useModal} from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage'
  import XgsFavoriteJobModal from './components/PositionFavoriteDetail.vue'
  import XgsPositionApplyModalNew from './components/XgsPositionApplyModalNew.vue'
  import {columns, searchFormSchema} from './XgsFavoriteJob.data';
  import {list, deleteOne} from './XgsFavoriteJob.api';
  import { getJobById } from '@/api/xgsrms/home';
  import { useUserStore } from '/@/store/modules/user';
  import { message } from 'ant-design-vue';
  
  const queryParam = reactive<any>({});
  const userStore = useUserStore();
  const registerModalRef = ref();
  const applyRecord = ref<any>({});
  //注册model（岗位详情弹窗）
  const [registerModal, {openModal}] = useModal();
  //注册table数据
  const { tableContext } = useListPage({
      tableProps:{
           title: '职位收藏',
           api: list,
           columns,
           canResize:false,
           useSearchForm: searchFormSchema.length > 0,
           formConfig: {
              //labelWidth: 120,
              schemas: searchFormSchema,
              autoSubmitOnEnter:true,
              showAdvancedButton:true,
              showResetButton: false,
              fieldMapToNumber: [
              ],
              fieldMapToTime: [
              ],
            },
           actionColumn: {
               width: 220,
               fixed:'right'
            },
            beforeFetch: (params) => {
             return Object.assign(params, queryParam);
           },
     },
 })

 const [registerTable, {reload}] = tableContext

  /**
   * 详情
  */
  function handleDetail(record: Recordable) {
     openModal(true, {
       record,
       isUpdate: true,
       showFooter: false,
     });
   }
   /**
    * 删除事件
    */
  async function handleDelete(record) {
    await deleteOne({id: record.id}, handleSuccess);
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
      reload();
   }
   /**
    * 在线申请事件 - 参照 positionDetail.vue 的实现
    */
  async function handleApply(record: Recordable) {
    if (!userStore.userInfo) {
      message.warning('请先登录');
      return;
    }
    
    try {
      // 先获取完整的岗位信息
      const response = await getJobById({ id: record.positionId });
      if (response && response.result) {
        const job = response.result;
        
        // 使用 dept_dictText 或收藏记录中的部门名称
        const deptName = job.dept_dictText || record.positionDept || '';
        
        applyRecord.value = {
          applyId: '',
          disabled: false,
          mark: '',
          positionDept: deptName,
          positionId: job.id,
          positionName: job.positionName,
          positionType: job.category,
          resumeId: '',
          resumeName: userStore.getUserInfo.realname + userStore.getUserInfo.username + '_' + job.positionName,
          status: '申请中',
          userName: userStore.getUserInfo.realname,
        };
        
        // 调用模态框的 addJob 方法
        registerModalRef.value.addJob(applyRecord.value);
      } else {
        message.error('获取岗位信息失败，请稍后再试');
      }
    } catch (error) {
      console.error('获取岗位信息失败:', error);
      message.error('获取岗位信息失败，请稍后再试');
    }
  }

  /**
   * 处理申请成功回调
   */
  function handleApplySuccess() {
    // 刷新列表，更新是否已申请状态
    reload();
  }

   /**
      * 操作栏
      */
  function getTableAction(record){
      // 根据是否已申请显示不同的按钮
      const actions: any[] = [];
      
      // 如果未申请，显示在线申请按钮
      if (record.hasApplied === '0') {
        actions.push({
          label: '在线申请',
          onClick: handleApply.bind(null, record),
        });
      }
      // 查看按钮始终显示
      actions.push({
        label: '查看',
        onClick: handleDetail.bind(null, record),
      });

      // 取消收藏按钮
      actions.push({
        label: '取消收藏',
        popConfirm: {
          title: '是否确认取消收藏该岗位？',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
      });
      
      return actions;
  }


</script>

<style lang="less" scoped>
  :deep(.ant-picker),:deep(.ant-input-number){
    width: 100%;
  }
</style>
