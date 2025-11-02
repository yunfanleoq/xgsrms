<template>
  <div class="apply-form-container">
    <!-- 照片预览模态框 -->
    <a-modal
      v-model:open="previewVisible"
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

          <!-- 第七行：是否应届毕业生 + 是否统招统分 - 仅普通岗位 -->
          <tr v-if="isPT">
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
          
          <!-- 婚姻状况 - 博士后和副高 -->
          <tr v-if="isBSH || isFG">
            <td class="label-cell">婚姻状况</td>
            <td class="field-cell">
              <a-form-item name="maritalStatus">
                <a-input v-model:value="formData.maritalStatus" placeholder="请输入婚姻状况" />
              </a-form-item>
            </td>
            <td class="label-cell" v-if="isFG">国籍</td>
            <td class="field-cell" v-if="isFG">
              <a-form-item name="nationality">
                <a-input v-model:value="formData.nationality" placeholder="请输入国籍" />
              </a-form-item>
            </td>
            <td colspan="2" v-else></td>
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
        
        <!-- 人才派遣岗位推荐人信息 -->
        <tr v-if="isTJ">
          <td colspan="6" class="section-header">推荐人信息</td>
        </tr>
        <tr v-if="isTJ">
          <td class="label-cell">被推荐人</td>
          <td class="field-cell">
            <a-form-item name="propositus">
              <a-input v-model:value="formData.propositus" placeholder="请输入被推荐人" />
            </a-form-item>
          </td>
          <td class="label-cell">推荐人</td>
          <td class="field-cell">
            <a-form-item name="referrer">
              <a-input v-model:value="formData.referrer" placeholder="请输入推荐人" />
            </a-form-item>
          </td>
          <td class="label-cell">推荐人技术职务</td>
          <td class="field-cell">
            <a-form-item name="referrerPosition">
              <a-input v-model:value="formData.referrerPosition" placeholder="请输入推荐人技术职务" />
            </a-form-item>
          </td>
        </tr>
        <tr v-if="isTJ">
          <td class="label-cell">推荐人工作单位</td>
          <td colspan="3" class="field-cell">
            <a-form-item name="referrerUnit">
              <a-input v-model:value="formData.referrerUnit" placeholder="请输入推荐人工作单位" />
            </a-form-item>
          </td>
          <td class="label-cell">拟申报岗位等级</td>
          <td class="field-cell">
            <a-form-item name="positionClass">
              <a-input v-model:value="formData.positionClass" placeholder="请输入拟申报岗位等级" />
            </a-form-item>
          </td>
        </tr>
        <tr v-if="isTJ">
          <td class="label-cell">推荐人联系方式</td>
          <td class="field-cell">
            <a-form-item name="referrerMobile">
              <a-input v-model:value="formData.referrerMobile" placeholder="请输入推荐人联系方式" />
            </a-form-item>
          </td>
          <td class="label-cell">推荐人电子邮箱</td>
          <td class="field-cell">
            <a-form-item name="referrerEmail">
              <a-input v-model:value="formData.referrerEmail" placeholder="请输入推荐人电子邮箱" />
            </a-form-item>
          </td>
          <td class="label-cell">推荐人签名</td>
          <td class="field-cell">
            <a-form-item name="referrerSignature">
              <JImageUpload
                v-model:value="formData.referrerSignature"
                :fileMax="1"
                aria-label="上传推荐人签名"
              />
            </a-form-item>
          </td>
        </tr>
        <tr v-if="isTJ">
          <td class="label-cell">推荐原因</td>
          <td colspan="5" class="field-cell">
            <a-form-item name="cause">
              <a-textarea v-model:value="formData.cause" placeholder="请输入推荐原因" :rows="4" />
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
        <AAffix :target="getAffixContainer" :offset-top="affixOffsetTop">
          <div class="anchor-container">
            <AAnchor
              :get-container="getAnchorContainer"
              :items="anchorItems"
              @click="handleAnchorClick"
            />
          </div>
        </AAffix>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick, computed, defineProps, defineExpose } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { message, Modal, Anchor, Affix } from 'ant-design-vue';
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
const AAffix = Affix;

// 锚点导航配置
const anchorItems = [
  { key: 'basic-info', href: '#basic-info', title: '基本信息' },
  { key: 'work-experience', href: '#work-experience', title: '工作经历' },
  { key: 'education', href: '#education', title: '教育经历' },
  { key: 'family-info', href: '#family-info', title: '家庭状况' },
  { key: 'work-achievement', href: '#work-achievement', title: '工作主要业绩' },
  { key: 'position-statement', href: '#position-statement', title: '应聘岗位陈述' },
  { key: 'research-direction', href: '#research-direction', title: '研究方向与专长' },
  { key: 'paper-patent', href: '#paper-patent', title: '论文专著专利' },
  { key: 'other-files', href: '#other-files', title: '上传其它材料' },
];

