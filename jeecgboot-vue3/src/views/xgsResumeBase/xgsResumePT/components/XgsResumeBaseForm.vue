<template>
  <div>
    <a-card>
      <!-- 子表单区域 -->
      <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs" tab-position="left">
        <!--主表区域 -->
        <a-tab-pane tab="基本信息" :key="refKeys[0]" :forceRender="true" :style="tabsStyle">
          <div class="resume-form-table">
            <table class="form-table">
              <tbody>
                <!-- 第一行：姓名、本人照片 -->
                <tr>
                  <td class="label-cell">姓名</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.name" placeholder="请输入姓名" />
                  </td>
                  <td class="label-cell" rowspan="4" colspan="1">
                    本人照片
                  </td>                  
                  <td class="photo-cell" rowspan="4" colspan="1">
                    <div class="image-container">
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
                        <a-button type="primary" size="small" @click="clearError" style="margin-top: 8px;">
                          重新上传
                        </a-button>
                      </div>
                      
                      <!-- 显示大图 -->
                      <div v-else-if="formData.photograph" class="large-image-display">
                        <!-- 直接显示大图 -->
                        <img 
                          :src="formData.photograph" 
                          class="large-image"
                          :style="{ 
                            height: photoCellHeight + 'px',
                            width: 'auto',
                            maxWidth: '100%',
                            objectFit: 'contain'
                          }"
                          alt="本人照片"
                          role="img"
                          tabindex="0"
                          @click="previewImage(formData.photograph)"
                          @keydown.enter="previewImage(formData.photograph)"
                          @keydown.space.prevent="previewImage(formData.photograph)"
                          @error="handleImageError"
                        />
                        
                        <!-- 图片操作按钮 -->
                        <div class="image-actions" role="group" aria-label="照片操作">
                          <a-button 
                            type="text" 
                            size="small" 
                            @click="previewImage(formData.photograph)"
                            class="preview-btn"
                            title="预览照片"
                            aria-label="预览照片"
                          >
                            <template #icon>
                              <EyeOutlined />
                            </template>
                          </a-button>
                          <a-button 
                            type="text" 
                            size="small" 
                            @click="removeImage"
                            class="remove-btn"
                            title="删除照片"
                            aria-label="删除照片"
                          >
                            <template #icon>
                              <DeleteOutlined />
                            </template>
                          </a-button>
                        </div>
                      </div>
                      
                      <!-- 上传按钮 -->
                      <div v-else class="upload-placeholder" role="region" aria-label="照片上传区域">
                        <JImageUpload 
                          v-model:value="formData.photograph" 
                          :fileMax="1"
                          :showUploadList="false"
                          :listType="'picture-card'"
                          :style="{ height: photoCellHeight + 'px', width: '100%' }"
                          @change="handleImageUploadSuccess"
                          @error="handleUploadError"
                          aria-label="上传本人照片"
                        />
                        <div class="upload-tips" role="note" aria-label="上传要求说明">
                          <p>支持 JPG、PNG、GIF 格式</p>
                          <p>文件大小不超过 2MB</p>
                          <p>建议尺寸：400×500 像素</p>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
                
                <!-- 第二行：性别 -->
                <tr>
                  <td class="label-cell">性别</td>
                  <td class="input-cell">
                    <a-select v-model:value="formData.sex" placeholder="请选择性别">
                      <a-select-option value="1">男</a-select-option>
                      <a-select-option value="2">女</a-select-option>
                    </a-select>
                  </td>
                </tr>
                
                <!-- 第三行：出生年月 -->
                <tr>
                  <td class="label-cell">出生年月</td>
                  <td class="input-cell">
                    <a-date-picker v-model:value="formData.birthday" placeholder="请选择出生年月" />
                  </td>
                </tr>
                
                <!-- 第四行：民族 -->
                <tr>
                  <td class="label-cell">民族</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.nation" placeholder="请输入民族" />
                  </td>
                </tr>
                
                <!-- 第五行：政治面貌、户口所在地 -->
                <tr>
                  <td class="label-cell">政治面貌</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.politicBackground" placeholder="请输入政治面貌" />
                  </td>
                  <td class="label-cell">户口所在地 <br>(应届毕业生填生源地)</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.hukou" placeholder="请输入户口所在地" />
                  </td>
                </tr>
                
                <!-- 第六行：身份证号、籍贯 -->
                <tr>
                  <td class="label-cell">身份证号</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.idNumber" placeholder="请输入身份证号" />
                  </td>
                  <td class="label-cell">籍贯</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" />
                  </td>
                </tr>
                
                <!-- 第七行：是否应届毕业生、是否统招统分 -->
                <tr>
                  <td class="label-cell">是否应届毕业生</td>
                  <td class="input-cell">
                    <a-select v-model:value="formData.yjbys" placeholder="请选择">
                      <a-select-option value="1">是</a-select-option>
                      <a-select-option value="0">否</a-select-option>
                    </a-select>
                  </td>
                  <td class="label-cell">是否统招统分</td>
                  <td class="input-cell">
                    <a-select v-model:value="formData.tz" placeholder="请选择">
                      <a-select-option value="1">是</a-select-option>
                      <a-select-option value="0">否</a-select-option>
                    </a-select>
                  </td>
                </tr>
                
                <!-- 第八行：毕业院校、学历 -->
                <tr>
                  <td class="label-cell">毕业院校</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.graduateCollege" placeholder="请输入毕业院校" />
                  </td>
                  <td class="label-cell">最高学历</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.education" placeholder="请输入学历" />
                  </td>
                </tr>
                
                <!-- 第九行：学位、专业 -->
                <tr>
                  <td class="label-cell">最高学位</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.degree" placeholder="请输入学位" />
                  </td>
                  <td class="label-cell">专业</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.profession" placeholder="请输入专业" />
                  </td>
                </tr>

                <!-- 第十行：毕业时间、参加工作时间 -->
                <tr>
                  <td class="label-cell">毕业时间</td>
                  <td class="input-cell">
                    <a-date-picker v-model:value="formData.graduateDate" placeholder="请选择毕业时间" />
                  </td>
                  <td class="label-cell">参加工作时间</td>
                  <td class="input-cell">
                    <a-date-picker v-model:value="formData.workDate" placeholder="请选择参加工作时间" />
                  </td>
                </tr>

                <!-- 第十一行：目前工作单位、人事档案关系所在单位 -->
                <tr>
                  <td class="label-cell">目前工作单位</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.currentWorkUnit" placeholder="请输入目前工作单位" />
                  </td>
                  <td class="label-cell">人事档案关系所在单位</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.personnelFileUnit" placeholder="请输入人事档案关系所在单位" />
                  </td>
                </tr>

                <!-- 第十二行：现行政职务、任职时间 -->
                <tr>
                  <td class="label-cell">现行政职务</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.currentAdminPosition" placeholder="请输入现行政职务" />
                  </td>
                  <td class="label-cell">任职时间</td>
                  <td class="input-cell">
                    <a-date-picker v-model:value="formData.adminPositionTime" placeholder="请选择任职时间" />
                  </td>
                </tr>

                <!-- 第十三行：现专业技术岗位、聘任时间 -->
                <tr>
                  <td class="label-cell">现专业技术岗位 <br>或职员职级</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.currentTechPosition" placeholder="请输入现专业技术岗位" />
                  </td>
                  <td class="label-cell">聘任时间</td>
                  <td class="input-cell">
                    <a-date-picker v-model:value="formData.techPositionTime" placeholder="请选择聘任时间" />
                  </td>
                </tr>

                <!-- 第十四行：关系声明 -->
                <tr>
                  <td class="label-cell" colspan="2">
                    与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、<br>三代以内旁系血亲或者近姻亲关系(若有请写出姓名)? (请务必勾选)
                  </td>
               
                  <td class="checkbox-cell" style="text-align: center;vertical-align: middle;" colspan="1">
                    <div style="display: flex; align-items: center; white-space: nowrap; flex-wrap: nowrap;">
                      <a-radio-group v-model:value="formData.relationshipType" style="display: flex; align-items: center; white-space: nowrap; flex-wrap: nowrap;">
                        <a-radio value="no">无</a-radio>
                        <a-radio value="yes">有,姓名</a-radio>
                      </a-radio-group>
                      
                    </div>
                  </td>
                  <td class="input-cell" style="text-align: center;vertical-align: middle;" colspan="1">
                    <a-input 
                          v-model:value="formData.relationshipName" 
                          placeholder="请输入姓名"
                          style="width: 100%; padding: 0 10px; box-sizing: border-box; " 
                          v-if="formData.relationshipType === 'yes'"
                        />
                  </td>
                </tr>
                
                <!-- 第十五行：E-mail、联系电话 -->
                <tr>
                  <td class="label-cell">E-mail</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.email" placeholder="请输入E-mail" />
                  </td>
                  <td class="label-cell">联系电话</td>
                  <td class="input-cell">
                    <a-input v-model:value="formData.phone" placeholder="请输入联系电话" />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </a-tab-pane>
        <!--子表单区域 -->
        <a-tab-pane tab="工作经历" key="xgsResumeWorks" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeWorks"
            v-if="xgsResumeWorksTable.show"
            :loading="xgsResumeWorksTable.loading"
            :columns="xgsResumeWorksTable.columns"
            :dataSource="xgsResumeWorksTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="教育经历" key="xgsResumeEdus" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeEdus"
            v-if="xgsResumeEdusTable.show"
            :loading="xgsResumeEdusTable.loading"
            :columns="xgsResumeEdusTable.columns"
            :dataSource="xgsResumeEdusTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="家庭状况" key="xgsResumeHome" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeHome"
            v-if="xgsResumeHomeTable.show"
            :loading="xgsResumeHomeTable.loading"
            :columns="xgsResumeHomeTable.columns"
            :dataSource="xgsResumeHomeTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="工作主要业绩" key="xgsResumeResearchResult" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeResearchResult"
            v-if="xgsResumeResearchResultTable.show"
            :loading="xgsResumeResearchResultTable.loading"
            :columns="xgsResumeResearchResultTable.columns"
            :dataSource="xgsResumeResearchResultTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="应聘岗位陈述" key="xgsResumePositionDescription" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumePositionDescription"
            v-if="xgsResumePositionDescriptionTable.show"
            :loading="xgsResumePositionDescriptionTable.loading"
            :columns="xgsResumePositionDescriptionTable.columns"
            :dataSource="xgsResumePositionDescriptionTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="研究方向与专长" key="xgsResumeResearchDirection" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeResearchDirection"
            v-if="xgsResumeResearchDirectionTable.show"
            :loading="xgsResumeResearchDirectionTable.loading"
            :columns="xgsResumeResearchDirectionTable.columns"
            :dataSource="xgsResumeResearchDirectionTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
        <a-tab-pane tab="论文专著专利" key="xgsResumeResearchPaper" :forceRender="true" :style="tabsStyle">
          <JVxeTable
            keep-source
            resizable
            ref="xgsResumeResearchPaper"
            v-if="xgsResumeResearchPaperTable.show"
            :loading="xgsResumeResearchPaperTable.loading"
            :columns="xgsResumeResearchPaperTable.columns"
            :dataSource="xgsResumeResearchPaperTable.dataSource"
            :height="340"
            :disabled="formDisabled"
            :rowNumber="true"
            :rowSelection="true"
            :toolbar="true"
          />
        </a-tab-pane>
      </a-tabs>
    </a-card>
    <div style="width: 100%; text-align: center; margin-top: 10px" v-if="false">
      <a-button preIcon="ant-design:check-outlined" style="width: 126px" type="primary" @click="handleSubmit">提 交</a-button>
    </div>
    
    <!-- 照片预览模态框 -->
    <a-modal
      v-model:visible="previewState.visible"
      :title="previewState.title"
      :footer="null"
      width="auto"
      :centered="true"
      @cancel="closePreview"
      role="dialog"
      aria-modal="true"
      aria-labelledby="photo-preview-title"
    >
      <div class="photo-preview-container">
        <img 
          :src="previewState.url" 
          alt="照片预览"
          class="preview-image"
          role="img"
          tabindex="0"
          @keydown.escape="closePreview"
        />
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { defHttp } from '/@/utils/http/axios';
  import {ref, computed, reactive, onMounted, defineProps, defineExpose, watch, nextTick, onUnmounted} from 'vue';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods';
  import { xgsResumeWorksColumns, xgsResumeEdusColumns, xgsResumeHomeColumns, xgsResumeResearchResultColumns, xgsResumePositionDescriptionColumns, xgsResumeResearchDirectionColumns, xgsResumeResearchPaperColumns } from '../XgsResumeBase.data';
  import { xgsResumeWorksList, xgsResumeEdusList, xgsResumeHomeList, xgsResumeResearchResultList, xgsResumePositionDescriptionList, xgsResumeResearchDirectionList, xgsResumeResearchPaperList } from '../XgsResumeBase.api';
  import JImageUpload from '/@/components/Form/src/jeecg/components/JImageUpload.vue';
  import { message } from 'ant-design-vue';
  import { DeleteOutlined, EyeOutlined } from '@ant-design/icons-vue';

  const refKeys = ref(['xgsResumeBase', 'xgsResumeWorks', 'xgsResumeEdus', 'xgsResumeHome', 'xgsResumeResearchResult', 'xgsResumePositionDescription', 'xgsResumeResearchDirection', 'xgsResumeResearchPaper']);
  const activeKey = ref('xgsResumeBase');
  // const activeKey = ref(['1', '2', '3', '4']);
  const xgsResumeWorks = ref();
  const xgsResumeEdus = ref();
  const xgsResumeHome = ref();
  const xgsResumeResearchResult = ref();
  const xgsResumePositionDescription = ref();
  const xgsResumeResearchDirection = ref();
  const xgsResumeResearchPaper = ref();
  const tableRefs = { xgsResumeWorks, xgsResumeEdus, xgsResumeHome, xgsResumeResearchResult, xgsResumePositionDescription, xgsResumeResearchDirection, xgsResumeResearchPaper };
  const xgsResumeWorksTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeWorksColumns,
    show: true,
  });
  const xgsResumeEdusTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeEdusColumns,
    show: true,
  });
  const xgsResumeHomeTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeHomeColumns,
    show: true,
  });
  const xgsResumeResearchResultTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeResearchResultColumns,
    show: true,
  });
  const xgsResumePositionDescriptionTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumePositionDescriptionColumns,
    show: true,
  });
  const xgsResumeResearchDirectionTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeResearchDirectionColumns,
    show: true,
  });
  const xgsResumeResearchPaperTable = reactive({
    loading: false,
    dataSource: [],
    columns: xgsResumeResearchPaperColumns,
    show: true,
  });

  const props = defineProps({
    formData: { type: Object, default: () => {} },
    formBpm: { type: Boolean, default: true },
  });

      // 创建响应式的表单数据
      const formData = reactive({
        name: '',
        sex: '',
        nativePlace: '',
        photograph: '',
        birthday: '',
        nation: '',
        idNumber: '',
        politicBackground: '',
        hukou: '',
        yjbys: '',
        tz: '',
        graduateCollege: '',
        education: '',
        degree: '',
        profession: '',
        graduateDate: '',
        workDate: '',
        currentWorkUnit: '',
        personnelFileUnit: '',
        currentAdminPosition: '',
        adminPositionTime: '',
        currentTechPosition: '',
        techPositionTime: '',
        relationshipType: 'no',
        relationshipName: '',
        email: '',
        phone: '',
  });

  // 照片单元格动态高度
  const photoCellHeight = ref(240);
  let resizeObserver: ResizeObserver | null = null;
  
  // 图片显示模式：true=直接显示，false=使用JImageUpload组件
  const useDirectImageDisplay = ref(true);

  // 照片上传配置
  const photoConfig = {
    // 允许的文件格式
    allowedTypes: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'],
    // 最大文件大小 (2MB)
    maxSize: 2 * 1024 * 1024,
    // 最小尺寸
    minWidth: 200,
    minHeight: 200,
    // 最大尺寸
    maxWidth: 2000,
    maxHeight: 2000,
    // 推荐尺寸
    recommendedWidth: 400,
    recommendedHeight: 500
  };

  // 上传状态
  const uploadState = reactive({
    uploading: false,
    error: null as string | null,
    progress: 0
  });

  // 测量照片单元格高度
  const measurePhotoCellHeight = () => {
    nextTick(() => {
      const photoCell = document.querySelector('.photo-cell') as HTMLElement;
      if (photoCell) {
        const height = photoCell.offsetHeight;
        if (height > 0) {
          photoCellHeight.value = height;
        }
      }
    });
  };

  // 设置照片组件高度
  const setPhotoHeight = () => {
    // 由于现在使用 :style 绑定，高度会自动更新
    // 这里可以添加其他需要动态调整的样式
  };

  // 验证照片文件
  const validatePhotoFile = (file: File): Promise<boolean> => {
    return new Promise((resolve, reject) => {
      // 检查文件类型
      if (!photoConfig.allowedTypes.includes(file.type)) {
        const errorMsg = `不支持的文件格式。请上传 ${photoConfig.allowedTypes.map(type => type.split('/')[1].toUpperCase()).join('、')} 格式的图片。`;
        message.error(errorMsg);
        reject(new Error(errorMsg));
        return;
      }

      // 检查文件大小
      if (file.size > photoConfig.maxSize) {
        const errorMsg = `文件大小不能超过 ${Math.round(photoConfig.maxSize / 1024 / 1024)}MB。`;
        message.error(errorMsg);
        reject(new Error(errorMsg));
        return;
      }

      // 检查图片尺寸
      const img = new Image();
      img.onload = () => {
        const { width, height } = img;
        
        if (width < photoConfig.minWidth || height < photoConfig.minHeight) {
          const errorMsg = `图片尺寸过小。最小尺寸要求：${photoConfig.minWidth}×${photoConfig.minHeight}像素。`;
          message.error(errorMsg);
          reject(new Error(errorMsg));
          return;
        }

        if (width > photoConfig.maxWidth || height > photoConfig.maxHeight) {
          const errorMsg = `图片尺寸过大。最大尺寸限制：${photoConfig.maxWidth}×${photoConfig.maxHeight}像素。`;
          message.error(errorMsg);
          reject(new Error(errorMsg));
          return;
        }

        // 尺寸建议
        if (width !== photoConfig.recommendedWidth || height !== photoConfig.recommendedHeight) {
          message.warning(`建议使用 ${photoConfig.recommendedWidth}×${photoConfig.recommendedHeight} 像素的照片以获得最佳效果。`);
        }

        resolve(true);
      };

      img.onerror = () => {
        const errorMsg = '无法读取图片文件，请检查文件是否损坏。';
        message.error(errorMsg);
        reject(new Error(errorMsg));
      };

      img.src = URL.createObjectURL(file);
    });
  };

  // 处理图片上传成功
  const handleImageUploadSuccess = async (fileList: any[]) => {
    console.log('上传成功，文件列表:', fileList);
    
    if (fileList && fileList.length > 0) {
      const file = fileList[fileList.length - 1]; // 获取最后一个文件
      
      // 如果是新上传的文件，先进行验证
      if (file.originFileObj) {
        try {
          uploadState.uploading = true;
          uploadState.error = null;
          
          await validatePhotoFile(file.originFileObj);
          
          if (file.status === 'done' || file.status === 'success') {
            // JImageUpload组件使用 file.response.message 来获取URL
            formData.photograph = file.response?.message || file.url || file.response?.url || file.response?.data?.url;
            console.log('设置照片URL:', formData.photograph);
            message.success('照片上传成功！');
          }
        } catch (error: any) {
          console.error('照片验证失败:', error);
          uploadState.error = error.message || '照片验证失败';
          // 清除无效的文件
          formData.photograph = '';
          return;
        } finally {
          uploadState.uploading = false;
        }
      } else if (file.status === 'done' || file.status === 'success') {
        // 已存在的文件，直接设置URL
        formData.photograph = file.response?.message || file.url || file.response?.url || file.response?.data?.url;
        console.log('设置照片URL:', formData.photograph);
      }
    }
  };

  // 获取图片URL（处理相对路径）
  const getImageUrl = (url: string) => {
    if (!url) return '';
    
    console.log('原始图片URL:', url);
    
    // 直接返回原始URL，让浏览器处理编码
    return url;
  };

  // 预览图片状态
  const previewState = reactive({
    visible: false,
    url: '',
    title: '本人照片预览'
  });

  // 预览图片
  const previewImage = (url: string) => {
    if (url) {
      previewState.url = getImageUrl(url);
      previewState.visible = true;
    }
  };

  // 关闭预览
  const closePreview = () => {
    previewState.visible = false;
    previewState.url = '';
  };

  // 删除图片
  const removeImage = () => {
    // 使用 Ant Design 的确认对话框
    import('ant-design-vue').then(({ Modal }) => {
      Modal.confirm({
        title: '确认删除',
        content: '确定要删除当前照片吗？删除后需要重新上传。',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          formData.photograph = '';
          uploadState.error = null;
          message.success('照片已删除');
        }
      });
    });
  };

  // 处理图片加载错误
  const handleImageError = (event: Event) => {
    console.error('图片加载失败:', event);
    const img = event.target as HTMLImageElement;
    console.error('失败的图片URL:', img.src);
    
    uploadState.error = '图片加载失败，请检查图片链接是否有效';
    message.error('图片加载失败，请重新上传');
    
    // 切换到JImageUpload组件显示模式
    console.log('切换到JImageUpload组件显示模式');
    useDirectImageDisplay.value = false;
  };

  // 处理上传错误
  const handleUploadError = (error: any) => {
    console.error('上传错误:', error);
    uploadState.uploading = false;
    uploadState.error = error.message || '上传失败，请重试';
    message.error(uploadState.error);
  };

  // 清除错误状态
  const clearError = () => {
    uploadState.error = null;
  };



  const formDisabled = computed(() => {
    if (props.formBpm === true) {
      if (props.formData.disabled === false) {
        return false;
      }
    }
    return true;
  });

  onMounted(() => {
    initFormData();
    // 同步props.formData到本地formData
    if (props.formData) {
      Object.assign(formData, props.formData);
    }
    
    // 添加窗口大小变化监听
    window.addEventListener('resize', handleResize);
    
    // 初始化照片单元格高度测量
    setTimeout(() => {
      measurePhotoCellHeight();
      setPhotoHeight();
      
      // 设置ResizeObserver监听窗口大小变化
      if (window.ResizeObserver) {
        resizeObserver = new ResizeObserver(() => {
          measurePhotoCellHeight();
          setPhotoHeight();
        });
        const photoCell = document.querySelector('.photo-cell') as HTMLElement;
        if (photoCell && resizeObserver) {
          resizeObserver.observe(photoCell);
        }
      }
    }, 100);
  });

  // 组件卸载时清理ResizeObserver和事件监听器
  onUnmounted(() => {
    if (resizeObserver) {
      resizeObserver.disconnect();
      resizeObserver = null;
    }
    window.removeEventListener('resize', handleResize);
  });

  // 监听props.formData变化
  watch(() => props.formData, (newData) => {
    if (newData) {
      Object.assign(formData, newData);
    }
  }, { deep: true });

  // 监听照片单元格高度变化
  watch(photoCellHeight, () => {
    setPhotoHeight();
  });

  // 监听窗口大小变化
  const handleResize = () => {
    measurePhotoCellHeight();
  };
  //渲染流程表单数据
  const queryByIdUrl = '/xgsResume/xgsResumeBase/queryById';
  async function initFormData() {
    if (props.formData.dataId) {
      await reset();
      let params = { id: props.formData.dataId };
      const data = await defHttp.get({ url: queryByIdUrl, params });
      //表单赋值
      Object.assign(formData, data);
      // 加载子表数据
      try {
        const worksData = await defHttp.get({ url: xgsResumeWorksList, params: { id: data.id } });
        xgsResumeWorksTable.dataSource = worksData || [];
        xgsResumeWorksTable.show = true;
        
        const edusData = await defHttp.get({ url: xgsResumeEdusList, params: { id: data.id } });
        xgsResumeEdusTable.dataSource = edusData || [];
        xgsResumeEdusTable.show = true;
        
        const homeData = await defHttp.get({ url: xgsResumeHomeList, params: { id: data.id } });
        xgsResumeHomeTable.dataSource = homeData || [];
        xgsResumeHomeTable.show = true;
        
        const resultData = await defHttp.get({ url: xgsResumeResearchResultList, params: { id: data.id } });
        xgsResumeResearchResultTable.dataSource = resultData || [];
        xgsResumeResearchResultTable.show = true;
        
        const positionData = await defHttp.get({ url: xgsResumePositionDescriptionList, params: { id: data.id } });
        xgsResumePositionDescriptionTable.dataSource = positionData || [];
        xgsResumePositionDescriptionTable.show = true;
        
        const directionData = await defHttp.get({ url: xgsResumeResearchDirectionList, params: { id: data.id } });
        xgsResumeResearchDirectionTable.dataSource = directionData || [];
        xgsResumeResearchDirectionTable.show = true;
        
        const paperData = await defHttp.get({ url: xgsResumeResearchPaperList, params: { id: data.id } });
        xgsResumeResearchPaperTable.dataSource = paperData || [];
        xgsResumeResearchPaperTable.show = true;
      } catch (error) {
        console.error('加载子表数据失败:', error);
      }
    }
  }

  //方法配置
  const [handleChangeTabs, handleSubmit] = useJvxeMethod(
    requestAddOrEdit,
    classifyIntoFormData,
    tableRefs,
    activeKey,
    refKeys
  );
  // 弹窗tabs滚动区域的高度
  const tabsStyle = computed(() => {
    let height: Nullable<string> = null;
    let minHeight = '100px';
    // 弹窗wrapper
    let overflow = 'auto';
    return { height, minHeight, overflow };
  });

  async function reset() {
        // 重置formData
        Object.keys(formData).forEach(key => {
          if (key === 'relationshipType') {
            formData[key] = 'no';
          } else if (typeof formData[key] === 'boolean') {
            formData[key] = false;
          } else {
            formData[key] = '';
          }
        });
    activeKey.value = 'xgsResumeBase';
    xgsResumeWorksTable.dataSource = [];
    xgsResumeEdusTable.dataSource = [];
    xgsResumeHomeTable.dataSource = [];
        xgsResumeResearchResultTable.dataSource = [];
        xgsResumePositionDescriptionTable.dataSource = [];
        xgsResumeResearchDirectionTable.dataSource = [];
        xgsResumeResearchPaperTable.dataSource = [];
  }
  function classifyIntoFormData(allValues) {
    let main = Object.assign({}, formData);
    return {
      ...main, // 展开
      xgsResumeWorksList: allValues.tablesValue[0].tableData,
      xgsResumeEdusList: allValues.tablesValue[1].tableData,
      xgsResumeHomeList: allValues.tablesValue[2].tableData,
      xgsResumeResearchResultList: allValues.tablesValue[3].tableData,
      xgsResumePositionDescriptionList: allValues.tablesValue[4].tableData,
      xgsResumeResearchDirectionList: allValues.tablesValue[5].tableData,
      xgsResumeResearchPaperList: allValues.tablesValue[6].tableData,
    };
  }
  //表单提交事件
  async function requestAddOrEdit(values) {
    //提交表单
    // await saveOrUpdate(values, true);
    return new Promise((resolve) => {
      resolve(values);
    });
  }
  async function setDataByPDF(data) {
    // 同步数据到formData
    Object.assign(formData, data.value);
    xgsResumeWorksTable.dataSource = data.value.xgsResumeWorks;
    xgsResumeEdusTable.dataSource = data.value.xgsResumeEdus;
    xgsResumeHomeTable.dataSource = data.value.xgsResumeHome;
    xgsResumeResearchResultTable.dataSource = data.value.xgsResumeResearchResult;
    xgsResumePositionDescriptionTable.dataSource = data.value.xgsResumePositionDescription;
    xgsResumeResearchDirectionTable.dataSource = data.value.xgsResumeResearchDirection;
    xgsResumeResearchPaperTable.dataSource = data.value.xgsResumeResearchPaper;
  }
  defineExpose({
    reset,
    handleSubmit,
    setDataByPDF,
  });
