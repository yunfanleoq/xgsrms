<template>
  <div class="work-achievement-table">
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
        <a-form-item label="工作主要业绩" name="researchResult" :rules="[{ required: true, message: '请输入工作主要业绩' }]">
          <a-textarea
            v-model:value="formState.researchResult"
            placeholder="请输入工作主要业绩"
            :rows="6"
            :maxlength="2000"
            show-count
          />
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

const columns = [
  { title: '工作主要业绩', dataIndex: 'researchResult', key: 'researchResult', ellipsis: true },
  { title: '操作', key: 'action', width: 150, fixed: 'right' },
];

const modalVisible = ref(false);
const modalTitle = ref('新增');
const currentIndex = ref(-1);
const formRef = ref();

const formState = reactive({
  researchResult: '',
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
  modalTitle.value = '新增工作主要业绩';
  currentIndex.value = -1;
  resetForm();
  modalVisible.value = true;
};

// 编辑
const handleEdit = (record: any, index: number) => {
  modalTitle.value = '编辑工作主要业绩';
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
  formState.researchResult = '';
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
.work-achievement-table {
  :deep(.ant-table) {
    font-size: 13px;
  }
}
</style>

