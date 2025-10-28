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
          <li v-for="(news, index) in paginatedNews" :key="news.type" @click="goToNewsDetail(String(news.id))">
            <div class="top-border">
              <span class="news-index">{{ (currentPage - 1) * jobsPerPage + index + 1 }}</span>
              <div class="news-content">
                <div class="head">{{ news.journalismHead }}</div>
                <div class="news-text" v-html="truncatedText(news.shortText)"></div>
                <span class="date-time">{{ news.createTime }}</span>
              </div>
            </div>
          </li>
        </ul>

        <!--        其他新闻-->
        <ul class="job-list-2" v-if="activeMenu != firstType">
          <li v-for="(news, index) in paginatedNews" :key="news.type" @click="goToNewsDetail(String(news.id))">
            <div class="top-border-2">
              <span class="news-index-2">{{ (currentPage - 1) * jobsPerPage + index + 1 }}.</span>
              <div class="head">{{ news.journalismHead }}</div>
              <span class="date-time">{{ news.createTime }}</span>
            </div>
          </li>
        </ul>

        <!-- 分页控件 -->
        <div class="pagination">
          <div class="pagination-left">
            <button class="pagination-btn" @click="goToFirstPage" :disabled="currentPage === 1">首页</button>
            <button class="pagination-btn" @click="prevPage" :disabled="currentPage === 1">上一页</button>
            <div class="page-numbers">
              <button
                v-for="page in paginationRange"
                :key="page"
                :class="['page-number', { active: page === currentPage, ellipsis: page === '...' }]"
                @click="typeof page === 'number' && goToPage(page)"
                :disabled="page === '...'"
              >
                {{ page }}
              </button>
            </div>
            <button class="pagination-btn" @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
            <button class="pagination-btn" @click="goToLastPage" :disabled="currentPage === totalPages">尾页</button>
          </div>
          <div class="pagination-right">
            <label for="jobs-per-page">每页：</label>
            <select id="jobs-per-page" v-model.number="jobsPerPage" @change="changeJobsPerPage">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
              <option value="100">100</option>
            </select>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>
