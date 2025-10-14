<template>
  <div class="work-achievement-table">
    <div class="table-operations" v-if="!disabled">
      <a-button type="primary" @click="handleAdd">
        <plus-outlined /> 新增工作业绩
      </a-button>
    </div>
    
    <a-table
      :columns="columns"
      :dataSource="localDataSource"
      :pagination="false"
      :rowKey="record => record.id || record.tempId"
      :scroll="{ x: 800 }"
      size="middle"
      class="mt-2"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'action' && !disabled">
          <a-space>
            <a @click="handleEdit(record, index)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除该条记录?"
              @confirm="handleDelete(record, index)"
            >
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
        <template v-else-if="column.key === 'researchResult'">
          <div class="content-cell" :title="record.researchResult">
            {{ record.researchResult }}
          </div>
        </template>
      </template>
    </a-table>

    <!-- 编辑表单弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      width="800px"
      @ok="handleModalSubmit"
      :maskClosable="false"
    >
      <a-form
        ref="formRef"
        :model="formState"
        layout="vertical"
      >
        <a-form-item
          label="工作主要业绩"
          name="researchResult"
          :rules="[
            { required: true, message: '请输入工作主要业绩' },
            { max: 500, message: '内容过长，请限制在5行以内' }
          ]"
        >
          <a-textarea
            v-model:value="formState.researchResult"
            placeholder="请输入工作主要业绩，不超过5行"
            :rows="5"
            :maxLength="500"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { defineComponent, ref, reactive, computed, onMounted, watch, defineProps, defineEmits } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';

const props = defineProps({
  dataSource: { type: Array, default: () => [] },
  disabled: { type: Boolean, default: false }
});

const emit = defineEmits(['update:dataSource']);

// 临时ID计数器
let tempIdCounter = 0;

// 本地数据源
const localDataSource = ref([...props.dataSource]);

// 监听外部数据源变化
watch(() => props.dataSource, (newVal) => {
  localDataSource.value = [...newVal];
}, { deep: true });

// 表格列定义
const columns = [
  {
    title: '工作主要业绩',
    dataIndex: 'researchResult',
    key: 'researchResult',
    width: '80%',
    ellipsis: true,
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 120,
  }
];

// 模态框状态
const modalVisible = ref(false);
const modalTitle = ref('新增工作业绩');
const formRef = ref();
const editIndex = ref(-1);

// 表单数据
const formState = reactive({
  id: '',
  researchResult: '',
  tempId: ''
});

// 重置表单
const resetForm = () => {
  formState.id = '';
  formState.researchResult = '';
  formState.tempId = '';
  
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 新增按钮点击
const handleAdd = () => {
  resetForm();
  modalTitle.value = '新增工作业绩';
  editIndex.value = -1;
  modalVisible.value = true;
};

// 编辑按钮点击
const handleEdit = (record, index) => {
  // 拷贝记录数据到表单
  Object.assign(formState, record);
  
  modalTitle.value = '编辑工作业绩';
  editIndex.value = index;
  modalVisible.value = true;
};

// 删除按钮点击
const handleDelete = (record, index) => {
  localDataSource.value.splice(index, 1);
  updateDataSource();
  message.success('删除成功');
};

// 模态框确认
const handleModalSubmit = async () => {
  // 表单验证
  try {
    await formRef.value.validateFields();
    
    const newRecord = {
      ...formState,
    };
    
    // 如果没有ID，生成临时ID
    if (!newRecord.id && !newRecord.tempId) {
      newRecord.tempId = 'temp_' + (++tempIdCounter);
    }
    
    if (editIndex.value === -1) {
      // 新增
      localDataSource.value.push(newRecord);
    } else {
      // 编辑
      localDataSource.value.splice(editIndex.value, 1, newRecord);
    }
    
    // 更新数据源
    updateDataSource();
    
    modalVisible.value = false;
    message.success(editIndex.value === -1 ? '新增成功' : '修改成功');
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

// 更新数据源
const updateDataSource = () => {
  emit('update:dataSource', localDataSource.value);
};

// 获取当前数据方法（供父组件调用）
const getData = () => {
  return localDataSource.value;
};

// 暴露方法
defineExpose({
  getData,
});
</script>

<style lang="less" scoped>
.work-achievement-table {
  width: 100%;

  .table-operations {
    margin-bottom: 16px;
  }
  
  .mt-2 {
    margin-top: 8px;
  }
  
  .content-cell {
    max-height: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    white-space: normal;
  }
}
</style>
