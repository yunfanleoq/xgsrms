<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'xgsMyresume:xgs_myresume:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增简历</a-button>
      
        <a-button type="primary" v-auth="'xgsMyresume:xgs_myresume:deleteBatch'" @click="batchHandleDelete" preIcon="ant-design:delete-outlined">删除简历</a-button>

        <a-dropdown>
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="handleIntelligentModify">
                <Icon icon="ant-design:edit-outlined"></Icon>
                智能修改
              </a-menu-item>
              <a-menu-item key="2" @click="handleIntelligentAnalysis">
                <Icon icon="ant-design:bulb-outlined"></Icon>
                智能分析
              </a-menu-item>
              <a-menu-item key="3" @click="handleUploadResume">
                <Icon icon="ant-design:cloud-upload-outlined"></Icon>
                简历上传
              </a-menu-item>
            </a-menu>
          </template>
          <a-button type="primary" preIcon="ant-design:edit-outlined"
            >修改简历
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>

        <!-- <a-button  type="primary" v-auth="'xgsMyresume:xgs_myresume:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button> -->
        <!-- <j-upload-button type="primary" v-auth="'xgsMyresume:xgs_myresume:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button> -->
        <!-- <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'xgsMyresume:xgs_myresume:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown> -->
        <!-- 高级查询 -->
        <!-- <super-query :config="superQueryConfig" @search="handleSuperQuery" /> -->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }"> </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsMyresumeModal @register="registerModal" @success="handleSuccess"></XgsMyresumeModal>
  </div>
</template>

<script lang="ts" name="xgsMyresume-xgsMyresume" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsMyresumeModal from './components/XgsMyresumeModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsMyresume.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsMyresume.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  import { Modal, message } from 'ant-design-vue'; // 确保导入 Modal 和 message

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '简历列表',
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
        // return Object.assign(params, queryParam);
        // 获取当前登录用户的ID
        const currentUserId = userStore.getUserInfo.id;
        // 添加用户ID作为过滤条件
        return Object.assign(params, queryParam, { userId: currentUserId });
      },
    },
    exportConfig: {
      name: '简历列表',
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
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'xgsMyresume:xgs_myresume:edit',
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
        auth: 'xgsMyresume:xgs_myresume:delete',
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
