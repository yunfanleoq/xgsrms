<template>
  <div class="basic-info-form">
    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      layout="vertical"
      :disabled="formDisabled"
    >
      <table class="resume-table">
        <tbody>
        <!-- 照片行 -->
        <tr>
          <td class="label-cell">本人照片</td>
          <td class="field-cell photo-cell">
            <a-form-item name="photograph">
            <div class="photo-upload-container">
              <!-- 上传状态提示 -->
              <div v-if="uploadState.uploading" class="upload-loading">
                <a-spin size="large" />
                <p>正在上传照片...</p>
              </div>
              
              <!-- 错误提示 -->
              <div v-else-if="uploadState.error" class="upload-error">
                <a-alert 
                  :message="uploadState.error" 
                  type="error" 
                  show-icon
                  closable
                  @close="clearError"
                />
                <a-button type="primary" size="small" @click="reupload" style="margin-top: 8px;">
                  重新上传
                </a-button>
              </div>
              
              <!-- 显示照片 -->
              <div v-else-if="formData.photograph" class="photo-display">
                <img 
                  :src="getImageUrl(formData.photograph)" 
                  class="photo-image"
                  alt="本人照片"
                  @click="previewImage(formData.photograph)"
                  @error="handleImageError"
                />
                
                <!-- 图片操作按钮 -->
                <div class="image-actions">
                  <a-button 
                    type="primary" 
                    size="small" 
                    @click="previewImage(formData.photograph)"
                    class="preview-btn"
                  >
                    <template #icon><eye-outlined /></template>
                  </a-button>
                  <a-button 
                    type="primary" 
                    danger
                    size="small" 
                    @click="removeImage"
                    class="remove-btn"
                  >
                    <template #icon><delete-outlined /></template>
                  </a-button>
                </div>
              </div>
              
              <!-- 上传按钮 -->
              <div v-else class="upload-placeholder">
                <a-upload
                  :key="uploaderKey"
                  name="file"
                  list-type="picture-card"
                  :show-upload-list="false"
                  :before-upload="beforeUpload"
                  :customRequest="customUpload"
                  accept=".jpg,.jpeg,.png,.gif"
                >
                  <div>
                    <plus-outlined />
                    <div style="margin-top: 8px">上传照片</div>
                  </div>
                </a-upload>
                <div class="upload-tips">
                  <p>支持 JPG、PNG、GIF 格式</p>
                  <p>文件大小不超过 2MB</p>
                  <p>建议尺寸：400×500 像素</p>
                </div>
              </div>
            </div>
          </a-form-item>
          </td>
        </tr>
        <!-- 姓名 -->
        <tr>
          <td class="label-cell">姓名</td>
          <td class="field-cell">
            <a-form-item name="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
          </td>
        </tr>
        <!-- 性别 -->
        <tr>
          <td class="label-cell">性别</td>
          <td class="field-cell">
            <a-form-item name="sex">
              <a-select v-model:value="formData.sex" placeholder="请选择性别">
                <a-select-option value="1">男</a-select-option>
                <a-select-option value="2">女</a-select-option>
              </a-select>
            </a-form-item>
          </td>
        </tr>
        <!-- 籍贯 -->
        <tr>
          <td class="label-cell">籍贯</td>
          <td class="field-cell">
            <a-form-item name="nativePlace">
              <a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" />
            </a-form-item>
          </td>
        </tr>
        <!-- 出生年月 -->
        <tr>
          <td class="label-cell">出生年月</td>
          <td class="field-cell">
            <a-form-item name="birthday">
              <a-date-picker
                v-model:value="formData.birthday"
                placeholder="请选择出生年月"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
              />
            </a-form-item>
          </td>
        </tr>
        <!-- 民族 -->
        <tr>
          <td class="label-cell">民族</td>
          <td class="field-cell">
            <a-form-item name="nation">
              <a-input v-model:value="formData.nation" placeholder="请输入民族" />
            </a-form-item>
          </td>
        </tr>
        <!-- 身份证号 -->
        <tr>
          <td class="label-cell">身份证号</td>
          <td class="field-cell">
            <a-form-item name="idNumber">
              <a-input v-model:value="formData.idNumber" placeholder="请输入身份证号" />
            </a-form-item>
          </td>
        </tr>
        <!-- 政治面貌 -->
        <tr>
          <td class="label-cell">政治面貌</td>
          <td class="field-cell">
            <a-form-item name="politicBackground">
              <a-input v-model:value="formData.politicBackground" placeholder="请输入政治面貌" />
            </a-form-item>
          </td>
        </tr>
        <!-- 户口所在地 -->
        <tr>
          <td class="label-cell">户口所在地</td>
          <td class="field-cell">
            <a-form-item name="hukou">
              <a-input v-model:value="formData.hukou" placeholder="请输入户口所在地" />
            </a-form-item>
          </td>
        </tr>
        <!-- 是否应届毕业生 -->
        <tr>
          <td class="label-cell">是否应届毕业生</td>
          <td class="field-cell">
            <a-form-item name="yjbys">
              <a-select v-model:value="formData.yjbys" placeholder="请选择">
                <a-select-option value="1">是</a-select-option>
                <a-select-option value="0">否</a-select-option>
              </a-select>
            </a-form-item>
          </td>
        </tr>
        <!-- 是否统招统分 -->
        <tr>
          <td class="label-cell">是否统招统分</td>
          <td class="field-cell">
            <a-form-item name="tztf">
              <a-select v-model:value="formData.tztf" placeholder="请选择">
                <a-select-option value="1">是</a-select-option>
                <a-select-option value="0">否</a-select-option>
              </a-select>
            </a-form-item>
          </td>
        </tr>
        <!-- 毕业院校 -->
        <tr>
          <td class="label-cell">毕业院校</td>
          <td class="field-cell">
            <a-form-item name="graduateCollege">
              <a-input v-model:value="formData.graduateCollege" placeholder="请输入毕业院校" />
            </a-form-item>
          </td>
        </tr>
        <!-- 最高学历 -->
        <tr>
          <td class="label-cell">最高学历</td>
          <td class="field-cell">
            <a-form-item name="education">
              <a-input v-model:value="formData.education" placeholder="请输入学历" />
            </a-form-item>
          </td>
        </tr>
        <!-- 最高学位 -->
        <tr>
          <td class="label-cell">最高学位</td>
          <td class="field-cell">
            <a-form-item name="degree">
              <a-input v-model:value="formData.degree" placeholder="请输入学位" />
            </a-form-item>
          </td>
        </tr>
        <!-- 专业 -->
        <tr>
          <td class="label-cell">专业</td>
          <td class="field-cell">
            <a-form-item name="profession">
              <a-input v-model:value="formData.profession" placeholder="请输入专业" />
            </a-form-item>
          </td>
        </tr>
        <!-- 毕业时间 -->
        <tr>
          <td class="label-cell">毕业时间</td>
          <td class="field-cell">
            <a-form-item name="graduateDate">
              <a-date-picker
                v-model:value="formData.graduateDate"
                placeholder="请选择毕业时间"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
              />
            </a-form-item>
          </td>
        </tr>
        <!-- 参加工作时间 -->
        <tr>
          <td class="label-cell">参加工作时间</td>
          <td class="field-cell">
            <a-form-item name="workDate">
              <a-date-picker
                v-model:value="formData.workDate"
                placeholder="请选择参加工作时间"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
              />
            </a-form-item>
          </td>
        </tr>
        <!-- 目前工作单位 -->
        <tr>
          <td class="label-cell">目前工作单位</td>
          <td class="field-cell">
            <a-form-item name="workUnit">
              <a-input v-model:value="formData.workUnit" placeholder="请输入目前工作单位" />
            </a-form-item>
          </td>
        </tr>
        <!-- 人事档案关系所在单位 -->
        <tr>
          <td class="label-cell">人事档案关系所在单位</td>
          <td class="field-cell">
            <a-form-item name="personFilesUnit">
              <a-input v-model:value="formData.personFilesUnit" placeholder="请输入人事档案关系所在单位" />
            </a-form-item>
          </td>
        </tr>
        <!-- 现行政职务 -->
        <tr>
          <td class="label-cell">现行政职务</td>
          <td class="field-cell">
            <a-form-item name="adminPosition">
              <a-input v-model:value="formData.adminPosition" placeholder="请输入现行政职务" />
            </a-form-item>
          </td>
        </tr>
        <!-- 任职时间 -->
        <tr>
          <td class="label-cell">任职时间</td>
          <td class="field-cell">
            <a-form-item name="adminPositionDate">
              <a-date-picker
                v-model:value="formData.adminPositionDate"
                placeholder="请选择任职时间"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
              />
            </a-form-item>
          </td>
        </tr>
        <!-- 现专业技术岗位或职员职级 -->
        <tr>
          <td class="label-cell">现专业技术岗位或职员职级</td>
          <td class="field-cell">
            <a-form-item name="professionLevel">
              <a-input v-model:value="formData.professionLevel" placeholder="请输入现专业技术岗位" />
            </a-form-item>
          </td>
        </tr>
        <!-- 聘任时间 -->
        <tr>
          <td class="label-cell">聘任时间</td>
          <td class="field-cell">
            <a-form-item name="professionLevelDate">
              <a-date-picker
                v-model:value="formData.professionLevelDate"
                placeholder="请选择聘任时间"
                style="width: 100%"
                :format="dateFormat"
                :valueFormat="dateFormat"
              />
            </a-form-item>
          </td>
        </tr>
        <!-- 联系电话 -->
        <tr>
          <td class="label-cell">联系电话</td>
          <td class="field-cell">
            <a-form-item name="mobile">
              <a-input v-model:value="formData.mobile" placeholder="请输入联系电话" />
            </a-form-item>
          </td>
        </tr>
        <!-- E-mail -->
        <tr>
          <td class="label-cell">E-mail</td>
          <td class="field-cell">
            <a-form-item name="email">
              <a-input v-model:value="formData.email" placeholder="请输入E-mail" />
            </a-form-item>
          </td>
        </tr>
        <!-- 应聘部门 -->
        <tr>
          <td class="label-cell">应聘部门</td>
          <td class="field-cell">
            <a-form-item name="applyDept">
              <a-input v-model:value="formData.applyDept" placeholder="请输入应聘部门" />
            </a-form-item>
          </td>
        </tr>
        <!-- 应聘岗位名称 -->
        <tr>
          <td class="label-cell">应聘岗位名称</td>
          <td class="field-cell">
            <a-form-item name="applyPosition">
              <a-input v-model:value="formData.applyPosition" placeholder="请输入应聘岗位名称" />
            </a-form-item>
          </td>
        </tr>
        <!-- 与本所关系 -->
        <tr>
          <td class="label-cell">与本所关系</td>
          <td class="field-cell">
            <a-form-item name="relationshipType">
              <div class="relationship-container">
                <span class="relationship-question">与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系? (请务必勾选)</span>
                <a-radio-group v-model:value="formData.relationshipType">
                  <a-radio value="no">无</a-radio>
                  <a-radio value="yes">有</a-radio>
                </a-radio-group>
                
                <a-input
                  v-if="formData.relationshipType === 'yes'"
                  v-model:value="formData.relationshipName"
                  placeholder="请输入姓名"
                  style="margin-top: 8px; max-width: 300px;"
                />
              </div>
            </a-form-item>
          </td>
        </tr>
        </tbody>
      </table>
    </a-form>

    <!-- 照片预览模态框 -->
    <a-modal
      v-model:visible="previewVisible"
      :title="'照片预览'"
      :footer="null"
      width="auto"
    >
      <img :src="previewImageUrl" alt="照片预览" style="max-width: 100%; max-height: 80vh;" />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, defineProps, defineEmits } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { EyeOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
import { defHttp } from '/@/utils/http/axios';

const props = defineProps({
  formData: { type: Object, default: () => ({}) },
  formDisabled: { type: Boolean, default: false }
});

const emit = defineEmits(['update:formData']);

// 日期格式
const dateFormat = 'YYYY-MM-DD';

// 表单引用
const formRef = ref();

// 上传状态
const uploadState = reactive({
  uploading: false,
  error: null as string | null,
});

// 用于强制重建上传控件
const uploaderKey = ref('uploader-initial');

// 预览相关
const previewVisible = ref(false);
const previewImageUrl = ref('');

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  nativePlace: [{ required: true, message: '请输入籍贯', trigger: 'blur' }],
  birthday: [{ required: true, message: '请选择出生年月', trigger: 'change' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

// 获取图片URL
const getImageUrl = (url: string) => {
  if (!url) return '';
  try {
    const raw = String(url).trim();
    
    // 直接透传 data/blob URL
    if (/^(data:|blob:)/i.test(raw)) return raw;
    
    // 通过平台工具补全文件访问前缀
    return getFileAccessHttpUrl(raw);
  } catch (e) {
    console.error('构建图片URL失败:', e, url);
    return url;
  }
};

// 预览图片
const previewImage = (url: string) => {
  if (url) {
    previewImageUrl.value = getImageUrl(url);
    previewVisible.value = true;
  }
};

// 删除图片
const removeImage = () => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除当前照片吗？删除后需要重新上传。',
    okText: '确定',
    cancelText: '取消',
    onOk() {
      props.formData.photograph = '';
      uploadState.error = null;
      message.success('照片已删除');
      // 删除后强制重建上传控件
      uploaderKey.value = 'uploader-' + Date.now();
      // 更新父组件数据
      emit('update:formData', props.formData);
    }
  });
};

