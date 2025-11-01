<template>
  <div class="job-detail-wrapper">
    <div class="job-detail-container">
      <!-- 顶部标题和按钮区域 -->
      <div class="detail-header">
        <h1 class="page-title">职位详情</h1>
        <div class="action-buttons">
          <a-button size="large" @click="goBack">
            <template #icon>
              <LeftOutlined />
            </template>
            返回
          </a-button>
          <a-button v-if="applyStatus" type="primary" size="large" @click="positionApply">
            <template #icon>
              <FileTextOutlined />
            </template>
            在线申请
          </a-button>
          <a-button v-else type="default" size="large" disabled>
            <template #icon>
              <CheckCircleOutlined />
            </template>
            岗位已申请
          </a-button>
          <a-button v-if="!isCollected" danger size="large" @click="markFavoriteJob">
            <template #icon>
              <HeartOutlined />
            </template>
            收藏职位
          </a-button>
          <a-button v-else danger size="large" @click="delFavoriteJob">
            <template #icon>
              <HeartFilled />
            </template>
            已收藏
          </a-button>
        </div>
      </div>

      <!-- 职位信息卡片 -->
      <div v-if="job" class="job-info-card">
        <!-- 基本信息区域 -->
        <div class="info-section">
          <h2 class="section-title">
            <IdcardOutlined class="title-icon" />
            基本信息（{{ job.category }}）
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
                  <a-tag color="green">{{ job.status }}</a-tag>
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
    </div>
  </div>
  <!-- 使用新的重构后的在线申请表单 -->
  <XgsPositionApplyModalNew ref="registerModalRef" :form-data="record" :formBpm="true" @success="handleApplySuccess" />
</template>

