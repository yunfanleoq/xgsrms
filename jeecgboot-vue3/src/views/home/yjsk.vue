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
          <li v-for="job in jobList" :key="job.type">
            <div v-if="job.type === typeY" v-html="job.text" v-show="shouldRender(job.type)"></div>
          </li>
        </ul>
        <div class="pagination" v-if="typeFirst">
          <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1" class="pagination-button">
            <i class="fas fa-angle-left"></i> 上一页
          </button>
          <span>第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
          <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages" class="pagination-button">
            下一页 <i class="fas fa-angle-right"></i>
          </button>
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

const currentPage = ref(1)
const itemsPerPage = ref(100)
const totalPages = computed(() => Math.ceil(jobList.value.length / itemsPerPage.value))

const listUrl = "/xgsIntroduce/xgsIntroduce/list"

const dictCode = "introduce_type"
const listTypeUrl = (dictCode: string) =>
  `/sys/dict/getDictItems/${dictCode}`

const changePage = (page: number) => {
  if (page > 0 && page <= totalPages.value) {
    currentPage.value = page
  }
}

function byType(thisTypeY) {
  typeFirst.value = true
  typeY.value = thisTypeY
}

const selectTypeList = () => {
  const params = { dictCode: "introduce_type" }
  const dictCode = "introduce_type"

  const url = listTypeUrl(dictCode)
  defHttp.get({ url: url, params: { dictCode } }, { isTransformResponse: false })
    .then((res) => {
      try {
        if (res.success) {
          console.log('res>>>>>>>>>>>', res)
          jobTypeList.value = res.result
          byType(res.result[0].value)
        }
      } catch (e) {
        console.log('e>>>>>>>>>>>', e)
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
        console.log('res654665>>>>>>>>>>>', res)
        jobList.value = res.result.records
      }
    } catch (e) {
      console.log('e>>>>>>>>>>>', e)
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
    console.log("zzzzz")
    renderedFirst.value = true
    return true
  } else if (inType !== jobTypeList.value[0].value) {
    console.log("zz22=-zz")
    renderedFirst.value = true
    return true
  }
  console.log("fffff==")
  renderedFirst.value = true
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
  padding: 0;
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
</style>
