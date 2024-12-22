<template>
  <div>

<!--    &lt;!&ndash; 横幅 &ndash;&gt;-->
<!--    <section class="banner">-->
<!--    <img src="https://via.placeholder.com/1200x300" alt="招聘横幅" />-->
<!--    </section>-->

    <!-- 分类和搜索框 -->
    <section class="filters">
    <div class="categories">
      <button
        v-for="(dept, index) in depts"
        :key="index"
        @click="filterDept(dept)"
        class="category-button"
      >
        {{ dept }}
      </button>
    </div>

    <div class="search">
      <input v-model="searchQuery" placeholder="请输入职位名称" />
      <button @click="resetDept">重置</button>
      <button @click="searchJobs">搜索</button>
    </div>
    </section>

    <!-- 职位列表 -->
    <section class="job-list">
      <div v-for="(job, index) in paginatedJobs" :key="index" class="job-card" @click="goToJobDetail(job.id)">
        <h3>{{ job.positionName }}</h3>
        <p>职位数量：<strong>{{ job.personCount }}</strong></p>
        <p>招聘部门：<strong>{{ job.dept_dictText }}</strong></p>
        <p>工作年限：<span class="salary">{{ job.workYears }}</span></p>
        <p>招聘状态：<span class="status-filter">{{ job.status }}</span></p>
        <p><span>{{ job.dept_dictText }} </span></p>
      </div>
    </section>
    <!-- 分页控件 -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
      <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
    </div>

    <!-- 每页显示条数选择 -->
    <div class="jobs-per-page">
      <label for="jobs-per-page">每页显示：</label>
      <select id="jobs-per-page" v-model="jobsPerPage" @change="changeJobsPerPage">
        <option value=5>5</option>
        <option value=10>10</option>
        <option value=20>20</option>
        <option value=50>50</option>
        <option value=100>100</option>
      </select>
    </div>
  </div>
</template>

