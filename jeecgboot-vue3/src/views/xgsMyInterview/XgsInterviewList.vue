<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
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
      <!--字段回显插槽-->
      <template #bodyCell="{ column, record, index, text }"> </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsInterviewModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="xgsInterview-xgsInterview" setup>
  import { ref, reactive, computed, unref, onMounted } from 'vue';
  import { BasicTable, useTable, TableAction } from '/src/components/Table';
  import { useModal } from '/src/components/Modal';
  import { useListPage } from '/src/hooks/system/useListPage';
  import XgsInterviewModal from './components/XgsInterviewModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsInterview.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsInterview.api';
  import { downloadFile } from '/src/utils/common/renderUtils';
  import { useUserStore } from '/src/store/modules/user';
  import { saveOrUpdate } from '@/views/xgsIntroduce/XgsIntroduce.api';
  import { getUserData } from '/src/views/system/usersetting/UserSetting.api';
  import {id} from "postcss-html";

  const queryParam = reactive<any>({
    candidateId: useUserStore().getUserInfo?.id || '',
  });
  // 定义 realname 响应式变量用于装载用户名
  const realname = ref(null);
  const isRealnameLoaded = ref(false);
  // 在组件挂载时获取 realname
  onMounted(async () => {
  });
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '我的面试',
      api: list,
      columns,
      canResize: false,
      // formConfig: {
      //   //labelWidth: 120,
      //   schemas: searchFormSchema,
      //   autoSubmitOnEnter: true,
      //   showAdvancedButton: true,
      //   fieldMapToNumber: [],
      //   fieldMapToTime: [['interviewDate', ['interviewDate_begin', 'interviewDate_end'], 'YYYY-MM-DD HH:mm:ss']],
      // },
      actionColumn: {
        width: 150,
        fixed: 'right',
      },
      beforeFetch: (params) => {
      },
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
    if (record.inviteStatus === '接受邀请' || record.inviteStatus === '拒绝邀请') {
      // 如果已经接受或拒绝，只显示状态信息
      return [
        {
          label: '详情',
          onClick: handleDetail.bind(null, record),
        },
        {
          label: record.inviteStatus,
          disabled: true, // 禁用操作
        },
      ];
    } else {
      return [
        {
          label: '详情',
          onClick: handleDetail.bind(null, record),
        },
        {
          label: '回复',
          onClick: handleEdit.bind(null, record),
          auth: 'positions:xgs_position_apply:edit',
        },
      ];
    }
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
