<template>
  <div>
    <div class="main-content">
      <!-- 轮播图 -->
      <div class="carousel" @mouseenter="isHovered = true" @mouseleave="isHovered = false">
        <!-- Loading 状态 -->
        <div v-if="isLoadingCarousel" class="carousel-loading">
          <a-spin size="large" tip="加载中..." />
        </div>
        <!-- 无图片占位 -->
        <div v-else-if="!currentImgSrc" class="carousel-placeholder">
          <PictureOutlined class="placeholder-icon" />
          <p class="placeholder-text">暂无轮播图片</p>
        </div>
        <!-- 轮播图内容 -->
        <template v-else>
          <button class="carousel-button left" @click="showPrevImage">◀</button>
          <img :src="currentImgSrc" alt="轮播图" @error="handleImageError" />
          <button class="carousel-button right" @click="showNextImage">▶</button>
          <!-- 圆点导航 -->
          <div class="carousel-dots">
            <span
              v-for="(_image, index) in carouselImages"
              :key="index"
              :class="['dot', { active: index === currentIndex }]"
              @click="currentIndex = index"
            ></span>
          </div>
        </template>
      </div>
      <!-- 新闻区域 -->
      <div class="news">
        <div v-for="(news, index) in newsList" :key="index" class="news-item" @click="news.id && goToNewsDetail(String(news.id))">
          <h3 v-html="news.title"></h3>
          <span class="subText" v-if="index === 0" v-html="news.shortText"></span>
        </div>
        <!-- 查看更多链接 -->
        <div class="news-more">
          <router-link to="/home/news"> <a-button type="link" class="more-link">查看更多 +</a-button></router-link>
        </div>
      </div>
    </div>
    <!-- 公告导航栏和内容 -->
    <div class="announcements-container">
      <div class="tabs-container">
        <ul class="tabs">
          <!-- <li :class="{ selected: tabSelected === 'notice' }" @mouseover="tabSelected = 'notice'">通知公告</li> -->
          <li :class="{ selected: tabSelected === 'recruitment' }" @mouseover="tabSelected = 'recruitment'">
            <span class="tab-icon"><BulbOutlined /></span>
            招聘公告
          </li>
        </ul>
        <!-- <a class="more-link" :href="tabSelected === 'notice' ? './home/news' : './home/positions'">查看更多 +</a> -->
        <router-link to="/home/positions"> <span class="more-link">查看更多 +</span></router-link>
      </div>
      <div class="announcements-content">
        <ul class="recruitment-list">
          <li v-for="(item, index) in recruitments" :key="index" @click="goToAnnouncementDetail(String(item.id))">
            <span class="item-icon"><FileTextOutlined /></span>
            <p v-html="item.announcement"></p>
            <span class="item-arrow"><RightOutlined /></span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount, watchEffect } from 'vue';
  import { defHttp } from '@/utils/http/axios';
  import { router } from '@/router';
  import { BulbOutlined, FileTextOutlined, RightOutlined, PictureOutlined } from '@ant-design/icons-vue';

  interface CarouselImage {
    image: string;
    createTime: string;
  }

  interface NewsItem {
    title: string;
    shortText?: string;
    id: string;
    createTime?: string;
  }

  interface RecruitmentItem {
    id: string;
    announcement: string;
    title: string;
    shortText: string;
    createTime: string;
  }

  const listUrl = '/xgsJournalism/xgsJournalism/list';
  const imgListUrl = '/xgsHome/xgsHome/listForHome';

  const carouselImages = ref<CarouselImage[]>([]);
  const currentIndex = ref(0);
  const currentImgSrc = ref('');
  const isLoadingCarousel = ref(true); // 轮播图加载状态
  
  watchEffect(() => {
    currentImgSrc.value = carouselImages.value[currentIndex.value]?.image || '';
  });

  // 获取轮播图数据的函数
  const fetchCarouselImages = async () => {
    isLoadingCarousel.value = true; // 开始加载
    try {
      const response = await defHttp.get({
        url: imgListUrl, // 替换为你的轮播图数据接口
        params: { newsType: 'homeImages', pageNo: 1, pageSize: 10 },
      });

      if (response && response.records) {
        carouselImages.value = response.records
          .filter((item) => item.images) // 过滤掉没有images字段的数据
          .map((item) => {
            // 直接使用images字段（Base64编码）
            let imageUrl = item.images;
            
            // 如果Base64数据不包含data URI前缀，则添加
            if (imageUrl && !imageUrl.startsWith('data:image')) {
              imageUrl = `data:image/jpeg;base64,${imageUrl}`;
            }
            
            return {
              image: imageUrl, // 使用Base64编码的图片数据
              createTime: item.createTime,
            };
          })
          .slice(0, 5); // 截取前5张轮播图
        console.log('处理后的 carouselImages', carouselImages.value);
      }
    } catch (error) {
      console.error('请求轮播图数据失败:', error);
    } finally {
      isLoadingCarousel.value = false; // 加载结束
    }
  };

  // 自动轮播
  let carouselTimer: ReturnType<typeof setInterval> | null = null;

  const startCarousel = () => {
    if (carouselTimer) return;
    carouselTimer = setInterval(() => {
      if (!isHovered.value && carouselImages.value.length > 0) {
        currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
      }
    }, 3000);
  };

  const stopCarousel = () => {
    if (carouselTimer) {
      clearInterval(carouselTimer);
      carouselTimer = null;
    }
  };

  // 控制轮播图按钮的显隐状态
  const isHovered = ref(false);

  // 轮播图按钮切换逻辑
  const showPrevImage = () => {
    currentIndex.value = (currentIndex.value - 1 + carouselImages.value.length) % carouselImages.value.length;
  };

  const showNextImage = () => {
    currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
  };

  // 图片加载错误处理
  const handleImageError = () => {
    console.error('轮播图加载失败');
    // 可以在这里设置一个默认的错误图片
  };

  // 新闻列表数据
  const newsList = ref<NewsItem[]>([]);
  const fetchNews = async () => {
    try {
      const homeResponse = await defHttp.get({
        url: listUrl,
        params: { typeCode: 'ttxw', pageNo: 1, pageSize: 5 },
      });

      if (homeResponse?.records) {
        newsList.value = homeResponse.records.map((item: any) => ({
          title: item.journalismHead,
          shortText: item.shortText,
          id: item.id,
          createTime: item.createTime || '',
        }));
      }
    } catch (error) {
      console.error('请求新闻数据失败:', error);
    }
  };

  // 跳转至头条新闻详情
  const goToNewsDetail = (itemId: string) => {
    router.push({ name: 'newsDetail', params: { id: itemId } });
  };

  // 数据
  const recruitments = ref<RecruitmentItem[]>([]);
  const tabSelected = ref('recruitment');

  // 获取招聘公告标题数据
  const fetchRecruitments = async () => {
    try {
      const response = await defHttp.get({
        url: listUrl,
        params: { typeCode: 'rczp', pageNo: 1, pageSize: 10 },
      });

      if (response && response.records) {
        recruitments.value = response.records.map((item: any) => ({
          id: item.id,
          announcement: item.journalismHead,
          title: item.journalismHead,
          shortText: item.shortText,
          createTime: item.createTime,
        }));
      }
    } catch (error) {
      console.error('请求招聘公告数据失败:', error);
    }
  };

  // 跳转至招聘公告详情
  const goToAnnouncementDetail = (itemId: string) => {
    router.push({ name: 'newsDetail', params: { id: itemId } });
  };

  // 组件挂载时加载数据并启动轮播
  onMounted(() => {
    fetchCarouselImages(); // 首页图片
    fetchNews(); // 首页新闻
    fetchRecruitments(); //  首页招聘信息
    startCarousel(); // 启动轮播
  });

  // 组件卸载时清理定时器
  onBeforeUnmount(() => {
    stopCarousel();
  });