<script setup  lang="ts" name="positions">
// 无需额外脚本内容
import {ref, computed, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import {defHttp}   from "../../utils/http/axios";


const goToJobDetail = (jobId: number) => {
  router.push({ name: 'JobDetail', params: { id: jobId } });
};


const searchQuery = ref("");
const selectedCategory = ref(null);
const selectedDept = ref('');

const  jobListUrl = '/positions/xgsPositions/list'
const  deptListUrl = '/sys/sysDepart/listAll'


const getJobList = (params) => defHttp.get({ url:jobListUrl, params }, { isTransformResponse: false });
const getDeptList = (params) => defHttp.get({ url:deptListUrl, params }, { isTransformResponse: false });

const statusFilter = ref('招聘中'); // 定义状态过滤参数，默认为空

const depts = ref([
  "密码理论与技术研究室",
  "体系结构安全研究室",
  "数据安全研究室"

]);

// 分页相关的响应式数据
const currentPage = ref(1);
const jobsPerPage = ref(10); // 每页显示的条数

// 计算分页后的职位列表
const paginatedJobs = computed(() => {
  console.log('Current Page:', currentPage.value);
  console.log('Jobs Per Page:', jobsPerPage.value);
  const start = (currentPage.value - 1) * jobsPerPage.value;
  const end = start + jobsPerPage.value;
  console.log('Start Index:', start, 'End Index:', end);
  const result = filteredJobs.value.slice(start, end);
  console.log('Paginated Jobs:', result);
  return result;
});


// 计算总页数
const totalPages = computed(() => {
  const total = Math.ceil(filteredJobs.value.length / jobsPerPage.value);
  console.log('Total Pages:', total);
  return total;
});

// 分页控制函数
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const changeJobsPerPage = (event: Event) => {
  const value = (event.target as HTMLSelectElement).value;
  console.log('Changing Jobs Per Page to:', value);
  jobsPerPage.value = parseInt(value, 10);
  currentPage.value = 1; // 重置到第一页
};

const jobs = ref([
  {
    id: "1870026492048125964",
    sysOrgCode: "A01",
    dept: "1870023309355724801",
    ktz: "1870023433565843457",
    telphone: "010-82345271",
    email: "mima@iie.ac.cn",
    positionName: "工程师",
    researchDirection: "安全测评，密码开发",
    personCount: "1",
    duty: "安全测评工作，安全系统研发",
    xlxw: "硕士研究生及以上",
    professional: "计算机、电子信息、网络空间安全相关专业",
    workYears: "2年及以上",
    memo: null,
    status: "招聘中",
    dept_dictText: "密码理论与技术研究室",
    ktz_dictText: "密码测评研究组",
    status_dictText: "招聘中",
  },

]);

// const filteredJobs = computed(() => {
//   if (selectedDept.value) {
//     return jobs.value.filter((job) => job.dept_dictText === selectedDept.value);
//   } else if (searchQuery.value !== "") {
//     return jobs.value.filter(
//       (job) =>
//         job.positionName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
//         job.dept_dictText.toLowerCase().includes(searchQuery.value.toLowerCase()),
//     );
//   }
//
// });

const fetchJobs = () => {

  const params = {
    status: statusFilter.value, // 将状态参数添加到请求参数中
    pageNo: 1,
    pageSize: 100
  };


  getJobList(params)
    .then((res) => {
      if (res.success) {
        let list = res.result.records;
        // jobs.value = underlineToCamelCase(list);
        jobs.value = list;
        console.log('getJobList>>>>>>>>>>>',list,depts.value)
      }
    })
};

const resetDept = () => {
  selectedDept.value = null;
};

const fetchDepts = () => {
  console.log('fetchDepts>>>>>>>>>>>BEGIN')
  const params = {
    orgType: 1 // 只获取一级部门
  };

  getDeptList(params)
    .then((res) => {
      if (res.success) {
        console.log('fetchDepts>>>>>>>>>>>',res)
        let list = res.result;
        // 提取 list 中的 dept 字段形成数组，并赋值给 depts.value
        list = list.map((item) => item.departName);

        depts.value = list;

        console.log('getDeptList>>>>>>>>>>>',list,depts.value)
      }
    })
};

onMounted(fetchDepts );
onMounted(fetchJobs );
//
// 响应式数据
// const categories = ref(["Java开发", "C++开发", "前端开发", "后端开发", "大数据开发", "测试开发", "PHP开发"]);
// 响应式数据




// 计算属性：过滤后的职位列表
const filteredJobs = computed(() => {
  let filtered = jobs.value;

  // if (selectedCategory.value) {
  //   filtered = filtered.filter((job) => job.category === selectedCategory.value);
  // }

  if (searchQuery.value) {
    filtered = filtered.filter(
      (job) =>
        job.positionName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        job.dept_dictText.toLowerCase().includes(searchQuery.value.toLowerCase()),
    );
  }

  if (selectedDept.value) {
    filtered = filtered.filter((job) => job.dept_dictText === selectedDept.value);
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

.status-filter {
  /*display: flex;*/
  /*justify-content: space-between;*/
  /*align-items: center;*/
  /*padding: 20px;*/
  /*background-color: white;*/
  /*border-radius: 10px;*/
  /*box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);*/
  /*margin: 20px;*/
  font-size: 16px;
  font-weight: bold;
  color: #2e9232;
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

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 10px; /* 按钮之间的间距 */
}

.category-button {
  padding: 8px 16px; /* 按钮内边距 */
  background-color: #007bff; /* 背景颜色 */
  color: white; /* 文字颜色 */
  border: none; /* 去掉边框 */
  border-radius: 4px; /* 圆角 */
  cursor: pointer; /* 鼠标悬停时显示为指针 */
  transition: background-color 0.3s; /* 背景颜色过渡效果 */
}

.category-button:hover {
  background-color: #0056b3; /* 悬停时的背景颜色 */
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
  background-color: #1c4997;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.filters .search button:hover {
  background-color: #a52542;
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
  color: #17449e;
  font-weight: bold;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
}

.jobs-per-page {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}

.jobs-per-page label {
  margin-right: 10px;
}

.jobs-per-page select {
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
</style>
