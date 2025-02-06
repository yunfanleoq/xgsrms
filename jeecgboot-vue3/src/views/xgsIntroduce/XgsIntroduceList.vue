<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'xgsIntroduce:xgs_introduce:add'" @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" v-auth="'xgsIntroduce:xgs_introduce:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls">
          导出</a-button
        >
        <j-upload-button type="primary" v-auth="'xgsIntroduce:xgs_introduce:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls"
          >导入</j-upload-button
        >
        <a-button type="primary" preIcon="ant-design:sync-outlined" @click="handleAutoSync"> 自动同步</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'xgsIntroduce:xgs_introduce:deleteBatch'"
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
        <template v-if="column.dataIndex === 'text'">
          <!--富文本件字段回显插槽-->
          <div v-html="text"></div>
        </template>
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <XgsIntroduceModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="xgsIntroduce-xgsIntroduce" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import axios from 'axios';
  import { saveOrUpdate } from './XgsIntroduce.api';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import XgsIntroduceModal from './components/XgsIntroduceModal.vue';
  import { columns, searchFormSchema, superQuerySchema } from './XgsIntroduce.data';
  import { list, deleteOne, batchDelete, getImportUrl, getExportUrl } from './XgsIntroduce.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //注册model
  const [registerModal, { openModal }] = useModal();
  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: '机构概况',
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
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: '机构概况',
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
   *  自动同步事件
   */
  const handleAutoSync = async () => {
    try {
      // 先获取所有数据的id
      const allData = await list({});
      const allIds = allData.records.map((item) => item.id);
      // 调用批量删除函数删除所有数据
      batchDelete({ ids: allIds }, handleSuccess, false);
      //获取数据
      const response = await axios.get('http://www.iie.cas.cn/jggk2020/dwjj2020/');
      const parser = new DOMParser();
      const doc = parser.parseFromString(response.data, 'text/html');
      const targetDiv = doc.querySelector('div.t3d');
      if (targetDiv) {
        const record = {
          //组装数据
          text: targetDiv.outerHTML, // 概况内容
          picture: null, // 图片的URL
          pictureText: null, // 图片附文
          id: '1871084441866518530', // 数据的唯一标识
          type: '单位简介', // 概况类型的实际值
        };
        //将最新数据放置到数据库中
        await saveOrUpdate(record, false); // false 表示新增
        await reload();
      }
      const response1 = await axios.get('http://www.iie.cas.cn/jggk2020/ysfm2020/');
      const parser1 = new DOMParser();
      const doc1 = parser1.parseFromString(response1.data, 'text/html');
      const targetDiv1 = doc1.querySelector('div.new-graphics');
      if (targetDiv1) {
        //此处是将获取的数据中的相对路径修改为绝对路径
        const absolutePathPrefix = 'https://www.iie.ac.cn/jggk2020/ysfm2020/';
        const links = targetDiv1.querySelectorAll('a[href^="./"]');
        const images = targetDiv1.querySelectorAll('img[src^="./"]');
        links.forEach((link) => {
          link.href = absolutePathPrefix + link.href.slice(2);
        });
        images.forEach((img) => {
          img.src = absolutePathPrefix + img.src.slice(2);
        });
        let outerHTML = targetDiv1.outerHTML;
        outerHTML = outerHTML.replace(/(https:\/\/www\.iie\.ac\.cn\/jggk2020\/ysfm2020\/)tp:\/\/localhost:3100\/xgsIntroduce\//g, '$1');
        if (outerHTML) {
          //组装数据
          const record = {
            text: outerHTML, // 概况内容
            picture: null, // 图片的URL
            pictureText: null, // 图片附文
            id: '1871493342910930946', // 数据的唯一标识
            type: '院所风貌', // 概况类型的实际值
          };
          //将最新数据放置到数据库中
          await saveOrUpdate(record, false); // false 表示新增
          await reload();
        }
      }
    } catch (error) {
      console.error('获取或处理内容时出错：', error);
    }
  };
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
        auth: 'xgsIntroduce:xgs_introduce:edit',
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
        auth: 'xgsIntroduce:xgs_introduce:delete',
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

