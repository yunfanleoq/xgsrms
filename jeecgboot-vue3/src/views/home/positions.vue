<template>
  <div id="app" class = 'positionContainer'>
      <aside class="sidebar">
       <ul >
        <li  v-for="(category, index) in jobCategories"
             :key="index"
             @click="filterJobCategory(category)"
             :class="{ active: selectedCategory === category }">

          {{ category }}
        </li>
      </ul>
      </aside>
      <main class='content'>
        <div>
        <section class="filters">
          <div class="categories">
            <button
              v-for="(dept, index) in depts"
              :key="index"
              @click="filterDept(dept)"
              class="dept-button"
            >
              {{ dept }}
            </button>
          </div>
          <div class="search-container">
            <input v-model="searchQuery" placeholder="请输入职位名称" class="search-input" />
            <div class="search-buttons">
              <button @click="reset" class="search-button">重置</button>
              <button @click="searchJobs" class="search-button">搜索</button>
            </div>
          </div>
        </section>

              <div class="job-list-container">
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
                  <!--        <div class="jobs-per-page">-->
                  <label for="jobs-per-page">每页：</label>
                  <select id="jobs-per-page" v-model.number ="jobsPerPage" @change="changeJobsPerPage">
                    <option value=5>5</option>
                    <option value=10>10</option>
                    <option value=20>20</option>
                    <option value=50>50</option>
                    <option value=100>100</option>
                  </select>
                  <!--        </div>-->
                </div>
              </div>


      </div>
      </main>
  </div>

</template>

<script setup  lang="ts" name="positions">
// 无需额外脚本内容
import {ref, computed, onMounted, reactive} from 'vue';
import { useRouter } from 'vue-router';
import {xgsFavoriteJobList} from "@/api/xgsrms/positions";

import { getDictItems,getJobList,getDeptList } from "@/api/xgsrms/home";

// const xgsFavoriteJobList = ref([]);
// xgsFavoriteJobList({userId: userStore.userInfo.username}).then(res => {
//   // console.log('xgsFavoriteJobList', res);
//   if (res.result.records.length > 0) {
//     xgsFavoriteJobList.value = res.result.records;
//     console.log('xgsFavoriteJobList', xgsFavoriteJobList.value);
//   }
//
// });
//
// onMounted(xgsFavoriteJobList);

const jobCategories = ref( []);
//
const goToJobDetail = (jobId: number) => {
  router.push({ name: 'JobDetail', params: { id: jobId } });
};

const filterJobCategory = (category) =>{
  // 根据选择的岗位分类进行过滤的逻辑
  console.log('Selected job category:', category);
  selectedCategory.value = category;
  // 这里可以添加具体的过滤逻辑
};

const searchQuery = ref("");
const selectedCategory = ref(null);
const selectedDept = ref('');

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
  // 遍历结果，为每个职位添加一个isFavorite属性
  // result.forEach((job) => {
  //   // job.isFavorite = xgsFavoriteJobList.value.some((favJob) => favJob.positionId === job.id);
  //   pass;
  // });
  console.log('>>>>>Paginated Jobs:', result);
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
    category: "普通岗位",
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
  console.log('fetchJobs>>>>>>>>>>>BEGIN')
  const params = {
    status: statusFilter.value, // 将状态参数添加到请求参数中
    pageNo: 1,
    pageSize: 1000
  };


  getJobList(params)
    .then((res) => {
      if (res.success) {
        let list = res.result.records;
        // jobs.value = underlineToCamelCase(list);
        // 只获取需要的字段，并赋值给 jobs.value
        list = list.map((item) => {
          return {
            id: item.id,
            // sysOrgCode: item.sysOrgCode,
            category: item.category,
            dept: item.dept,
            ktz: item.ktz,
            // telphone: item.telphone,
            // email: item.email,
            positionName: item.positionName,
            // researchDirection: item.researchDirection,
            personCount: item.personCount,
            // duty: item.duty,
            // xlxw: item.xlxw,
            // professional: item.professional,
            workYears: item.workYears,
            // memo: item.memo,
            status: item.status,
            dept_dictText: item.dept_dictText,
            ktz_dictText: item.ktz_dictText,
            status_dictText: item.status_dictText,
          }
        })
        jobs.value = list;
        console.log('getJobList>>>>>>>>>>>',list,depts.value)
      }
    })
};

const reset = () => {
  selectedDept.value = null;
  searchQuery.value = "";
  selectedCategory.value = null;
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

const fetchCategorys = () => {
  console.log('fetchCategorys>>>>>>>>>>>BEGIN')
  const params = {
    dictCode: '岗位分类' // 只获取一级部门
  };
  getDictItems(params)
    .then((res) => {
      if (res.success) {
        console.log('getDictItems>>>>>>>>>>>',res)
        let list = res.result;
        // 提取 list 中的 dept 字段形成数组，并赋值给 depts.value
        list = list.map((item) => item.title);

        jobCategories.value = list;

        console.log('fetchCategorys>>>>>>>>>>>',list,jobCategories.value)
      }
  })
};


onMounted(fetchDepts );
onMounted(fetchJobs );
onMounted(fetchCategorys );



// 计算属性：过滤后的职位列表
const filteredJobs = computed(() => {
  let filtered = jobs.value;

  if (selectedCategory.value) {
    filtered = filtered.filter((job) => job.category === selectedCategory.value);
  }

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

.positionContainer {
  display: flex;
  /*height: 100vh; !* 填充整个视口高度 *!*/
}

.job-categories {
  /*width: 20vw; !* 设置宽度为视口宽度的 20% *!*/
  padding: 20px; /* 可选：添加内边距 */
  background-color: #f9f9f9; /* 可选：添加背景颜色 */
  border-right: 1px solid #ddd; /* 可选：添加右侧边框 */
}

.category-button {
  display: block; /* 可选：使按钮垂直排列 */
  width: 100%; /* 可选：使按钮宽度占满容器 */
  margin-bottom: 10px; /* 可选：添加按钮之间的间距 */
}
.dept-button {
  display: flex;
}

.job-list-container {
  /*width: 80vw; !* 设置宽度为视口宽度的 80% *!*/
  /*flex  : 1 1 auto;*/
  padding: 20px;
}

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
  flex: 1 1 auto ;
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
  justify-content: space-between;

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

#app {
  display: flex;
  flex-direction: row; /* 设置为行方向 */
  flex: 1;
  padding: 20px;
  /*flex-direction: column;*/
  font-family: Arial, sans-serif;
  background-color: #f4f4f9;
  color: #333;
}

main {
  flex-grow: 1; /* 让 main 占据剩余空间 */
  padding: 15px; /* 可选：设置内边距 */
}

.sidebar {
  width: 250px;
  background-color: #fff;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-right: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin: 10px 0;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sidebar li:hover {
  background-color: #f0f0f0;
}

.sidebar li.active {
  background-color: #3d54a7;
  color: #fff;
}

.content {
  flex: 1;
  background-color: #fff;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.content h2 {
  margin-bottom: 20px;
  color: #3d54a7;
}

.search-container {
  display: flex;
  justify-content: space-between;
  flex-direction: column;
  /*padding: 10px;*/
  align-items: center;
  /*margin-bottom: 20px;*/
}

.search-input {
  flex: 1;
  padding: 10px;
  /*margin-right: 10px;*/
  margin: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-buttons {
  display: flex;
  margin: 5px 20px;
  margin: 5px 20px;
  align-items: center
}

.search-button {
  padding: 10px 20px;
  margin-left: 10px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;

}

.search-button:hover {
  background-color: #0056b3;
}
</style>

