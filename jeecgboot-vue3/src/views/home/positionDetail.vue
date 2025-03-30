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
  import { ref, onMounted, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getJobById } from '@/api/xgsrms/home';
  import { xgsFavoriteJobAdd, xgsFavoriteJobDel, xgsFavoriteJobList } from '@/api/xgsrms/positions';
  import { useUserStore } from '@/store/modules/user';
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  import { useMessage } from '@/hooks/web/useMessage'; // 假设你有一个 API 来获取职位信息
  import XgsPositionApplyModal from '@/views/home/position/XgsPositionApplyModal.vue';
  import { message } from 'ant-design-vue';
  import { useModal } from '@/components/Modal';
  import { defHttp } from '@/utils/http/axios';
  // 接收父组件传递的参数，获取职位 ID,当id变化时，重新获取职位信息
  const props = defineProps({
    id: { type: String, default: '' },
  });

  const { createMessage } = useMessage();
  const registerModalRef = ref();
  const positionApplyStore = usePositionApplyStoreWithOut();

  // const record = positionApplyStore.currPositionApply;
  const route = useRoute();
  const router = useRouter();
  const jobId = route.params.id as string;
  const job = ref(null);
  const applyStatus = ref(false); // 是否可以申请
  // 检查 是否已经申请
  function checkHasApplied() {
    defHttp
      .post({ url: '/positions/xgsPositionApply/checkApplyByPosId', timeout: 600000, data: { positionId: jobId } }, { isTransformResponse: false })
      .then((data) => {
        if (data && data.success) {
          applyStatus.value = true;
        } else {
          createMessage.warning('岗位已申请，请勿重复申请！');
        }
      });
  }

  const userStore = useUserStore();
  const goBack = () => {
    // router.go(-1); // 返回上一页
    router.push({ name: 'homePositions', query: { showPositionList: 'true' } });
  };

  const isCollected = ref(false);
  const favoriteJob = ref({});
  const userId = ref('');

  const record = ref({});

  const fetchFavoriteJob = () => {
    // 判断 userStore.userInfo 是否为 null，为null则赋值为 false，不为 null 则赋值 true
    let curUserId = '';
    if (userStore.userInfo === null) {
      curUserId = '';
    } else {
      curUserId = userStore.userInfo.username;
    }
    xgsFavoriteJobList({ userId: curUserId, positionId: jobId }).then((res) => {
      if (res.result.records.length > 0) {
        isCollected.value = true;
        favoriteJob.value = res.result.records[0];
      }
    });
  };

  onMounted(() => {
    checkHasApplied();
    fetchFavoriteJob();
  });

  const markFavoriteJob = () => {
    // 判断 userStore.userInfo 是否为 null，为null则提示用户登录，不为 null 则赋值 true
    if (userStore.userInfo === null) {
      // 使用 message.warning
      message.warning('请先登录');
      return;
    }
    // TODO: 实现收藏职位的功能
    let params = {
      // id: jobId,
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
        // 使用 message.warning
        message.success(`收藏职位成功`);
        console.log(`${res.message}`);
      } else {
        message.error(`${res.message}`);
        console.log(`${res.message}`);
      }
    });
  };

  // 取消收藏职位的功能
  const delFavoriteJob = () => {
    // TODO: 实现取消收藏职位的功能
    let params = {
      id: favoriteJob.value.id,
    };

    xgsFavoriteJobDel(params).then((res) => {
      if (res.code === 200) {
        isCollected.value = false;

        message.success(`取消收藏职位成功`);
        console.log(`${res.message}`, isCollected.value);
      } else {
        message.error(`取消收藏职位失败`);
        console.log(`${res.message}`);
      }
    });
  };
  const positionApplyFormData = ref({});
  const XgsPositionApplyFormShow = ref(false);

  const [registerModal, { openModal }] = useModal();

  const positionApply = () => {
    if (userStore.userInfo === null) {
      // 使用 message.warning
      message.warning('请先登录');
      return;
    } else {
      XgsPositionApplyFormShow.value = true;
      registerModalRef.value.addJob(positionApplyStore.currPositionApply);
      // record.value = positionApplyStore.currPositionApply;
      record.value.applyId = '';
      record.value.disabled = false;
      record.value.mark = '';
      record.value.positionDept = job.value.dept_dictText;
      record.value.positionId = job.value.id;
      record.value.positionName = job.value.positionName;
      record.value.positionType = job.value.category;
      record.value.resumeId = '';
      record.value.resumeName = userStore.getUserInfo.realname + userStore.getUserInfo.username + '_' + job.value.positionName;
      record.value.status = '申请中';
      record.value.userName = userStore.getUserInfo.realname;
    }
  };

  const fetchCurrApplyPosition = async () => {
    try {
      let params = {
        id: jobId,
      };
      const response = await getJobById(params);
      job.value = response.result.records[0];
      // 将job存pinia
      // 获取 Pinia store 实例

      console.log('>>>>>>fetchCurrApplyPosition', positionApplyStore.currPositionApply);
      positionApplyStore.currPositionApply = JSON.parse(JSON.stringify(response.result.records[0]));
      console.log('>>>>>>fetchCurrApplyPosition', positionApplyStore.currPositionApply);
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
  };

  onMounted(() => {
    fetchCurrApplyPosition();
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