</script>

<style lang="less" scoped>
  /** 时间和数字输入框样式 */
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }

  /** 照片布局特殊处理 - 基于模板重新设计 */
  :deep(.ant-form) {
    position: relative;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: start;
  }

  /** 照片上传组件特殊样式 - 右上角位置，对应两行字段高度 */
  :deep(.ant-form-item) {
    &:has(.ant-upload) {
      position: absolute;
      top: 0;
      right: 0;
      width: calc(45% - 10px);
      z-index: 10;
      height: 140px; /* 对应两行字段的高度 */
      
      .ant-form-item-control-input {
        min-height: 140px;
      }
    }
  }

  /** 为照片下方的字段留出空间 */
  :deep(.ant-form-item[data-field="nation"]) {
    margin-top: 160px; /* 为照片留出空间 */
  }

  /** 优化照片上传组件样式 - 基于模板设计 */
  :deep(.ant-form-item) {
    &:has(.ant-upload) {
      .ant-upload-list {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
      }
      
      .ant-upload-list-item {
        width: 100px;
        height: 100px;
        margin: 0;
        border-radius: 6px;
        overflow: hidden;
        border: 1px solid #d9d9d9;
      }
      
      .ant-upload-list-item-thumbnail {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .ant-upload.ant-upload-select-picture-card {
        width: 100px;
        height: 100px;
        margin: 0;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 2px dashed #d9d9d9;
        background-color: #fafafa;
        
        &:hover {
          border-color: #40a9ff;
        }
      }
      
      .ant-upload-list-item-actions {
        .anticon {
          color: #fff;
        }
      }
    }
  }

  /** 姓名字段 - 左上角，增加宽度 */
  :deep(.ant-form-item[data-field="name"]) {
    margin-bottom: 0;
    width: 100%;
    
    .ant-form-item-control-input {
      width: 100%;
    }
    
    .ant-input {
      width: 100%;
    }
  }

  /** 性别字段 - 紧挨着姓名，增加宽度 */
  :deep(.ant-form-item[data-field="sex"]) {
    margin-top: 0;
    margin-bottom: 0;
    width: 100%;
    
    .ant-form-item-control-input {
      width: 100%;
    }
    
    .ant-select {
      width: 100%;
    }
  }

  /** 表格中所有下拉框样式 - 确保高度一致 */
  .form-table .input-cell .ant-select {
    height: 60px !important;
    min-height: 60px !important;
  }

  .form-table .input-cell .ant-select .ant-select-selector {
    height: 60px !important;
    min-height: 60px !important;
    line-height: 60px !important;
    display: flex !important;
    align-items: center !important;
  }

  .form-table .input-cell .ant-select .ant-select-selection-item {
    line-height: 60px !important;
    height: 60px !important;
    display: flex !important;
    align-items: center !important;
  }

  .form-table .input-cell .ant-select .ant-select-selection-placeholder {
    line-height: 60px !important;
    height: 60px !important;
    display: flex !important;
    align-items: center !important;
  }

  .form-table .input-cell .ant-select .ant-select-arrow {
    top: 50% !important;
    transform: translateY(-50%) !important;
  }

  /** 强制所有下拉框高度一致 - 最高优先级 */
  .resume-form-table .form-table .ant-select {
    height: 60px !important;
    min-height: 60px !important;
  }

  .resume-form-table .form-table .ant-select .ant-select-selector {
    height: 60px !important;
    min-height: 60px !important;
    line-height: 60px !important;
  }

  .resume-form-table .form-table .ant-select .ant-select-selection-item {
    height: 60px !important;
    line-height: 60px !important;
  }

  .resume-form-table .form-table .ant-select .ant-select-selection-placeholder {
    height: 60px !important;
    line-height: 60px !important;
  }

  .resume-form-table .form-table .ant-select .ant-select-arrow {
    top: 50% !important;
    transform: translateY(-50%) !important;
  }

  /** 强制照片显示框高度一致 - 最高优先级 */
  .resume-form-table .form-table .photo-cell {
    height: 60px !important;
    min-height: 60px !important;
  }

  .resume-form-table .form-table .photo-container {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
    padding: 0 !important;
  }

  .resume-form-table .form-table .photo-upload {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  .resume-form-table .form-table .photo-cell .ant-upload-list-picture-card .ant-upload-list-item {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  .resume-form-table .form-table .photo-cell .ant-upload.ant-upload-select-picture-card {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  /** 使用 :deep() 强制穿透组件样式 */
  :deep(.resume-form-table .form-table .ant-select) {
    height: 60px !important;
    min-height: 60px !important;
  }

  :deep(.resume-form-table .form-table .ant-select .ant-select-selector) {
    height: 60px !important;
    min-height: 60px !important;
    line-height: 60px !important;
  }

  :deep(.resume-form-table .form-table .photo-cell) {
    height: 60px !important;
    min-height: 60px !important;
  }

  :deep(.resume-form-table .form-table .photo-container) {
    height: 60px !important;
    min-height: 60px !important;
  }

  :deep(.resume-form-table .form-table .photo-upload) {
    height: 60px !important;
    min-height: 60px !important;
  }

  /** 强制照片组件完全填充单元格 */
  :deep(.resume-form-table .form-table .photo-cell .ant-upload) {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  :deep(.resume-form-table .form-table .photo-cell .ant-upload-list) {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  :deep(.resume-form-table .form-table .photo-cell .ant-upload-list-picture-card) {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  :deep(.resume-form-table .form-table .photo-cell .ant-upload-list-picture-card .ant-upload-list-item) {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  :deep(.resume-form-table .form-table .photo-cell .ant-upload.ant-upload-select-picture-card) {
    height: 60px !important;
    min-height: 60px !important;
    width: 100% !important;
  }

  /** 最强制照片样式 - 确保完全填充 */
  .resume-form-table .form-table .photo-cell {
    padding: 0 !important;
    margin: 0 !important;
  }

  .resume-form-table .form-table .photo-cell * {
    height: 60px !important;
    min-height: 60px !important;
    max-height: 60px !important;
  }

  .resume-form-table .form-table .photo-cell .ant-upload,
  .resume-form-table .form-table .photo-cell .ant-upload-list,
  .resume-form-table .form-table .photo-cell .ant-upload-list-picture-card,
  .resume-form-table .form-table .photo-cell .ant-upload-list-picture-card .ant-upload-list-item,
  .resume-form-table .form-table .photo-cell .ant-upload.ant-upload-select-picture-card {
    height: 60px !important;
    min-height: 60px !important;
    max-height: 60px !important;
    width: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
    box-sizing: border-box !important;
  }

  /** 强制照片组件完全填充 - 覆盖所有可能的边距 */
  .resume-form-table .form-table .photo-cell,
  .resume-form-table .form-table .photo-cell *,
  .resume-form-table .form-table .photo-cell .photo-container,
  .resume-form-table .form-table .photo-cell .photo-upload {
    padding: 0 !important;
    margin: 0 !important;
    border: none !important;
    box-sizing: border-box !important;
  }

  /** 简化照片样式 - 回到基本方法 */
  .photo-cell {
    padding: 0 !important;
    margin: 0 !important;
    height: 60px !important;
  }

  .photo-upload {
    height: 60px !important;
    width: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }

  /** 所有输入框宽度 - 基于模板优化 */
  :deep(.ant-input),
  :deep(.ant-select),
  :deep(.ant-picker) {
    width: 100%;
    max-width: 100%;
    min-width: 180px; /* 适中的最小宽度 */
  }

  /** 所有表单项宽度 */
  :deep(.ant-form-item) {
    width: 100%;
    
    .ant-form-item-control-input {
      width: 100%;
    }
  }

  /** 根据模板优化字段布局 - 某些字段可以更紧凑 */
  :deep(.ant-form-item[data-field="education"]),
  :deep(.ant-form-item[data-field="degree"]) {
    .ant-input,
    .ant-select {
      min-width: 120px; /* 学历、学位字段可以更紧凑 */
    }
  }

  /** 长文本字段保持较长宽度 */
  :deep(.ant-form-item[data-field="name"]),
  :deep(.ant-form-item[data-field="graduateCollege"]),
  :deep(.ant-form-item[data-field="profession"]),
  :deep(.ant-form-item[data-field="hukou"]) {
    .ant-input {
      min-width: 200px; /* 长文本字段保持较长宽度 */
    }
  }

  /** 日期字段优化 */
  :deep(.ant-form-item[data-field="birthday"]),
  :deep(.ant-form-item[data-field="graduateDate"]),
  :deep(.ant-form-item[data-field="workDate"]) {
    .ant-picker {
      min-width: 150px; /* 日期字段适中宽度 */
    }
  }

  /** 表格布局样式 */
  .resume-form-table {
    width: 100%;
    padding: 20px;
    overflow-x: auto;
    min-width: 800px;
  }

  .form-table {
    width: 100%;
    min-width: 800px;
    border-collapse: collapse;
    border: 1px solid #d9d9d9;
    font-size: 14px;
    table-layout: fixed;
  }

  .form-table td {
    border: 1px solid #d9d9d9;
    padding: 12px 16px;
    vertical-align: middle;
    overflow: hidden;
    word-wrap: break-word;
    word-break: break-all;
    position: relative;
    height: 60px;
  }

  .form-table .photo-cell {
    padding: 0 !important;
    margin: 0 !important;
    height: auto !important;
    min-height: 240px !important;
  }

  .label-cell {
    background-color: #f5f5f5;
    font-weight: 500;
    text-align: center;
    white-space: nowrap;
    width: 15%;
    min-width: 80px;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .input-cell {
    background-color: #fff;
    width: 25%;
    min-width: 120px;
    max-width: 180px;
    overflow: hidden;
  }

  .photo-cell {
    background-color: #fafafa;
    text-align: center;
    vertical-align: top;
    width: 30%;
    min-width: 200px;
    max-width: 280px;
    position: relative;
    overflow: hidden;
    padding: 0 !important;
    margin: 0 !important;
  }

  .photo-container {
    padding: 0;
    margin: 0;
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .photo-label {
    font-weight: 500;
    margin-bottom: 10px;
    color: #333;
    font-size: 14px;
  }

  .photo-cell .ant-upload {
    width: 100% !important;
    height: 100% !important;
    overflow: hidden;
    transition: height 0.3s ease;
  }

  /** 上传占位符内的上传组件样式 */
  .upload-placeholder .ant-upload {
    width: 100% !important;
    height: 100% !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  /** 照片组件内部样式优化 - 确保图片充满单元格且比例不失真 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item {
    width: 100% !important;
    height: 100% !important;
    border-radius: 4px !important;
    overflow: hidden;
    position: relative;
    padding: 0 !important;
    margin: 0 !important;
  }

  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-thumbnail {
    width: 100% !important;
    height: 100% !important;
    object-fit: cover !important;
    object-position: center !important;
    border-radius: 4px !important;
  }

  .photo-cell .ant-upload.ant-upload-select-picture-card {
    width: 100% !important;
    height: 100% !important;
    border-radius: 4px !important;
    border: 2px dashed #d9d9d9 !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  .photo-cell .ant-upload.ant-upload-select-picture-card .ant-upload {
    width: 100% !important;
    height: 100% !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  /** 确保删除按钮可见 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-actions {
    opacity: 1 !important;
  }

  /** 确保图片列表容器充满单元格 */
  .photo-cell .ant-upload-list-picture-card {
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
  }

  /** 确保图片列表项充满容器 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item {
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
    border: none !important;
  }

  /** 确保图片缩略图充满列表项 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-thumbnail {
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    padding: 0 !important;
    border: none !important;
  }

  /** 照片容器样式 - 参考示例代码 */
  .image-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;  /* 垂直居中 */
    justify-content: center;  /* 水平居中 */
    padding: 0;
    margin: 0;
  }

  /** 大图显示容器 */
  .large-image-display {
    position: relative;
    width: 100% !important;
    height: 100% !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    padding: 0 !important;
    margin: 0 !important;
    overflow: hidden !important;
  }

  /** 大图样式 */
  .large-image {
    width: 100% !important;
    height: 100% !important;
    max-width: 100% !important;
    max-height: 100% !important;
    min-width: 100% !important;
    min-height: 100% !important;
    object-fit: cover !important; /* 充满容器，可能裁剪部分图片 */
    object-position: center !important;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: transform 0.2s ease;
    display: block !important;
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    right: 0 !important;
    bottom: 0 !important;
  }

  .large-image:hover {
    transform: scale(1.02);
  }

  /** 图片操作按钮 */
  .image-actions {
    position: absolute;
    top: 8px;
    right: 8px;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .large-image-display:hover .image-actions {
    opacity: 1;
  }

  /** 删除按钮样式 */
  .remove-btn {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid #ff4d4f;
    color: #ff4d4f;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .remove-btn:hover {
    background: #ff4d4f;
    color: white;
  }

  /** 上传占位符样式 */
  .upload-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
  }

  /** 上传提示信息 */
  .upload-tips {
    position: absolute;
    bottom: 8px;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(0, 0, 0, 0.7);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    text-align: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
  }

  .upload-placeholder:hover .upload-tips {
    opacity: 1;
  }

  .upload-tips p {
    margin: 2px 0;
    line-height: 1.2;
  }

  /** 上传加载状态 */
  .upload-loading {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 4px;
  }

  .upload-loading p {
    margin-top: 12px;
    color: #666;
    font-size: 14px;
  }

  /** 上传错误状态 */
  .upload-error {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8px;
  }

  /** 预览按钮样式 */
  .preview-btn {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid #1890ff;
    color: #1890ff;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-right: 4px;
  }

  .preview-btn:hover {
    background: #1890ff;
    color: white;
  }

  /** 照片预览模态框样式 */
  .photo-preview-container {
    text-align: center;
    max-width: 90vw;
    max-height: 90vh;
  }

  .preview-image {
    max-width: 100%;
    max-height: 80vh;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  /** 无障碍支持样式 */
  .large-image:focus {
    outline: 2px solid #1890ff;
    outline-offset: 2px;
  }

  .preview-image:focus {
    outline: 2px solid #1890ff;
    outline-offset: 2px;
  }

  .image-actions .ant-btn:focus {
    outline: 2px solid #1890ff;
    outline-offset: 2px;
  }

  /** 高对比度模式支持 */
  @media (prefers-contrast: high) {
    .large-image {
      border: 2px solid #000;
    }
    
    .preview-image {
      border: 2px solid #000;
    }
    
    .image-actions .ant-btn {
      border: 1px solid #000;
    }
  }

  /** 减少动画模式支持 */
  @media (prefers-reduced-motion: reduce) {
    .large-image {
      transition: none;
    }
    
    .image-actions {
      transition: none;
    }
    
    .upload-tips {
      transition: none;
    }
  }


  /** 确保照片单元格内的所有元素都填充完整 */
  .photo-cell {
    padding: 0 !important;
    margin: 0 !important;
    position: relative;
  }

  /** 强制所有上传相关元素充满单元格 */
  .photo-cell .ant-upload,
  .photo-cell .ant-upload-list,
  .photo-cell .ant-upload-list-picture-card,
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item,
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-thumbnail {
    width: 100% !important;
    height: 100% !important;
    max-width: 100% !important;
    max-height: 100% !important;
    min-width: 100% !important;
    min-height: 100% !important;
  }

  /** 确保图片内容充满缩略图容器并最大化显示 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-thumbnail img {
    width: 100% !important;
    height: 100% !important;
    /* 使用 contain 保持完整图片，使用 cover 充满容器 */
    object-fit: cover !important; /* 充满容器，可能裁剪部分图片 */
    object-position: center !important;
    background-color: #f5f5f5; /* 添加背景色填充空白区域 */
  }

  /** 如果需要显示完整图片（不裁剪），使用这个样式 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item .ant-upload-list-item-thumbnail img.full-image {
    object-fit: contain !important; /* 显示完整图片，保持比例 */
  }

  /** 确保照片组件完全填充单元格 */
  .photo-container {
    padding: 0 !important;
    margin: 0 !important;
    height: 100% !important;
    width: 100% !important;
  }

  .photo-upload {
    width: 100% !important;
    height: 100% !important;
    padding: 0 !important;
    margin: 0 !important;
  }

  /** 照片单元格完全填充 */
  .photo-cell {
    padding: 0 !important;
    margin: 0 !important;
    height: auto !important;
  }

  .empty-cell {
    background-color: #fff;
  }

  .checkbox-cell {
    background-color: #ffffff;
    text-align: left;
    padding: 15px;
    white-space: nowrap;
    overflow: visible;
  }

  /** 单选框组样式 */
  .form-table .ant-radio-group {
    display: flex;
    align-items: center;
    white-space: nowrap;
    flex-wrap: nowrap;
    flex-shrink: 0;
  }

  .form-table .ant-radio-wrapper {
    margin-right: 16px;
    white-space: nowrap;
    flex-shrink: 0;
  }

  .form-table .ant-radio-wrapper:last-child {
    margin-right: 16px;
  }

  /** 单选框融合样式 */
  .form-table .ant-radio-wrapper {
    background: transparent;
    border: none;
    padding: 0;
    margin: 0 16px 0 0;
  }

  .form-table .ant-radio-wrapper .ant-radio {
    margin-right: 6px;
  }

  .form-table .ant-radio-wrapper .ant-radio-inner {
    border-color: #d9d9d9;
  }

  .form-table .ant-radio-wrapper:hover .ant-radio-inner {
    border-color: #40a9ff;
  }

  .form-table .ant-radio-wrapper-checked .ant-radio-inner {
    border-color: #40a9ff;
    background-color: #40a9ff;
  }

  /** 关系输入框样式 */
  .checkbox-cell .ant-input {
    display: inline-block;
    flex-shrink: 0;
    margin-left: 20px;
    width: 120px;
    vertical-align: middle;
    height: 32px;
    line-height: 32px;
  }

  .checkbox-cell .ant-input input {
    height: 40px;
    line-height: 40px;
    border: 1px solid #757494;
    border-radius: 4px;
    padding: 0 8px;
    text-align: center;
    vertical-align: middle;
    box-sizing: border-box;
    margin: 0;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .checkbox-cell .ant-input:focus-within {
    border-color: #2a7dc0;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  }

  /** 输入框样式优化 - 与单元格完全融合 */
  .form-table .ant-input,
  .form-table .ant-select,
  .form-table .ant-picker {
    width: 100%;
    max-width: 100%;
    border: none;
    box-shadow: none;
    overflow: hidden;
    text-overflow: ellipsis;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
  }

  .form-table .ant-input:focus,
  .form-table .ant-select:focus,
  .form-table .ant-picker:focus {
    border: none;
    box-shadow: none;
    background: rgba(24, 144, 255, 0.1);
  }

  .form-table .ant-input:hover,
  .form-table .ant-select:hover,
  .form-table .ant-picker:hover {
    background: rgba(0, 0, 0, 0.02);
  }

  /** 确保输入框内容不超出 */
  .form-table .ant-input input {
    width: 100%;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
    line-height: 60px;
  }

  .form-table .ant-input input:focus {
    border: none;
    box-shadow: none;
    background: transparent;
  }

  .form-table .ant-select-selector {
    width: 100%;
    max-width: 100%;
    overflow: hidden;
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
    line-height: 60px;
  }

  .form-table .ant-select-selector:focus {
    border: none;
    box-shadow: none;
    background: transparent;
  }

  .form-table .ant-picker {
    width: 100%;
    max-width: 100%;
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
  }

  .form-table .ant-picker .ant-picker-input {
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
    line-height: 60px;
  }

  .form-table .ant-picker .ant-picker-input input {
    border: none;
    background: transparent;
    padding: 0;
    margin: 0;
    height: 100%;
    min-height: 60px;
    line-height: 60px;
  }

  /** 完全融合效果 */
  .form-table .ant-input,
  .form-table .ant-select,
  .form-table .ant-picker {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }

  .form-table .input-cell {
    position: relative;
  }

  /** 选择器下拉箭头样式 */
  .form-table .ant-select .ant-select-arrow {
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
  }

  /** 日期选择器图标样式 */
  .form-table .ant-picker .ant-picker-suffix {
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
  }

  /** 照片上传组件样式 */
  .form-table .ant-upload-list-picture-card .ant-upload-list-item {
    width: 150px;
    height: 150px;
    margin: 0 auto;
    max-width: 100%;
    overflow: hidden;
  }

  .form-table .ant-upload.ant-upload-select-picture-card {
    width: 150px;
    height: 150px;
    margin: 0 auto;
    max-width: 100%;
    border: 2px dashed #d9d9d9;
    background-color: #fafafa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
  }

  .form-table .ant-upload.ant-upload-select-picture-card:hover {
    border-color: #40a9ff;
  }

  /** 照片单元格中的上传组件高度调整 */
  .photo-cell .ant-upload-list-picture-card .ant-upload-list-item {
    width: 100%;
    height: 60px;
    max-width: 100%;
    overflow: hidden;
  }

  .photo-cell .ant-upload.ant-upload-select-picture-card {
    width: 100%;
    height: 60px;
    max-width: 100%;
    overflow: hidden;
  }

  .form-table .ant-upload.ant-upload-select-picture-card .ant-upload {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
  }

  .form-table .ant-upload.ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    font-size: 12px;
    color: #666;
  }

  .form-table .ant-upload.ant-upload-select-picture-card::before {
    content: "近期免冠正面照片";
    position: absolute;
    top: 8px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 12px;
    color: #999;
    white-space: nowrap;
  }

  /** 响应式布局优化 */
  @media (max-width: 1200px) {
    .resume-form-table {
      min-width: 700px;
    }
    
    .form-table {
      min-width: 700px;
    }
    
    .label-cell {
      width: 18%;
      min-width: 70px;
      max-width: 100px;
    }
    
    .input-cell {
      width: 27%;
      min-width: 100px;
      max-width: 150px;
    }
    
    .photo-cell {
      width: 28%;
      min-width: 180px;
      max-width: 220px;
    }
  }

  @media (max-width: 992px) {
    .resume-form-table {
      min-width: 600px;
    }
    
    .form-table {
      min-width: 600px;
    }
    
    .label-cell {
      width: 20%;
      min-width: 60px;
      max-width: 80px;
    }
    
    .input-cell {
      width: 30%;
      min-width: 80px;
      max-width: 120px;
    }
    
    .photo-cell {
      width: 20%;
      min-width: 150px;
      max-width: 180px;
    }
    
    .photo-upload {
      max-width: 120px;
    }
    
    .form-table .ant-upload-list-picture-card .ant-upload-list-item,
    .form-table .ant-upload.ant-upload-select-picture-card {
      width: 120px;
      height: 120px;
    }
  }

  @media (max-width: 768px) {
    .resume-form-table {
      min-width: 500px;
    }
    
    .form-table {
      min-width: 500px;
    }
    
    .label-cell {
      width: 22%;
      min-width: 50px;
      max-width: 70px;
    }
    
    .input-cell {
      width: 33%;
      min-width: 70px;
      max-width: 100px;
    }
    
    .photo-cell {
      width: 12%;
      min-width: 120px;
      max-width: 150px;
    }
    
    .photo-upload {
      max-width: 100px;
    }
    
    .form-table .ant-upload-list-picture-card .ant-upload-list-item,
    .form-table .ant-upload.ant-upload-select-picture-card {
      width: 100px;
      height: 100px;
    }
  }

</style>

<style lang="less">
  // Online表单Tab风格专属样式
  .j-cgform-tab-modal {
    .ant-modal-header {
      padding-top: 8px;
      padding-bottom: 8px;
      border-bottom: none !important;
    }

    .ant-modal .ant-modal-body > .scrollbar,
    .ant-tabs-nav .ant-tabs-tab {
      padding-top: 0;
    }

    .ant-tabs-top-bar {
      width: calc(100% - 55px);
      position: relative;
      left: -14px;
    }

    .ant-tabs .ant-tabs-top-content > .ant-tabs-tabpane {
      overflow: hidden auto;
    }
  }
</style>
