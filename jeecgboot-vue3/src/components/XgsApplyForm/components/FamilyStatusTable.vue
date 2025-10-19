<template>
  <div class="family-status-table">
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
      :scroll="{ x: 'max-content' }"
      bordered
      size="small"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button v-if="!disabled" type="link" size="small" @click="handleEdit(record, index)">编辑</a-button>
            <a-button v-if="!disabled" type="link" danger size="small" @click="handleDelete(index)">删除</a-button>
          </a-space>
        </template>
        <template v-else-if="column.key === 'birthday'">
          {{ record[column.key] ? record[column.key].substring(0, 10) : '' }}
        </template>
        <template v-else>
          {{ record[column.key] }}
        </template>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      @ok="handleOk"
      @cancel="handleCancel"
      width="900px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="关系" name="relation" :rules="[{ required: true, message: '请输入关系' }]">
          <a-input v-model:value="formState.relation" placeholder="请输入关系" />
        </a-form-item>
        <a-form-item label="家属姓名" name="name" :rules="[{ required: true, message: '请输入家属姓名' }]">
          <a-input v-model:value="formState.name" placeholder="请输入家属姓名" />
        </a-form-item>
        <a-form-item label="性别" name="sex">
          <a-select v-model:value="formState.sex" placeholder="请选择性别">
            <a-select-option value="男">男</a-select-option>
            <a-select-option value="女">女</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="国籍" name="nationality">
          <a-input v-model:value="formState.nationality" placeholder="请输入国籍" />
        </a-form-item>
        <a-form-item label="籍贯" name="nativePlace">
          <a-input v-model:value="formState.nativePlace" placeholder="请输入籍贯" />
        </a-form-item>
        <a-form-item label="出生年月" name="birthday">
          <a-date-picker
            v-model:value="formState.birthday"
            placeholder="请选择出生年月"
            style="width: 100%"
            :format="dateFormat"
            :valueFormat="dateFormat"
          />
        </a-form-item>
        <a-form-item label="政治面貌" name="politicBackground">
          <a-input v-model:value="formState.politicBackground" placeholder="请输入政治面貌" />
        </a-form-item>
        <a-form-item label="工作单位及职务" name="workUnitPosition">
          <a-input v-model:value="formState.workUnitPosition" placeholder="请输入工作单位及职务" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, watch, defineProps, defineEmits, defineExpose } from 'vue';
import { message } from 'ant-design-vue';

const props = defineProps({
  dataSource: {
    type: Array,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:dataSource']);

const dateFormat = 'YYYY-MM-DD';

const columns = [
  { title: '关系', dataIndex: 'relation', key: 'relation', width: 100 },
  { title: '家属姓名', dataIndex: 'name', key: 'name', width: 120 },
  { title: '性别', dataIndex: 'sex', key: 'sex', width: 80 },
  { title: '国籍', dataIndex: 'nationality', key: 'nationality', width: 100 },
  { title: '籍贯', dataIndex: 'nativePlace', key: 'nativePlace', width: 120 },
  { title: '出生年月', dataIndex: 'birthday', key: 'birthday', width: 120 },
  { title: '政治面貌', dataIndex: 'politicBackground', key: 'politicBackground', width: 120 },
  { title: '工作单位及职务', dataIndex: 'workUnitPosition', key: 'workUnitPosition', width: 200 },
  { title: '操作', key: 'action', width: 150, fixed: 'right' },
];

const modalVisible = ref(false);
const modalTitle = ref('新增');
const currentIndex = ref(-1);
const formRef = ref();

const formState = reactive({
  relation: '',
  name: '',
  sex: '',
  nationality: '',
  nativePlace: '',
  birthday: '',
  politicBackground: '',
  workUnitPosition: '',
});

const dataSource = ref<any[]>([]);

// 监听 props 变化
watch(
  () => props.dataSource,
  (newVal) => {
    dataSource.value = newVal ? [...newVal] : [];
  },
  { immediate: true, deep: true }
);

// 新增
const handleAdd = () => {
  modalTitle.value = '新增家庭成员';
  currentIndex.value = -1;
  resetForm();
  modalVisible.value = true;
};

// 编辑
const handleEdit = (record: any, index: number) => {
  modalTitle.value = '编辑家庭成员';
  currentIndex.value = index;
  Object.assign(formState, record);
  modalVisible.value = true;
};

// 删除
const handleDelete = (index: number) => {
  dataSource.value.splice(index, 1);
  emit('update:dataSource', dataSource.value);
  message.success('删除成功');
};

// 确定
const handleOk = async () => {
  try {
    await formRef.value.validate();
    const newData = { ...formState };
    
    if (currentIndex.value >= 0) {
      // 编辑
      dataSource.value[currentIndex.value] = newData;
      message.success('编辑成功');
    } else {
      // 新增
      dataSource.value.push(newData);
      message.success('新增成功');
    }
    
    emit('update:dataSource', dataSource.value);
    modalVisible.value = false;
    resetForm();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

// 取消
const handleCancel = () => {
  modalVisible.value = false;
  resetForm();
};

// 重置表单
const resetForm = () => {
  formState.relation = '';
  formState.name = '';
  formState.sex = '';
  formState.nationality = '';
  formState.nativePlace = '';
  formState.birthday = '';
  formState.politicBackground = '';
  formState.workUnitPosition = '';
  formRef.value?.resetFields();
};

// 获取数据
const getData = () => {
  return dataSource.value;
};

// 暴露方法
defineExpose({
  handleAdd,
  getData,
});
</script>

<style lang="less" scoped>
.family-status-table {
  :deep(.ant-table) {
    font-size: 13px;
  }
}
</style>

