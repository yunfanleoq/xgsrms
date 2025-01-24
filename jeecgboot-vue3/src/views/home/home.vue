<template>
  <div>
    <div class="main-content">
      <!-- 轮播图 -->
      <div class="carousel">
        <button class="carousel-button left"
                @click="showPrevImage"
                v-show="isHovered">◀</button>
        <img :src="carouselImages[currentIndex].image"
             alt="轮播图"
             @mouseover="isHovered = true"
             @mouseleave="isHovered = false"/>
        <button class="carousel-button right"
                @click="showNextImage"
                v-show="isHovered">▶</button>
        <!-- 圆点导航 -->
        <div class="carousel-dots">
          <span v-for="(image, index) in carouselImages"
                :key="index"
                :class="['dot', { active: index === currentIndex }]"
                @click="currentIndex = index"></span>
        </div>
      </div>

      <!-- 新闻区域 -->
      <div class="news">
        <div v-for="(news, index) in newsList" :key="index" class="news-item" @click="news.id && goToNewsDetail(String(news.id))">
          <h3 v-html="news.title"></h3>
        </div>
        <!-- 查看更多链接 -->
        <div class="news-more">
          <a class="more-link" :href="'./home/news'">查看更多 +</a>
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
<!--        <ul v-if="tabSelected === 'notice'" class="notice-list">-->
<!--          <li v-for="(item, index) in notices" :key="index">-->
<!--            <span>{{ item.date }}</span>-->
<!--            <a :href="item.link">{{ item.title }}</a>-->
<!--          </li>-->
<!--        </ul>-->
        <ul class="recruitment-list">
          <li v-for="(item, index) in recruitments" :key="index"
              @click="goToAnnouncementDetail(String(item.id))">
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

const carouselImages = ref([
  "http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg",
]);
const currentIndex = ref(0);


// 获取轮播图数据的函数
const fetchCarouselImages = async (page = 1, size = 5) => {
  try {
    const response = await defHttp.get({
      url: listUrl, // 替换为你的轮播图数据接口
      params: { pageNo: 1, pageSize: 1000 },
    });
    console.log(response.records)

    if (response && response.records) {
      carouselImages.value = response.records.map((item) => {
          const imgTag = item.photograph;  // 获取 HTML 字符串
          let imgUrl = "";  // 用于存储图片链接
          // 如果是 HTML 格式的 <img> 标签，提取 src 属性
          if (imgTag && imgTag.includes('<img')) {
            const match = imgTag.match(/src="(.*?)"/);  // 提取 <img> 标签中的 src 链接
            imgUrl = match ? match[1] : "";
          }
          // 如果是纯图片链接，直接使用该链接
          else if (imgTag && !imgTag.includes('<img')) {
            imgUrl = imgTag;
          }
          return {
            image: imgUrl,  // 将图片链接放入 image 字段
            createTime: item.createTime || "",
          };
        })
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()) // 按时间降序排序
        .slice(0, 5); // 截取前5张轮播图
      console.log("处理后的 carouselImages",carouselImages.value)

    }
  } catch (error) {
    console.error("请求轮播图数据失败:", error);
  }
};

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
  { title: "新闻标题", content: "新闻内容" },
]);

// 获取新闻数据接口
// const fetchNews = async (page = 1, size = 5) => {
//   try {
//     const response = await defHttp.get({
//       url: listUrl,
//       params: { pageNo: page, pageSize: 1000 },
//     });
//
//     if (response && response.records) {
//       newsList.value = response.records.map((item: any) => ({
//         title: item.newTitle || "无标题",
//         // content: item.news || "无内容",
//         createTime: item.createTime || "", // 假设服务器返回的时间格式为字符串
//       }))
//       .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()) // 按时间降序排序
//       .slice(0, size); // 截取前5条
//     }
//   } catch (error) {
//     console.error("请求新闻数据失败:", error);
//   }
// };

