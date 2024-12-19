<template>
  <div class="job-listing-page">
    <!-- Navigation Bar -->
    <div class="navbar">
      <ul>
        <li><a href="#">首页</a></li>
        <li><a href="#">咨询</a></li>
        <li><a href="#">校园</a></li>
        <li><a href="#">离职</a></li>
      </ul>
    </div>

    <!-- Main Content Area -->
    <div>
      <!-- Banner Image -->
      <img src="" alt="Banner Image" class="banner">

      <!-- Job Categories Sidebar -->
      <aside class="sidebar">
        <h3>Job Categories</h3>
        <ul>
          <li><a href="#">Java开发</a></li>
          <li><a href="#">C++开发</a></li>
          <li><a href="#">前端开发</a></li>
          <li><a href="#">后端开发</a></li>
          <li><a href="#">大数据开发</a></li>
          <li><a href="#">软件测试</a></li>
          <li><a href="#">PHP开发</a></li>
        </ul>
      </aside>

      <!-- Search Box -->
      <div class="search-box">
        <input type="text" placeholder="请输入职位名称" v-model="searchQuery">
        <button @click="searchJobs">查询</button>
      </div>

      <!-- Job Listings Grid -->
      <section class="job-listings">
        <div class="job-card" v-for="job in filteredJobs" :key="job.id">
          <h4>{{ job.title }}</h4>
          <p>{{ job.location }}</p>
          <p>{{ job.salaryRange }}</p>
          <p>{{ job.description }}</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Sample data for jobs
const jobs = ref([
  { id: 1, title: 'Java工程师', location: '北京市', salaryRange: '7000-12000', description: '' },
  { id: 2, title: 'C++工程师', location: '南京市', salaryRange: '5000-8000', description: '' },
  // Add more job listings...
]);

// Filtered jobs based on search query
const searchQuery = ref('');
const filteredJobs = computed(() => {
  return jobs.value.filter(job =>
    job.title.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

function searchJobs() {
  console.log('Searching for:', searchQuery.value);
}
</script>

<style scoped>
/* Add styles for layout and design */
.navbar ul {
  list-style-type: none;
}

.sidebar {
  float: left;
  width: 200px;
}

.banner {
  display: block;
  margin: auto;
  max-width: 100%;
  height: auto;
}

.search-box input {
  padding: 10px;
  font-size: 16px;
}

.job-listings {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.job-card {
  border: 1px solid #ccc;
  padding: 15px;
  background-color: #f9f9f9;
}
</style>