</script>

<style scoped>
  /* 主容器样式 */
  .main-content {
    display: flex;
    justify-content: space-between;
    align-items: stretch;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    box-sizing: border-box;
    gap: 20px;
  }

  /* 标题样式 */
  h1 {
    color: #42b983;
    margin: 0;
  }

  h2 {
    color: #3d54a7;
    text-decoration: none;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;
    margin: 0;
  }

  /* 轮播图和新闻区域高度统一 */
  .carousel,
  .news {
    height: 400px;
  }

  /* 轮播图样式 */
  .carousel {
    flex: 3;
    max-width: calc(75% - 10px);
    height: 400px;
    margin: 0;
    padding: 0;
    position: relative;
    overflow: hidden;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .carousel img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  /* 轮播图加载状态 */
  .carousel-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  }

  /* 轮播图占位符 */
  .carousel-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  }

  .placeholder-icon {
    font-size: 80px;
    color: #d9d9d9;
    margin-bottom: 16px;
  }

  .placeholder-text {
    font-size: 16px;
    color: #999;
    margin: 0;
  }

  /* 轮播图按钮 */
  .carousel-button {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    font-size: 20px;
    border: none;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    z-index: 10;
    opacity: 0;
    pointer-events: none;
    transition: all 0.3s ease;
  }

  .carousel-button.left {
    left: 10px;
  }

  .carousel-button.right {
    right: 10px;
  }

  .carousel:hover .carousel-button {
    opacity: 1;
    pointer-events: auto;
  }

  .carousel-button:hover {
    background-color: rgba(0, 0, 0, 0.8);
    transform: translateY(-50%) scale(1.1);
  }

  /* 圆点导航 */
  .carousel-dots {
    position: absolute;
    bottom: 15px;
    right: 15px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
  }

  .dot {
    width: 10px;
    height: 10px;
    background-color: rgba(255, 255, 255, 0.6);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .dot.active {
    width: 20px;
    border-radius: 10px;
    background-color: #3d54a7;
  }

  .dot:hover {
    width: 20px;
    border-radius: 10px;
    background-color: #3d54a7;
  }

  /* 新闻区域样式 */
  .news {
    flex: 1;
    max-width: calc(25% - 10px);
    height: 400px;
    background: linear-gradient(135deg, #3d54a7 0%, #5a6fb8 100%);
    padding: 15px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    display: flex;
    flex-direction: column;
  }

  /* 新闻项样式 */
  .news-item {
    margin-bottom: 10px;
    padding: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 4px;
  }

  .news-item:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  .news-item h3 {
    width: 100%;
    margin: 0;
    color: white;
    font-size: 16px;
    font-weight: bold;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    transition: color 0.3s ease;
  }

  .news-item:hover h3 {
    color: #ffd700;
  }

  .news-item .subText {
    display: -webkit-box;
    color: rgba(255, 255, 255, 0.9);
    margin: 8px 0 0 0;
    font-size: 13px;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 3;
    line-clamp: 3;
    -webkit-box-orient: vertical;
  }

  /* 查看更多按钮 */
  .news-more {
    position: absolute;
    bottom: 15px;
    right: 15px;
  }

  .news-more .more-link {
    color: white;
    text-decoration: none;
    font-weight: 600;
    transition: color 0.3s ease;
  }

  .news-more .more-link:hover {
    color: #ffd700;
  }

  /* 招聘公告容器 */
  .announcements-container {
    max-width: 1200px;
    margin: 20px auto 0;
    padding: 0 20px;
    box-sizing: border-box;
  }

  .tabs-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    margin-bottom: 10px;
    background: linear-gradient(135deg, #3d54a7 0%, #5a6fb8 100%);
    padding: 0 15px;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .announcements-content {
    width: 100%;
    background-color: white;
    border-radius: 0 0 8px 8px;
    padding: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .tabs {
    display: flex;
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .tabs li {
    cursor: pointer;
    font-size: 18px;
    font-weight: bold;
    color: white;
    transition: all 0.3s ease;
    padding: 12px 20px;
    border-radius: 4px;
    display: flex;
    align-items: center;
  }
  
  .tab-icon {
    margin-right: 8px;
    display: flex;
    align-items: center;
  }
  
  .tab-icon i {
    font-size: 20px;
  }

  .tabs li:hover,
  .tabs li.selected {
    background-color: rgba(255, 255, 255, 0.2);
  }

  .more-link {
    font-size: 14px;
    color: white;
    text-decoration: none;
    cursor: pointer;
    padding: 8px 15px;
    transition: color 0.3s ease;
  }

  .more-link:hover {
    color: #ffd700;
  }

  .recruitment-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .recruitment-list li {
    cursor: pointer;
    color: #333;
    transition: all 0.3s ease;
    margin: 8px 0;
    padding: 10px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    position: relative;
  }

  .recruitment-list li:hover {
    background-color: rgba(61, 84, 167, 0.05);
    color: #3d54a7;
  }
  
  .item-icon {
    margin-right: 10px;
    color: #3d54a7;
    display: flex;
    align-items: center;
  }
  
  .item-icon i {
    font-size: 16px;
  }
  
  .item-arrow {
    position: absolute;
    right: 10px;
    color: #999;
    display: flex;
    align-items: center;
  }
  
  .item-arrow i {
    font-size: 14px;
  }
  
  .recruitment-list li:hover .item-arrow {
    color: #3d54a7;
  }

  .recruitment-list li p {
    margin: 0;
    padding: 0;
    line-height: 1.6;
    font-size: 15px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
    padding-right: 24px;
  }

  /* 响应式设计 */
  @media (max-width: 1024px) {
    .main-content {
      flex-direction: column;
      gap: 20px;
    }

    .carousel,
    .news {
      max-width: 100%;
      height: 300px;
    }
  }

  @media (max-width: 768px) {
    .main-content {
      padding: 0 10px;
    }

    .carousel,
    .news {
      height: 250px;
    }

    .news-item h3 {
      font-size: 14px;
    }

    .news-item .subText {
      font-size: 12px;
    }

    .tabs li {
      font-size: 16px;
      padding: 10px 15px;
    }

    .recruitment-list li p {
      font-size: 14px;
    }
  }
</style>
