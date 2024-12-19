<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header>
      <div class="logo">青春招聘系统</div>
      <nav>
        <a href="#">首页</a>
        <a href="#">研究所况</a>
        <a href="#">科研发展</a>
        <a href="#">招聘信息</a>
        <a href="#">联系我们</a>
        <button  @click="goToLoginPage" class="login"> 登录 </button>
        <button class="register">注册</button>
      </nav>
    </header>

    <!-- 横幅 -->
    <section class="banner">
      <img src="https://via.placeholder.com/1200x300" alt="招聘横幅" />
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

<script>
// import { createRouter, createWebHistory } from 'vue-router';
// import Login from '../views/login/Login.vue';
//
// const routes = [
//   {
//     path: '/login',
//     name: 'Login',
//     component: Login
//   },
//   // 其他路由配置
// ];
//
// const router = createRouter({
//   history: createWebHistory(),
//   routes
// });
//
// export default router;

export default {
  data() {
    return {
      categories: ["Java开发", "C++开发", "前端开发", "后端开发", "大数据开发", "测试开发", "PHP开发"],
      jobs: [
        { title: "Java工程师", count: 336, location: "北京市", salary: "7000-12000", description: "熟悉RPC框架，具备分布式开发经验", category: "Java开发" },
        { title: "C++工程师", count: 13, location: "南京市", salary: "5000-8000", description: "可独立编写驱动程序", category: "C++开发" },
        { title: "PHP工程师", count: 307, location: "北京市", salary: "7000-12000", description: "熟悉PHP框架，如Laravel等", category: "PHP开发" },
        { title: "Python开发", count: 178, location: "天津市", salary: "14000-16000", description: "熟悉AI框架，算法优先", category: "大数据开发" },
        { title: "前端开发", count: 215, location: "杭州市", salary: "15000-20000", description: "熟悉Vue.js框架", category: "前端开发" },
      ],
      searchQuery: "",
      selectedCategory: null,
    };
  },
  computed: {
    filteredJobs() {
      let jobs = this.jobs;

      if (this.selectedCategory) {
        jobs = jobs.filter((job) => job.category === this.selectedCategory);
      }

      if (this.searchQuery) {
        jobs = jobs.filter((job) => job.title.includes(this.searchQuery));
      }

      return jobs;
    },
  },
  methods: {
    goToLoginPage() {
      this.$router.push('/login');
    },
    filterCategory(category) {
      this.selectedCategory = category === this.selectedCategory ? null : category;
    },
    searchJobs() {
      console.log("搜索职位：" + this.searchQuery);
    },
  },
};
</script>

<style>
/* 全局样式 */
body {
  margin: 0;
  font-family: 'Poppins', sans-serif;
  background-color: #f7f9fc;
}

#app {
  width: 100%;
}

/* 顶部导航栏 */
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #6a11cb, #2575fc);
  color: white;
  padding: 10px 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

header .logo {
  font-size: 24px;
  font-weight: bold;
}

header nav {
  display: flex;
  align-items: center;
}

header nav a {
  color: white;
  margin: 0 15px;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
}

header nav .login,
header nav .register {
  margin-left: 15px;
  padding: 5px 15px;
  border-radius: 5px;
  cursor: pointer;
  border: none;
  font-size: 14px;
}

header nav .login {
  background-color: white;
  color: #6a11cb;
}

header nav .register {
  background-color: #ff7eb3;
  color: white;
}

/* 横幅 */
.banner img {
  width: 100%;
  border-radius: 10px;
  margin: 20px 0;
}

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