// 处理图片加载错误
const handleImageError = (event: Event) => {
  console.error('图片加载失败:', (event.target as HTMLImageElement).src);
  
  uploadState.error = '图片加载失败，请检查图片链接是否有效';
  message.error('图片加载失败，请重新上传');

  // 清空当前图片地址，显示上传控件
  props.formData.photograph = '';
  // 更新父组件数据
  emit('update:formData', props.formData);
};

// 清除错误状态
const clearError = () => {
  uploadState.error = null;
};

// 重新上传
const reupload = () => {
  uploadState.error = null;
  uploadState.uploading = false;
  props.formData.photograph = '';
  message.info('请重新选择并上传照片');
  uploaderKey.value = 'uploader-' + Date.now();
  // 更新父组件数据
  emit('update:formData', props.formData);
};

// 上传前验证
const beforeUpload = (file: File) => {
  // 检查文件类型
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'].includes(file.type);
  if (!isValidType) {
    message.error('只能上传 JPG/PNG/GIF 格式的图片!');
    return false;
  }
  
  // 检查文件大小，限制2MB
  const isValidSize = file.size / 1024 / 1024 < 2;
  if (!isValidSize) {
    message.error('图片大小不能超过 2MB!');
    return false;
  }
  
  return true;
};

// 自定义上传实现
const customUpload = async (options: any) => {
  const { file, onSuccess, onError } = options;
  
  uploadState.uploading = true;
  uploadState.error = null;
  
  try {
    // 创建表单数据
    const formData = new FormData();
    formData.append('file', file);
    
    // 调用文件上传接口
    const result = await defHttp.uploadFile(
      { url: '/sys/common/upload' },
      { file: file }
    );
    
    if (result?.success) {
      // 上传成功
      props.formData.photograph = result.message;
      // 更新父组件数据
      emit('update:formData', props.formData);
      onSuccess(result);
      message.success('照片上传成功!');
    } else {
      throw new Error(result?.message || '上传失败');
    }
  } catch (error: any) {
    console.error('照片上传失败:', error);
    uploadState.error = error.message || '上传失败';
    onError(error);
    message.error('照片上传失败: ' + uploadState.error);
  } finally {
    uploadState.uploading = false;
  }
};

