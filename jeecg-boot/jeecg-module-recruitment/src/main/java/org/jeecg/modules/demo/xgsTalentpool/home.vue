<template> 
  <div>

    <!-- 横幅 -->
    <!-- <section class="banner"> -->
<!--    <img src="http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg" alt="招聘横幅" />-->
      <!-- <img src="http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg" alt="招聘横幅" /> -->
    <!-- </section> -->

  <div class="main-content">
    <!-- 轮播图 -->
    <div class="carousel">
      <button
        class="carousel-button left"
        @click="showPrevImage"
        v-show="isHovered">◀</button>
      <img
        :src="carouselImages[currentIndex]"
        alt="轮播图"
        @mouseover="isHovered = true"
        @mouseleave="isHovered = false"/>
      <button
        class="carousel-button right"
        @click="showNextImage"
        v-show="isHovered">▶</button>
      <!-- 圆点导航 -->
      <div class="carousel-dots">
        <span 
          v-for="(image, index) in carouselImages"
          :key="index"
          :class="['dot', { active: index === currentIndex }]"
          @click="currentIndex = index"></span>
      </div>
      
    </div>

    <!-- 新闻区域 -->
    <div class="news">
      <div v-for="(news, index) in newsList" :key="index" class="news-item">
        <h3 v-html="news.title"></h3>
        <p v-html="news.content"></p>
      </div>
    </div>
  </div>

  <!-- 公告导航栏和内容 -->
  <div class="announcements-container">
    <div class="tabs-container">
      <ul class="tabs">
        <!-- <li :class="{ selected: tabSelected === 'notice' }" @mouseover="tabSelected = 'notice'">通知公告</li> -->
        <li :class="{ selected: tabSelected === 'recruitment' }" @mouseover="tabSelected = 'recruitment'">招聘公告</li>
      </ul>
      <!-- <a class="more-link" :href="tabSelected === 'notice' ? './home/news' : './home/positions'">查看更多 +</a> -->
      <a class="more-link" :href="'./home/positions'">查看更多 +</a>
    </div>
    <div class="announcements-content">
      <ul v-if="tabSelected === 'notice'" class="notice-list">
        <li v-for="(item, index) in notices" :key="index">
          <span>{{ item.date }}</span>
            <a :href="item.link">{{ item.title }}</a>
        </li>
      </ul>
      <ul v-else class="recruitment-list">
        <li v-for="(item, index) in recruitments" :key="index">
          <!-- <span>{{ item.publishDate }}</span> -->
          <!-- <a :href="item.link">{{ item.title }}</a> -->
          <p v-html="item.announcement"></p>
         </li>
      </ul>
    </div>
  </div>


<!--    <div style="height: 500px">-->


<!--    </div>-->
    <!-- 分类和搜索框 -->
<!--    <section class="filters">-->
<!--    <div class="categories">-->
<!--      <button v-for="(category, index) in categories" :key="index" @click="filterCategory(category)">-->
<!--        {{ category }}-->
<!--      </button>-->
<!--    </div>-->
<!--    <div class="search">-->
<!--      <input v-model="searchQuery" placeholder="请输入职位名称" />-->
<!--      <button @click="searchJobs">搜索</button>-->
<!--    </div>-->
<!--    </section>-->

    <!-- 职位列表 -->
<!--    <section class="job-list">-->
<!--    <div v-for="(job, index) in filteredJobs" :key="index" class="job-card">-->
<!--      <h3>{{ job.title }}</h3>-->
<!--      <p>职位数量：<strong>{{ job.count }}</strong></p>-->
<!--      <p>工作地点：<strong>{{ job.location }}</strong></p>-->
<!--      <p>薪资：<span class="salary">{{ job.salary }}</span></p>-->
<!--      <p>{{ job.description }}</p>-->
<!--    </div>-->
<!--    </section>-->
  </div>
</template>

<script setup lang="ts">
// 无需额外脚本内容
import { ref, computed ,onMounted} from 'vue';
import { useRouter } from 'vue-router';
import Positions from './positions.vue';
import {defHttp} from "@/utils/http/axios";

const listUrl = "/xgsHome/xgsHome/list"

// 轮播图数据
const carouselImages = ref([
  "http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg",
  "http://www.iie.cas.cn/xwdt2020/ttxw2020/202412/W020241227741553185612.jpg",
  "http://www.iie.cas.cn/xwdt2020/ttxw2020/202411/W020241119655225033945.JPG",
]);

const currentIndex = ref(0);

// 自动轮播
setInterval(() => {
  currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
}, 3000);

// 控制轮播图按钮的显隐状态
const isHovered = ref(false);

// 轮播图按钮切换逻辑
const showPrevImage = () => {
  currentIndex.value = (currentIndex.value - 1 + carouselImages.value.length) % carouselImages.value.length;
};

const showNextImage = () => {
  currentIndex.value = (currentIndex.value + 1) % carouselImages.value.length;
};


