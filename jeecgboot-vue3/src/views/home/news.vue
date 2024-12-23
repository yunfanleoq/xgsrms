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
        <ul class="job-list">
          <li v-for="news in filterNewsList" :key="news.type">
            <div class="top-border">
              <div>
                <div class="head">{{news.journalismHead}}</div>
                <br>
                <div class="news-text" v-html="truncatedText(news.journalismText)"></div>
                <span class="date-time">{{news.updateTime}}</span>
              </div>
            </div>
          </li>
        </ul>
        <div class="pagination">
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
<script setup  lang="ts">
import {onMounted, ref, computed} from 'vue';
import {defHttp} from "@/utils/http/axios";

const filterNewsList = computed(() => {
  return newsList.value.filter((news) => news.type === activeMenu.value);
});

const activeMenu = ref("头条新闻");
const menuList = ref([
  {
    value: "头条新闻",
    icon: "icon-    ",
  },
  {
    value: "社会新闻",
    icon: "icon-    ",
  },
  {
    value: "科技新闻",
    icon: "icon-    ",
  },
 ])

const newsList = ref([
  {
    id: 1,
    journalismHead: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
    updateTime: "2024-12-06",
    image: "path-to-image-1.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "头条新闻",
    journalismText: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
  },
  {
    id: 2,
    journalismHead: "信工所召开党委理论学习中心组专题学习会",
    updateTime: "2024-11-19",
    image: "path-to-image-2.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "科技新闻",
    journalismText: "",
  },
  {
    id: 3,
    journalismHead: "信工所召开2024年度“信工党校”专题报告会",
    updateTime: "2024-11-19",
    image: "path-to-image-3.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "头条新闻",
    journalismText: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
  },
  {
    id: 4,
    journalismHead: "网络空间安全的职业体验活动",
    updateTime: "2024-11-15",
    image: "path-to-image-4.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "头条新闻",
    journalismText: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
  },
  {
    id: 5,
    journalismHead: "陈恺研究员入选2024年度国联优秀青年领军者",
    updateTime: "2024-11-14",
    image: "path-to-image-5.jpg",
    url: "https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg",
    type: "头条新闻",
    journalismText: "【喜报】《Cybersecurity》入选“卓越行动计划”英文期刊",
  },
]);

const currentPage = ref(1)
const itemsPerPage = ref(100)
const totalPages = computed(() => Math.ceil(newsList.value.length / itemsPerPage.value))

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
            console.log('res>>>>>>>>>>>', res)
            menuList.value = res.result
            setActiveMenu(res.result[0].value)
          }
        } catch (e) {
          console.log('e>>>>>>>>>>>', e)
        }
      })
}

const getList = (param) => defHttp.get({ url: listUrl, params: param }, { isTransformResponse: false })

const selectY = () => {
  const params = { pageNo: currentPage.value, pageSize: itemsPerPage.value, type: activeMenu.value }

  getList(params).then((res) => {
    try {
      if (res.success) {
        console.log("res===============", res)
        newsList.value = res.result.records
      }
    } catch (e) {
      console.log('e>>>>>>>>>>>', e)
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

.top-border * .head {
  font-weight: bold; /* 加粗 */
  color: #3d54a7; /* 红色 */
  font-size: 20px;
}

.top-border * .date-time {
  color: #aaa; /* 红色 */
  font-size: 15px;
}

.top-border * .news-text {
  max-width: 1000px;
  //white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
