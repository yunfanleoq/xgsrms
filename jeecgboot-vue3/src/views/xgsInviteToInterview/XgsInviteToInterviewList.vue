<template>
  <div v-auth="'xgsInviteToInterview:xgs_invite_to_interview:list'">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <!--          <a-button type="primary" v-auth="'xgsInviteToInterview:xgs_invite_to_interview:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>-->
        <a-button type="primary" v-auth="'positions:xgs_position_apply:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls">
          导出</a-button
        >
        <!--          <j-upload-button type="primary" v-auth="'xgsInviteToInterview:xgs_invite_to_interview:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>-->
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'positions:xgs_position_apply:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <!--字段回显插槽-->
    </BasicTable>
    <!-- 表单区域 -->
    <XgsPositionApplyModal ref="userDetail" @success="handleSuccess" />
    <XgsInviteToInterviewModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="xgsInviteToInterview-xgsInviteToInterview" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsInviteToInterviewModal from './components/XgsInviteToInterviewModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsInviteToInterview.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsInviteToInterview.api';
  import XgsPositionApplyModal from '@/views/xgsResumeApproval/departApproval/components/XgsPositionApplyModal.vue';
  import {defHttp} from "@/utils/http/axios";
  import {useMessage} from "@/hooks/web/useMessage";
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);

  const { createConfirm } = useMessage();
  //注册model
  const [registerModal, { openModal }] = useModal();
  //用于详情展示
  const userDetail = ref();
  //注册table数据
  const { tableContext, onExportXls } = useListPage({
    tableProps: {
      title: '面试',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        //labelWidth: 120,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
        fieldMapToNumber: [],
        fieldMapToTime: [['interviewDate', ['interviewDate_begin', 'interviewDate_end'], 'YYYY-MM-DD HH:mm:ss']],
      },
      actionColumn: {
        width: 350,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        // 添加筛选条件，仅获取人力处通过的名单
        params.approvalStatus = '人力处通过';
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: '面试',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  // 高级查询配置
  const superQueryConfig = reactive(superQuerySchema);

  /**
   * 高级查询事件
   */
  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }
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
    userDetail.value.disableSubmit = true;
    userDetail.value.detail(record);
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
  function interviewPass(record) {
    defHttp.post({ url: '/xgsInviteToInterview/xgsInviteToInterview/interviewPass', data: record }).then(() => {
      reload();
    });
  }
  function interviewFail(record) {
    defHttp.post({ url: '/xgsInviteToInterview/xgsInviteToInterview/interviewFail', data: record }).then(() => {
      reload();
    });
  }
  function cancelInviteV2(record) {
    createConfirm({
      iconType: 'warning',
      title: '确认',
      content: '是否撤回邀请',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        return defHttp.post({ url: '/xgsInviteToInterview/xgsInviteToInterview/cancelInvite', data: record }).then(() => {
          reload();
        });
      },
    });
  }

  function strNonEmpty(v: unknown): boolean {
    return v != null && String(v).trim() !== '';
  }

  /**
   * 操作栏（无「编辑」）
   * 邀请：邀请状态为「已发送邀请」或「是否接受邀请」非空 → 禁用
   * 撤回：仅「已发送邀请」且候选人尚未在「是否接受邀请」中回应（为空）时可撤回；已接受/已拒绝后不可撤回
   * 通过/未通过：仅「接受邀请」且「面试结果」尚未录入（为空）时可操作；已有面试结果则不可再点
   */
  function getTableAction(record: Recordable) {
    const inviteStatus = record.inviteStatus;
    const inviteResult = record.inviteResult;
    const interviewResult = record.interviewResult;

    const inviteDisabled = inviteStatus === '已发送邀请' || strNonEmpty(inviteResult);
    const withdrawEnabled = inviteStatus === '已发送邀请' && !strNonEmpty(inviteResult);
    const passFailEnabled = inviteResult === '接受邀请' && !strNonEmpty(interviewResult);

    return [
      {
        label: '查看',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '邀请',
        onClick: handleEdit.bind(null, record),
        auth: 'positions:xgs_position_apply:edit',
        disabled: inviteDisabled,
      },
      {
        label: '撤回',
        onClick: () => cancelInviteV2(record),
        disabled: !withdrawEnabled,
      },
      {
        label: '通过',
        popConfirm: {
          title: '是否面试通过',
          confirm: interviewPass.bind(null, record),
        },
        auth: 'positions:xgs_position_apply:edit',
        disabled: !passFailEnabled,
      },
      {
        label: '未通过',
        popConfirm: {
          title: '面试未通过',
          confirm: interviewFail.bind(null, record),
        },
        disabled: !passFailEnabled,
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
        auth: 'positions:xgs_position_apply:delete',
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
