<script setup>
import { ref } from 'vue';

const activeMenu = ref("头条新闻");
const menuList = ref([
  {
    name: "头条新闻",
    icon: "icon-    ",
  },
  {
    name: "社会新闻",
    icon: "icon-    ",
  },
  {
    name: "科技新闻",
    icon: "icon-    ",
  },
 ])

const newsList = ref([
  {
    id: 1,
    title: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
    date: "2024-12-06",
    image: "path-to-image-1.jpg",
  },
  {
    id: 2,
    title: "信工所召开党委理论学习中心组专题学习会",
    date: "2024-11-19",
    image: "path-to-image-2.jpg",
  },
  {
    id: 3,
    title: "信工所召开2024年度“信工党校”专题报告会",
    date: "2024-11-19",
    image: "path-to-image-3.jpg",
  },
  {
    id: 4,
    title: "网络空间安全的职业体验活动",
    date: "2024-11-15",
    image: "path-to-image-4.jpg",
  },
  {
    id: 5,
    title: "陈恺研究员入选2024年度国联优秀青年领军者",
    date: "2024-11-14",
    image: "path-to-image-5.jpg",
  },
]);

const setActiveMenu = (menu) => {
  activeMenu.value = menu;
};
</script>

<template>
  <div>
    <!-- 你的模板代码 -->
    <div class="news-list">
      <div v-for="news in newsList" :key="news.id" class="news-item">
        <img :src="news.image" :alt="news.title" class="news-image">
        <h3>{{ news.title }}</h3>
        <p>{{ news.date }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 你的样式代码 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.news-item {
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 5px;
}

.news-image {
  width: 100%;
  height: auto;
  border-radius: 5px;
}
</style>