const fetchNews = async (page = 1, size = 5) => {
  try {
    // 获取首页数据
    const homeResponse = await defHttp.get({
      url: listUrl,
      params: { pageNo: page, pageSize: 1000 },
    });

    // 获取详情表数据
    const journalismResponse = await defHttp.get({
      url: '/xgsJournalism/xgsJournalism/list',
      params: { pageNo: page, pageSize: 1000 },
    });

    console.log("xgs_home 数据:", homeResponse.records);
    console.log("xgs_journalism 数据:", journalismResponse.records);

    if (homeResponse?.records && journalismResponse?.records) {
      const journalismData = journalismResponse.records;

      // 标题标准化函数
      const normalize = (str) => str?.toLowerCase().trim();

      // 将首页数据与详情表关联
      newsList.value = homeResponse.records
        .map((item: any) => {
          // 在详情表中根据标题匹配
          const matchedJournalism = journalismData.find(
            (journalism) =>
              normalize(journalism.journalismHead) === normalize(item.newTitle)
          );

          if (!matchedJournalism) {
            console.warn(`未匹配到详情表的新闻: ${item.newTitle}`);
          }

          return {
            title: item.newTitle,
            id: matchedJournalism?.id || null, // 关联详情表的 id
            createTime: item.createTime || '',
            // content: matchedJournalism?.journalismText, // 可选：用于显示新闻内容摘要
          };
        })
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime()) // 按时间降序排序
        .slice(0, size); // 截取前 `size` 条
    }
  } catch (error) {
    console.error('请求新闻数据失败:', error);
  }
};


// 跳转至头条新闻详情
const goToNewsDetail = (itemId: string) => {
  router.push({ name: 'newsDetail', params: { id: itemId } });
};

// const tabSelected = ref('notice');

// 数据
const recruitments = ref([
  { announcement: "这是招聘公告内容1" }
]); // 初始化为空的招聘公告数组
const tabSelected = ref('recruitment');

// 获取招聘公告标题数据
const fetchRecruitments = async (page = 1, size = 5) => {
  try {
    const response = await defHttp.get({
      url: listUrl,
      params: { pageNo: page, pageSize: 1000 },
    });

    if (response && response.records) {
      recruitments.value = response.records
        .map((item: any) => ({
          id: item.id,
          announcement: item.recruitAnnouncementTitle ,
          createTime: item.createTime , // 假设服务器返回的时间格式为字符串
        }))
        .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())  // 按时间降序排序
        .slice(0, size); // 截取前5条
    }
  } catch (error) {
    console.error("请求招聘公告数据失败:", error);
  }
};

// 跳转至招聘公告详情
const goToAnnouncementDetail = (itemId: string) => {
  router.push({ name: 'announcementDetail', params: { id: itemId } });
};