// Props 接收参数
const props = defineProps({
  // 表单是否禁用（查看模式）
  formDisabled: { type: Boolean, default: false },
  // 表单BPM模式
  formBpm: { type: Boolean, default: false },
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

// Affix 的动态偏移量
const affixOffsetTop = ref<number>(20);

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

// 岗位申请信息
const positionApply = ref<any>({
  positionType: '普通岗位'
});

// 岗位类型计算属性
const positionType = computed(() => formData?.positionType || positionApply.value.positionType || '普通岗位');
const isPT = computed(() => positionType.value === '普通岗位');
const isBSH = computed(() => positionType.value === '博士后岗位');
const isFG = computed(() => positionType.value === '副高级以上岗位');
const isTJ = computed(() => positionType.value === '人才派遣岗位');

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
  positionType: '普通岗位',
  
  // 博士后、副高特有字段
  maritalStatus: '', // 婚姻状况
  
  // 副高特有字段
  nationality: '', // 国籍
  
  // 人才派遣特有字段
  propositus: '', // 被推荐人
  referrer: '', // 推荐人
  referrerPosition: '', // 推荐人技术职务
  referrerUnit: '', // 推荐人工作单位
  referrerMobile: '', // 推荐人联系方式
  referrerEmail: '', // 推荐人电子邮箱
  referrerSignature: '', // 推荐人签名
  positionClass: '', // 拟申报岗位等级
  cause: '', // 推荐原因
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
const loadFormData = async (positionApplyInfo) => {
  positionApply.value = positionApplyInfo;
  // 设置岗位类型
  if (positionApplyInfo.positionType) {
    formData.positionType = positionApplyInfo.positionType;
  }
  const queryId = positionApplyInfo.resumeId;
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
    
    // 数据加载完成后，重新计算 Affix offset
    await nextTick();  
    affixOffsetTop.value = 21;
  } catch (error) {
    console.error('加载表单数据失败:', error);
    message.error('加载表单数据失败');
  }
};


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

// 获取 Affix 的目标容器（固定定位的参考容器）
const getAffixContainer = () => {
  // 优先查找审核弹窗的滚动容器（BasicModal + tab）
  const scrollablePane = document.querySelector('.scrollable-pane');
  if (scrollablePane) {
    return scrollablePane as HTMLElement;
  }
  
  // 其次查找 BasicModal 的 body
  const basicModalBody = document.querySelector('.ant-modal-body');
  if (basicModalBody) {
    return basicModalBody as HTMLElement;
  }
  
  // 最后查找 j-modal 的 body
  const jModalBody = document.querySelector('.j-modal-body');
  if (jModalBody) {
    return jModalBody as HTMLElement;
  }
  
  return window;
};

// 获取锚点容器（用于 anchor 滚动）
const getAnchorContainer = () => {
  // 优先查找审核弹窗的滚动容器（BasicModal + tab）
  const scrollablePane = document.querySelector('.scrollable-pane');
  if (scrollablePane) {
    return scrollablePane as HTMLElement;
  }
  
  // 其次查找 BasicModal 的 body
  const basicModalBody = document.querySelector('.ant-modal-body');
  if (basicModalBody) {
    return basicModalBody as HTMLElement;
  }
  
  // 最后查找 j-modal 的 body
  const jModalBody = document.querySelector('.j-modal-body');
  if (jModalBody) {
    return jModalBody as HTMLElement;
  }
  
  return window;
};

// Anchor 点击监听 - 阻止默认行为并手动滚动
const handleAnchorClick = (e: Event, link: any) => {
  // 阻止默认行为，防止点击锚点时窗口关闭
  e.preventDefault();
  
  // 手动处理滚动
  if (link && link.href) {
    const targetId = link.href.replace('#', '');
    const targetElement = document.getElementById(targetId);
    const container = getAnchorContainer();
    
    if (targetElement && container) {
      if (container === window) {
        // 如果容器是 window，使用 scrollIntoView
        targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      } else {
        // 如果容器是具体的 DOM 元素，计算滚动位置
        const containerElement = container as HTMLElement;
        const containerRect = containerElement.getBoundingClientRect();
        const targetRect = targetElement.getBoundingClientRect();
        const scrollTop = containerElement.scrollTop;
        
        // 判断是否在审核弹窗中（scrollable-pane）
        const isInScrollablePane = containerElement.classList?.contains('scrollable-pane') || false;
        
        // 根据容器类型调整偏移量
        const offsetAdjust = isInScrollablePane ? 20 : 80; // 审核弹窗偏移量小一些
        const offset = targetRect.top - containerRect.top + scrollTop - offsetAdjust;
        
        (container as HTMLElement).scrollTo({
          top: offset,
          behavior: 'smooth'
        });
      }
    }
  }
};

// 暴露方法给父组件
defineExpose({
  handleSubmit,
  handleReset,
  loadFormData,
  setDataByPDF
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
        width: 100%;
      }
      
      :deep(.ant-anchor) {
        padding: 12px 0;
        background: #ffffff;
        width: 100%;
      }
      
      // 修复 Affix 固定后的宽度
      :deep(.ant-affix) {
        width: 200px !important;
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
