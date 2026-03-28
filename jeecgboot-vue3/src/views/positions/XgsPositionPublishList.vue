<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle #jSelectUser="{model, status}">
        <a-button type="primary" v-auth="'positions:xgs_positions:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
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
            >删除批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <!--        &lt;!&ndash; 高级查询 &ndash;&gt;-->
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
    <XgsPositionPublishModal @register="registerModal" @success="handleSuccess"></XgsPositionPublishModal>
  </div>
</template>

<script lang="ts" name="positions-xgsPositions" setup>
  import { ref, reactive, computed, unref, onMounted } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsPositionPublishModal from './components/XgsPositionPublishModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsPositionPublish.data';
  import {
    list,
    deleteOne,
    batchDelete,
    getImportUrl,
    getExportUrl,
    saveOrUpdate
  } from './XgsPositionPublish.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { sanitizeRichTextHtml } from '/@/utils/security/sanitizeRichTextHtml';
  import { useUserStore } from '/@/store/modules/user';

  const userStore = useUserStore();
  const departName = userStore.getDepartName;

  const queryParam = reactive<any>({});
  // const checkedKeys = ref<Array<string | number>>([]);

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
        width: 200,
        fixed: 'right',
      },
      beforeFetch: (params) => {
        // 获取当前用户信息
        const username = userStore.getUserInfo?.username || '';
        
        // 不再按状态过滤，显示用户创建的所有岗位
        return Object.assign(params, queryParam, {
          dept: departName,
          createBy: username, // 只显示当前用户创建的岗位
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

  const [registerTable, { reload }, { rowSelection: defaultRowSelection, selectedRowKeys }] = tableContext;
  
  // 自定义 rowSelection，只允许选择草稿状态的行
  const rowSelection = computed(() => ({
    ...defaultRowSelection,
    getCheckboxProps: (record: Recordable) => ({
      disabled: record.status !== '草稿', // 只有草稿状态可以选中
      name: record.id,
    }),
  }));

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
  function handleAdd() {
    // const loginInfo = userStore.getLoginInfo(); // 修改: 使用 getLoginInfo 获取登录信息
    openModal(true, {
      isUpdate: false,
      showFooter: true,
      data: {
        dept: departName,
        status: '草稿', // 设置默认岗位状态为“草稿”并添加部门名称
      },
    });
  }
  /**
   * 申请事件
   */
  function handleApply(record: Recordable) {
    if (record.status === '草稿' || record.status === '审核未通过') {
      const record1 = {
        ...record, // 合并 record 的值
        status: '待审核', // 设置 status 为 '待审核'
      };

      // 调用 openModal 方法
      openModal(true, {
        record: record1,
        isUpdate: true,
        showFooter: true,
      });
    }
    return; // 如果不是草稿，不继续执行
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
   * 批量删除事件 - 只允许删除草稿状态的岗位
   */
  async function batchHandleDelete() {
    // 由于表格限制了只能选择草稿状态的行，这里可以直接删除
    await batchDelete({ ids: selectedRowKeys.value.join(',') }, handleSuccess);
  }
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
  /**
   * 操作栏 - 根据岗位状态显示不同的操作按钮
   */
  function getTableAction(record) {
    // 只有草稿状态的岗位才显示申请和编辑按钮
    if (record.status === '草稿' || record.status === '审核未通过') {
      return [
        {
          label: '申请',
          onClick: handleApply.bind(null, record),
          auth: 'positions:xgs_positions:edit',
        },
        {
          label: '编辑',
          onClick: handleEdit.bind(null, record),
          auth: 'positions:xgs_positions:edit',
        },
      ];
    } else {
      // 非草稿状态不显示操作按钮，只在下拉菜单中显示详情
      return [];
    }
  }
  
  /**
   * 下拉操作栏 - 根据岗位状态显示不同的操作按钮
   */
  function getDropDownAction(record) {
    // 基础操作按钮
    const actions: any[] = [];
    
    // 所有状态都可以查看详情
    actions.push({
      label: '详情',
      onClick: handleDetail.bind(null, record),
    });
    
    // 只有草稿状态的岗位可以删除
    if (record.status === '草稿') {
      actions.push({
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'positions:xgs_positions:delete',
      });
    }
    
    return actions;
  }

  // onMounted(() => {
  //   openModal(true, {
  //     isUpdate: false,
  //     showFooter: true,
  //     data: { status: '草稿' }, // 设置默认岗位状态为“草稿”
  //   });
  // });
</script>

<style lang="less" scoped>
  :deep(.ant-picker),
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
