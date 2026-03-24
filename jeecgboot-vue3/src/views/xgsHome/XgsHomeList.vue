<template>
  <div>
    <!-- Radio Button Group切换：招聘公告和新闻 -->
    <div style="text-align: center; margin: 6px 0 16px 0">
      <a-radio-group v-model:value="activeKey" @change="handleTabChange" button-style="solid" size="mini">
        <a-radio-button value="homeImages">新闻图片</a-radio-button>
        <a-radio-button value="rczp">招聘公告</a-radio-button>
      </a-radio-group>
    </div>

    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'xgsHome:xgs_home:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" v-auth="'xgsHome:xgs_home:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary" v-auth="'xgsHome:xgs_home:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls"
          >导入</j-upload-button
        >
        <a-button type="primary" @click="syncHomeContent" preIcon="ant-design:sync-outlined"> 自动同步</a-button>
        <!-- 添加同步按钮 -->
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'xgsHome:xgs_home:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template #bodyCell="{ column, record, index, text }">
        <template v-if="column.dataIndex === 'rczp'">
          <!--富文本件字段回显插槽-->
          <div v-html="text"></div>
        </template>
        <template v-if="column.dataIndex === 'homeImages'">
          <!--富文本件字段回显插槽-->
          <div v-html="text"></div>
        </template>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsHomeModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="xgsHome-xgsHome" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsHomeModal from './components/XgsHomeModal.vue';
  import { announcementColumns, newsColumns, searchFormSchema, superQuerySchema } from './XgsHome.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsHome.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  import { defHttp } from '@/utils/http/axios';

  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  
  // Tab激活key，默认招聘公告
  const activeKey = ref<string>('homeImages');
  
  // 根据Tab动态获取列定义
  const currentColumns = computed(() => {
    return activeKey.value === 'homeImages' ? newsColumns : announcementColumns;
  });
  
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '首页',
      api: list,
      columns: currentColumns,
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
        // 根据Tab设置newsType过滤条件
        params.newsType = activeKey.value === 'announcement' ? 'rczp' : 'homeImages';
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: '首页',
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
   * Tab切换事件
   */
  function handleTabChange() {
    reload();
  }
  
  /**
   * 新增事件
   */
  function handleAdd() {
    const newsType = activeKey.value === 'announcement' ? 'rczp' : 'homeImages';
    openModal(true, {
      isUpdate: false,
      showFooter: true,
      newsType,
    });
  }
  
  /**
   * 自动同步首页数据
   */
  function syncHomeContent() {
    defHttp
      .post({ url: '/xgsHome/xgsHome/syncHomeContent' }, { isTransformResponse: false })
      .then((res: any) => {
        if (res && res.success) {
          reload();
        }
      })
      .catch((error) => {
        console.error('同步失败', error);
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
      newsType: record.newsType || (activeKey.value === 'announcement' ? 'rczp' : 'homeImages'),
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
      newsType: record.newsType || (activeKey.value === 'announcement' ? 'rczp' : 'homeImages'),
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
        auth: 'xgsHome:xgs_home:edit',
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
        auth: 'xgsHome:xgs_home:delete',
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