<script setup lang="ts">
  import { onMounted, ref, computed } from 'vue';
  import { defHttp } from '@/utils/http/axios';
  import { useRouter } from 'vue-router';

  const activeMenu = ref('');
  const menuList = ref([
    {
      value: '头条新闻',
      icon: 'icon-    ',
    },
  ]);
  const firstType = ref('');

  const newsList = ref<any[]>([
    {
      id: 1,
      journalismHead: '【喜报】《Cybersecurity》入选"卓越行动计划"英文期刊',
      updateTime: '2024-12-06',
      image: 'path-to-image-1.jpg',
      url: 'https://www.gizbot.com/img/2016/11/whatsapp-error-lead-image-08-1478607387.jpg',
      type: '头条新闻',
      journalismText: '【喜报】《Cybersecurity》入选"卓越行动计划"英文期刊',
      shortText: '',
      createTime: '',
      state: '',
    },
  ]);

  //分页
  const currentPage = ref(1);
  const itemsPerPage = ref(100);
  const jobsPerPage = ref(10); // 每页显示的条数

  const paginatedNews = computed(() => {
    const start = (currentPage.value - 1) * jobsPerPage.value;
    const end = start + jobsPerPage.value;
    const result = filteredJobs.value.slice(start, end);
    return result.reverse();
  });

  // 计算总页数
  const totalPages = computed(() => {
    const total = Math.ceil(filteredJobs.value.length / jobsPerPage.value);
    return total;
  });

  // 计算分页显示范围
  const paginationRange = computed(() => {
    const total = totalPages.value;
    const current = currentPage.value;
    const range: (number | string)[] = [];

    if (total <= 7) {
      // 总页数小于等于7，显示所有页码
      for (let i = 1; i <= total; i++) {
        range.push(i);
      }
    } else {
      // 总页数大于7，显示省略号
      if (current <= 3) {
        // 当前页在前面
        for (let i = 1; i <= 5; i++) {
          range.push(i);
        }
        range.push('...');
        range.push(total);
      } else if (current >= total - 2) {
        // 当前页在后面
        range.push(1);
        range.push('...');
        for (let i = total - 4; i <= total; i++) {
          range.push(i);
        }
      } else {
        // 当前页在中间
        range.push(1);
        range.push('...');
        for (let i = current - 1; i <= current + 1; i++) {
          range.push(i);
        }
        range.push('...');
        range.push(total);
      }
    }
    return range;
  });

  // 分页控制函数
  const goToPage = (page: number) => {
    if (page >= 1 && page <= totalPages.value) {
      currentPage.value = page;
    }
  };

  const goToFirstPage = () => {
    currentPage.value = 1;
  };

  const goToLastPage = () => {
    currentPage.value = totalPages.value;
  };

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
    filtered = filtered.filter((news) => news.state === '已发布');
    return filtered;
  });

  //更改类型
  const setActiveMenu = (menu) => {
    activeMenu.value = menu;
    selectY();
  };

  const listUrl = '/xgsJournalism/xgsJournalism/list';
  const listTypeUrl = (dictCode: string) => `/sys/dict/getDictItems/${dictCode}`;

  const selectTypeList = () => {
    const dictCode = 'jour_type';

    const url = listTypeUrl(dictCode);
    defHttp.get({ url: url, params: { dictCode } }, { isTransformResponse: false }).then((res) => {
      try {
        if (res.success) {
          menuList.value = res.result;
          firstType.value = res.result[0].value;
          setActiveMenu(res.result[0].value);
          selectY();
        }
      } catch (e) {}
    });
  };

  const getList = (param) => defHttp.get({ url: listUrl, params: param }, { isTransformResponse: false });

  const selectY = () => {
    const params = { pageNo: currentPage.value, pageSize: itemsPerPage.value, type: activeMenu.value };

    getList(params).then((res) => {
      try {
        if (res.success) {
          newsList.value = res.result.records;
        }
      } catch (e) {}
    });
  };

  onMounted(selectTypeList);
  onMounted(selectY);

  /*
优化新闻内容信息
 */
  function truncatedText(htmlText) {
    const maxLength = 120;
    if (htmlText && htmlText.length > maxLength) {
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
    background: linear-gradient(to bottom, #f8f9fa, #e9ecef);
    padding: 15px;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
    margin-right: 25px;
    border-radius: 8px;
  }

  .sidebar ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .sidebar li {
    margin: 8px 0;
    padding: 12px 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 6px;
    font-size: 15px;
    line-height: 1.6;
    border-left: 3px solid transparent;
    font-weight: 500;
    color: #333;
  }

  .sidebar li:hover {
    background-color: #e3e7ed;
    border-left-color: #3d54a7;
    transform: translateX(3px);
  }

  .sidebar li.active {
    background: linear-gradient(135deg, #3d54a7, #5068b8);
    color: #fff;
    border-left-color: #2a3d7a;
    box-shadow: 0 2px 8px rgba(61, 84, 167, 0.3);
  }

  .content {
    flex: 1;
    background-color: #fff;
    padding: 30px 40px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border-radius: 8px;
  }

  .content h2 {
    margin-bottom: 25px;
    color: #2c3e50;
    font-size: 24px;
    font-weight: 600;
    padding-bottom: 15px;
    border-bottom: 3px solid #3d54a7;
  }

  .job-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .job-list li {
    margin-bottom: 0;
    transition: background-color 0.2s ease;
  }

  .job-list li:hover {
    background-color: #f8f9fa;
  }

  .job-list-2 {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .job-list-2 li {
    transition: background-color 0.2s ease;
    border-bottom: 1px solid #e9ecef;
  }

  .job-list-2 li:last-child {
    border-bottom: none;
  }

  .job-list-2 li:hover {
    background-color: #f8f9fa;
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
    display: flex;
    align-items: flex-start;
    border-bottom: 1px solid #e9ecef;
    padding: 20px 0;
    cursor: pointer;
  }

  .top-border:last-child {
    border-bottom: none;
  }

  .news-index {
    flex-shrink: 0;
    width: 35px;
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #3d54a7, #5068b8);
    color: #fff;
    border-radius: 4px;
    font-weight: 600;
    font-size: 14px;
    margin-right: 15px;
    margin-top: 3px;
  }

  .news-content {
    flex: 1;
    min-width: 0;
  }

  .top-border .head {
    font-weight: 600;
    color: #2c3e50;
    font-size: 18px;
    line-height: 1.6;
    margin-bottom: 10px;
    transition: color 0.2s ease;
  }

  .top-border:hover .head {
    color: #3d54a7;
  }

  .top-border .date-time {
    color: #999;
    font-size: 13px;
    display: inline-block;
    margin-top: 8px;
  }

  .top-border .news-text {
    color: #666;
    line-height: 1.8;
    font-size: 14px;
    margin-bottom: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .top-border-2 {
    padding: 14px 10px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .news-index-2 {
    flex-shrink: 0;
    color: #3d54a7;
    font-weight: 600;
    font-size: 14px;
    width: 30px;
  }

  .top-border-2 .head {
    flex: 1;
    font-weight: 500;
    color: #2c3e50;
    font-size: 15px;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    transition: color 0.2s ease;
  }

  .top-border-2:hover .head {
    color: #3d54a7;
  }

  .top-border-2 .date-time {
    flex-shrink: 0;
    color: #999;
    font-size: 13px;
    margin-left: 15px;
  }

  .pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #e9ecef;
  }

  .pagination-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .pagination-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .pagination-btn {
    padding: 8px 16px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background-color: #fff;
    color: #333;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .pagination-btn:hover:not(:disabled) {
    border-color: #3d54a7;
    color: #3d54a7;
  }

  .pagination-btn:disabled {
    background-color: #f5f5f5;
    color: #bbb;
    cursor: not-allowed;
    border-color: #e0e0e0;
  }

  .page-numbers {
    display: flex;
    gap: 6px;
  }

  .page-number {
    min-width: 36px;
    height: 36px;
    padding: 0 8px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background-color: #fff;
    color: #333;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .page-number:hover:not(.active):not(.ellipsis):not(:disabled) {
    border-color: #3d54a7;
    color: #3d54a7;
  }

  .page-number.active {
    background: linear-gradient(135deg, #3d54a7, #5068b8);
    color: #fff;
    border-color: #3d54a7;
    font-weight: 600;
  }

  .page-number.ellipsis {
    border: none;
    background: transparent;
    cursor: default;
    color: #999;
  }

  .pagination-right label {
    font-size: 14px;
    color: #666;
  }

  .pagination-right select {
    padding: 6px 12px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background-color: #fff;
    font-size: 14px;
    cursor: pointer;
    transition: border-color 0.2s ease;
  }

  .pagination-right select:hover {
    border-color: #3d54a7;
  }

  .pagination-right select:focus {
    outline: none;
    border-color: #3d54a7;
    box-shadow: 0 0 0 2px rgba(61, 84, 167, 0.1);
  }
</style>