<script setup lang="ts" name="PositionDetail">
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getJobById, getPageById } from '@/api/xgsrms/home';
  import { xgsFavoriteJobAdd, xgsFavoriteJobDel, xgsFavoriteJobList } from '@/api/xgsrms/positions';
  import { useUserStore } from '@/store/modules/user';
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  import { useMessage } from '@/hooks/web/useMessage';
  // 引入新的重构表单组件
  import XgsPositionApplyModalNew from '@/views/positions/components/XgsPositionApplyModalNew.vue';
  import { message } from 'ant-design-vue';
  import { defHttp } from '@/utils/http/axios';
  // 引入图标
  import {
    LeftOutlined,
    FileTextOutlined,
    CheckCircleOutlined,
    HeartOutlined,
    HeartFilled,
    IdcardOutlined,
    SolutionOutlined,
    ProfileOutlined,
    PhoneOutlined,
    InfoCircleOutlined,
  } from '@ant-design/icons-vue';

  // --- 类型定义 ---
  interface Job {
    id: string;
    positionName: string;
    dept: string;
    dept_dictText: string;
    researchDirection: string;
    professional: string;
    workYears: string;
    xlxw: string;
    status: string;
    status_dictText: string;
    duty: string;
    ktz_dictText: string;
    telphone?: string;
    email: string;
    memo?: string;
    category: string;
    category_dictText: string;
    personCount?: number;
  }

  interface FavoriteJob {
    id: string;
  }

  interface ApplyRecord {
    applyId: string;
    disabled: boolean;
    mark: string;
    positionDept: string;
    positionId: string;
    positionName: string;
    positionType: string;
    resumeId: string;
    resumeName: string;
    status: string;
    userName: string;
  }

  // 接收父组件传递的参数，获取职位 ID
  const props = defineProps({
    id: { type: String, default: '' },
  });

  const { createMessage } = useMessage();
  const registerModalRef = ref();
  const positionApplyStore = usePositionApplyStoreWithOut();

  const route = useRoute();
  const router = useRouter();
  const jobId = route.params.id as string;
  const job = ref<Job | null>(null);
  const applyStatus = ref(true); // 是否可以申请, 默认为 true
  const userStore = useUserStore();
  const isCollected = ref(false);
  const favoriteJob = ref<FavoriteJob | null>(null);
  const record = ref<Partial<ApplyRecord>>({});

  /**
   * 检查是否已经申请
   * @param showMessage 是否显示消息提示，默认为 false（静默更新按钮状态）
   */
  function checkHasApplied(showMessage = false) {
    defHttp
      .post({ url: '/positions/xgsPositionApply/checkApplyByPosId', timeout: 600000, data: { positionId: jobId } }, { isTransformResponse: false })
      .then((data) => {
        if (data && data.success) {
          applyStatus.value = true;
        } else {
          applyStatus.value = false;
          // 只在需要时显示消息提示（页面首次加载时）
          if (showMessage) {
            createMessage.warning('岗位已申请，请勿重复申请！');
          }
        }
      });
  }

  const goBack = () => {
    router.push({ name: 'homePositions', query: { showPositionList: 'true' } });
  };

  const fetchFavoriteJob = () => {
    if (!userStore.userInfo) return;
    xgsFavoriteJobList({ userId: userStore.userInfo.username, positionId: jobId }).then((res) => {
      if (res.success && res.result.records.length > 0) {
        isCollected.value = true;
        favoriteJob.value = res.result.records[0];
      }
    });
  };

  const markFavoriteJob = () => {
    if (!userStore.userInfo) {
      message.warning('请先登录');
      return;
    }
    if (!job.value) {
      message.error('职位信息加载失败，请稍后再试');
      return;
    }
    const params = {
      userId: userStore.userInfo.username,
      userName: userStore.userInfo.realname,
      positionId: jobId,
      positionName: job.value.positionName,
      positionDept: job.value.dept_dictText,
      positionKtz: job.value.ktz_dictText,
      positionCount: job.value.personCount,
    };

    xgsFavoriteJobAdd(params).then((res) => {
      if (res.code == 200) {
        isCollected.value = true;
        message.success(`收藏职位成功`);
        fetchFavoriteJob(); // 重新获取收藏信息以更新ID
      } else {
        message.error(`${res.message}`);
      }
    });
  };

  const delFavoriteJob = () => {
    if (!favoriteJob.value) return;
    const params = {
      id: favoriteJob.value.id,
    };

    xgsFavoriteJobDel(params).then((res) => {
      if (res.code === 200) {
        isCollected.value = false;
        favoriteJob.value = null;
        message.success(`取消收藏职位成功`);
      } else {
        message.error(`取消收藏职位失败`);
      }
    });
  };

  const positionApply = () => {
    if (!userStore.userInfo) {
      message.warning('请先登录');
      return;
    }
    if (!job.value) {
      message.error('职位信息加载失败，请稍后再试');
      return;
    }
    // 先设置 record 数据
    record.value = {
      applyId: '',
      disabled: false,
      mark: '',
      positionDept: job.value.dept_dictText,
      positionId: job.value.id,
      positionName: job.value.positionName,
      positionType: job.value.category,
      resumeId: '',
      resumeName: userStore.getUserInfo.realname + userStore.getUserInfo.username + '_' + job.value.positionName,
      status: '申请中',
      userName: userStore.getUserInfo.realname,
    };
    // 然后打开 Modal，传递 record 数据
    registerModalRef.value.addJob(record.value);
  };

  /**
   * 处理申请成功回调
   * 重新检查申请状态，更新按钮显示（静默更新，不显示提示）
   */
  const handleApplySuccess = () => {
    console.log('✅ 申请成功，重新检查申请状态');
    // 重新检查是否已申请，更新 applyStatus 状态（不显示消息提示）
    checkHasApplied(false);
  };

  const fetchCurrApplyPosition = async () => {
    try {
      const response = await getPageById({ id: jobId });
      if (response.success && response.result) {
        job.value = response.result.records[0];
        positionApplyStore.currPositionApply = job.value as any;
      }
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
  };

  onMounted(async () => {
    await fetchCurrApplyPosition();
    // 仅在用户登录后执行需要认证的API调用
    if (userStore.userInfo) {
      // 页面首次加载时检查申请状态，如果已申请则显示提示消息
      checkHasApplied(true);
      fetchFavoriteJob();
    }
  });
</script>

<style scoped lang="less">
  .job-detail-wrapper {
    min-height: 100vh;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
    padding: 24px;
  }

  .job-detail-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  /* 顶部标题和按钮区域 */
  .detail-header {
    background: #fff;
    padding: 24px 32px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
  }

  .page-title {
    margin: 0;
    font-size: 28px;
    font-weight: 600;
    color: #1a1a1a;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  /* 职位信息卡片 */
  .job-info-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    padding: 32px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }
  }

  /* 信息区块 */
  .info-section {
    margin-bottom: 8px;
  }

  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 2px solid #f0f0f0;

    .title-icon {
      margin-right: 10px;
      font-size: 22px;
      color: #1890ff;
    }
  }

  /* 信息项 */
  .info-item {
    label {
      display: block;
      font-size: 13px;
      color: #8c8c8c;
      margin-bottom: 8px;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .info-value {
      font-size: 15px;
      color: #262626;
      line-height: 1.6;
      font-weight: 500;
      padding: 8px 12px;
      background: #fafafa;
      border-radius: 6px;
      border-left: 3px solid #1890ff;

      &.highlight {
        font-size: 18px;
        font-weight: 600;
        color: #1890ff;
        background: #e6f7ff;
        border-left-color: #1890ff;
      }
    }
  }

  /* 内容区域（多行文本） */
  .info-content {
    font-size: 15px;
    color: #262626;
    line-height: 1.8;
    padding: 16px;
    background: #fafafa;
    border-radius: 8px;
    border: 1px solid #e8e8e8;
    white-space: pre-wrap;
    word-wrap: break-word;
    min-height: 60px;
  }

  /* 分割线 */
  :deep(.ant-divider) {
    margin: 32px 0;
    border-color: #f0f0f0;
  }

  /* 加载状态 */
  .loading-container {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    padding: 80px 32px;
    text-align: center;
  }

  .loading-text {
    margin-top: 16px;
    font-size: 16px;
    color: #8c8c8c;
  }

  /* 标签样式优化 */
  :deep(.ant-tag) {
    padding: 4px 12px;
    font-size: 14px;
    border-radius: 4px;
    font-weight: 500;
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .job-detail-wrapper {
      padding: 12px;
    }

    .detail-header {
      padding: 16px;
      flex-direction: column;
      align-items: stretch;
    }

    .page-title {
      font-size: 22px;
      text-align: center;
    }

    .action-buttons {
      justify-content: center;
    }

    .job-info-card {
      padding: 20px;
    }

    .section-title {
      font-size: 18px;
    }

    .info-item .info-value.highlight {
      font-size: 16px;
    }
  }

  @media (max-width: 576px) {
    .action-buttons {
      flex-direction: column;
      width: 100%;

      :deep(.ant-btn) {
        width: 100%;
      }
    }
  }
</style>