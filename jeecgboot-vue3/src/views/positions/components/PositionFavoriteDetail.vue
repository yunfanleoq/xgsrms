<template>
  <BasicModal v-bind="$attrs" @register="registerModalWin" destroyOnClose :title="title" :width="1000" :height="720">
    <!-- 职位信息卡片 -->
    <div v-if="job" class="job-info-card">
      <!-- 基本信息区域 -->
      <div class="info-section">
        <h2 class="section-title">
          <IdcardOutlined class="title-icon" />
          基本信息
        </h2>
        <a-row :gutter="[24, 16]">
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>职位名称</label>
              <div class="info-value highlight">{{ job.positionName }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>所属部门</label>
              <div class="info-value">{{ job.dept_dictText }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>招聘状态</label>
              <div class="info-value">
                <a-tag color="green">{{ job.status_dictText }}</a-tag>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <a-divider />

      <!-- 任职要求区域 -->
      <div class="info-section">
        <h2 class="section-title">
          <SolutionOutlined class="title-icon" />
          任职要求
        </h2>
        <a-row :gutter="[24, 16]">
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>学历要求</label>
              <div class="info-value">{{ job.xlxw }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>工作年限</label>
              <div class="info-value">{{ job.workYears }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>研究方向</label>
              <div class="info-value">{{ job.researchDirection }}</div>
            </div>
          </a-col>
          <a-col :xs="24">
            <div class="info-item">
              <label>专业要求</label>
              <div class="info-content">{{ job.professional }}</div>
            </div>
          </a-col>
        </a-row>
      </div>

      <a-divider />

      <!-- 岗位职责区域 -->
      <div class="info-section">
        <h2 class="section-title">
          <ProfileOutlined class="title-icon" />
          岗位职责
        </h2>
        <div class="info-content">{{ job.duty }}</div>
      </div>

      <a-divider />

      <!-- 联系方式区域 -->
      <div class="info-section">
        <h2 class="section-title">
          <PhoneOutlined class="title-icon" />
          联系方式
        </h2>
        <a-row :gutter="[24, 16]">
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>联系人</label>
              <div class="info-value">{{ job.ktz_dictText }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>联系电话</label>
              <div class="info-value">{{ job.telphone || '无' }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8">
            <div class="info-item">
              <label>电子邮箱</label>
              <div class="info-value">{{ job.email }}</div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 备注区域（如果有） -->
      <template v-if="job.memo">
        <a-divider />
        <div class="info-section">
          <h2 class="section-title">
            <InfoCircleOutlined class="title-icon" />
            备注
          </h2>
          <div class="info-content">{{ job.memo }}</div>
        </div>
      </template>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <a-spin size="large" />
      <p class="loading-text">职位信息加载中...</p>
    </div>
  </BasicModal>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { getJobById } from '@/api/xgsrms/home';
  import {BasicModal, useModalInner} from '@/components/Modal';
  // 引入图标
  import {
    IdcardOutlined,
    SolutionOutlined,
    ProfileOutlined,
    PhoneOutlined,
    InfoCircleOutlined,
  } from '@ant-design/icons-vue';

  interface Job {
    id: string;
    positionName: string;
    dept_dictText: string;
    researchDirection: string;
    professional: string;
    workYears: string;
    xlxw: string;
    status_dictText: string;
    duty: string;
    ktz_dictText: string;
    telphone?: string;
    email: string;
    memo?: string;
    category: string;
    personCount?: number;
  }

  let jobId = '';
  const job = ref<Job | null>(null);
  let favoriteRecord: any = null; // 保存收藏记录，用于备用数据

  const fetchCurrApplyPosition = async () => {
    try {
      if (!jobId) {
        console.error('岗位ID为空');
        return;
      }
      const response = await getJobById({ id: jobId });
      console.log('岗位详情API响应:', response);
      if (response && response.result) {
        const jobData = response.result;
        console.log('岗位数据:', jobData);
        
        // 如果 dept_dictText 为空或是ID，使用收藏记录中的部门名称
        if (favoriteRecord && favoriteRecord.positionDept) {
          if (!jobData.dept_dictText || /^\d+$/.test(jobData.dept_dictText)) {
            jobData.dept_dictText = favoriteRecord.positionDept;
          }
        }
        
        // 处理招聘状态字段 - 可能是 status_dictText 或 status
        if (!jobData.status_dictText) {
          jobData.status_dictText = jobData.status || '招聘中';
        }
        
        console.log('处理后的招聘状态:', jobData.status_dictText);
        console.log('处理后的部门:', jobData.dept_dictText);
        
        job.value = jobData;
      }
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
  };

  //表单赋值
  const [registerModalWin, {setModalProps}] = useModalInner(async (data) => {
    setModalProps({confirmLoading: false, showCancelBtn: false, showOkBtn: false});
    // 保存收藏记录
    favoriteRecord = data.record;
    // 从收藏记录中获取岗位ID
    jobId = data.record.positionId || data.record.id || data.record;
    await fetchCurrApplyPosition();
  });
  //设置标题
  const title = ref('岗位详情');
</script>

<style scoped lang="less">
  .job-info-card {
    padding: 16px;
    background: #fff;
  }

  .info-section {
    margin-bottom: 8px;

    .section-title {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: #1890ff;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 2px solid #e8f4ff;

      .title-icon {
        margin-right: 8px;
        font-size: 20px;
      }
    }

    .info-item {
      margin-bottom: 8px;

      label {
        display: block;
        font-size: 13px;
        color: #666;
        margin-bottom: 6px;
        font-weight: 500;
      }

      .info-value {
        font-size: 15px;
        color: #262626;
        line-height: 1.6;
        word-break: break-word;

        &.highlight {
          font-size: 18px;
          font-weight: 600;
          color: #1890ff;
        }
      }
    }

    .info-content {
      font-size: 14px;
      color: #595959;
      line-height: 1.8;
      white-space: pre-wrap;
      word-wrap: break-word;
      background-color: #fafafa;
      padding: 16px;
      border-radius: 6px;
      border: 1px solid #e8e8e8;
    }
  }

  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 400px;

    .loading-text {
      margin-top: 16px;
      font-size: 14px;
      color: #8c8c8c;
    }
  }

  :deep(.ant-divider) {
    margin: 24px 0;
  }

  :deep(.ant-tag) {
    font-size: 14px;
    padding: 4px 12px;
    border-radius: 4px;
  }
</style>
