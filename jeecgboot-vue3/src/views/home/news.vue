<template>
  <div id="app">
    <main>
      <aside class="sidebar">
        <ul>
          <li v-for="menu in menuList" @click="setActiveMenu(menu.value)" :key="menu.value" :class="{ active: menu.value === activeMenu }">
            {{ menu.value }}
          </li>
        </ul>
      </aside>
      <section class="content">
        <h2>{{ activeMenu }}</h2>

<!--        主要新闻-->
        <ul class="job-list" v-if="activeMenu === firstType">
          <li v-for="news in paginatedNews" :key="news.type" @click="goToNewsDetail(String(news.id))">
            <div class="top-border">
              <div>
                <div class="head">{{news.journalismHead}}</div>
                <br>
                <div class="news-text" v-html="truncatedText(news.journalismText)"></div>
                <span class="date-time" v-if="news.updateTime">{{news.updateTime}}</span>
                <span class="date-time" v-if="!news.updateTime">{{news.createTime}}</span>
              </div>
            </div>
          </li>
        </ul>

<!--        其他新闻-->
        <ul class="job-list-2" v-if="activeMenu != firstType">
          <li v-for="(news, index) in paginatedNews" :key="news.type" @click="goToNewsDetail(String(news.id))">
            <br v-if="index % 5 == 0">
            <div class="top-border-2" v-if="activeMenu != firstType">
              <div class="head">{{news.journalismHead}}</div>
              <span class="date-time" v-if="news.updateTime">{{news.updateTime}}</span>
              <span class="date-time" v-if="!news.updateTime">{{news.createTime}}</span>
            </div>

          </li>
        </ul>

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
      </section>
    </main>
  </div>
</template>
<script setup  lang="ts">
import {onMounted, ref, computed} from 'vue';
import {defHttp} from "@/utils/http/axios";
import { useRouter } from 'vue-router';

const activeMenu = ref("");
const menuList = ref([
  {
    value: "头条新闻",
    icon: "icon-    ",
  },
 ])
const firstType = ref("")

const newsList = ref([
  {
    id: 1,
    journalismHead: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
    updateTime: "2024-12-06",
    image: "path-to-image-1.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "头条新闻",
    journalismText: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
    createTime:""
  },
]);


//分页
const currentPage = ref(1)
const itemsPerPage = ref(100)
const jobsPerPage = ref(10); // 每页显示的条数

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
  let filtered = newsList.value;
  filtered = filtered.filter((news) => news.type === activeMenu.value);
  return filtered;
});


//更改类型
const setActiveMenu = (menu) => {
  activeMenu.value = menu;
};

const listUrl = "/xgsJournalism/xgsJournalism/list"
const listTypeUrl = (dictCode: string) =>
    `/sys/dict/getDictItems/${dictCode}`

const selectTypeList = () => {
  const dictCode = "jour_type"

  const url = listTypeUrl(dictCode)
  defHttp.get({ url: url, params: { dictCode } }, { isTransformResponse: false })
      .then((res) => {
        try {
          if (res.success) {
            menuList.value = res.result
            firstType.value = res.result[0].value
            setActiveMenu(res.result[0].value)
          }
        } catch (e) {
        }
      })
}

const getList = (param) => defHttp.get({ url: listUrl, params: param }, { isTransformResponse: false })

const selectY = () => {
  const params = { pageNo: currentPage.value, pageSize: itemsPerPage.value, type: activeMenu.value }

  getList(params).then((res) => {
    try {
      if (res.success) {
        newsList.value = res.result.records
      }
    } catch (e) {
    }
  })
}

onMounted(selectTypeList)
onMounted(selectY)

const changePage = (page: number) => {
  if (page > 0 && page <= totalPages.value) {
    currentPage.value = page
  }
}

/*
优化新闻内容信息
 */
function truncatedText(htmlText) {
  const maxLength = 150;
  if (htmlText.length > maxLength) {
    return htmlText.slice(0, maxLength) + '...';
  }
  return htmlText;
}

/*
跳转到目标新闻页面
 */
const router = useRouter();
const goToNewsDetail = (newsId: string) => {
  router.push({ name: 'newsDetail', params: { id: newsId } });
};
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
  width: 1000px;
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

.job-list-2 {
  list-style: none;
  padding-left: 10px;
  padding-right: 10px;
}

.job-list-2 li {
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
  cursor: pointer;
}

.top-border * .head {
  font-weight: bold; /* 加粗 */
  color: #3d54a7; /* #3d54a7 */
  font-size: 20px;
}

.top-border * .head:hover {
  font-weight: bold; /* 加粗 */
  color: #7188da; /* #3d54a7 */
  font-size: 20px;
}

.top-border * .date-time {
  color: #aaa; /* #aaa */
  font-size: 15px;
}

.top-border * .news-text {
  max-width: 1000px;
  overflow: hidden;
  text-overflow: ellipsis;
}
.top-border * .news-text:hover {
  max-width: 1000px;
  color: #5578e7;
  overflow: hidden;
  text-overflow: ellipsis;
}

.top-border-2 {
  padding-bottom: 20px;
  cursor: pointer;
  display: flex;
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 可选，用于垂直居中对齐 */
}

.top-border-2 .head {
  font-weight: bold; /* 加粗 */
  color: #3d54a7; /* #3d54a7 */
  font-size: 15px;
}

.top-border-2 .head:hover {
  font-weight: bold; /* 加粗 */
  color: #7188da; /* #3d54a7 */
  font-size: 15px;
}

.top-border-2 .date-time {
  color: #aaa; /* #aaa */
  font-size: 10px;
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
