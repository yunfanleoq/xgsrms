<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header>
      <div class="logo">中科院信工所人才招聘管理系统</div>
      <nav>
        <RouterLink :to="{ name: 'homeHome' }">首页</RouterLink>|
        <RouterLink :to="{ name: 'homeYjsk' }">研究所况</RouterLink>|
        <RouterLink to="/scientific-development">科研发展</RouterLink>|
        <RouterLink :to="{ name: 'homePositions' }">招聘信息</RouterLink>|
        <RouterLink to="/contact-us">联系我们</RouterLink>

      </nav>
      <div  >
        <button @click="goToLoginPage" class="login" > 登录 </button>
        <button class="register">注册</button>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
    <!-- 友情链接 -->
    <div class="friend-links" >
      <h3>友情链接</h3>
      <div class="friend-link"> <a href="https://www.cas.cn">中国科学院</a>
        --
      <a href="http://www.iie.cas.cn/">中科院信工所</a>

      </div>
    </div>
    <footer >
      <br>
      <p>© 2024 中科院信工所人才招聘管理系统. All rights reserved.</p>
    </footer>
  </div>
</template>



<script setup lang="ts" name="HomeIndex">

import { useRouter } from 'vue-router';

const router = useRouter();

const goToLoginPage = () => {
  router.push({ name: 'Login' });
};


</script>

<style>
/* 全局样式 */
.friend-link {
  display: flex;
  /*justify-content: space-between;*/
  align-items: center;
  width: 100%;
  height: 50px;
  border-radius: 5px;
  background-color: #7fa5e0;
  /*text-align: center;*/
  padding: 10px;
}
.friend-links {
  /*display: flex;*/
  /*justify-content: space-between;*/
  align-items: center;
  width: 100%;
  /*height: 50px;*/
  border-radius: 8px;
  /*background-color: #7fa5e0;*/
  /*text-align: center;*/
  padding: 10px;
}
footer {
  text-align: center;
  padding: 10px;
}

body {
  margin: 0;
  font-family: 'Poppins', sans-serif;
  background-color: #f7f9fc;
}

#app {
  width: 100%;
}

/* 顶部导航栏 */
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, #3d54a7, #3d54a7);
  color: white;
  padding: 10px 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

header .logo {
  font-size: 24px;
  font-weight: bold;
}

header nav {
  display: flex;
  align-items: center;
  justify-content: center; /* 居中导航区内容 */

}

header nav a {
  color: white;
  margin: 0 15px;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  font-size: 20px;
  cursor: pointer;
  font-weight: bold;
}

header  .login,
header  .register {
  margin-left: 15px;
  padding: 5px 15px;
  border-radius: 5px;
  margin-right: 15px;

  background-color: transparent;
  color: white;
  font-size: 20px;
  cursor: pointer;
  font-weight: bold;
}



/* 横幅 */
.banner img {
  width: 100%;
  border-radius: 10px;
  margin: 20px 0;
}

/* 分类和搜索 */
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin: 20px;
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

.filters .search input {
  padding: 10px;
  border-radius: 20px;
  border: 1px solid #ddd;
  width: 250px;
  margin-right: 10px;
}

.filters .search button {
  padding: 10px 20px;
  background-color: #ff7eb3;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.filters .search button:hover {
  background-color: #ff5177;
}

/* 职位列表 */
.job-list {
  display: grid;
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
  color: #ff5177;
  font-weight: bold;
}
</style>
