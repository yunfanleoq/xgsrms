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
        <li @click="showAnnouncements" :class="{ active: showAnnouncementList }">
          招聘公告
        </li>
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
      <div v-else>
        <section class="filters">
          <div class="categories">
            <button v-for="dept in depts" :key="dept.id" @click="filterDept(dept)" :class="{ active: selectedDept.id === dept.id }">
              {{ dept.departName }}
            </button>
          </div>
          <div class="search-container">
            <input v-model="searchQuery" placeholder="请输入职位名称" class="search-input" />
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
              <h3>{{ job.positionName }}</h3>
              <p
                >职位数量：<strong>{{ job.personCount }}</strong></p
              >
              <p
                >招聘部门：<strong>{{ job.dept_dictText }}</strong></p
              >
              <p
                >工作年限：<span class="salary">{{ job.workYears }}</span></p
              >
              <p
                >招聘状态：<span class="status-filter">{{ job.status }}</span></p
              >
              <p
                ><span>{{ job.dept_dictText }} </span></p
              >
            </div>
          </section>
          <!-- 分页控件 -->
          <div class="pagination">
            <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
            <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
            <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>

            <!-- 共多少条-->
            <label for="total-positions">共{{ totalCount }}条</label>
            <label for="total-positions">|</label>
            <label for="positions-per-page">每页：</label>
            <select id="positions-per-page" v-model.number="positionsPerPage" @change="changePositionsPerPage">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
              <option value="100">100</option>
            </select>
            <!--        </div>-->
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts" name="positions">
  // 无需额外脚本内容
  import { ref, computed, onMounted, reactive, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { usePositionStore } from '@/store/modules/positions';
  import { getDictItems, getPositionList, getDeptList } from '@/api/xgsrms/home';
  import Line from "@/views/demo/charts/Line.vue";
  import {defHttp} from "@/utils/http/axios";

  const positionStore = usePositionStore();

  const jobCategories = ref([]);

  // 招聘公告相关数据
  const announcements = ref([]); // 用于存储招聘公告数据
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

  // -----------------

  //
  const goToPositionDetail = (positionId: number) => {
    router.push({ name: 'positionDetail', params: { id: positionId } });
  };

  const filterJobCategory = (category) => {
    showAnnouncementList.value = false; // 退出招聘公告模式
    // 根据选择的岗位分类进行过滤的逻辑
    console.log('Selected job category:', category);
    selectedCategory.value = category;
    // 这里可以添加具体的过滤逻辑
  };

  const searchQuery = ref(null);
  const selectedCategory = ref(null);
  const selectedDept = ref({ departName: '', id: null });

  const statusFilter = ref('招聘中'); // 定义状态过滤参数，默认为空

  const depts = ref([{ departName: '', id: null }]);

  // 分页相关的响应式数据
  const currentPage = ref(1);
  const positionsPerPage = ref(10); // 每页显示的条数

  // 计算分页后的职位列表
  const paginatedPositions = computed(() => {
    console.log('Current Page:', currentPage.value);
    console.log('Positions Per Page:', positionsPerPage.value);
    const start = (currentPage.value - 1) * positionsPerPage.value;
    const end = start + positionsPerPage.value;
    console.log('Start Index:', start, 'End Index:', end);
    const result = filteredPositions.value.slice(start, end);

    console.log('>>>>>Paginated Positions:', result);
    return result;
  });

  // 计算总页数
  const totalPages = computed(() => {
    const total = Math.ceil(filteredPositions.value.length / positionsPerPage.value);
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

  const changePositionsPerPage = (event: Event) => {
    const value = (event.target as HTMLSelectElement).value;
    console.log('Changing Positions Per Page to:', value);
    positionsPerPage.value = parseInt(value, 10);
    currentPage.value = 1; // 重置到第一页
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
  let positions = ref([]); // 确保 positions 是响应式引用

  let totalCount = ref(0);
  const fetchPositions = () => {
    getPositionList({
      pageNo: 1,
      pageSize: 1000,
      // // searchQuery: searchQuery.value,
      // category: selectedCategory.value,
      // dept: selectedDept.value.id,
      status: statusFilter.value,
    }).then((res) => {
      if (res.success) {
        console.log('fetchPositions>>>>>getPositionList>>>>>>', res);
        let list = res.result;
        // positionStore.setPositions(list);
        // console.log('fetchPositions positionStore.positions>>>>>>>>>>>', positionStore.positions);
        positions.value = list.records;
        // totalPages.value = list.pages;

        // totalCount.value = list.total;

        // paginatedPositions.value = positions;
      }
    });
    // console.log('fetchPositions positionStore.positions>>>>>>>>>>>', positionStore.positions);
    // positions = positionStore.positions.records;
    // totalPages.value = positionStore.positions.pages;
    //
    // totalCount.value = positionStore.positions.total;
    //
    // // paginatedPositions.value = positions;
    //
    console.log('<<<<<<<<<<<<<<<<<<<<<<fetchPositions>>>>>END>>>>>>positions', positions);
  };

  // 使用 watch 监控这些值的变化
  watch([currentPage, selectedCategory, () => selectedDept.value.id], ([newPage, newCategory, newDeptId]) => {
    console.log('WATCH---------------------Values changed:', newPage, newCategory, newDeptId);
    fetchPositions();
    console.log('WATCH-------END--------------Values changed:', newPage, newCategory, newDeptId);
  });

  const reset = () => {
    selectedDept.value.id = null;
    selectedDept.value.departName = '';
    searchQuery.value = '';
    selectedCategory.value = null;
  };

  const fetchDepts = () => {
    console.log('fetchDepts>>>>>>>>>>>BEGIN');
    const params = {
      orgType: 1, // 只获取一级部门
    };

    getDeptList(params).then((res) => {
      if (res.success) {
        console.log('fetchDepts>>>>>>>>>>>', res);
        let list = res.result;
        // 提取 list 中的 dept 字段形成数组，并赋值给 depts.value
        list = list.map((item) => ({ departName: item.departName, id: item.id }));

        depts.value = list;

        console.log('getDeptList>>>>>>>>>>>', list, depts.value);
      }
    });
  };

  const fetchCategorys = () => {
    console.log('fetchCategorys>>>>>>>>>>>BEGIN');
    const params = {
      dictCode: '岗位分类', // 只获取一级部门
    };
    getDictItems(params).then((res) => {
      if (res.success) {
        console.log('getDictItems>>>>>>>>>>>', res);
        let list = res.result;
        // 提取 list 中的 dept 字段形成数组，并赋值给 depts.value
        list = list.map((item) => item.title);

        jobCategories.value = list;

        console.log('fetchCategorys>>>>>>>>>>>', list, jobCategories.value);
      }
    });
  };

  onMounted(fetchDepts);
  onMounted(fetchPositions);
  onMounted(fetchCategorys);

  onMounted(() => {
    reset();
  });
  // 在组件挂载后立即执行一次监听回调

  // 计算属性：过滤后的职位列表
  const filteredPositions = computed(() => {
    console.log('@@@@@@@@@@@@filteredPositions>>>>>>>>>>>positions:', positions.value);
    let filtered = positions.value;

    if (selectedCategory.value) {
      console.log('@@@@@@@@@@@@filteredPositions>>>>>>>>>>>selectedCategory:', selectedCategory.value);
      filtered = filtered.filter((p) => p.category === selectedCategory.value);
    }

    if (searchQuery.value) {
      console.log('@@@@@@@@@@@@filteredPositions>>>>>>>>>>>searchQuery:', searchQuery.value);
      filtered = filtered.filter(
        (p) =>
          p.positionName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          p.dept_dictText.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    }

    if (selectedDept.value.id) {
      console.log('@@@@@@@@@@@@filteredPosition.idzs>>>>>>>>>>>selectedDept:', selectedDept.value);
      filtered = filtered.filter((p) => p.dept_dictText === selectedDept.value.departName);
    }
    console.log('>>>>>过滤后的职位列表：', filtered);
    totalCount.value = filtered.length;
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
    console.log('过滤部门：' + dept);
    selectedDept.value.id = dept.id;
    selectedDept.value.departName = dept.departName;
  };
  // 搜索职位
  const searchPositions = () => {
    console.log('搜索职位：' + searchQuery.value);
    selectedDept.value.id = '';
    selectedDept.value.departName = searchQuery.value;
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
    flex: 1 1 auto;
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

  .filters .categories .active {
    background-color: #6a11cb;
    color: #fff;
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

  .sidebar ul li:hover {
    background-color: #f0f0f0;
  }

  .sidebar ul .active {
    background-color: #3d54a7;
    color: #fff;
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


  .filters .categories button .active {
    background-color: #4caf50;
    color: white;
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
    align-items: center;
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
