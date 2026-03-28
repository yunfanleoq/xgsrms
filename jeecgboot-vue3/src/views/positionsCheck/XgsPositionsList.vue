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
      <template #bodyCell="{ column, record, index, text }">
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
    <!-- 审核表单 -->
    <XgsFlowOpinionsModal @register="registerModal" @success="handleSuccess" />
    <!-- 详情模态框 -->
    <XgsPositionsDetailModal @register="registerDetailModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="positions-xgsPositions" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/src/components/Table';
  import { useModal } from '/src/components/Modal';
  import { useListPage } from '/src/hooks/system/useListPage';
  import XgsPositionsModal from './components/XgsPositionsModal.vue';
  import XgsFlowOpinionsModal from './opinions/components/XgsFlowOpinionsModal.vue';
  import XgsPositionsDetailModal from './components/XgsPositionsDetailModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsPositions.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsPositions.api';
  import { downloadFile } from '/src/utils/common/renderUtils';
  import { sanitizeRichTextHtml } from '/@/utils/security/sanitizeRichTextHtml';
  import { useUserStore } from '/src/store/modules/user';
  import { defHttp } from '@/utils/http/axios';
  import {saveOrUpdate} from "@/views/xgsIntroduce/XgsIntroduce.api";

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册审核模态框
  const [registerModal, { openModal }] = useModal();
  //注册详情模态框
  const [registerDetailModal, { openModal: openDetailModal }] = useModal();
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
        // ['待审核', '审核通过', '审核未通过'],
        return Object.assign(params, queryParam, {
          status: ['待审核'],
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
    // 判断记录的 status 是否为 '待审核'，如果是则执行后续操作
    console.log(1);
    console.log('handleCheckhandleCheckhandleCheckhandleCheck', record);
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
    openDetailModal(true, {
      record,
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
   * 操作栏 - 只显示审核和详情按钮
   */
  function getTableAction(record) {
    return [
      {
        label: '审核',
        onClick: handleCheck.bind(null, record),
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
