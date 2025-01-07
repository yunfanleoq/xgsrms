<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
<!--        <a-button type="primary" v-auth="'positions:xgs_positions:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>-->
        <a-button type="primary" v-auth="'positions:xgs_positions:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls">
          导出</a-button
        >
        <j-upload-button type="primary" v-auth="'positions:xgs_positions:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls"
          >导入</j-upload-button
        >
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'positions:xgs_positions:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
<!--        <super-query :config="superQueryConfig" @search="handleSuperQuery" />-->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
        <template v-if="column.dataIndex === 'duty'">
          <!--富文本件字段回显插槽-->
          <div v-html="text"></div>
        </template>
        <template v-if="column.dataIndex === 'memo'">
          <!--富文本件字段回显插槽-->
          <div v-html="text"></div>
        </template>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsPositionsModal @register="registerModal" @success="handleSuccess"></XgsPositionsModal>
  </div>
</template>

<script lang="ts" name="positions-xgsPositions" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/src/components/Table';
  import { useModal } from '/src/components/Modal';
  import { useListPage } from '/src/hooks/system/useListPage';
  import XgsPositionsModal from './components/XgsPositionsModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsPositions.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsPositions.api';
  import { downloadFile } from '/src/utils/common/renderUtils';
  import { useUserStore } from '/src/store/modules/user';

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '招聘岗位列表',
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
        return Object.assign(params, queryParam, {
          status: '待审核',
        });
      },
    },
    exportConfig: {
      name: '招聘岗位列表',
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  // // 高级查询配置
  // const superQueryConfig = reactive(superQuerySchema);
  //
  // /**
  //  * 高级查询事件
  //  */
  // function handleSuperQuery(params) {
  //   Object.keys(params).map((k) => {
  //     queryParam[k] = params[k];
  //   });
  //   reload();
  // }
  /**
   * 新增事件
   */
  // function handleAdd() {
  //   openModal(true, {
  //     isUpdate: false,
  //     showFooter: true,
  //   });
  // }
  /**
   * 审核事件
   */
  function handleCheck(record: Recordable) {
    if (record.status === '待审核') {
      const record1 = {
        ...record, // 合并 record 的值
        status: '审核通过', // 设置 status 为 '待审核'
      };
      openModal(true, {
        record: record1,
        isUpdate: true,
        showFooter: true,
      });
    }
    return;
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
    return [
      {
        label: '审核',
        onClick: handleCheck.bind(null, record),
        auth: 'positions:xgs_positions:edit',
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
        auth: 'positions:xgs_positions:delete',
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
