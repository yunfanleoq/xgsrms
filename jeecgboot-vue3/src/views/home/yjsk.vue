<template>
  <div id="yjskApp">
    <main>
      <aside class="sidebar">
        <ul>
          <li
            v-for="item in jobTypeList"
            :key="item.value"
            :class="{ active: item.value === currentType }"
            @click="handleTypeChange(item.value.toString())"
          >
            {{ item.text }}
          </li>
        </ul>
      </aside>
      <section class="content">
        <h2>{{ currentType }}</h2>

        <!-- 内容区域 -->
        <div class="content-wrapper top-border">
          <!-- 院所风貌：特殊渲染 -->
          <template v-if="currentType === '院所风貌'">
            <div class="fengmao-gallery">
              <div v-for="(item, index) in fengmaoData" :key="index" class="fengmao-item">
                <img :src="item.src" :alt="item.text" />
                <p>{{ item.text }}</p>
              </div>
            </div>
          </template>

          <!-- 其他类型：使用 v-html -->
          <template v-else>
            <div v-for="job in jobList" :key="job.id" v-html="job.text"></div>
          </template>
        </div>

        <!-- 分页控件 -->
        <div class="pagination" v-if="totalPages > 1">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts" name="homeYjsk">
  import { ref, computed, onMounted, watch } from 'vue';
  import { defHttp } from '@/utils/http/axios';

  // --- 状态定义 ---
  const jobTypeList = ref<{ text: string; value: string }[]>([]);
  const jobList = ref<any[]>([]);
  const currentType = ref('');
  const currentPage = ref(1);
  const itemsPerPage = ref(10); // 每页10条记录
  const totalPages = ref(1);

  // --- API 定义 ---
  const listUrl = '/xgsIntroduce/xgsIntroduce/list';
  const listTypeUrl = (dictCode: string) => `/sys/dict/getDictItems/${dictCode}`;

  // --- 数据获取 ---
  const fetchJobTypes = async () => {
    try {
      const res = await defHttp.get({ url: listTypeUrl('introduce_type') }, { isTransformResponse: false });
      if (res.success && res.result.length > 0) {
        jobTypeList.value = res.result;
        // 默认选中第一个
        currentType.value = res.result[0].value;
      }
    } catch (e) {
      console.error('获取类型列表失败', e);
    }
  };

  const fetchJobList = async () => {
    // '院所风貌' 类型下，一次性获取所有数据以展示画廊；其他类型则进行分页。
    const isFengMao = currentType.value === '院所风貌';
    const pageSize = isFengMao ? 999 : itemsPerPage.value;
    const pageNo = isFengMao ? 1 : currentPage.value;

    if (!currentType.value) return;

    try {
      const params = {
        pageNo: pageNo,
        pageSize: pageSize,
        type: currentType.value,
        column: 'createTime', // 按创建时间排序
        order: 'desc',
      };
      const res = await defHttp.get({ url: listUrl, params }, { isTransformResponse: false });
      if (res.success) {
        jobList.value = res.result.records || [];
        totalPages.value = isFengMao ? 1 : res.result.pages || 1;
      }
    } catch (e) {
      console.error('获取列表内容失败', e);
      jobList.value = [];
      totalPages.value = 1;
    }
  };

  // --- 计算属性 ---
  const fengmaoData = computed(() => {
    if (currentType.value !== '院所风貌' || jobList.value.length === 0) {
      return [];
    }
    // 假定所有"院所风貌"的HTML内容都在返回记录的 `text` 字段中
    const combinedHtml = jobList.value.map((job) => job.text || '').join('');
    if (!combinedHtml) {
      return [];
    }

    const parser = new DOMParser();
    const doc = parser.parseFromString(combinedHtml, 'text/html');
    const items: { href: string; src: string; text: string }[] = [];

    // 根据用户提供的数据结构，精确查找每个列表项
    doc.querySelectorAll('li.col-md-3.col-sm-6').forEach((li) => {
      const link = li.querySelector('a.img_hovbig') as HTMLAnchorElement;
      const img = link ? (link.querySelector('img') as HTMLImageElement) : null;
      const textElement = li.querySelector('.txtinfo h4 a') as HTMLAnchorElement;

      if (link && img && img.src && textElement) {
        items.push({
          href: link.href,
          src: img.src,
          text: textElement.innerText.trim(),
        });
      }
    });

    return items;
  });

  // --- 事件处理 ---
  const handleTypeChange = (newType: string) => {
    currentType.value = newType;
    currentPage.value = 1; // 切换类型时重置到第一页
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

  // --- 生命周期和侦听器 ---
  onMounted(async () => {
    await fetchJobTypes();
  });

  // 侦听类型和页码的变化，自动获取新数据
  watch([currentType, currentPage], fetchJobList, { immediate: true });
</script>

<style>
  @import '@/design/xgs_webstyle.css';
  @import '@/design/bootstrap.css';
</style>
<style scoped>
  #yjskApp {
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

  .top-border {
    border-top: 1px solid #3d54a7;
    padding: 20px 0;
  }

  .content-wrapper {
    padding: 20px;
  }

  .fengmao-gallery {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }

  .fengmao-item {
    text-align: center;
    border: 1px solid #eee;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    overflow: hidden;
    padding: 10px;
  }

  .fengmao-item img {
    width: 100%;
    aspect-ratio: 16 / 9;
    object-fit: cover;
    margin-bottom: 10px;
  }

  .fengmao-item p {
    margin: 0;
    color: #666;
    font-size: 14px;
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
