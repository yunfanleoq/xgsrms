<template>
  <div class="job-detail">
    <h2>职位详情</h2>
    <div class="button-container">
      <a-button type="primary" @click="goBack">返回</a-button>
      <a-button type="primary" @click="positionApply" v-if="applyStatus">在线申请</a-button>
      <a-button type="default" disabled v-else>岗位已申请</a-button>
      <div>
        <a-button v-if="!isCollected" danger @click="markFavoriteJob">收藏职位</a-button>
        <a-button v-else danger @click="delFavoriteJob"> 已收藏</a-button>
      </div>
    </div>

    <div v-if="job" class="job-info">
      <div class="job-field"> <strong>职位名称:</strong> {{ job.positionName }} </div>
      <div class="job-field"> <strong>部门:</strong> {{ job.dept_dictText }} </div>
      <div class="job-field"> <strong>研究方向:</strong> {{ job.researchDirection }} </div>
      <div class="job-field">
        <strong>专业要求:</strong>
        <pre> {{ job.professional }} </pre>
      </div>
      <div class="job-field"> <strong>工作年限:</strong> {{ job.workYears }} </div>
      <div class="job-field"> <strong>学历要求:</strong> {{ job.xlxw }} </div>
      <div class="job-field"> <strong>招聘状态:</strong> {{ job.status_dictText }} </div>
      <div class="job-field">
        <strong>职责:</strong>
        <pre>{{ job.duty }}</pre>
      </div>
      <div class="job-field"> <strong>联系人:</strong> {{ job.ktz_dictText }} </div>
      <div class="job-field"> <strong>联系电话:</strong> {{ job.telphone || '无' }} </div>
      <div class="job-field"> <strong>电子邮箱:</strong> {{ job.email }} </div>
      <div class="job-field">
        <strong>备注:</strong>
        <pre> {{ job.memo || '无' }} </pre>
      </div>
    </div>
    <div v-else>
      <p>职位信息加载中...</p>
    </div>
  </div>
  <XgsPositionApplyModal ref="registerModalRef" :form-data="record" :formBpm="true" />
</template>

<script setup lang="ts" name="PositionDetail">
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getJobById } from '@/api/xgsrms/home';
  import { xgsFavoriteJobAdd, xgsFavoriteJobDel, xgsFavoriteJobList } from '@/api/xgsrms/positions';
  import { useUserStore } from '@/store/modules/user';
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  import { useMessage } from '@/hooks/web/useMessage';
  import XgsPositionApplyModal from '@/views/home/position/XgsPositionApplyModal.vue';
  import { message } from 'ant-design-vue';
  import { useModal } from '@/components/Modal';
  import { defHttp } from '@/utils/http/axios';

  // --- 类型定义 ---
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

  // 检查 是否已经申请
  function checkHasApplied() {
    defHttp
      .post({ url: '/positions/xgsPositionApply/checkApplyByPosId', timeout: 600000, data: { positionId: jobId } }, { isTransformResponse: false })
      .then((data) => {
        if (data && data.success) {
          applyStatus.value = true;
        } else {
          applyStatus.value = false;
          createMessage.warning('岗位已申请，请勿重复申请！');
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

  const [registerModal, { openModal }] = useModal();

  const positionApply = () => {
    if (!userStore.userInfo) {
      message.warning('请先登录');
      return;
    }
    if (!job.value) {
      message.error('职位信息加载失败，请稍后再试');
      return;
    }
    registerModalRef.value.addJob(positionApplyStore.currPositionApply);
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
  };

  const fetchCurrApplyPosition = async () => {
    try {
      const response = await getJobById({ id: jobId });
      if (response.success && response.result.records.length > 0) {
        job.value = response.result.records[0];
        positionApplyStore.currPositionApply = JSON.parse(JSON.stringify(job.value));
      }
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
  };

  onMounted(async () => {
    await fetchCurrApplyPosition();
    // 仅在用户登录后执行需要认证的API调用
    if (userStore.userInfo) {
      checkHasApplied();
      fetchFavoriteJob();
    }
  });
</script>

<style scoped>
  .job-detail {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .job-info {
    margin-top: 20px;
  }

  .job-field {
    margin-bottom: 10px;
  }

  .job-field strong {
    display: inline-block;
    width: 120px;
    color: #333;
  }

  .job-field span {
    color: #555;
  }

  h2 {
    text-align: center;
  }

  h3 {
    margin-top: 20px;
  }

  p {
    margin: 10px 0;
  }

  strong {
    font-weight: bold;
  }

  .button-container {
    display: flex;
    justify-content: space-between; /* 将按钮分别放置在左右两边 */
    margin-top: 20px; /* 可选：添加顶部间距 */
  }

  .back-button,
  .apply-button {
    margin-bottom: 20px;
    padding: 10px 20px;
    background-color: #4a90e2;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
  }
  .favorite-button {
    margin-bottom: 20px;
    padding: 10px 20px;
    background-color: #c47e6e;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
  }

  .marked-favorite-button {
    margin-bottom: 20px;
    padding: 10px 20px;
    background-color: #b84035;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
  }

  .back-button:hover,
  .apply-button:hover {
    background-color: #357ab8;
  }

  .favorite-button:hover {
    background-color: #ba372a;
  }
  .marked-favorite-button:hover {
    background-color: #52130d;
  }

  pre {
    margin: 0;
    white-space: pre-wrap; /* 保留换行符并自动换行 */
    word-wrap: break-word; /* 防止长单词溢出 */
    background-color: #f0f0f0;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
  }
</style>