// 新闻列表数据
const newsList = ref([
  { title: "", content: "" },
]);

// 获取新闻数据接口
const fetchNews = async (page = 1, size = 2) => {
  try {
    const response = await defHttp.get({
      url: listUrl,  
      params: { pageNo: page, pageSize: size },
    });

    if (response && response.records) {
      newsList.value = response.records
        .map((item: any) => ({
          title: item.newTitle || "无标题",
          content: item.news || "无内容",
          createTime: item.createTime || "", // 假设服务器返回的时间格式为字符串
        }))
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()); // 按时间降序排序
    }
  } catch (error) {
    console.error("请求新闻数据失败:", error);
  }
};


// const tabSelected = ref('notice');

// 数据
const recruitments = ref([
  { announcement: "这是招聘公告内容1" }
]); // 初始化为空的招聘公告数组
const tabSelected = ref('recruitment');

// 获取招聘公告数据
const fetchRecruitments = async (page = 1, size = 3) => {
  try {
    const response = await defHttp.get({
      url: listUrl,
      params: { pageNo: page, pageSize: size }, 
    });

    if (response && response.records) {
      recruitments.value = response.records
        .map((item: any) => ({
          announcement: item.recruitAnnouncement || "暂无内容",
          createTime: item.createTime || "", // 假设服务器返回的时间格式为字符串
        }))
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()); // 按时间降序排序
    }
  } catch (error) {
    console.error("请求招聘公告数据失败:", error);
  }
};

// 组件挂载时加载招聘公告
onMounted(() => {
  fetchRecruitments();
  fetchNews();
});

// 响应式数据
const categories = ref(["Java开发", "C++开发", "前端开发", "后端开发", "大数据开发", "测试开发", "PHP开发"]);
const jobs = ref([
  { title: "Java工程师", count: 336, location: "北京市", salary: "7000-12000", description: "熟悉RPC框架，具备分布式开发经验", category: "Java开发" },
  { title: "C++工程师", count: 13, location: "南京市", salary: "5000-8000", description: "可独立编写驱动程序", category: "C++开发" },
  { title: "PHP工程师", count: 307, location: "北京市", salary: "7000-12000", description: "熟悉PHP框架，如Laravel等", category: "PHP开发" },
  { title: "Python开发", count: 178, location: "天津市", salary: "14000-16000", description: "熟悉AI框架，算法优先", category: "大数据开发" },
  { title: "前端开发", count: 215, location: "杭州市", salary: "15000-20000", description: "熟悉Vue.js框架", category: "前端开发" },
]);

const searchQuery = ref("");
const selectedCategory = ref(null);

// 计算属性：过滤后的职位列表
const filteredJobs = computed(() => {
  let filtered = jobs.value;

  if (selectedCategory.value) {
    filtered = filtered.filter((job) => job.category === selectedCategory.value);
  }

  if (searchQuery.value) {
    filtered = filtered.filter((job) => job.title.includes(searchQuery.value));
  }

  return filtered;
});

// 路由操作
const router = useRouter();
const goToLoginPage = () => {
  router.push('/login');
};

// 分类过滤
const filterCategory = (category) => {
  selectedCategory.value = category === selectedCategory.value ? null : category;
};

// 搜索职位
const searchJobs = () => {
  console.log("搜索职位：" + searchQuery.value);
};
</script>

<style scoped>

.banner {
  /*display: flex;*/
  justify-content: center;
  /*align-items: center;*/
  /*width: 100%; !* 确保banner区域有固定高度 *!*/
  height: 40%;
  margin: 10px;
  padding: 20px;

}

.banner img {
  height: 70%;
  width: 50%;
}

/* 添加样式以美化首页 */
h1 {
  color: #42b983;
}
h2 {
  color: #3d54a7;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  font-size: 20px;
  cursor: pointer;
  font-weight: bold;
}

.main-content {
  display: flex;
  justify-content: center;  /* 居中对齐 */
  align-items: flex-start;  /* 保证顶部对齐 */
  width: 100%; 
  max-width: 1300px; /* 限制容器最大宽度 */
  margin: 20px auto; 
  padding: 0 20px; /* 两侧留足间隙 */
  flex-wrap: nowrap; /* 确保不换行 */
}

.carousel,
.news {
  height: 400px; /* 确保新闻区域与轮播图高度一致 */
}


/* 轮播图区域样式 */
.carousel {
  width: 1000px; /* 固定宽度 */
  max-width: 1000px; /* 限制最大宽度 */
  margin-right: 40px; /* 轮播图与新闻区域之间的间距 */
  position: relative; /* 用于定位按钮 */
  overflow: hidden;
}

/* 轮播图图片样式 */
.carousel img {
  width: 1000px;    /* 填充轮播图容器 */
  height: 400px;
  object-fit: cover; /* 确保图片比例不失真 */
  border-radius: 10px; /* 图片圆角 */
}

