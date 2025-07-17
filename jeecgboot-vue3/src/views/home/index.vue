<template>
  <div id="app">
    <div class="banner">
      <img src="@/assets/images/xgs_logo_.png" alt="Banner Image" class="banner-image" />
      <img src="@/assets/images/xgs_logo01.png" alt="" class="banner-image" />
    </div>

    <!-- 顶部导航栏 -->
    <header>
      <div class="logo">中科院信工所人才招聘管理系统</div>
      <nav>
        <RouterLink :to="{ name: 'homeHome' }">首页</RouterLink> | <RouterLink :to="{ name: 'homeYjsk' }">研究所况</RouterLink>|
        <RouterLink :to="{ name: 'homeNews' }">科研发展</RouterLink> | <RouterLink :to="{ name: 'homePositions' }">招聘信息</RouterLink>|
        <RouterLink :to="{ name: 'contactUs' }">联系我们</RouterLink>
      </nav>
      <div>
        <div v-if="!isLoggedIn">
          <button @click="goToLoginPage" class="login">登录</button>
          |
          <button @click="goToRegisterPage" class="register">注册</button>
        </div>
        <div v-else>
          <span class="user-info" v-if="loggedInUser">欢迎, {{ loggedInUser.realname }}</span>
          <button @click="logout" class="logout">退出</button>
          <button @click="goToUserCenter" class="user-center">个人中心</button>
        </div>
      </div>
    </header>
    <main class="main-content">
      <RouterView />
    </main>
    <!-- 友情链接 -->
    <div class="friend-links">
      <!--      <h3>友情链接</h3>-->
      <div class="friend-link">
        <a href="https://www.cas.cn" target="_blank">中国科学院</a>
        |
        <a href="http://www.nsfc.gov.cn/" target="_blank">国家自然科学基金</a>
        |
        <a href="https://www.casjob.com/" target="_blank">中科人才网</a>
        |
        <a href="http://www.iie.cas.cn/" target="_blank">中科院信工所</a>
      </div>
    </div>
    <footer>
      <br />
      <p>© {{ currentYear }} 中科院信工所人才招聘管理系统. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';
  import {computed, onMounted, reactive, ref, watchEffect} from 'vue';
  import { useUserStore } from '@/store/modules/user';
  import { doLogout } from '@/api/sys/user';
  import {useMultipleTabStore} from "@/store/modules/multipleTab";
  const router = useRouter();
  const userStore = useUserStore();

  const isLoggedIn = computed(() => !!userStore.userInfo?.username);
  const loggedInUser = computed(() => userStore.userInfo);

  onMounted(() => {
    userStore.getUserInfoAction();
  });

  const currentYear = computed(() => new Date().getFullYear());

  const logout = () => {
    doLogout().then(() => {
      router.push({ name: 'Login' });
    });
  };

  const goToUserCenter = () => {

    // useMultipleTabStore.closeAllTabs(router);
    // 正确调用方式：先获取 store 实例
    const tabStore = useMultipleTabStore();
    tabStore.closeAllTab(router);
    router.push({ path: '/dashboard/analysis' });
  };

  const goToLoginPage = () => {
    router.push({ name: 'Login' });
  };

  const goToRegisterPage = () => {
    router.push({ name: 'Login', query: { tab: 'register' } });
  };
</script>

<style>
  #app nav a:hover,
  #app nav a:focus {
    color: #ffffff;
    text-decoration: underline;
  }
  .main-content {
    flex: 1; /* 允许容器根据内容自动扩展 */
    min-height: 1000px; /* 设置最小高度为1000像素 */
    padding: 20px; /* 可选：添加内边距 */
    background-color: #f9f9f9; /* 可选：添加背景颜色 */
  }

  /* 全局样式 */
  .friend-link {
    display: flex;
    justify-content: space-around; /* 或者使用 space-between 根据需要 */
    align-items: center;
    width: 100%;
    height: 50px;
    border-radius: 5px;
    background-color: #7fa5e0;
    padding: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  }

  .link-item {
    color: white; /* 文字颜色与背景颜色对比 */
    text-decoration: none; /* 去掉下划线 */
    font-family: Arial, sans-serif; /* 清晰易读的字体 */
    font-size: 16px;
    transition: color 0.3s ease; /* 添加过渡效果 */
  }

  .link-item:hover {
    color: #ffcc00; /* 悬停时的文字颜色 */
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

  header nav a:hover {
    color: white;
    text-decoration: underline;
  }

  header .login,
  header .register {
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

  .banner {
    background-image: url('@/assets/images/xgs_topbg.png'); /* 替换为你的背景图URL */
    background-size: cover;
    display: flex;
    align-items: center;
  }

  .banner-image {
    height: 100px;
    width: 80%;
    border-radius: 7px;
    object-fit: contain; /* 保持图像的宽高比 */
    margin-right: 10px; /* 可选：设置图像之间的间距 */
  }

  /* 横幅 */
  .banner img {
    width: 100%;
    border-radius: 10px;
    margin: 20px 0;
  }
  .logout {
    margin-left: 15px;
    padding: 5px 15px;
    border-radius: 5px;
    margin-right: 15px;

    background-color: #7b467f;
    color: white;
    font-size: 20px;
    cursor: pointer;
  }

  .user-center {
    margin-left: 15px;
    padding: 5px 15px;
    border-radius: 5px;
    margin-right: 15px;

    background-color: #74c25e;
    color: white;
    font-size: 20px;
    cursor: pointer;
  }
  .user-info {
    margin-left: 15px;
    padding: 5px 15px;
    border-radius: 5px;
    /*background-color: #0c8fcf;*/
    font-size: 20px;
  }
</style>
