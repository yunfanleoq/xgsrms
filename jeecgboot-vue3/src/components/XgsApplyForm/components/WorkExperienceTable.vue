<template>
  <div class="work-experience-table">
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
        <template v-else-if="column.key === 'beginDate' || column.key === 'endDate'">
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
      width="800px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="开始日期" name="beginDate" :rules="[{ required: true, message: '请选择开始日期' }]">
          <a-date-picker
            v-model:value="formState.beginDate"
            placeholder="请选择开始日期"
            style="width: 100%"
            :format="dateFormat"
            :valueFormat="dateFormat"
          />
        </a-form-item>
        <a-form-item label="终止日期" name="endDate" :rules="[{ required: true, message: '请选择终止日期' }]">
          <a-date-picker
            v-model:value="formState.endDate"
            placeholder="请选择终止日期"
            style="width: 100%"
            :format="dateFormat"
            :valueFormat="dateFormat"
          />
        </a-form-item>
        <a-form-item label="工作单位" name="workUnit" :rules="[{ required: true, message: '请输入工作单位' }]">
          <a-input v-model:value="formState.workUnit" placeholder="请输入工作单位" />
        </a-form-item>
        <a-form-item label="工作部门" name="deptment">
          <a-input v-model:value="formState.deptment" placeholder="请输入工作部门" />
        </a-form-item>
        <a-form-item label="职务" name="position">
          <a-input v-model:value="formState.position" placeholder="请输入职务" />
        </a-form-item>
        <a-form-item label="专业技术岗位或职员职级" name="professionLevel">
          <a-input v-model:value="formState.professionLevel" placeholder="请输入专业技术岗位或职员职级" />
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
  { title: '开始日期', dataIndex: 'beginDate', key: 'beginDate', width: 120 },
  { title: '终止日期', dataIndex: 'endDate', key: 'endDate', width: 120 },
  { title: '工作单位', dataIndex: 'workUnit', key: 'workUnit', width: 200 },
  { title: '工作部门', dataIndex: 'deptment', key: 'deptment', width: 150 },
  { title: '职务', dataIndex: 'position', key: 'position', width: 120 },
  { title: '专业技术岗位或职员职级', dataIndex: 'professionLevel', key: 'professionLevel', width: 200 },
  { title: '操作', key: 'action', width: 150, fixed: 'right' },
];

const modalVisible = ref(false);
const modalTitle = ref('新增');
const currentIndex = ref(-1);
const formRef = ref();

const formState = reactive({
  beginDate: '',
  endDate: '',
  workUnit: '',
  deptment: '',
  position: '',
  professionLevel: '',
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
  modalTitle.value = '新增工作经历';
  currentIndex.value = -1;
  resetForm();
  modalVisible.value = true;
};

// 编辑
const handleEdit = (record: any, index: number) => {
  modalTitle.value = '编辑工作经历';
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
  formState.beginDate = '';
  formState.endDate = '';
  formState.workUnit = '';
  formState.deptment = '';
  formState.position = '';
  formState.professionLevel = '';
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
.work-experience-table {
  :deep(.ant-table) {
    font-size: 13px;
  }
}
</style>

