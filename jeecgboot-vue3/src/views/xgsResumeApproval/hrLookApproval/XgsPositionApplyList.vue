<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'resume:xgs_position_apply:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'resume:xgs_position_apply:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
        <a-radio-group v-model:value="queryParam.approvalStatusValue" button-style="solid" @change="reload" style="margin: 0 auto">
          <a-radio-button value="1">待查看</a-radio-button>
          <a-radio-button value="2">已查看</a-radio-button>
        </a-radio-group>
        <!-- 导出按钮 -->
        <a-button 
          type="primary" 
          v-auth="'xgsUserResume:xgs_position_apply:exportXls'" 
          preIcon="ant-design:download-outlined" 
          @click="handleCustomExport" 
          style="margin-left: 8px"
        > 
          导出
        </a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsPositionApplyModal ref="registerModal" @success="handleSuccess" />
    <XgsFlowOpinionsModal ref="opinionModal" @success="handleSuccess" />
    <XgsFlowOpinionsListModal ref="opinionListModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="resume-xgsPositionApply" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsPositionApplyModal from '../departApproval/components/XgsPositionApplyModal.vue';
  import { columns, searchFormSchema } from './XgsPositionApply.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsPositionApply.api';
  import XgsFlowOpinionsModal from '@/views/xgsResumeApproval/opinions/components/XgsFlowOpinionsModal.vue';
  import XgsFlowOpinionsListModal from '@/views/xgsResumeApproval/opinions/components/XgsFlowOpinionsListModal.vue';
  import { APPROVAL_NODES } from '/@/enums/approval';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useGlobSetting } from '/@/hooks/setting';
  import { getToken } from '/@/utils/auth';

  const queryParam = reactive<any>({
    approvalNode: APPROVAL_NODES.HR_PENDING_REVIEW,
    approvalStatusValue: '1',
  });

  const { createMessage } = useMessage();
  const globSetting = useGlobSetting();

  //注册model
  const registerModal = ref();
  //注册审核意见modal
  const opinionModal = ref();
  //注册审批意见列表modal
  const opinionListModal = ref();
  //注册table数据
  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '岗位申请 - 人力处查看',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        //labelWidth: 120,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToNumber: [],
        fieldMapToTime: [],
      },
      actionColumn: {
        width: 120,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        // 确保查询参数包含审批环节为"待人力处查看"的条件
        const finalParams = Object.assign(params, queryParam);
        if (queryParam.approvalStatusValue === '1') {
          finalParams.approvalNode = APPROVAL_NODES.HR_PENDING_REVIEW;
        }
        return finalParams;
      },
    },
    exportConfig: {
      name: '岗位申请-人力处查看',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 新增事件
   */
  function handleAdd() {
    registerModal.value.add();
  }
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    registerModal.value.edit(record);
  }
  function handleOpinion(record: Recordable) {
    opinionModal.value?.open({
      record,
      isUpdate: false,
      showFooter: true,
    });
  }
  function handleOpinionList(record: Recordable) {
    opinionListModal.value?.open({
      record,
      isUpdate: false,
      showFooter: false,
    });
  }
  /**
   * 详情
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.detail(record);
  }
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value }, handleSuccess);
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
  /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'resume:xgs_position_apply:edit',
      },
      {
        label: '审核',
        onClick: handleOpinion.bind(null, record),
        ifShow: () => queryParam.approvalStatusValue === '1',
      },
      {
        label: '办理过程',
        onClick: handleOpinionList.bind(null, record),
        ifShow: () => queryParam.approvalStatusValue === '2',
      },
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
    ];
  }
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record) {
    return [
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'resume:xgs_position_apply:delete',
      },
    ];
  }

  /**
   * 自定义导出
   */
  function handleCustomExport() {
    // 1. 检查是否选中了数据
    if (selectedRowKeys.value.length === 0) {
      createMessage.warning('请选择一条数据进行导出');
      return;
    }
    // 2. 检查是否只选中了一条数据
    if (selectedRowKeys.value.length > 1) {
      createMessage.warning('只能选择一条数据进行导出');
      return;
    }
    // 3. 调用导出接口
    const applyId = selectedRowKeys.value[0];
    const token = getToken();
    const url = `${globSetting.apiUrl}/xgsExport/exportResumeWord?applyId=${applyId}&token=${token}`;
    window.open(url, '_blank');
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
