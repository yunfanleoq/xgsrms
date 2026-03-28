<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
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
          <!--富文本件字段回显插槽（xss 净化）-->
          <div v-html="sanitizeRichTextHtml(text)"></div>
        </template>
        <template v-if="column.dataIndex === 'memo'">
          <!--富文本件字段回显插槽（xss 净化）-->
          <div v-html="sanitizeRichTextHtml(text)"></div>
        </template>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsPositionsModal @register="registerModal" @success="handleSuccess"></XgsPositionsModal>
  </div>
</template>

<script lang="ts" name="positions-xgsPositions" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsPositionsModal from './components/XgsPositionsModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsPositions.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsPositions.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { sanitizeRichTextHtml } from '/@/utils/security/sanitizeRichTextHtml';
  import { useUserStore } from '/@/store/modules/user';
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
          status: '审核通过',
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
   * 发布事件
   */
  function handlePost(record: Recordable) {
    if (record.status === '审核通过') {
      const record1 = {
        ...record, // 合并 record 的值
        status: '招聘中', // 设置 status 为 '待审核'
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
   * 操作栏 - 只显示发布和详情按钮
   */
  function getTableAction(record) {
    return [
      {
        label: '发布',
        onClick: handlePost.bind(null, record),
        auth: 'positions:xgs_positions:edit',
      },
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
    ];
  }
  /**
   * 下拉操作栏 - 不显示更多菜单
   */
  function getDropDownAction(record) {
    return [];
  }
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
