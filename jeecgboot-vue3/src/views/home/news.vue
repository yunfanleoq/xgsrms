<template>
  <div class="news-page">
    <aside class="sidebar">
      <h2 class="sidebar-title">研究队伍</h2>
      <ul>
        <li class="active">人才招聘</li>
      </ul>
    </aside>
    <section class="content">
      <h2>人才招聘</h2>
      <p v-if="loading" class="empty">加载中</p>
      <p v-else-if="!newsList.length" class="empty">暂无招聘通知</p>
      <ul v-else class="notice-list">
        <li v-for="news in paginatedNews" :key="news.id">
          <a
            v-if="officialHref(news)"
            class="notice-link"
            :href="officialHref(news)"
            target="_blank"
            rel="noopener noreferrer"
          >
            <span class="head">{{ news.journalismHead }}</span>
            <time>{{ formatDate(news.newsDate || news.createTime) }}</time>
          </a>
          <a v-else class="notice-link" href="" @click.prevent="openNotice(news)">
            <span class="head">{{ news.journalismHead }}</span>
            <time>{{ formatDate(news.newsDate || news.createTime) }}</time>
          </a>
        </li>
      </ul>

      <div class="pagination" v-if="newsList.length">
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
          <select id="jobs-per-page" v-model.number="jobsPerPage" @change="currentPage = 1">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
            <option :value="100">100</option>
          </select>
        </div>
      </div>
    </section>
  </div>
</template>
<script setup lang="ts">
  import { onMounted, ref, computed } from 'vue';
  import { defHttp } from '@/utils/http/axios';
  import { useRouter } from 'vue-router';

  interface NoticeRow {
    id: string;
    journalismHead: string;
    newsDate?: string;
    createTime?: string;
    href?: string;
  }

  const newsList = ref<NoticeRow[]>([]);
  const loading = ref(true);
  const currentPage = ref(1);
  const jobsPerPage = ref(10);
  const router = useRouter();

  const paginatedNews = computed(() => {
    const start = (currentPage.value - 1) * jobsPerPage.value;
    return newsList.value.slice(start, start + jobsPerPage.value);
  });

  const totalPages = computed(() => Math.max(1, Math.ceil(newsList.value.length / jobsPerPage.value)));

  const paginationRange = computed(() => {
    const total = totalPages.value;
    const current = currentPage.value;
    const range: (number | string)[] = [];

    if (total <= 7) {
      for (let i = 1; i <= total; i++) {
        range.push(i);
      }
    } else if (current <= 3) {
      for (let i = 1; i <= 5; i++) {
        range.push(i);
      }
      range.push('...');
      range.push(total);
    } else if (current >= total - 2) {
      range.push(1);
      range.push('...');
      for (let i = total - 4; i <= total; i++) {
        range.push(i);
      }
    } else {
      range.push(1);
      range.push('...');
      for (let i = current - 1; i <= current + 1; i++) {
        range.push(i);
      }
      range.push('...');
      range.push(total);
    }
    return range;
  });

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

  const formatDate = (value?: string) => {
    if (!value) return '';
    const matched = String(value).match(/\d{4}-\d{2}-\d{2}/);
    return matched ? matched[0] : String(value).slice(0, 10);
  };

  const officialHref = (news: NoticeRow) => {
    const href = (news.href || '').trim();
    return /^https?:\/\//i.test(href) ? href : '';
  };

  const openNotice = (news: NoticeRow) => {
    router.push({ name: 'newsDetail', params: { id: String(news.id) } });
  };

  const loadNotices = async () => {
    loading.value = true;
    try {
      const response = await defHttp.get({
        url: '/xgsJournalism/xgsJournalism/listForHomeNews',
        params: { typeCode: 'rczp', pageNo: 1, pageSize: 100 },
      });
      const records = response?.records || response?.result?.records || [];
      newsList.value = records;
      currentPage.value = 1;
    } catch (error) {
      console.error('加载人才招聘通知失败:', error);
      newsList.value = [];
    } finally {
      loading.value = false;
    }
  };

  onMounted(loadNotices);
</script>

