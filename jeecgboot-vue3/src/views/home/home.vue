<template>
  <div>

    <!-- 横幅 -->
    <section class="banner">
<!--    <img src="http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg" alt="招聘横幅" />-->
      <img src="http://www.iie.cas.cn/jggk2020/ysfm2020/202302/W020230206556755877695.jpg" alt="招聘横幅" />
    </section>

    <!-- 分类和搜索框 -->
    <section class="filters">
    <div class="categories">
      <button v-for="(category, index) in categories" :key="index" @click="filterCategory(category)">
        {{ category }}
      </button>
    </div>
    <div class="search">
      <input v-model="searchQuery" placeholder="请输入职位名称" />
      <button @click="searchJobs">搜索</button>
    </div>
    </section>

    <!-- 职位列表 -->
    <section class="job-list">
    <div v-for="(job, index) in filteredJobs" :key="index" class="job-card">
      <h3>{{ job.title }}</h3>
      <p>职位数量：<strong>{{ job.count }}</strong></p>
      <p>工作地点：<strong>{{ job.location }}</strong></p>
      <p>薪资：<span class="salary">{{ job.salary }}</span></p>
      <p>{{ job.description }}</p>
    </div>
    </section>
  </div>
</template>

<script setup>
// 无需额外脚本内容
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';


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
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%; /* 确保banner区域有固定高度 */
  height: 600px;
}

.banner img {
  height: 100%;
  width: 70%;
}

/* 添加样式以美化首页 */
h1 {
  color: #42b983;
}
</style>
