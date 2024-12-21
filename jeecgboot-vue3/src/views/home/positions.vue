<template>
  <div>

<!--    &lt;!&ndash; 横幅 &ndash;&gt;-->
<!--    <section class="banner">-->
<!--    <img src="https://via.placeholder.com/1200x300" alt="招聘横幅" />-->
<!--    </section>-->

    <!-- 分类和搜索框 -->
    <section class="filters">
    <div class="categories">
      <button v-for="(dept, index) in depts" :key="index" @click="filterDept(dept)">
        {{ dept }}
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
      <p>{{ job.dept }}</p>
    </div>
    </section>
  </div>
</template>

<script setup>
// 无需额外脚本内容
import {ref, computed, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import {defHttp}   from "../../utils/http/axios";


const searchQuery = ref("");
const selectedCategory = ref(null);
const selectedDept = ref('');

  // /online/cgformList/a03837d0fbfc4c49a3392672d3bdc570
    // jobs = '/online/cgform/api/getData/a03837d0fbfc4c49a3392672d3bdc570?hasQuery=true&column=slot_number&order=asc&pageNo=1&pageSize=100',
const  jobListUrl = '/online/cgform/api/getData/a03837d0fbfc4c49a3392672d3bdc570?hasQuery=true&column=status&order=asc&pageNo=1&pageSize=100'
    // batChargingList = '/online/cgreport/api/getData/1855806234161901570?hasQuery=true&pageNo=1&pageSize=100',


const getJobList = (params) => defHttp.get({ url:jobListUrl, params }, { isTransformResponse: false });
 // const batChargingList = (params) => defHttp.get({ url: Api.batChargingList, params }, { isTransformResponse: false });
const statusFilter = ref('招聘中'); // 定义状态过滤参数，默认为空

const depts = ref([
  "密码理论与技术研究室",
  "体系结构安全研究室",
  "体系结构安全研究室",
  "体系结构安全研究室",
  "体系结构安全研究室",
  "体系结构安全研究室",
  "体系结构安全研究室",
  "数据安全研究室",
  "数据安全研究室",
  "数据安全研究室",
  "数据安全研究室"
]);

const getRandomDept = () => {
  const randomIndex = Math.floor(Math.random() * depts.value.length);
  return depts.value[randomIndex];
};


const jobs = ref([
  { title: "Java工程师", count: 336, location: "北京市", salary: "7000-12000", description: "熟悉RPC框架，具备分布式开发经验", category: "Java开发", dept: getRandomDept() },
  { title: "C++工程师", count: 13, location: "南京市", salary: "5000-8000", description: "可独立编写驱动程序", category: "C++开发", dept: getRandomDept() },
  { title: "PHP工程师", count: 307, location: "北京市", salary: "7000-12000", description: "熟悉PHP框架，如Laravel等", category: "PHP开发", dept: getRandomDept() },
  { title: "Python开发", count: 178, location: "天津市", salary: "14000-16000", description: "熟悉AI框架，算法优先", category: "大数据开发", dept: getRandomDept() },
  { title: "前端开发", count: 215, location: "杭州市", salary: "15000-20000", description: "熟悉Vue.js框架", category: "前端开发", dept: getRandomDept() },
]);



const fetchJobs = () => {

  const params = {
    status: statusFilter.value // 将状态参数添加到请求参数中
  };

  getJobList(params)
    .then((res) => {
      if (res.success) {
        let list = res.result.records;
        // jobs.value = underlineToCamelCase(list);
        jobs.value = list;

      }
    })
};

onMounted(fetchJobs);
//
// 响应式数据
const categories = ref(["Java开发", "C++开发", "前端开发", "后端开发", "大数据开发", "测试开发", "PHP开发"]);
// 响应式数据




// 计算属性：过滤后的职位列表
const filteredJobs = computed(() => {
  let filtered = jobs.value;

  if (selectedCategory.value) {
    filtered = filtered.filter((job) => job.category === selectedCategory.value);
  }

  if (searchQuery.value) {
    filtered = filtered.filter((job) => job.title.includes(searchQuery.value));
  }

  if (selectedDept.value) {
    filtered = filtered.filter((job) => job.dept === selectedDept.value);
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

// 根据dept过滤
const filterDept = (dept) => {
  console.log("过滤部门：" + dept);
  selectedDept.value = dept === selectedDept.value ? null : dept;
};
// 搜索职位
const searchJobs = () => {
  console.log("搜索职位：" + searchQuery.value);
};
</script>

<style scoped>
/* 添加样式以美化首页 */
h1 {
  color: #42b983;
}
</style>
