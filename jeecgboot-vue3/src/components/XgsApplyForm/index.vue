<template>
  <div class="apply-form-container">
    <!-- 照片预览模态框 -->
    <a-modal
      v-model:visible="previewVisible"
      :title="'照片预览'"
      :footer="null"
      width="auto"
    >
      <img :src="previewImageUrl" alt="照片预览" style="max-width: 100%; max-height: 80vh;" />
    </a-modal>

    <!-- 主内容区域：左侧表单 + 右侧锚点 -->
    <div class="content-wrapper">
      <!-- 左侧表单区域 -->
      <div class="form-content">
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          :disabled="formDisabled"
        >
    <table class="unified-form-table">
      <tbody>
        <!-- 基本信息区域 -->
              <tr id="basic-info">
                <td colspan="6" class="section-header">基本信息</td>
              </tr>

              <!-- 第一行：姓名 + 性别 + 照片（右侧，占6行） -->
              <tr>
                <td class="label-cell">姓名</td>
                <td class="field-cell">
                  <a-form-item name="name">
                    <a-input v-model:value="formData.name" placeholder="请输入姓名" />
                  </a-form-item>
                </td>
                <td class="label-cell">性别</td>
                <td class="field-cell">
                  <a-form-item name="sex">
                    <a-select v-model:value="formData.sex" placeholder="请选择性别">
                      <a-select-option value="1">男</a-select-option>
                      <a-select-option value="2">女</a-select-option>
                    </a-select>
                  </a-form-item>
                </td>
                <td rowspan="6" class="label-cell photo-label">本人照片</td>
                <td rowspan="6" class="field-cell photo-cell">
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
                        <div class="image-actions" v-if="!formDisabled">
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
                        <JImageUpload
                          v-if="!formDisabled"
                          :key="uploaderKey"
                          v-model:value="formData.photograph"
                          :fileMax="1"
                          :showUploadList="false"
                          :listType="'picture-card'"
                          @change="handleImageUploadSuccess"
                          @error="handleUploadError"
                          aria-label="上传本人照片"
                        />
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

          <!-- 第二行：出生年月 + 民族 -->
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
            <td class="label-cell">民族</td>
            <td class="field-cell">
              <a-form-item name="nation">
                <a-input v-model:value="formData.nation" placeholder="请输入民族" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第三行：籍贯 + 政治面貌 -->
          <tr>
            <td class="label-cell">籍贯</td>
            <td class="field-cell">
              <a-form-item name="nativePlace">
                <a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" />
              </a-form-item>
            </td>
            <td class="label-cell">政治面貌</td>
            <td class="field-cell">
              <a-form-item name="politicBackground">
                <a-input v-model:value="formData.politicBackground" placeholder="请输入政治面貌" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第四行：联系电话 + E-mail -->
          <tr>
            <td class="label-cell">联系电话</td>
            <td class="field-cell">
              <a-form-item name="mobile">
                <a-input v-model:value="formData.mobile" placeholder="请输入联系电话" />
              </a-form-item>
            </td>
            <td class="label-cell">E-mail</td>
            <td class="field-cell">
              <a-form-item name="email">
                <a-input v-model:value="formData.email" placeholder="请输入E-mail" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第五行：身份证号（占两列） -->
          <tr>
            <td class="label-cell">身份证号</td>
            <td colspan="3" class="field-cell">
              <a-form-item name="idNumber">
                <a-input v-model:value="formData.idNumber" placeholder="请输入身份证号" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第六行：户口所在地（占两列） -->
          <tr>
            <td class="label-cell">户口所在地</td>
            <td colspan="3" class="field-cell">
              <a-form-item name="hukou">
                <a-input v-model:value="formData.hukou" placeholder="请输入户口所在地" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第七行：是否应届毕业生 + 是否统招统分 -->
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
            <td class="label-cell">是否统招统分</td>
            <td class="field-cell">
              <a-form-item name="tztf">
                <a-select v-model:value="formData.tztf" placeholder="请选择">
                  <a-select-option value="1">是</a-select-option>
                  <a-select-option value="0">否</a-select-option>
                </a-select>
              </a-form-item>
            </td>
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

          <!-- 第八行：毕业院校 + 最高学历 + 最高学位 -->
          <tr>
            <td class="label-cell">毕业院校</td>
            <td class="field-cell">
              <a-form-item name="graduateCollege">
                <a-input v-model:value="formData.graduateCollege" placeholder="请输入毕业院校" />
              </a-form-item>
            </td>
            <td class="label-cell">最高学历</td>
            <td class="field-cell">
              <a-form-item name="education">
                <a-input v-model:value="formData.education" placeholder="请输入学历" />
              </a-form-item>
            </td>
            <td class="label-cell">最高学位</td>
            <td class="field-cell">
              <a-form-item name="degree">
                <a-input v-model:value="formData.degree" placeholder="请输入学位" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第九行：专业 + 参加工作时间 -->
          <tr>
            <td class="label-cell">专业</td>
            <td colspan="3" class="field-cell">
              <a-form-item name="profession">
                <a-input v-model:value="formData.profession" placeholder="请输入专业" />
              </a-form-item>
            </td>
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

          <!-- 第十行：目前工作单位（占全行） -->
          <tr>
            <td class="label-cell">目前工作单位</td>
            <td colspan="5" class="field-cell">
              <a-form-item name="workUnit">
                <a-input v-model:value="formData.workUnit" placeholder="请输入目前工作单位" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第十一行：人事档案关系所在单位（占全行） -->
          <tr>
            <td class="label-cell">人事档案关系所在单位</td>
            <td colspan="5" class="field-cell">
              <a-form-item name="personFilesUnit">
                <a-input v-model:value="formData.personFilesUnit" placeholder="请输入人事档案关系所在单位" />
              </a-form-item>
            </td>
          </tr>

          <!-- 第十二行：现行政职务 + 任职时间 -->
          <tr>
            <td class="label-cell">现行政职务</td>
            <td colspan="3" class="field-cell">
              <a-form-item name="adminPosition">
                <a-input v-model:value="formData.adminPosition" placeholder="请输入现行政职务" />
              </a-form-item>
            </td>
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

          <!-- 第十三行：现专业技术岗位或职员职级 + 聘任时间 -->
          <tr>
            <td class="label-cell">现专业技术岗位或职员职级</td>
            <td colspan="3" class="field-cell">
              <a-form-item name="professionLevel">
                <a-input v-model:value="formData.professionLevel" placeholder="请输入现专业技术岗位" />
              </a-form-item>
            </td>
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

          <!-- 第十四行：应聘部门 + 应聘岗位名称 -->
          <tr>
            <td class="label-cell">应聘部门</td>
            <td colspan="2" class="field-cell">
              <a-form-item name="applyDept">
                <a-input v-model:value="formData.applyDept" placeholder="请输入应聘部门" />
              </a-form-item>
            </td>
            <td class="label-cell">应聘岗位名称</td>
            <td colspan="2" class="field-cell">
              <a-form-item name="applyPosition">
                <a-input v-model:value="formData.applyPosition" placeholder="请输入应聘岗位名称" />
              </a-form-item>
            </td>
        </tr>

          <!-- 第十五行：与本所关系（占全行） -->
          <tr>
            <td class="label-cell">与本所关系</td>
            <td colspan="5" class="field-cell">
              <a-form-item name="relationshipType">
                <div class="relationship-container">
                  <span class="relationship-question">与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系? (请务必勾选)</span>
                  <div class="relationship-content">
                    <a-radio-group v-model:value="formData.relationshipType">
                      <a-radio value="no">无</a-radio>
                      <a-radio value="yes">有</a-radio>
                    </a-radio-group>
                    
                    <a-input
                      v-if="formData.relationshipType === 'yes'"
                      v-model:value="formData.relationshipName"
                      placeholder="请输入姓名"
                      style="margin-left: 16px; width: 300px;"
                    />
                  </div>
                </div>
              </a-form-item>
          </td>
        </tr>

        <!-- 工作经历区域 -->
        <tr id="work-experience">
          <td colspan="6" class="section-header">
            工作经历
            <a-button v-if="!formDisabled" type="link" size="small" @click="workExperienceRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <WorkExperienceTable 
              ref="workExperienceRef"
              v-model:dataSource="tableData.xgsResumeWorksList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 教育经历区域 -->
        <tr id="education">
          <td colspan="6" class="section-header">
            教育经历
            <a-button v-if="!formDisabled" type="link" size="small" @click="educationRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <EducationTable 
              ref="educationRef"
              v-model:dataSource="tableData.xgsResumeEdusList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 家庭状况区域 -->
        <tr id="family-info">
          <td colspan="6" class="section-header">
            家庭状况
            <a-button v-if="!formDisabled" type="link" size="small" @click="familyStatusRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <FamilyStatusTable 
              ref="familyStatusRef"
              v-model:dataSource="tableData.xgsResumeHomeList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 工作主要业绩区域 -->
        <tr id="work-achievement">
          <td colspan="6" class="section-header">
            工作主要业绩
            <a-button v-if="!formDisabled" type="link" size="small" @click="workAchievementRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <WorkAchievementTable 
              ref="workAchievementRef"
              v-model:dataSource="tableData.xgsResumeResearchResultList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 应聘岗位陈述区域 -->
        <tr id="position-statement">
          <td colspan="6" class="section-header">
            应聘岗位陈述
            <a-button v-if="!formDisabled" type="link" size="small" @click="positionStatementRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <PositionStatementTable 
              ref="positionStatementRef"
              v-model:dataSource="tableData.xgsResumePositionDescriptionList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 研究方向与专长区域 -->
        <tr id="research-direction">
          <td colspan="6" class="section-header">
            研究方向与专长
            <a-button v-if="!formDisabled" type="link" size="small" @click="researchDirectionRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <ResearchDirectionTable 
              ref="researchDirectionRef"
              v-model:dataSource="tableData.xgsResumeResearchDirectionList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 论文专著专利区域 -->
        <tr id="paper-patent">
          <td colspan="6" class="section-header">
            论文专著专利
            <a-button v-if="!formDisabled" type="link" size="small" @click="paperPatentRef?.handleAdd()">
              <plus-outlined /> 新增
            </a-button>
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <PaperPatentTable 
              ref="paperPatentRef"
              v-model:dataSource="tableData.xgsResumeResearchPaperList" 
              :disabled="formDisabled" 
            />
          </td>
        </tr>

        <!-- 上传其它材料区域 -->
        <tr id="other-files">
          <td colspan="6" class="section-header">
            上传其它材料
          </td>
        </tr>
        <tr>
          <td colspan="6" class="content-cell">
            <div class="upload-section">
              <a-form-item name="otherFiles">
                <JUpload
                  v-model:value="formData.otherFiles"
                  :maxCount="5"
                  :maxSize="10"
                  text="点击上传（最多5个文件，单个文件不超过10MB）"
                  :returnUrl="false"
                  :disabled="formDisabled"
                  :multiple="true"
                />
              </a-form-item>
              <div class="upload-tips">
                <p>支持上传PDF、DOC、DOCX、XLS、XLSX、JPG、PNG等格式文件</p>
                <p>单个文件大小不超过10MB，最多上传5个文件</p>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="form-actions mt-4" v-if="!formDisabled">
      <a-button type="primary" @click="handleSubmit">提交</a-button>
      <a-button class="ml-2" @click="handleReset">重置</a-button>
    </div>
    </a-form>
      </div>

      <!-- 右侧锚点导航 -->
      <div class="anchor-wrapper">
        <AAffix :target="getAnchorContainer" :offset-top="20">
          <div class="anchor-container">
            <AAnchor
              :target-offset="80"
              :get-container="getAnchorContainer"
              @change="handleAnchorChange"
              @click="handleAnchorClick"
            >
              <AAnchorLink href="#basic-info" title="基本信息" />
              <AAnchorLink href="#work-experience" title="工作经历" />
              <AAnchorLink href="#education" title="教育经历" />
              <AAnchorLink href="#family-info" title="家庭状况" />
              <AAnchorLink href="#work-achievement" title="工作主要业绩" />
              <AAnchorLink href="#position-statement" title="应聘岗位陈述" />
              <AAnchorLink href="#research-direction" title="研究方向与专长" />
              <AAnchorLink href="#paper-patent" title="论文专著专利" />
              <AAnchorLink href="#other-files" title="上传其它材料" />
            </AAnchor>
          </div>
        </AAffix>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, watch, defineProps, defineExpose } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { message, Modal, Anchor, AnchorLink, Affix } from 'ant-design-vue';
import { EyeOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue';
import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
import { saveOrUpdate } from '/@/views/xgsResumeBase/xgsResumePT/XgsResumeBase.api';
import JImageUpload from '/@/components/Form/src/jeecg/components/JImageUpload.vue';
import { JUpload } from '/@/components/Form/src/jeecg/components/JUpload';
import { 
  xgsResumeWorksList, 
  xgsResumeEdusList, 
  xgsResumeHomeList, 
  xgsResumeResearchResultList, 
  xgsResumePositionDescriptionList,
  xgsResumeResearchDirectionList,
  xgsResumeResearchPaperList 
} from '/@/views/xgsResumeBase/xgsResumePT/XgsResumeBase.api';

// 导入子组件
import WorkExperienceTable from './components/WorkExperienceTable.vue';
import EducationTable from './components/EducationTable.vue';
import FamilyStatusTable from './components/FamilyStatusTable.vue';
import WorkAchievementTable from './components/WorkAchievementTable.vue';
import PositionStatementTable from './components/PositionStatementTable.vue';
import ResearchDirectionTable from './components/ResearchDirectionTable.vue';
import PaperPatentTable from './components/PaperPatentTable.vue';

// 使用 Ant Design Vue 组件别名（兼容性）
const AAnchor = Anchor;
const AAnchorLink = AnchorLink;
const AAffix = Affix;

// Props 接收参数
const props = defineProps({
  // 表单数据
  formData: { type: Object, default: () => ({}) },
  // 表单是否禁用（查看模式）
  formDisabled: { type: Boolean, default: false },
  // 表单BPM模式
  formBpm: { type: Boolean, default: false },
  // 表单数据ID
  dataId: { type: String, default: '' },
});

// 组件引用
const formRef = ref<any>(null);
const workExperienceRef = ref<any>(null);
const educationRef = ref<any>(null);
const familyStatusRef = ref<any>(null);
const workAchievementRef = ref<any>(null);
const positionStatementRef = ref<any>(null);
const researchDirectionRef = ref<any>(null);
const paperPatentRef = ref<any>(null);

// 日期格式
const dateFormat = 'YYYY-MM-DD';

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

// 表单数据
const formData = reactive<any>({
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
  tztf: '',
  graduateCollege: '',
  education: '',
  degree: '',
  profession: '',
  graduateDate: '',
  workDate: '',
  workUnit: '',
  personFilesUnit: '',
  adminPosition: '',
  adminPositionDate: '',
  professionLevel: '',
  professionLevelDate: '',
  relationshipType: 'no',
  relationshipName: '',
  email: '',
  mobile: '',
  applyDept: '',
  applyPosition: '',
  otherFiles: '',
});

// 子表数据
const tableData = reactive({
  xgsResumeWorksList: [],
  xgsResumeEdusList: [],
  xgsResumeHomeList: [],
  xgsResumeResearchResultList: [],
  xgsResumePositionDescriptionList: [],
  xgsResumeResearchDirectionList: [],
  xgsResumeResearchPaperList: [],
});

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
      formData.photograph = '';
      uploadState.error = null;
      message.success('照片已删除');
      // 删除后强制重建上传控件
      uploaderKey.value = 'uploader-' + Date.now();
    }
  });
};

