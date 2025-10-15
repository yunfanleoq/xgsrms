<template>
  <div class="family-status-table">
    <a-table
      :columns="columns"
      :dataSource="localDataSource"
      :pagination="false"
      :rowKey="record => record.id || record.tempId"
      :scroll="{ x: 1200 }"
      size="middle"
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
        <template v-else-if="column.key === 'sex'">
          {{ record.sex === '1' ? '男' : record.sex === '2' ? '女' : '' }}
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
        <a-row :gutter="16">
          <!-- 关系 -->
          <a-col :span="12">
            <a-form-item
              label="关系"
              name="relation"
              :rules="[{ required: true, message: '请输入关系' }]"
            >
              <a-input v-model:value="formState.relation" placeholder="请输入关系" />
            </a-form-item>
          </a-col>
          
          <!-- 家属姓名 -->
          <a-col :span="12">
            <a-form-item
              label="家属姓名"
              name="name"
              :rules="[{ required: true, message: '请输入家属姓名' }]"
            >
              <a-input v-model:value="formState.name" placeholder="请输入家属姓名" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <!-- 性别 -->
          <a-col :span="12">
            <a-form-item
              label="性别"
              name="sex"
              :rules="[{ required: true, message: '请选择性别' }]"
            >
              <a-select v-model:value="formState.sex" placeholder="请选择性别">
                <a-select-option value="1">男</a-select-option>
                <a-select-option value="2">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <!-- 国籍 -->
          <a-col :span="12">
            <a-form-item
              label="国籍"
              name="nationality"
            >
              <a-input v-model:value="formState.nationality" placeholder="请输入国籍" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <!-- 籍贯 -->
          <a-col :span="12">
            <a-form-item
              label="籍贯"
              name="nativePlace"
            >
              <a-input v-model:value="formState.nativePlace" placeholder="请输入籍贯" />
            </a-form-item>
          </a-col>
          
          <!-- 出生年月 -->
          <a-col :span="12">
            <a-form-item
              label="出生年月"
              name="birthday"
            >
              <a-date-picker
                v-model:value="formState.birthday"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
                placeholder="请选择出生年月"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <!-- 民族 -->
          <a-col :span="12">
            <a-form-item
              label="民族"
              name="nation"
            >
              <a-input v-model:value="formState.nation" placeholder="请输入民族" />
            </a-form-item>
          </a-col>
          
          <!-- 政治面貌 -->
          <a-col :span="12">
            <a-form-item
              label="政治面貌"
              name="politicBackground"
            >
              <a-input v-model:value="formState.politicBackground" placeholder="请输入政治面貌" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <!-- 毕业院校 -->
          <a-col :span="12">
            <a-form-item
              label="毕业院校"
              name="graduateCollege"
            >
              <a-input v-model:value="formState.graduateCollege" placeholder="请输入毕业院校" />
            </a-form-item>
          </a-col>
          
          <!-- 目前工作单位 -->
          <a-col :span="12">
            <a-form-item
              label="目前工作单位"
              name="workUnit"
            >
              <a-input v-model:value="formState.workUnit" placeholder="请输入目前工作单位" />
            </a-form-item>
          </a-col>
        </a-row>
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

// 日期格式
const dateFormat = 'YYYY-MM-DD';

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
    title: '关系',
    dataIndex: 'relation',
    key: 'relation',
    width: 100,
  },
  {
    title: '家属姓名',
    dataIndex: 'name',
    key: 'name',
    width: 120,
  },
  {
    title: '性别',
    dataIndex: 'sex',
    key: 'sex',
    width: 80,
  },
  {
    title: '出生年月',
    dataIndex: 'birthday',
    key: 'birthday',
    width: 120,
  },
  {
    title: '民族',
    dataIndex: 'nation',
    key: 'nation',
    width: 100,
  },
  {
    title: '政治面貌',
    dataIndex: 'politicBackground',
    key: 'politicBackground',
    width: 120,
  },
  {
    title: '毕业院校',
    dataIndex: 'graduateCollege',
    key: 'graduateCollege',
    width: 180,
  },
  {
    title: '工作单位',
    dataIndex: 'workUnit',
    key: 'workUnit',
    width: 180,
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
const modalTitle = ref('新增家庭成员');
const formRef = ref();
const editIndex = ref(-1);

// 表单数据
const formState = reactive({
  id: '',
  relation: '',
  name: '',
  sex: '',
  nationality: '',
  nativePlace: '',
  birthday: '',
  nation: '',
  politicBackground: '',
  graduateCollege: '',
  workUnit: '',
  tempId: ''
});

// 重置表单
const resetForm = () => {
  formState.id = '';
  formState.relation = '';
  formState.name = '';
  formState.sex = '';
  formState.nationality = '';
  formState.nativePlace = '';
  formState.birthday = '';
  formState.nation = '';
  formState.politicBackground = '';
  formState.graduateCollege = '';
  formState.workUnit = '';
  formState.tempId = '';
  
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 新增按钮点击
const handleAdd = () => {
  resetForm();
  modalTitle.value = '新增家庭成员';
  editIndex.value = -1;
  modalVisible.value = true;
};

// 编辑按钮点击
const handleEdit = (record, index) => {
  // 拷贝记录数据到表单
  Object.assign(formState, record);
  
  modalTitle.value = '编辑家庭成员';
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
  handleAdd,
});
</script>

<style lang="less" scoped>
.family-status-table {
  width: 100%;
}
</style>
