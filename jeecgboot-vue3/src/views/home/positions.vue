<template>
  <div id="app" class="positionContainer">
    <aside class="sidebar">
      <ul>
        <li
          v-for="(category, index) in jobCategories"
          :key="index"
          @click="filterJobCategory(category)"
          :class="{ active: selectedCategory === category && !showAnnouncementList }">
          {{ category }}
        </li>
<!--        <li @click="showAnnouncements" :class="{ active: showAnnouncementList }">-->
<!--          招聘公告-->
<!--        </li>-->
      </ul>
    </aside>
    <main class="content">

      <section v-if="showAnnouncementList" class="announcement-list">
        <h2>招聘公告</h2>
        <ul>
          <li
            v-for="(announcement, index) in announcements"
            :key="index"
            @click="goToAnnouncementDetail(String(announcement.id))"
            class="announcement-item">
            <div class="announcement-card">
              <h3>{{ announcement.title }}</h3>
              <p class="announcement-time">{{ formatDate(announcement.createTime) }}</p>
            </div>
          </li>
        </ul>
        <!-- 分页 -->
        <div class="pagination">
          <button @click="prevPage" :disabled="announce_currentPage === 1">上一页</button>
          <span>第 {{ announce_currentPage }} 页 / 共 {{ announce_totalPages }} 页</span>
          <button @click="nextPage" :disabled="announce_currentPage === announce_totalPages">下一页</button>
        </div>
      </section>
      <div v-else-if="showPositionList">
        <section class="filters">
          <div class="filter-section">
            <div class="filter-label">部门筛选：</div>
            <div class="dept-buttons">
              <button
                class="dept-button"
                :class="{ active: selectedDept === null }"
                @click="filterDept(null)"
              >
                全部部门
              </button>
              <button
                v-for="dept in depts"
                :key="dept.id || 'dept-' + dept.departName"
                class="dept-button"
                :class="{ active: selectedDept === dept.id }"
                @click="filterDept(dept.id)"
              >
                {{ dept.departName }}
              </button>
            </div>
          </div>
          <div class="search-container">
            <input v-model="searchQuery" placeholder="请输入职位名称或部门名称" class="search-input" @keyup.enter="searchPositions" />
            <div class="search-buttons">
              <button @click="reset" class="search-button">重置</button>
              <button @click="searchPositions" class="search-button">搜索</button>
            </div>
          </div>
        </section>
        <div class="job-list-container">
          <!-- 职位列表 -->
          <section class="job-list">
            <div v-for="(job, index) in paginatedPositions" :key="index" class="job-card" @click="goToPositionDetail(job.id)">
              <div class="job-card-header">
                <h3 class="job-title">{{ job.positionName }}</h3>
                <div class="job-status-badge" :class="getStatusClass(job.status)">
                  {{ job.status }}
                </div>
              </div>
              
              <div class="job-card-body">
                <div class="job-info-row">
                  <div class="job-info-item">
                    <span class="info-label">招聘部门</span>
                    <span class="info-value">{{ job.dept_dictText }}</span>
                  </div>
                  <div class="job-info-item">
                    <span class="info-label">招聘人数</span>
                    <span class="info-value highlight">{{ job.personCount }}人</span>
                  </div>
                </div>
                
                <div class="job-info-row">
                  <div class="job-info-item">
                    <span class="info-label">工作年限</span>
                    <span class="info-value">{{ job.workYears }}</span>
                  </div>
                  <div class="job-info-item" v-if="job.xlxw">
                    <span class="info-label">学历要求</span>
                    <span class="info-value">{{ job.xlxw }}</span>
                  </div>
                </div>
              </div>
              
              <div class="job-card-footer">
                <span class="view-detail-text">查看详情</span>
                <RightOutlined class="arrow-icon" />
              </div>
            </div>
          </section>
          <!-- 分页控件 -->
          <div class="pagination-wrapper">
            <div class="pagination-controls">
              <button 
                class="page-btn" 
                :disabled="currentPage === 1"
                @click="goToPage(1)"
              >
                首页
              </button>
              <button 
                class="page-btn" 
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)"
              >
                上一页
              </button>
              
              <span class="page-numbers">
                <template v-for="page in visiblePages" :key="page">
                  <span v-if="page === -1" class="page-ellipsis">...</span>
                  <button 
                    v-else
                    class="page-number"
                    :class="{ active: page === currentPage }"
                    @click="goToPage(page)"
                  >
                    {{ page }}
                  </button>
                </template>
              </span>
              
              <button 
                class="page-btn" 
                :disabled="currentPage === totalPages"
                @click="goToPage(currentPage + 1)"
              >
                下一页
              </button>
              <button 
                class="page-btn" 
                :disabled="currentPage === totalPages"
                @click="goToPage(totalPages)"
              >
                末页
              </button>
              
              <span class="page-size-selector">
                <label>每页</label>
                <select v-model.number="positionsPerPage" @change="handleSizeChange">
                  <option :value="2">2</option>
                  <option :value="5">5</option>
                  <option :value="10">10</option>
                  <option :value="20">20</option>
                  <option :value="50">50</option>
                </select>
                <label>条</label>
              </span>
              
              <div class="pagination-info">
                共 {{ totalCount }} 条记录
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!showPositionList">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts" name="positions">
  // 无需额外脚本内容
  import { ref, computed, onMounted, reactive, watch } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { usePositionStore } from '@/store/modules/positions';
  import {getDictItems, getPositionList, getDeptList, getJobById} from '@/api/xgsrms/home';
  import Line from "@/views/demo/charts/Line.vue";
  import {defHttp} from "@/utils/http/axios";
  // import positionDetail from "@/views/home/positionDetail.vue"
  import PositionDetail from "@/views/home/positionDetail.vue";
  import { usePositionApplyStoreWithOut } from '@/store/modules/positionApply';
  import { RightOutlined } from '@ant-design/icons-vue';

  const positionApplyStore = usePositionApplyStoreWithOut();

  // export default {
  //   components: {PositionDetail}
  // }
  const positionStore = usePositionStore();

  // 路由相关（需要在最前面定义，因为后面的函数会用到）
  const router = useRouter();
  const route = useRoute();

  const jobCategories = ref<any[]>([]);
  const showPositionDetail = ref(false);
  const selectedPosition = ref({});
  // 招聘公告相关数据
  const announcements = ref<any[]>([]); // 用于存储招聘公告数据
  const showAnnouncementList = ref(false); // 控制招聘公告列表的显示
  const announce_currentPage = ref(1); // 当前页码
  const itemsPerPage = ref(10); // 每页显示条目数

  // 格式化时间，仅显示年月日
  const formatDate = (dateTime: string) => {
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  };


  // 模拟从后端获取招聘公告
  const fetchAnnouncements = async () => {
    try {
      const response = await defHttp.get({
        url: "/xgsHome/xgsHome/list",
      });

      if (response && response.records) {
        announcements.value = response.records.map((item: any) => ({
            id: item.id, // 映射招聘公告的主键 ID
            title: item.recruitAnnouncementTitle,
            createTime: item.createTime,
          }))
          .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())  // 按时间降序排序
      }
    } catch (error) {
      console.error("请求招聘公告数据失败:", error);
    }
  };

  // 分页计算
  const paginatedAnnouncements = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return announcements.value.slice(start, start + itemsPerPage.value);
  });

  const announce_totalPages = computed(() => {
    return Math.ceil(announcements.value.length / itemsPerPage.value);
  });

  // 公告分页控制
  const prevPage = () => {
    if (announce_currentPage.value > 1) {
      announce_currentPage.value--;
    }
  };

  const nextPage = () => {
    if (announce_currentPage.value < announce_totalPages.value) {
      announce_currentPage.value++;
    }
  };

  // 显示招聘公告列表
  const showAnnouncements = () => {
    showAnnouncementList.value = true; // 切换到招聘公告模式
    selectedCategory.value = null; // 清除岗位分类选中状态
    fetchAnnouncements();
  };

  // 跳转至招聘公告详情
  const goToAnnouncementDetail = (announcementId: string) => {
    router.push({ name: 'announcementDetail', params: { id: announcementId } });
  };

  // 切换招聘公告页码 showPositionList
  const showPositionList = ref(true);
  
  // 监听路由变化，当回到positions页面（没有子路由）时，显示职位列表
  watch(
    () => route.path,
    (newPath) => {
      // 当路由是 /home/positions（没有子路由）时，显示职位列表
      if (newPath === '/home/positions') {
        showPositionList.value = true;
      } else if (newPath.includes('/home/positions/positionDetail/')) {
        // 当路由是详情页时，隐藏职位列表
        showPositionList.value = false;
      }
    },
    { immediate: true }
  );
  
  watch(
    () => router.currentRoute.value.query.showPositionList,
    (newId) => {
      if (newId) {
        showPositionList.value = true;
      }
    }
  );

  const goToPositionDetail = async (positionId: string | number) => {
    selectedPosition.value = {id: positionId};
    try {
      let params = {
        id: positionId,
      };
      const response = await getJobById(params);
      // 将job存pinia
      positionApplyStore.currPositionApply = response.result;
    } catch (error) {
      console.error('获取职位信息失败:', error);
    }
    // 显示职位详情组件，并传递职位信息
    showPositionDetail.value = false;
    
    // 跳转到详情页
    await router.push({ name: 'positionDetail', params: { id: String(positionId) } });
    
    // 路由跳转后立即滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    showPositionList.value = false;
  };

  const filterJobCategory = (category) => {
    //页面跳转到 positions 然后显示查询结果
    router.push({ name: 'homePositions', query: { category: category } });
    showPositionList.value = true;
    showAnnouncementList.value = false; // 退出招聘公告模式
    // 根据选择的岗位分类进行过滤的逻辑
    console.log('Selected job category:', category);
    selectedCategory.value = category;
    // 招聘详情页隐藏
    // showPositionDetail.value = false;
  };

  const searchQuery = ref('');
  const selectedCategory = ref(null);
  const selectedDept = ref(null); // 改为直接存储部门ID

  const statusFilter = ref('招聘中'); // 定义状态过滤参数，默认为空

  const depts = ref<any[]>([]);

  // 分页相关的响应式数据
  const currentPage = ref(1);
  const positionsPerPage = ref(10); // 每页显示的条数

  // 计算分页后的职位列表
  const paginatedPositions = computed(() => {
    // 后端已经处理分页，直接返回数据
    return positions.value;
  });

  // 计算总页数
  const totalPages = computed(() => {
    return Math.ceil(totalCount.value / positionsPerPage.value) || 1;
  });

  // 计算可见的页码
  const visiblePages = computed(() => {
    const total = totalPages.value;
    const current = currentPage.value;
    const pages: number[] = [];
    
    if (total <= 7) {
      // 总页数小于等于7，显示全部页码
      for (let i = 1; i <= total; i++) {
        pages.push(i);
      }
    } else {
      // 总页数大于7，显示部分页码
      if (current <= 4) {
        // 当前页在前面
        for (let i = 1; i <= 5; i++) {
          pages.push(i);
        }
        pages.push(-1); // 省略号
        pages.push(total);
      } else if (current >= total - 3) {
        // 当前页在后面
        pages.push(1);
        pages.push(-1); // 省略号
        for (let i = total - 4; i <= total; i++) {
          pages.push(i);
        }
      } else {
        // 当前页在中间
        pages.push(1);
        pages.push(-1); // 省略号
        for (let i = current - 1; i <= current + 1; i++) {
          pages.push(i);
        }
        pages.push(-1); // 省略号
        pages.push(total);
      }
    }
    
    return pages;
  });

  // 跳转到指定页
  const goToPage = (page: number) => {
    if (page < 1 || page > totalPages.value || page === currentPage.value) {
      return;
    }
    currentPage.value = page;
    fetchPositions();
  };

  // 修改每页显示数量
  const handleSizeChange = () => {
    currentPage.value = 1;
    fetchPositions();
  };

  // interface Position {
  //   id: string;
  //   create_by: string;
  //   create_time: Date;
  //   update_by: string;
  //   update_time: Date;
  //   sys_org_code: string;
  //   dept: string;
  //   ktz: string;
  //   telphone: string;
  //   email: string;
  //   position_name: string;
  //   research_direction: string;
  //   person_count: string;
  //   duty: string;
  //   xlxw: string;
  //   professional: string;
  //   work_years: string;
  //   memo: string;
  //   status: string;
  //   category: string;
  // }

  // let positions = reactive([] as  Position[])
  let positions = ref<any[]>([]); // 确保 positions 是响应式引用

  let totalCount = ref(0);
  const fetchPositions = () => {
    const params: any = {
      pageNo: currentPage.value,
      pageSize: positionsPerPage.value,
      status: statusFilter.value, // 招聘中
    };

    // 添加岗位分类筛选
    if (selectedCategory.value) {
      params.category = selectedCategory.value;
    }

    // 添加部门筛选
    if (selectedDept.value) {
      params.dept = selectedDept.value;
    }

    // 添加关键词搜索（同时搜索职位名称和部门名称）
    if (searchQuery.value) {
      params.keyword = searchQuery.value;
    }

    getPositionList(params)
      .then((res) => {
        if (res && res.success) {
          let list = res.result;
          positions.value = list.records;
          totalCount.value = list.total;
        }
      })
      .catch((e) => {
        console.error('getPositionList failed', e);
      });
  };

  // 监听查询条件变化，重置到第一页并重新获取数据
  // 注意：searchQuery 不在这里监听，而是通过点击搜索按钮触发
  watch([selectedCategory, selectedDept], () => {
    currentPage.value = 1;
    fetchPositions();
  });

  const reset = () => {
    selectedDept.value = null;
    searchQuery.value = '';
    selectedCategory.value = null;
    // 重置后重新加载数据
    currentPage.value = 1;
    fetchPositions();
  };

  // 根据状态返回对应的样式类
  const getStatusClass = (status: string) => {
    if (status === '招聘中' || status === '进行中') {
      return 'status-active';
    } else if (status === '已结束' || status === '已关闭') {
      return 'status-closed';
    }
    return 'status-default';
  };

  const fetchDepts = () => {
    getDeptList({})
      .then((res) => {
        if (res && res.success) {
          depts.value = res.result;
        }
      })
      .catch((e) => {
        console.error('getDeptList failed', e);
      });
  };

  const fetchCategorys = () => {
    const params = {
      dictCode: '岗位分类', // 只获取一级部门
    };
    getDictItems(params)
      .then((res) => {
        if (res && res.success) {
          let list = res.result;
          list = list.map((item) => item.title);
          jobCategories.value = list;
        }
      })
      .catch((e) => {
        console.error('getDictItems(岗位分类) failed', e);
      });
  };

  onMounted(() => {
    fetchDepts();
    fetchCategorys();
    fetchPositions(); // 初始加载数据
  });

  // 路由操作
  // const router = useRouter();
  const goToLoginPage = () => {
    router.push('/login');
  };

  // 分类过滤
  const filterCategory = (category) => {
    selectedCategory.value = category === selectedCategory.value ? null : category;
  };

  // 根据dept过滤
  const filterDept = (deptId) => {
    console.log('过滤部门：', deptId);
    selectedDept.value = deptId;
    // watch会自动触发fetchPositions
  };
  // 搜索职位
  const searchPositions = () => {
    console.log('搜索职位：', searchQuery.value);
    // 重置到第一页并触发搜索
    currentPage.value = 1;
    fetchPositions();
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
    flex-direction: column;
    gap: 20px;
    padding: 25px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
    margin: 20px;
  }

  .filter-section {
    display: flex;
    align-items: flex-start;
    gap: 15px;
  }

  .filter-label {
    font-weight: 600;
    color: #333;
    white-space: nowrap;
    padding-top: 8px;
    font-size: 15px;
  }

  .dept-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    flex: 1;
  }

  .dept-button {
    padding: 8px 20px;
    border-radius: 20px;
    border: 2px solid #e0e6ed;
    background-color: #fff;
    color: #5a6c7d;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    font-size: 14px;
  }

  .dept-button:hover {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-color: transparent;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
  }

  .dept-button.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-color: transparent;
    box-shadow: 0 3px 8px rgba(102, 126, 234, 0.4);
    font-weight: 600;
  }

  /* 职位列表 */
  .job-list {
    display: grid;
    justify-content: space-between;

    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    padding: 20px;
  }

  /* 职位卡片样式 */
  .job-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    padding: 24px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 2px solid transparent;
    position: relative;
    overflow: hidden;
  }

  .job-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    transform: scaleY(0);
    transition: transform 0.3s ease;
  }

  .job-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    border-color: #667eea;
  }

  .job-card:hover::before {
    transform: scaleY(1);
  }

  /* 卡片头部 */
  .job-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  .job-title {
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
    margin: 0;
    line-height: 1.4;
    flex: 1;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  /* 状态徽章 */
  .job-status-badge {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 500;
    white-space: nowrap;
    margin-left: 16px;
  }

  .status-active {
    background: #e6fffb;
    color: #13c2c2;
    border: 1px solid #87e8de;
  }

  .status-closed {
    background: #fff1f0;
    color: #cf1322;
    border: 1px solid #ffa39e;
  }

  .status-default {
    background: #f0f0f0;
    color: #595959;
    border: 1px solid #d9d9d9;
  }

  /* 卡片主体 */
  .job-card-body {
    margin-bottom: 16px;
  }

  .job-info-row {
    display: flex;
    gap: 24px;
    margin-bottom: 12px;
  }

  .job-info-row:last-child {
    margin-bottom: 0;
  }

  .job-info-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .info-label {
    font-size: 13px;
    color: #8c8c8c;
    font-weight: 500;
  }

  .info-value {
    font-size: 15px;
    color: #262626;
    font-weight: 500;
  }

  .info-value.highlight {
    color: #1890ff;
    font-weight: 600;
    font-size: 16px;
  }

  /* 卡片底部 */
  .job-card-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
    color: #1890ff;
    font-size: 14px;
    font-weight: 500;
  }

  .view-detail-text {
    transition: all 0.3s ease;
  }

  .arrow-icon {
    transition: all 0.3s ease;
    font-size: 12px;
  }

  .job-card:hover .view-detail-text {
    color: #096dd9;
  }

  .job-card:hover .arrow-icon {
    transform: translateX(4px);
    color: #096dd9;
  }

  /* 响应式设计 */
  @media (max-width: 768px) {
    .job-card {
      padding: 16px;
    }

    .job-card-header {
      flex-direction: column;
      gap: 12px;
    }

    .job-status-badge {
      margin-left: 0;
      align-self: flex-start;
    }

    .job-info-row {
      flex-direction: column;
      gap: 12px;
    }

    .job-title {
      font-size: 18px;
    }
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

  .positions-per-page {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 10px;
  }

  .positions-per-page label {
    margin-right: 10px;
  }

  .positions-per-page select {
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
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 30px 20px;
    box-shadow: 2px 0 15px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
    margin-right: 20px;
  }

  .sidebar ul {
    list-style: none;
    padding: 0;
  }

  .sidebar ul li {
    margin: 15px 0;
    padding: 12px 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 8px;
    color: #fff;
    font-weight: 500;
    background: rgba(255, 255, 255, 0.1);
  }

  .sidebar ul li:hover {
    background: rgba(255, 255, 255, 0.25);
    transform: translateX(5px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  }

  .sidebar ul .active {
    background: rgba(255, 255, 255, 0.3);
    font-weight: bold;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }

  .announcement-list {
    margin: 20px;
  }

  h2 {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
  }

  .announcement-item {
    cursor: pointer;
  }

  .announcement-item:hover {
    color: #3d54a7;
  }

  .announcement-card {
    padding: 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .announcement-card:hover {
    color: #3d54a7;
  }

  .announcement-card h3 {
    font-size: 16px;
    margin: 0;
    flex: 1; /* 使标题占满可用空间 */
  }

  .announcement-card .announcement-time {
    font-size: 16px;
    color: #888;
    text-align: right;
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
    gap: 10px;
    align-items: center;
  }

  .search-input {
    flex: 1;
    padding: 12px 20px;
    border: 2px solid #e0e6ed;
    border-radius: 25px;
    font-size: 14px;
    transition: all 0.3s ease;
    background-color: #fff;
  }

  .search-input:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  .search-buttons {
    display: flex;
    gap: 10px;
  }

  .search-button {
    padding: 12px 28px;
    border: none;
    border-radius: 25px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    font-size: 14px;
  }

  .search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.5);
  }

  /* 分页器样式 */
  .pagination-wrapper {
    margin-top: 30px;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  .pagination-info {
    color: #666;
    font-size: 14px;
    margin-left: 20px;
    white-space: nowrap;
  }

  .pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .page-btn {
    padding: 8px 16px;
    border: 1px solid #d9d9d9;
    background: #fff;
    color: #333;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
  }

  .page-btn:hover:not(:disabled) {
    color: #667eea;
    border-color: #667eea;
  }

  .page-btn:disabled {
    color: #ccc;
    cursor: not-allowed;
    background: #f5f5f5;
  }

  .page-numbers {
    display: flex;
    gap: 5px;
  }

  .page-number {
    min-width: 36px;
    height: 36px;
    padding: 0 8px;
    border: 1px solid #d9d9d9;
    background: #fff;
    color: #333;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
  }

  .page-number:hover {
    color: #667eea;
    border-color: #667eea;
  }

  .page-number.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-color: transparent;
    font-weight: 600;
  }

  .page-ellipsis {
    min-width: 36px;
    height: 36px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 14px;
  }

  .page-size-selector {
    display: flex;
    align-items: center;
    gap: 5px;
    margin-left: 15px;
    padding-left: 15px;
    border-left: 1px solid #e0e0e0;
  }

  .page-size-selector label {
    color: #666;
    font-size: 14px;
  }

  .page-size-selector select {
    padding: 6px 10px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    background: #fff;
    transition: all 0.3s ease;
  }

  .page-size-selector select:hover {
    border-color: #667eea;
  }

  .page-size-selector select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }
</style>