// 处理图片加载错误
const handleImageError = (event: Event) => {
  console.error('图片加载失败:', (event.target as HTMLImageElement).src);
  
  uploadState.error = '图片加载失败，请检查图片链接是否有效';
  message.error('图片加载失败，请重新上传');

  // 清空当前图片地址，显示上传控件
  formData.photograph = '';
};

// 清除错误状态
const clearError = () => {
  uploadState.error = null;
};

// 重新上传
const reupload = () => {
  uploadState.error = null;
  uploadState.uploading = false;
  formData.photograph = '';
  message.info('请重新选择并上传照片');
  uploaderKey.value = 'uploader-' + Date.now();
};

// 图片上传成功的处理
const handleImageUploadSuccess = (fileUrl: string) => {
  uploadState.uploading = false;
  uploadState.error = null;
  
  if (fileUrl) {
    formData.photograph = fileUrl;
    message.success('照片上传成功!');
  }
};

// 图片上传错误的处理
const handleUploadError = (error: any) => {
  uploadState.uploading = false;
  uploadState.error = error?.message || '上传失败';
  formData.photograph = '';
  
  console.error('照片上传失败:', error);
  message.error('照片上传失败: ' + uploadState.error);
};

// 表单提交处理
const handleSubmit = async () => {
  try {
    // 验证基本信息表单
    if (formRef.value) {
      await formRef.value.validate();
    }
    
    // 整合所有数据
    const submitData = {
      ...formData,
      xgsResumeWorksList: workExperienceRef.value?.getData() || [],
      xgsResumeEdusList: educationRef.value?.getData() || [],
      xgsResumeHomeList: familyStatusRef.value?.getData() || [],
      xgsResumeResearchResultList: workAchievementRef.value?.getData() || [],
      xgsResumePositionDescriptionList: positionStatementRef.value?.getData() || [],
      xgsResumeResearchDirectionList: researchDirectionRef.value?.getData() || [],
      xgsResumeResearchPaperList: paperPatentRef.value?.getData() || []
    };
    
    // 保存数据
    const isUpdate = !!formData.id;
    const result = await saveOrUpdate(submitData, isUpdate);
    
    message.success('保存成功');
    return result;
  } catch (error) {
    console.error('表单提交失败:', error);
    message.error('保存失败，请检查表单数据');
    return Promise.reject(error);
  }
};

