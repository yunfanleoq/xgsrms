<template>
  <div id="app">
    <main>
      <aside class="sidebar">
        <ul>
          <li v-for="jobType in jobTypeList" @click="byType(jobType.value.toString())" :key="jobType.value" :class="{ active: jobType.value === typeY }">
            {{ jobType.text }}
          </li>
        </ul>
      </aside>
      <section class="content">
        <h2>{{ typeY }}</h2>
        <ul class="job-list">
          <li v-for="job in paginatedNews" :key="job.type">
            <div v-if="job.type === typeY" v-html="job.text" v-show="shouldRender(job.type)" class="top-border"></div>
          </li>
        </ul>


        <!-- 分页控件 -->
        <div class="pagination" v-if="typeFirst">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
          <!--        <div class="jobs-per-page">-->
<!--          <label for="jobs-per-page">每页：</label>-->
<!--          <select id="jobs-per-page" v-model.number ="jobsPerPage" @change="changeJobsPerPage">-->
<!--            <option value=5>5</option>-->
<!--            <option value=10>10</option>-->
<!--            <option value=20>20</option>-->
<!--            <option value=50>50</option>-->
<!--            <option value=100>100</option>-->
<!--          </select>-->
          <!--        </div>-->
        </div>
      </section>
    </main>
  </div>
</template>


<script setup lang="ts" name="HomeYjsk">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { defHttp } from "@/utils/http/axios"
import { request } from "axios"

const jobTypeList = ref([
  { text: "研究队伍", value: "研究队伍" },
])

const jobList = ref([
  { type: "院所风貌", picture: "图片地址", pictureText: "图片名称", text: "文本信息" },
])


const typeY = ref("")
const typeFirst = ref(true)

//分页
const currentPage = ref(1)
const itemsPerPage = ref(100)
const jobsPerPage = ref(1); // 每页显示的条数
const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * jobsPerPage.value;
  const end = start + jobsPerPage.value;
  const result = filteredJobs.value.slice(start, end);
  return result;
});

// 计算总页数
const totalPages = computed(() => {
  const total = Math.ceil(filteredJobs.value.length / jobsPerPage.value);
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

// 计算属性：过滤后的列表
const filteredJobs = computed(() => {
  let filtered = jobList.value;
  filtered = filtered.filter((job) => job.type === typeY.value);
  return filtered;
});


const listUrl = "/xgsIntroduce/xgsIntroduce/list"

const listTypeUrl = (dictCode: string) =>
  `/sys/dict/getDictItems/${dictCode}`

const filterJobList = computed(() => {
  return jobList.value.filter((job) => job.type === typeY.value);
});

const changePage = (page: number) => {
  if (page > 0 && page <= totalPages.value) {
    currentPage.value = page
  }
}

function byType(thisTypeY) {
  typeY.value = thisTypeY
}

const selectTypeList = () => {
  const dictCode = "introduce_type"

  const url = listTypeUrl(dictCode)
  defHttp.get({ url: url, params: { dictCode } }, { isTransformResponse: false })
    .then((res) => {
      try {
        if (res.success) {
          jobTypeList.value = res.result
          byType(res.result[0].value)
        }
      } catch (e) {
      }
    })
}

onMounted(selectTypeList)

const getList = (param) => defHttp.get({ url: listUrl, params: param }, { isTransformResponse: false })

const selectY = () => {
  const params = { pageNo: currentPage.value, pageSize: itemsPerPage.value, type: typeY.value }

  getList(params).then((res) => {
    try {
      if (res.success) {
        jobList.value = res.result.records
      }
    } catch (e) {
    }
  })
}

onMounted(selectY)

/*
反转typeFirst的值
 */
const toggleTypeFirst = () => {
  typeFirst.value = !typeFirst.value
}


// 新增的标志变量
const renderedFirst = ref(false)

// 新增的方法
const shouldRender = (inType) => {

  if (inType === jobTypeList.value[0].value && !renderedFirst.value) {
    renderedFirst.value = true
    typeFirst.value = false
    return true
  } else if (inType !== jobTypeList.value[0].value) {
    renderedFirst.value = false
    typeFirst.value = true
    return true
  }
  renderedFirst.value = false
  return false
}
</script>


<style scoped>
#app {
  display: flex;
  flex-direction: column;
  font-family: Arial, sans-serif;
  background-color: #f4f4f9;
  color: #333;
}

main {
  display: flex;
  flex: 1;
  padding: 20px;
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

.job-list {
  list-style: none;
  padding-left: 50px;
  padding-right: 50px;
}

.job-list li {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.pagination-button {
  background-color: #00467f;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-button:hover {
  background-color: #003a63;
}

.pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-button i {
  margin: 0 5px;
}

.top-border {
  border-top: 1px solid #3d54a7;
  padding-bottom: 20px;
  padding-top: 20px;
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
</style>
