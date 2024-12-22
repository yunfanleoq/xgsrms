<template>
  <div id="app">
<!--    <header>-->
<!--      <h1>中国科学院信息工程研究所</h1>-->
<!--    </header>-->
    <main>
      <aside>
        <ul>
          <li><a href="#">研究队伍</a></li>
          <li><a href="#">正高级职位</a></li>
          <li><a href="#">副高级职位</a></li>
          <li class="active"><a href="#">人才招聘</a></li>
        </ul>
      </aside>
      <section>
        <h2>人才招聘</h2>
        <ul>
          <li v-for="(item, index) in currentPageItems" :key="index">
            <span>{{ item.title }}</span>
            <span class="date">{{ item.date }}</span>
          </li>
        </ul>
        <div class="pagination">
          <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">上一页</button>
          <span>第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
          <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">下一页</button>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      jobList: [
        { title: "2024-2025年招聘启事", date: "2024-09-18" },
        { title: "国家级人才项目招聘启事", date: "2021-06-15" },
        { title: "院级人才项目招聘启事", date: "2021-06-15" },
        { title: "空间安全研究室招聘启事", date: "2024-11-14" },
        { title: "七室实验室招聘启事", date: "2024-10-17" },
        { title: "空间安全研究室其他岗位招聘启事", date: "2024-10-10" },
        { title: "密码研究室招聘启事", date: "2024-10-09" },
        { title: "技术室招聘启事", date: "2024-09-26" },
        { title: "网络安全测评中心招聘启事", date: "2024-08-16" },
        { title: "大数据研究室招聘启事", date: "2024-08-01" },
      ],
      currentPage: 1,
      itemsPerPage: 5,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.jobList.length / this.itemsPerPage);
    },
    currentPageItems() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.jobList.slice(start, end);
    },
  },
  methods: {
    changePage(page) {
      if (page > 0 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
  },
};
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  font-family: Arial, sans-serif;
}

header {
  background-color: #00467f;
  color: white;
  padding: 10px;
  text-align: center;
}

main {
  display: flex;
  flex: 1;
}

aside {
  width: 200px;
  background-color: #f2f2f2;
  padding: 10px;
}

aside ul {
  list-style: none;
  padding: 0;
}

aside li {
  margin: 10px 0;
}

aside .active a {
  font-weight: bold;
  color: #00467f;
}

section {
  flex: 1;
  padding: 20px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px solid #ddd;
}

.date {
  color: gray;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
</style>