// 重置表单
const handleReset = async () => {
  // 重置基本表单
  formRef.value?.resetFields();
  
  // 清空子表数据
  tableData.xgsResumeWorksList = [];
  tableData.xgsResumeEdusList = [];
  tableData.xgsResumeHomeList = [];
  tableData.xgsResumeResearchResultList = [];
  tableData.xgsResumePositionDescriptionList = [];
  tableData.xgsResumeResearchDirectionList = [];
  tableData.xgsResumeResearchPaperList = [];
  
  // 重置照片上传状态
  uploadState.uploading = false;
  uploadState.error = null;
  uploaderKey.value = 'uploader-initial';
  
  message.success('表单已重置');
};

// 加载表单数据
const loadFormData = async (id?: string) => {
  const queryId = id || props.dataId;
  if (!queryId) return;
  
  try {
    // 查询主表数据
    const data = await defHttp.get({ 
      url: '/xgsResume/xgsResumeBase/queryById', 
      params: { id: queryId } 
    });
    
    // 设置主表数据
    Object.assign(formData, data);
    
    // 查询基础子表数据（这3个接口后端已存在）
    try {
      tableData.xgsResumeWorksList = await defHttp.get({ 
        url: xgsResumeWorksList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeWorksList = [];
    }
    
    try {
      tableData.xgsResumeEdusList = await defHttp.get({ 
        url: xgsResumeEdusList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeEdusList = [];
    }
    
    try {
      tableData.xgsResumeHomeList = await defHttp.get({ 
        url: xgsResumeHomeList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeHomeList = [];
    }
    
    // 查询扩展子表数据（这4个接口后端可能不存在，需要容错处理）
    try {
      tableData.xgsResumeResearchResultList = await defHttp.get({ 
        url: xgsResumeResearchResultList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeResearchResultList = [];
    }
    
    try {
      tableData.xgsResumePositionDescriptionList = await defHttp.get({ 
        url: xgsResumePositionDescriptionList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumePositionDescriptionList = [];
    }
    
    try {
      tableData.xgsResumeResearchDirectionList = await defHttp.get({ 
        url: xgsResumeResearchDirectionList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeResearchDirectionList = [];
    }
    
    try {
      tableData.xgsResumeResearchPaperList = await defHttp.get({ 
        url: xgsResumeResearchPaperList, 
        params: { id: data.id } 
      }) || [];
    } catch (e) {
      tableData.xgsResumeResearchPaperList = [];
    }
    
    // 数据加载完成后，强制触发 Affix 重新计算宽度
    // 使用 nextTick 确保 DOM 已更新，然后触发 resize 事件
    await nextTick();
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 100);
    
  } catch (error) {
    console.error('加载表单数据失败:', error);
    message.error('加载表单数据失败');
  }
};

// 初始化
onMounted(async () => {
  // 如果有数据ID则加载数据
  if (props.dataId) {
    await loadFormData();
  }
  
  // 如果有传入表单数据，则合并到本地formData
  if (props.formData) {
    Object.assign(formData, props.formData);
  }
  
  // 在组件挂载后，触发 resize 事件以确保 Affix 正确计算
  // 这对于 Tab 中的组件特别重要
  await nextTick();
  
  setTimeout(() => {
    window.dispatchEvent(new Event('resize'));
  }, 200);
  
  // 再次延迟触发，确保在 Tab 完全激活后也能正确计算
  setTimeout(() => {
    window.dispatchEvent(new Event('resize'));
  }, 500);
});

// 监听dataId变化，自动加载数据
watch(() => props.dataId, (newVal, oldVal) => {
  if (newVal && newVal !== oldVal) {
    loadFormData(newVal);
  }
}, { immediate: false });

// 设置数据方法（用于外部调用）
const setDataByPDF = (data: any) => {
  // 同步数据到formData
  Object.assign(formData, data.value);
  
  // 设置子表数据
  tableData.xgsResumeWorksList = data.value.xgsResumeWorks || [];
  tableData.xgsResumeEdusList = data.value.xgsResumeEdus || [];
  tableData.xgsResumeHomeList = data.value.xgsResumeHome || [];
  tableData.xgsResumeResearchResultList = data.value.xgsResumeResearchResult || [];
  tableData.xgsResumePositionDescriptionList = data.value.xgsResumePositionDescription || [];
  tableData.xgsResumeResearchDirectionList = data.value.xgsResumeResearchDirection || [];
  tableData.xgsResumeResearchPaperList = data.value.xgsResumeResearchPaper || [];
};

// 获取锚点容器（用于 anchor）
const getAnchorContainer = () => {
  // 尝试从当前组件向上查找最近的modal滚动容器
  const currentElement = document.querySelector('.apply-form-container');
  
  if (!currentElement) {
    return window as any;
  }
  
  // 向上查找滚动容器
  let parent = currentElement.parentElement;
  while (parent) {
    // 优先查找自定义 Tab 容器（审核弹出框中使用 v-if 的 scrollable-pane）
    if (parent.classList.contains('scrollable-pane')) {
      const computedStyle = window.getComputedStyle(parent);
      if (computedStyle.overflowY === 'auto' || computedStyle.overflowY === 'scroll') {
        return parent;
      }
    }
    
    // 其次查找原有的 ant-tabs-tabpane（兼容旧代码）
    if (parent.classList.contains('ant-tabs-tabpane') && parent.classList.contains('ant-tabs-tabpane-active')) {
      const computedStyle = window.getComputedStyle(parent);
      if (computedStyle.overflowY === 'auto' || computedStyle.overflowY === 'scroll') {
        return parent;
      }
    }
    
    // 再查找 ant-modal-body（详情弹出框使用）
    if (parent.classList.contains('ant-modal-body')) {
      return parent;
    }
    
    // 如果到达 modal-wrap，停止查找
    if (parent.classList.contains('ant-modal-wrap')) {
      break;
    }
    
    parent = parent.parentElement;
  }
  
  // 备用方案：查找所有可见的有滚动的容器
  // 优先查找自定义 scrollable-pane
  const scrollablePanes = document.querySelectorAll('.scrollable-pane');
  for (let i = scrollablePanes.length - 1; i >= 0; i--) {
    const pane = scrollablePanes[i] as HTMLElement;
    const isVisible = pane.offsetHeight > 0 && pane.offsetParent !== null;
    const containsCurrent = pane.contains(currentElement);
    const computedStyle = window.getComputedStyle(pane);
    
    if (isVisible && containsCurrent && (computedStyle.overflowY === 'auto' || computedStyle.overflowY === 'scroll')) {
      return pane;
    }
  }
  
  // 再尝试找 Tab pane
  const tabPanes = document.querySelectorAll('.ant-tabs-tabpane.ant-tabs-tabpane-active');
  for (let i = tabPanes.length - 1; i >= 0; i--) {
    const pane = tabPanes[i] as HTMLElement;
    const isVisible = pane.offsetHeight > 0 && pane.offsetParent !== null;
    const containsCurrent = pane.contains(currentElement);
    const computedStyle = window.getComputedStyle(pane);
    
    if (isVisible && containsCurrent && (computedStyle.overflowY === 'auto' || computedStyle.overflowY === 'scroll')) {
      return pane;
    }
  }
  
  // 最后尝试找 modal-body
  const modalBodies = document.querySelectorAll('.ant-modal-body');
  for (let i = modalBodies.length - 1; i >= 0; i--) {
    const body = modalBodies[i] as HTMLElement;
    const isVisible = body.offsetHeight > 0 && body.offsetParent !== null;
    const containsCurrent = body.contains(currentElement);
    
    if (isVisible && containsCurrent) {
      return body;
    }
  }
  
  // 最后返回window
  return window as any;
};

// Anchor 激活项变化监听
const handleAnchorChange = (_currentActiveLink: string) => {
  // 可以在这里添加激活项变化的业务逻辑
};

// Anchor 点击监听 - 阻止默认行为防止窗口关闭
const handleAnchorClick = (e: Event) => {
  // 阻止默认行为，防止点击锚点时窗口关闭
  e.preventDefault();
};

// 暴露方法给父组件
defineExpose({
  handleSubmit,
  handleReset,
  loadFormData,
  setDataByPDF,
});
</script>

<style lang="less" scoped>
.apply-form-container {
  padding: 0 20px;
  width: 100%;
  max-width: 100%;
  position: relative;
  
  // 主内容区域布局：左侧表单 + 右侧锚点
  .content-wrapper {
    display: flex;
    position: relative;
    gap: 24px;
    
    // 左侧表单内容
    .form-content {
      flex: 1;
      min-width: 0; // 防止 flex 子元素溢出
    }
    
    // 右侧锚点导航
    .anchor-wrapper {
      width: 200px;
      min-width: 200px;
      max-width: 200px;
      flex-shrink: 0;
      margin-top: 0;
      
      .anchor-container {
        width: 100%; // 使用100%宽度，与父元素一致
        background-color: #ffffff;
        border: 1px solid #e8e8e8;
        border-radius: 4px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        overflow: hidden;
      }
      
      :deep(.ant-anchor-wrapper) {
        background: #ffffff;
        padding: 0;
      }
      
      :deep(.ant-anchor) {
        padding: 12px 0;
        background: #ffffff;
      }
      
      :deep(.ant-anchor-link) {
        padding: 8px 0 8px 20px;
        line-height: 1.5;
        
        .ant-anchor-link-title {
          font-size: 14px;
          color: #595959;
          transition: all 0.3s;
          display: block;
          
          &:hover {
            color: #1890ff;
          }
        }
      }
      
      :deep(.ant-anchor-link-active) {
        > .ant-anchor-link-title {
          color: #1890ff !important;
          font-weight: 500;
        }
      }
      
      :deep(.ant-anchor-ink) {
        left: 0;
        
        &::before {
          width: 2px;
          background-color: #e8e8e8;
        }
      }
      
      :deep(.ant-anchor-ink-ball) {
        width: 8px;
        height: 8px;
        border: 2px solid #1890ff;
        background-color: #fff;
        border-radius: 50%;
        left: -3px;
      }
    }
  }
  
  .unified-form-table {
    width: 100%;
    border-collapse: collapse;
    border: 1px solid #d9d9d9;
    table-layout: fixed; // 固定表格布局，使列宽更容易控制
    
    td {
      border: 1px solid #d9d9d9;
      vertical-align: middle;
      word-wrap: break-word; // 长文本自动换行
      word-break: break-all; // 强制换行
    }
    
    .section-header {
      background-color: #e6f7ff;
      font-weight: 600;
      font-size: 16px;
      padding: 10px 16px;
      color: #0050b3;
      
      :deep(.ant-btn-link) {
        float: right;
        margin-top: -2px;
        color: #1890ff;
        padding: 0 8px;
      }
      text-align: center;
      border: 1px solid #91d5ff;
      scroll-margin-top: 80px; // 锚点跳转时的偏移量
    }
    
    .label-cell {
      width: 15%; // 每个label列占15%
      min-width: 100px;
      background-color: #f5f5f5;
      font-weight: 500;
      text-align: center;
      vertical-align: middle;
      padding: 8px 12px;
      white-space: normal; // 允许标签文字换行
      line-height: 1.5;
      
      &.photo-label {
        vertical-align: middle;
      }
    }
    
    .field-cell {
      width: 18.33%; // 每个field列占约18.33%（100% - 15%*2）/3
      padding: 8px 12px;
      background-color: #fff;
      
      .ant-form-item {
        margin-bottom: 0;
      }
      
      &.photo-cell {
        width: 25%; // 照片列稍宽一些
        padding: 12px;
      }
    }
    
    .content-cell {
      padding: 0;
      background-color: #fff;
      
      // 移除子表格组件的边框，避免双重边框
      :deep(.ant-table-wrapper) {
        border: none;
        
        .ant-table {
          border: none;
        }
      }
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
        line-height: 1.5;
        color: #595959;
      }
      
      .relationship-content {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 8px;
      }
    }
  }
  
  // 照片上传相关样式
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
    
    :deep(.ant-upload) {
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
      border-radius: 2px;
    }
    
    .image-actions {
      position: absolute;
      top: 8px;
      right: 8px;
      display: flex;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.3s;
      
      .preview-btn,
      .remove-btn {
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }
    }
    
    &:hover .image-actions {
      opacity: 1;
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 24px 16px;
    background-color: #fafafa;
    border-top: 1px solid #d9d9d9;
    margin-top: 16px;
    
    .ml-2 {
      margin-left: 16px;
    }
    
    :deep(.ant-btn) {
      min-width: 120px;
      height: 40px;
      font-size: 16px;
    }
  }
  
  .mt-4 {
    margin-top: 16px;
  }
  
  // 上传其它材料区域样式
  .upload-section {
    padding: 20px;
    
    .upload-tips {
      margin-top: 12px;
      padding: 12px;
      background-color: #f6f8fa;
      border-radius: 4px;
      color: #666;
      font-size: 13px;
      
      p {
        margin: 4px 0;
        line-height: 1.6;
        
        &:first-child {
          margin-top: 0;
        }
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}

// 响应式布局：在小屏幕下隐藏锚点导航
@media screen and (max-width: 1200px) {
  .apply-form-container {
    .content-wrapper {
      .anchor-wrapper {
        display: none;
      }
    }
  }
}

// 只读模式样式优化：提高文字可读性
.apply-form-container {
  // 只读表单输入框文字颜色
  :deep(.ant-input[disabled]),
  :deep(.ant-input-disabled),
  :deep(.ant-select-disabled .ant-select-selection-item),
  :deep(.ant-picker-input > input[disabled]),
  :deep(.ant-input-number-disabled .ant-input-number-input),
  :deep(.ant-radio-disabled + span),
  :deep(.ant-checkbox-disabled + span) {
    color: rgba(0, 0, 0, 0.85) !important;
    -webkit-text-fill-color: rgba(0, 0, 0, 0.85) !important;
    cursor: text !important;
  }
  
  // 只读表单输入框背景色调整为更浅的颜色
  :deep(.ant-input[disabled]),
  :deep(.ant-input-disabled),
  :deep(.ant-select-disabled .ant-select-selector),
  :deep(.ant-picker-disabled),
  :deep(.ant-input-number-disabled) {
    background-color: #fafafa !important;
    border-color: #e8e8e8 !important;
  }
  
  // 只读表格内容样式
  :deep(.ant-table-tbody > tr > td) {
    color: rgba(0, 0, 0, 0.85);
  }
}
</style>
