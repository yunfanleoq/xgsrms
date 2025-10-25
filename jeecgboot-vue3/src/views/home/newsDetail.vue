<template>
  <div class="job-detail">
    <h2>{{ activeMenu }}</h2>
    <div class="button-container">
      <a-button size="small" type="primary" @click="goBack">返回</a-button>
    </div>
    <div v-if="news" class="job-info">
      <div class="job-field" v-if="!news.journalismText">
        <span>{{ news.journalismHead }}</span>
      </div>
      <div class="job-field" v-if="!news.journalismText">
        <div> <div>发布时间:</div> {{ news.updateTime }} </div>
      </div>
      <div id="newsDetailContent" class="job-field" v-html="getNewsText(news)"> </div>
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

  function getNewsText(news) {
    if (news) {
      const htmlText = news.journalismText;
      if (htmlText) {
        return htmlText;
      }
      return news.shortText;
    }
    return news;
  }

  onMounted(() => {
    fetchNews();
  });
</script>
<style>
  #newsDetailContent .wztitle {
    font-size: 32px;
    font-weight: 700;
    text-align: center;
    display: block;
    color: #2c3e50;
    line-height: 1.4;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #e9ecef;
  }

  #newsDetailContent .lyandtime {
    text-align: center;
    color: #999;
    font-size: 14px;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;
  }

  #newsDetailContent {
    font-size: 16px;
    line-height: 1.8;
    color: #333;
  }

  #newsDetailContent p {
    margin: 16px 0;
    text-indent: 2em;
    color: #555;
  }

  #newsDetailContent img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 25px auto;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  #newsDetailContent h1,
  #newsDetailContent h2,
  #newsDetailContent h3,
  #newsDetailContent h4,
  #newsDetailContent h5,
  #newsDetailContent h6 {
    color: #2c3e50;
    font-weight: 600;
    margin-top: 30px;
    margin-bottom: 15px;
    line-height: 1.4;
  }

  #newsDetailContent h1 {
    font-size: 28px;
  }

  #newsDetailContent h2 {
    font-size: 24px;
  }

  #newsDetailContent h3 {
    font-size: 20px;
  }

  #newsDetailContent ul,
  #newsDetailContent ol {
    margin: 16px 0;
    padding-left: 2em;
  }

  #newsDetailContent li {
    margin: 8px 0;
    line-height: 1.8;
  }

  #newsDetailContent blockquote {
    margin: 20px 0;
    padding: 15px 20px;
    background-color: #f8f9fa;
    border-left: 4px solid #3d54a7;
    color: #666;
    font-style: italic;
  }

  #newsDetailContent a {
    color: #3d54a7;
    text-decoration: none;
    transition: color 0.2s ease;
  }

  #newsDetailContent a:hover {
    color: #5068b8;
    text-decoration: underline;
  }

  #newsDetailContent table {
    width: 100%;
    border-collapse: collapse;
    margin: 20px 0;
  }

  #newsDetailContent table th,
  #newsDetailContent table td {
    border: 1px solid #e9ecef;
    padding: 10px 15px;
    text-align: left;
  }

  #newsDetailContent table th {
    background-color: #f8f9fa;
    font-weight: 600;
    color: #2c3e50;
  }

  #newsDetailContent code {
    background-color: #f5f5f5;
    padding: 2px 6px;
    border-radius: 3px;
    font-family: 'Courier New', monospace;
    font-size: 14px;
    color: #e83e8c;
  }

  #newsDetailContent pre {
    background-color: #f5f5f5;
    padding: 15px;
    border-radius: 6px;
    overflow-x: auto;
    margin: 20px 0;
  }

  #newsDetailContent pre code {
    background: none;
    padding: 0;
    color: #333;
  }
</style>
<style scoped>
  .job-detail {
    max-width: 900px;
    margin: 20px auto;
    padding: 40px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.1);
  }

  .job-info {
    margin-top: 30px;
    line-height: 1.8;
  }

  .job-field {
    margin-bottom: 20px;
  }

  .job-field div {
    text-align: center;
    color: #999;
    font-size: 14px;
    margin-bottom: 10px;
  }

  .job-field div div {
    display: inline-block;
    width: 120px;
    font-weight: 500;
  }

  .job-field span {
    font-size: 28px;
    font-weight: 700;
    text-align: center;
    display: block;
    color: #2c3e50;
    line-height: 1.4;
    margin-bottom: 15px;
  }

  h2 {
    text-align: center;
    color: #2c3e50;
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 20px;
  }

  h3 {
    margin-top: 25px;
    color: #2c3e50;
    font-weight: 600;
  }

  p {
    margin: 15px 0;
    color: #555;
  }

  strong {
    font-weight: 600;
    color: #333;
  }

  .button-container {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 25px;
    padding-bottom: 20px;
    border-bottom: 1px solid #e9ecef;
  }

  .back-button,
  .apply-button {
    padding: 10px 24px;
    background: linear-gradient(135deg, #3d54a7, #5068b8);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 15px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(61, 84, 167, 0.2);
  }

  .favorite-button {
    padding: 10px 24px;
    background: linear-gradient(135deg, #e8b7ac, #d89b8f);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 15px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(232, 183, 172, 0.3);
  }

  .back-button:hover,
  .apply-button:hover {
    background: linear-gradient(135deg, #2a3d7a, #3d54a7);
    box-shadow: 0 4px 12px rgba(61, 84, 167, 0.3);
    transform: translateY(-1px);
  }

  .favorite-button:hover {
    background: linear-gradient(135deg, #d89b8f, #c57863);
    box-shadow: 0 4px 12px rgba(232, 183, 172, 0.4);
    transform: translateY(-1px);
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