// 表单方法
const validate = async () => {
  return await formRef.value.validate();
};

const resetFields = () => {
  formRef.value.resetFields();
};

// 暴露方法
defineExpose({
  validate,
  resetFields
});
</script>

<style lang="less" scoped>
.basic-info-form {
  width: 100%;
  
  .resume-table {
    width: 100%;
    border-collapse: collapse;
    
    td {
      border: 1px solid #d9d9d9;
      padding: 8px;
      vertical-align: top;
    }
    
    .label-cell {
      width: 200px;
      background-color: #f5f5f5;
      font-weight: 500;
      text-align: right;
      vertical-align: middle;
      padding-right: 12px;
    }
    
    .field-cell {
      padding: 8px 12px;
      
      .ant-form-item {
        margin-bottom: 0;
      }
    }
    
    .photo-cell {
      padding: 12px;
    }
    
    /* 确保表单元素占满单元格 */
    :deep(.ant-select),
    :deep(.ant-picker),
    :deep(.ant-input-number),
    :deep(.ant-input) {
      width: 100%;
    }
    
      .relationship-container {
      display: flex;
      flex-direction: column;
      row-gap: 8px;
      
      .relationship-question {
        margin-bottom: 8px;
        display: block;
        font-size: 14px;
      }
    }
  }
  
  .photo-upload-container {
    width: 100%;
    min-height: 180px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border: 1px dashed #d9d9d9;
    border-radius: 4px;
    background-color: #fafafa;
    padding: 12px;
    position: relative;
    
    &:hover {
      border-color: #1890ff;
    }
  }
  
  .upload-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    min-height: 168px;
    
    p {
      margin-top: 16px;
      color: #666;
    }
  }
  
  .upload-error {
    width: 100%;
    padding: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .upload-placeholder {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .ant-upload {
      width: 120px;
      height: 120px;
      margin: 0 auto;
    }
    
    .upload-tips {
      width: 100%;
      text-align: center;
      margin-top: 8px;
      color: #666;
      font-size: 12px;
      
      p {
        margin-bottom: 2px;
      }
    }
  }
  
  .photo-display {
    width: 100%;
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    
    .photo-image {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      cursor: pointer;
    }
    
    .image-actions {
      position: absolute;
      top: 8px;
      right: 8px;
      display: flex;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.3s;
    }
    
    &:hover .image-actions {
      opacity: 1;
    }
  }
}
</style>
