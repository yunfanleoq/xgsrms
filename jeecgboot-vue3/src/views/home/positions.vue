<template>
  <div class="portal-page positions-page">
    <header class="page-head">
      <h1>招聘岗位</h1>
    </header>
    <div class="type-bar" role="tablist" aria-label="岗位类型">
      <button type="button" class="type-link" :class="{ active: !selectedPositionType }" @click="selectPositionType(null)">全部</button>
      <button
        v-for="typeName in positionTypes"
        :key="typeName"
        type="button"
        class="type-link"
        :class="{ active: selectedPositionType === typeName }"
        @click="selectPositionType(typeName)"
      >
        {{ positionTypeLabel(typeName) }}
      </button>
    </div>
    <div v-if="selectedPositionType === talentParent" class="subtype-bar" role="tablist" aria-label="引进高层次人才子项">
      <button type="button" class="subtype-link" :class="{ active: !selectedPositionSubtype }" @click="selectPositionSubtype(null)">全部</button>
      <button
        v-for="subName in talentSubtypes"
        :key="subName"
        type="button"
        class="subtype-link"
        :class="{ active: selectedPositionSubtype === subName }"
        @click="selectPositionSubtype(subName)"
      >
        {{ subName }}
      </button>
    </div>
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
          <p v-if="!paginatedPositions.length" class="job-quiet">暂无在招岗位</p>
          <!-- 分页控件 -->
          <div v-if="totalCount > 0" class="pagination-wrapper">
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
  import {getPositionList, getDeptList, getJobById} from '@/api/xgsrms/home';
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

  const talentParent = '引进高层次人才';
  const namedTalentSubtypes = ['高层次领军人才', '海外优青', '高层次青年人才'];
  const talentSubtypes = [...namedTalentSubtypes, '其他副高及以上岗位'];
  const portalPositionTypes = ['引进高层次人才', '校园招聘', '社会招聘', '博士后招聘', '劳务派遣招聘'];
  const positionTypeLabels: Record<string, string> = {
    博士后招聘: '博士后',
    劳务派遣招聘: '劳务派遣',
  };
  const positionTypeLabel = (typeName: string) => positionTypeLabels[typeName] || typeName;
  const positionTypes = ref<string[]>([...portalPositionTypes]);
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
        url: "/xgsHome/xgsHome/listForHome",
      });

      if (response && response.records) {
        // 顺序与后端 listForHome（按 create_time 降序）一致；门户接口已脱敏不返回 createTime
        announcements.value = response.records.map((item: any) => ({
            id: item.id, // 映射招聘公告的主键 ID
            title: item.recruitAnnouncementTitle,
            createTime: item.createTime,
          }))
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
    await router.push({
      name: 'positionDetail',
      params: { id: String(positionId) },
      query: { from: route.fullPath },
    });
    
    // 路由跳转后立即滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    showPositionList.value = false;
  };

  const talentSubtypeOf = (job: { positionSubtype?: string; positionName?: string }) => {
    const stored = String(job?.positionSubtype || '').trim();
    if (talentSubtypes.includes(stored)) return stored;
    const name = String(job?.positionName || '').trim();
    if (namedTalentSubtypes.includes(name)) return name;
    return '其他副高及以上岗位';
  };

  const selectPositionType = (type: string | null) => {
    showPositionList.value = true;
    showAnnouncementList.value = false;
    selectedCategory.value = null;
    selectedPositionType.value = type;
    selectedPositionSubtype.value = null;
    const query: Record<string, string> = {};
    if (type) query.positionType = type;
    router.push({ name: 'homePositions', query });
  };

  const selectPositionSubtype = (subtype: string | null) => {
    showPositionList.value = true;
    showAnnouncementList.value = false;
    selectedCategory.value = null;
    selectedPositionType.value = talentParent;
    selectedPositionSubtype.value = subtype;
    const query: Record<string, string> = { positionType: talentParent };
    if (subtype) query.positionSubtype = subtype;
    router.push({ name: 'homePositions', query });
  };

  const searchQuery = ref('');
  const selectedCategory = ref<string | null>(null);
  const selectedPositionType = ref<string | null>(null);
  const selectedPositionSubtype = ref<string | null>(null);
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
    const subtype = selectedPositionType.value === talentParent ? selectedPositionSubtype.value : null;
    if (selectedPositionType.value) {
      params.positionType = selectedPositionType.value;
    }
    if (subtype) {
      params.positionSubtype = subtype;
      if (namedTalentSubtypes.includes(subtype)) {
        params.positionName = subtype;
      } else {
        params.pageNo = 1;
        params.pageSize = 100;
      }
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
          const list = res.result || {};
          let records = list.records || [];
          let total = list.total || 0;
          if (subtype) {
            const filtered = records.filter((job) => talentSubtypeOf(job) === subtype);
            if (namedTalentSubtypes.includes(subtype) && filtered.length === records.length) {
              records = filtered;
            } else if (namedTalentSubtypes.includes(subtype)) {
              records = filtered;
              total = filtered.length;
            } else {
              const start = (currentPage.value - 1) * positionsPerPage.value;
              total = filtered.length;
              records = filtered.slice(start, start + positionsPerPage.value);
            }
          }
          positions.value = records;
          totalCount.value = total;
        }
      })
      .catch((e) => {
        console.error('getPositionList failed', e);
      });
  };

  // 监听查询条件变化，重置到第一页并重新获取数据
  // 注意：searchQuery 不在这里监听，而是通过点击搜索按钮触发
  watch([selectedCategory, selectedPositionType, selectedPositionSubtype, selectedDept], () => {
    currentPage.value = 1;
    fetchPositions();
  });

  const reset = () => {
    selectedDept.value = null;
    searchQuery.value = '';
    selectedCategory.value = null;
    selectedPositionType.value = null;
    selectedPositionSubtype.value = null;
    currentPage.value = 1;
    if (route.query.positionType || route.query.positionSubtype || route.query.category || route.query.keyword) {
      router.replace({ name: 'homePositions' });
    }
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

  const categoryFromRoute = () => {
    const raw = route.query.category;
    const category = Array.isArray(raw) ? raw[0] : raw;
    return typeof category === 'string' && category ? category : null;
  };

  const positionTypeFromRoute = () => {
    const raw = route.query.positionType;
    const positionType = Array.isArray(raw) ? raw[0] : raw;
    return typeof positionType === 'string' && positionType ? positionType : null;
  };

  const keywordFromRoute = () => {
    const raw = route.query.keyword;
    const keyword = Array.isArray(raw) ? raw[0] : raw;
    return typeof keyword === 'string' ? keyword.trim() : '';
  };

  const positionSubtypeFromRoute = () => {
    if (positionTypeFromRoute() !== talentParent) return null;
    const raw = route.query.positionSubtype;
    const positionSubtype = Array.isArray(raw) ? raw[0] : raw;
    return typeof positionSubtype === 'string' && talentSubtypes.includes(positionSubtype) ? positionSubtype : null;
  };

  onMounted(() => {
    const category = categoryFromRoute();
    const positionType = positionTypeFromRoute();
    const positionSubtype = positionSubtypeFromRoute();
    const keyword = keywordFromRoute();
    if (keyword) {
      searchQuery.value = keyword;
    }
    if (category) {
      selectedCategory.value = category;
    }
    if (positionType) {
      selectedPositionType.value = positionType;
    }
    if (positionSubtype) {
      selectedPositionSubtype.value = positionSubtype;
    }
    fetchDepts();
    if (!category && !positionType) {
      fetchPositions();
    }
  });

  watch(
    () => {
      const raw = route.query.keyword;
      const keyword = Array.isArray(raw) ? raw[0] : raw;
      return typeof keyword === 'string' ? keyword.trim() : '';
    },
    (keyword) => {
      if (!keyword || keyword === searchQuery.value) return;
      searchQuery.value = keyword;
      currentPage.value = 1;
      showAnnouncementList.value = false;
      showPositionList.value = true;
      fetchPositions();
    },
  );

  watch(
    () => [route.query.category, route.query.positionType, route.query.positionSubtype],
    () => {
      const category = categoryFromRoute();
      const positionType = positionTypeFromRoute();
      const positionSubtype = positionSubtypeFromRoute();
      if (
        category !== selectedCategory.value
        || positionType !== selectedPositionType.value
        || positionSubtype !== selectedPositionSubtype.value
      ) {
        showAnnouncementList.value = false;
        showPositionList.value = true;
        selectedCategory.value = category;
        selectedPositionType.value = positionType;
        selectedPositionSubtype.value = positionSubtype;
      }
    },
  );

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
  .positions-page {
    color: #333333;
  }

  .type-bar {
    display: flex;
    flex-wrap: wrap;
    align-items: stretch;
    gap: 4px;
    margin: 0 0 18px;
    border-bottom: 1px solid #e6e6e6;
  }

  .type-link {
    position: relative;
    border: 0;
    background: transparent;
    color: #444444;
    font-size: 16px;
    font-weight: 400;
    letter-spacing: 0;
    padding: 8px 16px 10px;
    cursor: pointer;
  }

  .type-link:hover,
  .type-link.active {
    color: #004098;
  }

  .type-link.active::after {
    content: '';
    position: absolute;
    left: 16px;
    right: 16px;
    bottom: 0;
    height: 2px;
    background: #004098;
  }

  .subtype-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin: -8px 0 18px;
    padding: 0 8px;
  }

  .subtype-link {
    border: 1px solid #e6e6e6;
    background: #fff;
    color: #444444;
    font-size: 14px;
    line-height: 1.4;
    padding: 4px 12px;
    cursor: pointer;
  }

  .subtype-link:hover,
  .subtype-link.active {
    color: #004098;
    border-color: #004098;
    background: #f3f6fb;
  }

  .content {
    background: transparent;
    padding: 0;
  }

  .filters {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 20px 24px;
    background: #fff;
    border: 1px solid #e6e6e6;
    margin-bottom: 18px;
  }

  .filter-section {
    display: flex;
    align-items: flex-start;
    gap: 12px;
  }

  .filter-label {
    font-weight: 400;
    color: #8a8a8a;
    white-space: nowrap;
    padding-top: 6px;
    font-size: 14px;
  }

  .dept-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    flex: 1;
  }

  .dept-button {
    padding: 6px 12px;
    border-radius: 0;
    border: 1px solid #e6e6e6;
    background: #fff;
    color: #444444;
    font-weight: 400;
    cursor: pointer;
    font-size: 14px;
  }

  .dept-button:hover,
  .dept-button.active {
    color: #004098;
    border-color: #004098;
    background: #fff;
  }

  .job-list-container {
    padding: 0;
  }

  .job-quiet {
    margin: 8px 0 0;
    color: #8a8a8a;
    font-size: 14px;
  }

  .job-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(420px, 1fr));
    gap: 16px;
  }

  .job-card {
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: 0;
    box-shadow: none;
    padding: 20px 22px;
    margin: 0;
    cursor: pointer;
    border: 1px solid #e6e6e6;
    position: relative;
  }

  .job-card:hover {
    border-color: #004098;
  }

  .job-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e6e6e6;
  }

  .job-title {
    font-size: 18px;
    font-weight: 700;
    color: #333333;
    margin: 0;
    line-height: 1.4;
    flex: 1;
  }

  .job-card:hover .job-title {
    color: #004098;
  }

  .job-status-badge {
    padding: 2px 8px;
    border-radius: 0;
    font-size: 13px;
    font-weight: 400;
    white-space: nowrap;
    margin-left: 16px;
  }

  .status-active {
    background: #f7f7f7;
    color: #004098;
    border: 1px solid #e6e6e6;
  }

  .status-closed {
    background: #fff;
    color: #8a8a8a;
    border: 1px solid #e6e6e6;
  }

  .status-default {
    background: #f7f7f7;
    color: #8a8a8a;
    border: 1px solid #e6e6e6;
  }

  .job-card-body {
    flex: 1;
    margin-bottom: 12px;
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
    gap: 4px;
  }

  .info-label {
    font-size: 13px;
    color: #8a8a8a;
    font-weight: 400;
  }

  .info-value {
    font-size: 15px;
    color: #333333;
    font-weight: 400;
  }

  .info-value.highlight {
    color: #004098;
    font-weight: 400;
  }

  .job-card-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 8px;
    padding-top: 12px;
    border-top: 1px solid #e6e6e6;
    color: #004098;
    font-size: 14px;
  }

  .arrow-icon {
    font-size: 12px;
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
    border: 1px solid #e6e6e6;
    border-radius: 0;
    background: #fff;
    color: #444444;
    cursor: pointer;
  }

  .pagination button:disabled {
    background: #f7f7f7;
    color: #8a8a8a;
    cursor: not-allowed;
  }

  .announcement-list {
    background: #fff;
    border: 1px solid #e6e6e6;
    padding: 8px 20px 20px;
  }

  .announcement-list ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .announcement-list h2 {
    font-size: 18px;
    font-weight: 700;
    margin: 16px 0;
    color: #333333;
  }

  .announcement-item {
    cursor: pointer;
    list-style: none;
  }

  .announcement-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 14px 0;
    border-bottom: 1px solid #e6e6e6;
  }

  .announcement-card h3 {
    font-size: 16px;
    font-weight: 400;
    margin: 0;
    color: #444444;
  }

  .announcement-item:hover h3 {
    color: #004098;
  }

  .announcement-time {
    font-size: 14px;
    color: #8a8a8a;
    white-space: nowrap;
  }

  .search-container {
    display: flex;
    gap: 10px;
    align-items: center;
  }

  .search-input {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #e6e6e6;
    border-radius: 0;
    font-size: 14px;
    background: #fff;
    color: #333333;
  }

  .search-input:focus {
    outline: none;
    border-color: #004098;
  }

  .search-buttons {
    display: flex;
    gap: 8px;
  }

  .search-button {
    padding: 8px 18px;
    border: 1px solid #004098;
    border-radius: 0;
    background: #fff;
    color: #004098;
    font-weight: 400;
    cursor: pointer;
    font-size: 14px;
  }

  .search-button:last-child {
    background: #004098;
    color: #fff;
  }

  .search-button:hover {
    background: #10243f;
    border-color: #10243f;
    color: #fff;
  }

  .pagination-wrapper {
    margin-top: 18px;
    padding: 16px 0 0;
    background: transparent;
  }

  .pagination-info {
    color: #8a8a8a;
    font-size: 14px;
    margin-left: 16px;
    white-space: nowrap;
  }

  .pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .page-btn,
  .page-number {
    min-width: 36px;
    height: 36px;
    padding: 0 12px;
    border: 1px solid #e6e6e6;
    background: #fff;
    color: #444444;
    border-radius: 0;
    cursor: pointer;
    font-size: 14px;
  }

  .page-btn:hover:not(:disabled),
  .page-number:hover {
    color: #004098;
    border-color: #004098;
  }

  .page-btn:disabled {
    color: #cccccc;
    cursor: not-allowed;
    background: #f7f7f7;
  }

  .page-numbers {
    display: flex;
    gap: 6px;
  }

  .page-number.active {
    background: #004098;
    color: #fff;
    border-color: #004098;
  }

  .page-ellipsis {
    min-width: 36px;
    height: 36px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #8a8a8a;
    font-size: 14px;
  }

  .page-size-selector {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-left: 12px;
    padding-left: 12px;
    border-left: 1px solid #e6e6e6;
  }

  .page-size-selector label {
    color: #8a8a8a;
    font-size: 14px;
  }

  .page-size-selector select {
    padding: 6px 8px;
    border: 1px solid #e6e6e6;
    border-radius: 0;
    font-size: 14px;
    background: #fff;
    color: #333333;
  }

  .page-size-selector select:focus {
    outline: none;
    border-color: #004098;
  }

  @media (max-width: 768px) {
    .filter-section,
    .search-container {
      flex-direction: column;
      align-items: stretch;
    }

    .job-card-header,
    .job-info-row {
      flex-direction: column;
      gap: 8px;
    }

    .job-status-badge {
      margin-left: 0;
      align-self: flex-start;
    }

    .type-link {
      font-size: 15px;
      padding: 8px 12px 10px;
    }
  }
</style>
