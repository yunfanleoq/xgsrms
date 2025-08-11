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
    <XgsFlowOpinionsModal @register="opinionModal" @success="handleSuccess" />
    <XgsFlowOpinionsListModal @register="opinionListModal" @success="handleSuccess" />
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

  const queryParam = reactive<any>({
    approvalNode: APPROVAL_NODES.HR_PENDING_REVIEW,
    approvalStatusValue: '1',
  });

  //注册model
  const registerModal = ref();
  //注册model
  const [opinionModal, { openModal }] = useModal();
  const [opinionListModal, { openModal: openListModal }] = useModal();
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
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showFooter: true,
    });
  }
  function handleOpinion(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: false,
      showFooter: true,
    });
  }
  function handleOpinionList(record: Recordable) {
    openListModal(true, {
      record,
      isUpdate: false,
      showFooter: true,
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
  async function handleCustomExport() {
    try {
      // 获取当前选中的列配置
      const tableInstance = registerTable.value;
      if (!tableInstance) {
        console.error('表格实例未初始化');
        return;
      }
      
      // 获取列配置
      const columns = tableInstance.getColumns();
      
      // 过滤出可见的列（排除操作列和选择列）
      const visibleColumns = columns.filter(col => 
        col.dataIndex && 
        col.dataIndex !== 'action' && 
        col.dataIndex !== 'selection' &&
        col.title !== '操作'
      );
      
      // 构建导出参数
      const exportParams = {
        ...queryParam,
        columns: visibleColumns.map(col => ({
          key: col.dataIndex,
          title: col.title
        }))
      };
      
      // 调用导出
      await onExportXls(exportParams);
    } catch (error) {
      console.error('导出失败:', error);
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