// 组件挂载时加载招聘公告
onMounted(() => {
  fetchCarouselImages();
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

/* 外部容器，确保轮播图和公告区域宽度一致 */
/*.main-content {*/
/*  display: flex;           !* 使用 Flexbox 布局 *!*/
/*  justify-content: space-between; !* 两侧对齐 *!*/
/*  align-items: stretch;    !* 高度对齐 *!*/
/*  max-width: 1200px;       !* 统一容器宽度 *!*/
/*  margin: 0 auto;          !* 居中对齐 *!*/
/*  padding-left: 20px;      !* 左边距，与导航栏一致 *!*/
/*  padding-right: 20px;     !* 右边距 *!*/
/*  gap: 20px;               !* 轮播图与新闻区域的间距 *!*/
/*  box-sizing: border-box;  !* 包括 padding 在内的宽度计算 *!*/
/*  flex-wrap: nowrap;       !* 确保不换行 *!*/
/*}*/

.main-content {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  max-width: 1200px;
  margin: 0 auto;
  padding-left: 20px; /* 与 announcements-container 左右对齐 */
  padding-right: 20px;
  box-sizing: border-box;
}


.carousel,
.news {
  height: 400px; /* 确保新闻区域与轮播图高度一致 */
}

/* 调整轮播图样式 */
.carousel {
  flex: 3;                  /* 轮播图占比 3 */
  max-width: calc(1200px * 0.75); /* 限制宽度为容器的 75% */
  /*max-width: 800px;         !* 限制最大宽度 *!*/
  height: 400px;            /* 固定高度 */
  margin: 0;                /* 去除多余边距 */
  padding: 0;               /* 去除多余内边距 */
  position: relative;       /* 用于定位按钮 */
  overflow: hidden;         /* 防止溢出 */
}

/* 轮播图图片样式 */
.carousel img {
  display: block;
  width: 100%; /* 或具体宽度 */
  height: auto; /* 确保图片按比例缩放 */
  object-fit: cover;
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

/* 调整新闻区域样式 */
.news {
  flex: 1;                  /* 新闻区域占比 1 */
  /*max-width: 300px;         !* 限制最大宽度 *!*/
  max-width: calc(1200px * 0.25); /* 限制宽度为容器的 25% */
  height: 400px;            /* 固定高度，与轮播图一致 */
  background-color: #3d54a7; /* 背景色 */
  padding: 10px;            /* 内边距 */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  /*border-radius: 10px;      !* 圆角 *!*/
  overflow: hidden;         /* 防止溢出 */
  position: relative;       /* 方便定位“查看更多”按钮 */
}

/* 单个新闻项样式 */
.news-item {
  margin-bottom: 10px;       /* 新闻间距 */
  padding: 5px;
  cursor: pointer;
  transition: background-color 0.3s; /* 添加平滑背景变化效果 */
}

.news-item h3 {
  width: 100%;
  margin: 0;
  color: white;
  font-size: 20px;
  font-weight: bold;
  overflow: hidden;
  text-overflow: ellipsis;  /* 超出部分显示省略号 */
  white-space: nowrap;      /* 强制单行显示 */
  transition: color 0.3s;
}

.news-item:hover h3 {
  color: dodgerblue; /* 鼠标悬停时标题变黑 */
}


/* 查看更多样式 */
.news-more {
  position: absolute;
  bottom: 10px;  /* 距离底部 10px */
  right: 10px;   /* 距离右侧 10px */
}


/* 确保招聘公告导航栏左对齐 */
.announcements-container {
  max-width: 1200px; /* 确保与 main-content 宽度一致 */
  margin: 0 auto;
  padding-left: 20px; /* 与 main-content 左对齐 */
  padding-right: 20px; /* 与 main-content 右对齐 */
  box-sizing: border-box;
}


.tabs-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 10px;
  background-color: #3d54a7; /* 导航栏背景色 */
  /* padding: 0 15px; 内边距 */
  /*border-radius: 5px; !* 圆角样式 *!*/
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

.announcements-content {
  width: 100%;              /* 占满父容器宽度 */
  display: flex;            /* Flex 布局 */
  flex-direction: column;   /* 垂直排列内容 */
  gap: 10px;                /* 子元素间距 */
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

.recruitment-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recruitment-list li {
  cursor: default; /* 默认状态下的鼠标样式 */
  color: #333; /* 默认字体颜色 */
  transition: color 0.3s ease; /* 动态过渡效果 */
  margin: 5px;
}

.recruitment-list li:hover {
  cursor: pointer; /* 鼠标悬停时的鼠标样式 */
  color: #3d54a7; /* 鼠标悬停时的字体颜色 */
}

.recruitment-list li a {
  color: #333;
  text-decoration: none;
}

.recruitment-list li p {
  margin: 0;                    /* 去除上下边距 */
  padding: 3px 0;               /* 设置内边距 */
  line-height: 1.4;             /* 行高 */
  font-size: 16px;              /* 字体大小 */

  display: -webkit-box;         /* 必须使用这个属性来启用多行限制 */
  -webkit-line-clamp: 1;        /* 限制显示1行 */
  -webkit-box-orient: vertical; /* 必须设置的属性，用于设置多行文本的排列方向 */
  overflow: hidden;             /* 超出部分隐藏 */
  text-overflow: ellipsis;      /* 显示省略号 */
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
