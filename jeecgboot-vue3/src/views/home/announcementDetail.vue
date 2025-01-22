<template>
  <div class="announcement-detail">
    <div class="button-container">
      <button @click="goBack" class="back-button">返回</button>
    </div>
    <!-- 标题 -->
    <p class="announcementTitle">{{ announcement.title }}</p>
    <!-- 正文内容 -->
    <div>
      <div class="announcementContent" v-html="announcement.content"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import { defHttp } from '@/utils/http/axios';
import { getannouncementById } from '@/api/xgsrms/announcementDetail';

const route = useRoute();
const router = useRouter();
const announcementId = route.params.id as string;

const goBack = () => {
  router.go(-1); // 返回上一页
};

// 招聘公告详情数据
const announcement = ref({
  title: '',
  content: '',
});

// 获取公告详情数据
const fetchAnnouncementDetail = async () => {
  try {
    let params = {
      id: announcementId,
    };
    const response = await getannouncementById(params);
    // 确保获取正确的字段并赋值
    if (response && response.result) {
      announcement.value = {
        title: response.result.recruitAnnouncementTitle,
        content: response.result.recruitAnnouncement,
      };
    } else {
      console.error("无效的响应数据:", response);
    }

  } catch (error) {
    console.error('Failed to fetch announcement details:', error);
  }
};

// 初始化
onMounted(() => {
  fetchAnnouncementDetail();
});
</script>

<style scoped>
/* 按钮容器样式 */
.button-container {
  display: flex;
  justify-content: flex-start; /* 将按钮放置在左侧 */
  margin-top: 20px;
}

/* 返回按钮样式 */
.back-button {
  padding: 12px 24px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1.2rem;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

.back-button:hover {
  background-color: #0056b3; /* 鼠标悬停时的颜色 */
  transform: scale(1.05); /* 鼠标悬停时按钮放大 */
}

/* 招聘公告详情区域样式 */
.announcement-detail {
  max-width: 800px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* 公告标题样式 */
.announcementTitle {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  color: #333;
  transition: color 0.3s ease; /* 添加过渡效果 */
}

/* 公告内容样式 */
.announcementContent {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #555;
}

</style>