<style scoped>
  .news-page {
    display: flex;
    align-items: flex-start;
    gap: 24px;
    width: min(1180px, 100%);
    margin: 0 auto;
    color: #333333;
    font-family: 'PingFang SC', 'Hiragino Sans GB', 'Noto Sans SC', 'Source Han Sans SC', sans-serif;
  }

  .sidebar {
    width: 220px;
    flex: none;
    background: #fff;
    overflow: hidden;
    box-shadow: 0 0 0 1px rgba(16, 36, 63, 0.08);
  }

  .sidebar-title {
    margin: 0;
    padding: 16px 20px;
    background: #004098;
    color: #fff;
    font-size: 16px;
    font-weight: 650;
    letter-spacing: 0.08em;
    line-height: 1.4;
  }

  .sidebar ul {
    list-style: none;
    margin: 0;
    padding: 6px 0;
    background: #fff;
  }

  .sidebar li {
    margin: 0;
    padding: 14px 18px;
    border-left: 3px solid #004098;
    color: #004098;
    font-size: 15px;
    font-weight: 650;
    line-height: 1.5;
    background: #fff;
  }

  .content {
    flex: 1;
    min-width: 0;
    background: #fff;
    padding: 28px 36px 24px;
    box-shadow: 0 0 0 1px rgba(16, 36, 63, 0.08);
  }

  .content h2 {
    margin: 0 0 8px;
    padding-bottom: 12px;
    border-bottom: 2px solid #004098;
    color: #1c1917;
    font-family: 'Songti SC', 'STSong', 'Noto Serif SC', serif;
    font-size: 24px;
    font-weight: 700;
    letter-spacing: 0.06em;
  }

  .empty {
    margin: 18px 0 0;
    color: #8a8a8a;
    font-size: 14px;
  }

  .notice-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .notice-list li {
    border-bottom: 1px solid #e6e6e6;
  }

  .notice-link {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 24px;
    padding: 14px 4px;
    color: inherit;
    text-decoration: none;
    cursor: pointer;
  }

  .notice-link:hover .head,
  .notice-link:focus-visible .head {
    color: #004098;
  }

  .notice-link:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 2px;
  }

  .head {
    min-width: 0;
    color: #333333;
    font-size: 15px;
    font-weight: 400;
    line-height: 1.6;
    transition: color 200ms ease;
  }

  .notice-link time {
    flex: none;
    color: #8a8a8a;
    font-size: 14px;
    font-variant-numeric: tabular-nums;
  }

  .pagination {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    margin-top: 22px;
    padding-top: 16px;
  }

  .pagination-left,
  .pagination-right,
  .page-numbers {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .pagination-btn,
  .page-number,
  .pagination-right select {
    border: 1px solid #d9d9d9;
    border-radius: 2px;
    background: #fff;
    color: #333;
    font-size: 14px;
    cursor: pointer;
  }

  .pagination-btn {
    padding: 6px 12px;
  }

  .page-number {
    min-width: 32px;
    height: 32px;
    padding: 0 8px;
  }

  .pagination-btn:hover:not(:disabled),
  .page-number:hover:not(.active):not(.ellipsis):not(:disabled),
  .pagination-right select:hover {
    border-color: #004098;
    color: #004098;
  }

  .pagination-btn:disabled {
    background: #f5f5f5;
    color: #bbb;
    cursor: not-allowed;
  }

  .page-number.active {
    background: #004098;
    border-color: #004098;
    color: #fff;
  }

  .page-number.ellipsis {
    border: none;
    background: transparent;
    color: #999;
    cursor: default;
  }

  .pagination-right label {
    color: #666;
    font-size: 14px;
  }

  .pagination-right select {
    padding: 6px 10px;
  }

  .pagination-right select:focus,
  .pagination-btn:focus-visible,
  .page-number:focus-visible {
    outline: 2px solid #004098;
    outline-offset: 2px;
  }

  @media (max-width: 800px) {
    .news-page {
      flex-direction: column;
    }

    .sidebar {
      width: 100%;
    }

    .content {
      padding: 20px 16px;
    }

    .notice-link {
      flex-direction: column;
      gap: 4px;
    }
  }
</style>
