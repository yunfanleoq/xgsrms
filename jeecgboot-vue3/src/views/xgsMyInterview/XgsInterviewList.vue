<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--     <&lt;!&ndash;!&#45;&#45;插槽:table标题&ndash;&gt;
    <template #tableTitle>
      <a-button type="primary" v-auth="'xgsInterview:xgs_interview:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
      <a-button  type="primary" v-auth="'xgsInterview:xgs_interview:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      <j-upload-button type="primary" v-auth="'xgsInterview:xgs_interview:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <template #overlay>
          <a-menu>
            <a-menu-item key="1" @click="batchHandleDelete">
              <Icon icon="ant-design:delete-outlined"></Icon>
              删除
            </a-menu-item>
          </a-menu>
        </template>
        <a-button v-auth="'xgsInterview:xgs_interview:deleteBatch'">批量操作
          <Icon icon="mdi:chevron-down"></Icon>
        </a-button>
      </a-dropdown>
      &lt;!&ndash; 高级查询 &ndash;&gt;
      <super-query :config="superQueryConfig" @search="handleSuperQuery" />
     操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsInterviewModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="xgsInterview-xgsInterview" setup>
  import { reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsInterviewModal from './components/XgsInterviewModal.vue';
  import { columns, searchFormSchema } from './XgsInterview.data';
  import { list, deleteOne, getImportUrl, getExportUrl } from './XgsInterview.api';
  import { useUserStore } from '/@/store/modules/user';
  import {defHttp} from "@/utils/http/axios";

  const userStore = useUserStore();
  const queryParam = reactive<any>({
    candidateId: userStore.getUserInfo?.id || '',
  });
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { tableContext } = useListPage({
    tableProps: {
      title: '我的面试',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        showResetButton: false,
        fieldMapToNumber: [],
        fieldMapToTime: [['interviewDate', ['interviewDate_begin', 'interviewDate_end'], 'YYYY-MM-DD HH:mm:ss']],
      },
      actionColumn: {
        width: 250,
        fixed: 'right',
      },
      beforeFetch: (params) => Object.assign(params, queryParam),
    },
    exportConfig: {
      name: '面试管理',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload }] = tableContext;

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
    await deleteOne({ id: record.id }, handleSuccess);
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
    reload();
  }
  function invitePass(record) {
    defHttp.post({ url: '/xgsInviteToInterview/xgsInviteToInterview/invitePass', data: record }).then(() => {
      reload();
    });
  }
  function inviteRefuse(record) {
    defHttp.post({ url: '/xgsInviteToInterview/xgsInviteToInterview/inviteRefuse', data: record }).then(() => {
      reload();
    });
  }

  /** 「是否接受邀请」inviteResult 非空 */
  function inviteResultNonEmpty(record: Recordable): boolean {
    const v = record.inviteResult;
    return v != null && String(v).trim() !== '';
  }

  /**
   * 操作栏：「是否接受邀请」非空时禁用「接受邀请」「拒绝邀请」
   */
  function getTableAction(record) {
    const result = record.inviteResult;
    const decided = result === '接受邀请' || result === '拒绝邀请';
    const disableAcceptRefuse = inviteResultNonEmpty(record);

    if (decided) {
      return [
        {
          label: '详情',
          onClick: handleDetail.bind(null, record),
        },
        {
          label: result,
          disabled: true,
        },
      ];
    }
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '接受邀请',
        disabled: disableAcceptRefuse,
        popConfirm: {
          title: '是否接受邀请',
          confirm: invitePass.bind(null, record),
        },
      },
      {
        label: '拒绝邀请',
        disabled: disableAcceptRefuse,
        popConfirm: {
          title: '是否拒绝邀请',
          confirm: inviteRefuse.bind(null, record),
        },
      },
    ];
  }
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'xgsInterview:xgs_interview:delete',
      },
    ];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
