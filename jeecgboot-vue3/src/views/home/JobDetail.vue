<template>
  <div class="job-detail">
    <h2>职位详情</h2>
    <div class="button-container">
      <button @click="goBack" class="back-button">返回</button>
      <button @click="positionApply" class="apply-button">在线申请</button>
      <button @click="positionApply" class="favorite-button">收藏职位</button>
    </div>

    <div v-if="job" class="job-info">
      <div class="job-field">
        <strong>职位名称:</strong> {{ job.positionName }}
      </div>
      <div class="job-field">
        <strong>部门:</strong> {{ job.dept_dictText }}
      </div>
      <div class="job-field">
        <strong>研究方向:</strong> {{ job.researchDirection }}
      </div>
      <div class="job-field">
        <strong>专业要求:</strong>
        <pre> {{ job.professional }} </pre>
      </div>
      <div class="job-field">
        <strong>工作年限:</strong> {{ job.workYears }}
      </div>
      <div class="job-field">
        <strong>学历要求:</strong> {{ job.xlxw }}
      </div>
      <div class="job-field">
        <strong>招聘状态:</strong> {{ job.status_dictText }}
      </div>
      <div class="job-field">
        <strong>职责:</strong>
        <pre>{{ job.duty }}</pre>
      </div>
      <div class="job-field">
        <strong>联系人:</strong> {{ job.ktz_dictText }}
      </div>
      <div class="job-field">
        <strong>联系电话:</strong> {{ job.telphone || '无' }}
      </div>
      <div class="job-field">
        <strong>电子邮箱:</strong> {{ job.email }}
      </div>
      <div class="job-field">
        <strong>备注:</strong>
        <pre> {{ job.memo || '无' }} </pre>
      </div>
    </div>
    <div v-else>
      <p>职位信息加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getJobById } from '@/api/xgsrms/home'; // 假设你有一个 API 来获取职位信息

const route = useRoute();
const router = useRouter();
const jobId = route.params.id as string;
const job = ref(null);

const goBack = () => {
  router.go(-1); // 返回上一页
};

const positionApply = () => {
  let token = localStorage.getItem('token');
  if (token === null || token === '') {
    router.push({ name: 'Login' });
    return;
  }
  router.push({ name: 'PositionApply', params: { id: jobId } });
}

const fetchJob = async () => {
  try {
    let params = {
      id: jobId,
    };
    const response = await getJobById(params);
    job.value = response.result.records[0];
  } catch (error) {
    console.error('获取职位信息失败:', error);
  }
};

onMounted(() => {
  fetchJob();
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


.back-button, .apply-button {
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
  background-color: #e8b7ac;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;

}

.back-button:hover, .apply-button:hover{
  background-color: #357ab8;
}

.favorite-button:hover {
  background-color: #b84035;
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
