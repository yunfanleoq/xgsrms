<template>
  <div class="job-detail">
    <h2>{{ activeMenu }}</h2>
    <div class="button-container">
      <button @click="goBack" class="back-button">返回</button>
    </div>

    <div v-if="news" class="job-info">
      <div class="job-field">
        <span>{{ news.journalismHead }}</span>
      </div>
      <div class="job-field">
        <div> <div>发布时间:</div> {{ news.updateTime }} </div>
      </div>
      <div class="job-field" v-html="news.journalismText"> </div>
    </div>
    <div v-else>
      <p>新闻信息加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getNewsById } from '@/api/xgsrms/newsHome'; // 假设你有一个 API 来获取新闻信息

  const route = useRoute();
  const router = useRouter();
  const newsId = route.params.id as string;
  const news = ref(null);
  const activeMenu = ref('头条新闻');

  const goBack = () => {
    router.go(-1); // 返回上一页
  };

  const fetchNews = async () => {
    try {
      let params = {
        id: newsId,
      };
      const response = await getNewsById(params);
      news.value = response.result;
      activeMenu.value = response.result.type;
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
  };

  onMounted(() => {
    fetchNews();
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

  .job-field div {
    text-align: center; /* 居中文本 */
    color: #999;
  }

  .job-field div div {
    display: inline-block;
    width: 120px;
  }

  .job-field span {
    font-size: 1.75em; /* 放大字体 */
    font-weight: bold; /* 加粗字体 */
    text-align: center; /* 居中文本 */
    display: block;
    color: #666;
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
    background-color: #e8b7ac;
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