/* 轮播图按钮样式 */
.carousel-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
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
  z-index: 1;
  visibility: hidden; /* 默认隐藏 */
  pointer-events: none; /* 不允许点击 */
  transition: visibility 0.3s, opacity 0.3s;
}

.carousel-button.left {
  left: 10px;
}

.carousel-button.right {
  right: 10px;
}

/* 鼠标悬停时显示按钮 */
.carousel:hover .carousel-button {
  visibility: visible; /* 显示按钮 */
  pointer-events: auto; /* 允许点击 */
}

/* 圆点导航容器 */
.carousel-dots {
  position: absolute;
  bottom: 10px;
  right: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 圆点样式 */
.dot {
  width: 10px;
  height: 10px;
  background-color: white;
  border-radius: 50%;
  margin: 0 5px;
  cursor: pointer;
  transition: width 0.3s, border-radius 0.3s;
}

/* 激活的圆点样式 */
.dot.active {
  width: 20px;
  border-radius: 50px; /* 长椭圆效果 */
  background-color: #3d54a7; /* 激活状态下的颜色 */
}

/* 鼠标悬停时圆点变长椭圆 */
.dot:hover {
  width: 20px;
  border-radius: 50px;
  background-color: #3d54a7; /* 鼠标悬停时颜色 */
}


/* 新闻板块样式 */
.news {
  width: 300px; /* 固定宽度 */
  max-width: 300px; /* 确保不超过 300px */
  height: 400px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  box-sizing: border-box; /* 确保padding和边框不会影响尺寸 */
  overflow: hidden; /* 防止溢出 */
}

.news, .news-item {
  box-sizing: border-box;
}

.news-item {
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

/* 去掉最后一条新闻的分隔线 */
.news-item:last-child {
  border-bottom: none;
}

.news-item h3 {
  width: 100%;  /* 确保标题宽度与父容器对齐 */
  margin: 0;
  color: #3d54a7;
  font-size: 18px;
  font-weight: bold;
  word-wrap: break-word;   /* 自动换行 */
  word-break: break-word;  /* 强制换行 */
}

.news-item p {
  margin: 5px 0 0;
  color: #111;
  white-space: normal;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 显示最多3行 */
  -webkit-box-orient: vertical;
  width: 100%;  /* 改为100%以适应父容器 */
  height: auto; /* 自动调整高度 */
}


/* ----- */
.announcements-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 左对齐 */
  width: 940px; 
  max-width: 1000px; /* 限制最大宽度 */
  margin-top: 20px; /* 距离轮播图的间距 */
  margin-left: 195px; /* 与左侧轮播图对齐 */
}

.tabs-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 10px;
  background-color: #3d54a7; /* 导航栏背景色 */
  /* padding: 0 15px; 内边距 */
  border-radius: 5px; /* 圆角样式 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}


.tabs {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.tabs li {
  margin-right: 0;
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
  color: #fff; /* 默认字体颜色 */
  transition: color 0.3s, background-color 0.3s; /* 动态效果 */
  padding: 10px 15px; /* 内边距 */
  border-radius: 3px; /* 圆角 */
}

/* .tabs li:hover { */
  /* background-color: #ffffff;  */
  /* 鼠标悬停时背景色变白 */
  /* color: #453d3d;  */
  /* 鼠标悬停时字体变黑 */
/* } */

/* .tabs li.selected { */
  /* color: #453d3d; 选中项字体颜色 */
  /* background-color: #fff; 选中项背景色 */
/* } */


.more-link {
  font-size: 14px;
  color: #fff;
  text-decoration: none;
  cursor: pointer;
  padding: 0 10px;
}

.announcements-content {
  width: 100%;
}

.notice-list,
.recruitment-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-list li,
.recruitment-list li {
  margin-bottom: 10px;
}

.notice-list li a,
.recruitment-list li a {
  color: #333;
  text-decoration: none;
}

.notice-list li span,
.recruitment-list li span {
  margin-right: 10px;
  font-size: 14px;
  color: #666;
}
/*--------*/

/* 分类和搜索 */
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin: 20px;
}

.filters .categories button {
  margin-right: 10px;
  padding: 8px 15px;
  border-radius: 20px;
  border: none;
  background-color: #eef2f7;
  color: #6a11cb;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.filters .categories button:hover {
  background-color: #6a11cb;
  color: white;
}

.filters .search input {
  padding: 10px;
  border-radius: 20px;
  border: 1px solid #ddd;
  width: 250px;
  margin-right: 10px;
}

.filters .search button {
  padding: 10px 20px;
  background-color: #ff7eb3;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.filters .search button:hover {
  background-color: #ff5177;
}

/* 职位列表 */
.job-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.job-card {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: transform 0.2s;
}

.job-card:hover {
  transform: translateY(-5px);
}

.job-card h3 {
  font-size: 20px;
  color: #6a11cb;
}

.job-card .salary {
  color: #ff5177;
  font-weight: bold;
}
</style>